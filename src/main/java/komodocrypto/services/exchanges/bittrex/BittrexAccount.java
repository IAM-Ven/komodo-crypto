package komodocrypto.services.exchanges.bittrex;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import komodocrypto.configuration.exchange_utils.BittrexUtil;
import komodocrypto.services.exchanges.interfaces.ExchangeAccountService;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bittrex.dto.account.BittrexBalance;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Example showing the following:
 *
 * <ul>
 *   <li>Connect to Bitstamp exchange with authentication</li>
 *   <li>View account balance</li>
 *   <li>Get the bitcoin deposit address</li>
 *   <li>List unconfirmed deposits (raw interface only)</li>
 *   <li>List recent withdrawals (raw interface only)</li>
 *   <li>Withdraw a small amount of BTC</li>
 * </ul>
 */
@Service
public class BittrexAccount implements ExchangeAccountService{


    private AccountService accountService;
    private AccountInfo accountInfo;

    @Autowired
    private BittrexUtil bittrexUtil;


    /**
     * Configures the accountService to be used setting it up with the credentials for the account
     * defined for the Bittrex exchange.
     */
    @PostConstruct
    public void setupAccountServiceAndInfo() throws IOException{
        Exchange bittrex = bittrexUtil.createExchange();
        //AccountService accountService = bittrex.getAccountService();
        this.accountService = bittrex.getAccountService();
        this.accountInfo = accountService.getAccountInfo();
        //return accountService;
    }

//    /**
//     * Creates an AccountInfo object of an exchange with the credential used in setupAccountService()
//     * @return an AccountInfo obj
//     * @throws IOException
//     */
//    @PostConstruct
//    public void accountInfo() throws IOException {
//        AccountService accountService = setupAccountService();
//        return accountService.getAccountInfo();
//    }

    public String getUsername() throws IOException {
        //String username = this.accountInfo.getUsername();
        String username = this.getAccountInfo().getUsername();
        return username;
    }


    /**
     * Returns the trading fee for the Bittrex exchange.
     * Currently the XChange API returns null for Bittrex, so the value is hardcoded for now.
     * @return Bittrex fee percentage
     */
    public BigDecimal getTradingFee() {
       // BigDecimal tradingFee = this.accountInfo.getTradingFee(); // Currently returns null
        BigDecimal tradingFee = new BigDecimal(0.25);
        return tradingFee;
    }

    /**
     * Gets the deposit address of an exchange wallet for the given crypto currency
     * @param currency A currency obj
     * @return the deposit address of a wallet in String format
     */
    @Override
    public String getDepositAddress(Currency currency) {
        String depositAddress = null;
        try {
            depositAddress = this.accountService.requestDepositAddress(currency);
        } catch (IOException e) {
            System.out.println("IOException while trying to get the deposit address");
            e.printStackTrace();
        }
        return depositAddress;
    }

    /**
     * Returns a Balance obj containing the information regarding the balance of
     * a given currency. This includes the total balance, as well as the
     * available, borrowed, loaned and frozen balance.
     * @param currency
     * @return a Balance obj.
     */
    @Override
    public Balance getCurrencyBalance(Currency currency) {
        Balance balance = accountInfo.getWallet().getBalance(currency);
        return balance;
    }

    /**
     * Withdraws a given ammount of crypto currency to the given address.
     * @param currency the currency to withdraw
     * @param quantity the amount to withdraw
     * @param address the address of the wallet to sent it to
     * @return
     */
    @Override
    public String withdrawFunds(Currency currency, BigDecimal quantity, String address) {
        String transactionID = null;
        try {
            transactionID = this.accountService.withdrawFunds(currency, quantity, address);
        } catch (IOException e) {
            System.out.println("IOException while withdrawing funds");
            e.printStackTrace();
        }
        return transactionID;
    }

    private void generic(AccountService accountService) throws IOException {

        // Get the account information
        AccountInfo accountInfo = accountService.getAccountInfo();
        System.out.println("AccountInfo as String: " + accountInfo.toString());

        String depositAddress = accountService.requestDepositAddress(Currency.BTC);
        System.out.println("Deposit address: " + depositAddress);

        TradeHistoryParams tradeHistoryParams = accountService.createFundingHistoryParams();
        List<FundingRecord> fundingRecords = accountService.getFundingHistory(tradeHistoryParams);
        // Only works if you have transaction history. I do not.
        for (FundingRecord record : fundingRecords) {
            System.out.println(record.getStatus());
            System.out.println(record.getBlockchainTransactionHash());
            System.out.println(record.getAddress());
            System.out.println(record.getInternalId());
        }

        // WARNING: WILL WITHDRAW COINS IF THERE IS AN ADDRESS
        // Address is set to "XXX" so will throw an error, but don't actually use this to withdraw money.
        String withdrawResult =
                accountService.withdrawFunds(Currency.BTC, new BigDecimal(1).movePointLeft(4), "XXX");
        System.out.println("withdrawResult = " + withdrawResult);
    }


    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }
}
