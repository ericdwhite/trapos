package whitewerx.com.trapos.util;

import static whitewerx.com.trapos.util.CurrencyPairProvider.EURUSD;
import whitewerx.com.trapos.model.Amount;
import whitewerx.com.trapos.model.Rate;
import whitewerx.com.trapos.model.Trade;
import whitewerx.com.trapos.model.TradeType;

/**
 * Sample trades to test with.
 * 
 * @author ewhite
 */
public class TradeProvider {
    
    
    public static final Trade buyEURUSD;
    public static final Trade buy5mEURUSD;
    public static final Trade sell3mEURUSD;

    public static final int ONE_MILLION = 1000 * 1000;

    static {
        buyEURUSD = createSimpleEURUSDTrade();
        buy5mEURUSD = create5mEURUSDTrade();
        sell3mEURUSD = create3mEURUSDTrade();
    }
    
    private static Trade createSimpleEURUSDTrade() {
        Amount fivePointOneThousand = new Amount(5.1 * 1000, CurrencyProvider.EUR);
        Rate atEURUSDRate = new Rate(1.3124, EURUSD);
        return new Trade(TradeType.BUY, fivePointOneThousand, atEURUSDRate);
    }
    
    private static Trade create5mEURUSDTrade() {
        Amount amount = new Amount(5 * ONE_MILLION, CurrencyProvider.EUR);
        Rate atEURUSDRate = new Rate(1.3150, EURUSD);
        return new Trade(TradeType.BUY, amount, atEURUSDRate);
    }

    private static Trade create3mEURUSDTrade() {
        Amount amount = new Amount(3 * ONE_MILLION, CurrencyProvider.EUR);
        Rate atEURUSDRate = new Rate(1.3160, EURUSD);
        return new Trade(TradeType.SELL, amount, atEURUSDRate);
    }
}
