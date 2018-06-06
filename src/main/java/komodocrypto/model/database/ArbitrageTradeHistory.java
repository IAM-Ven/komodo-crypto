package komodocrypto.model.database;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ArbitrageTradeHistory{

//    int arbitrage_trade_id;
//    int currency_pair_id;
//    int buy_transaction_id;
//    int sell_transaction_id;
//    BigDecimal sell_price;
//    BigDecimal sell_amount;
//    BigDecimal buy_price;
//    BigDecimal buy_amount;
//    String status;
//    int buy_exchange_id;
//    int sell_exchange_id;
//    Timestamp timestamp;

    int arbitrage_trade_id;
    int trade_pair_id; //fk of arbitrage_id from arbitrage table
    int transaction_id; //fk of the transaction table - on real trade this will probably generated by the exchange
    int exchange_id;
    BigDecimal price;
    BigDecimal amount;
    String status;
    Timestamp timestamp;



}