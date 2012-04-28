package whitewerx.com.trapos.model;

/**
 * Buying or selling the quote currency.
 * 
 * E.g. BUY means buying EUR, in a EURUSD trade.
 * 
 * @author ewhite
 */
public enum TradeType {
    BUY("B"),
    SELL("S");
    
    private String token;
    
    private TradeType(String token) {
        this.token = token;
    }
    
    public static TradeType tradeTypeFor(String token) {
        if(BUY.token.equals(token))
            return BUY;
        if(SELL.token.equals(token))
            return SELL;
        throw new IllegalArgumentException("Invalid trade type token: "+token);
    }
}
