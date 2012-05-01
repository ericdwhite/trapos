package whitewerx.com.trapos.util;

import whitewerx.com.trapos.model.CurrencyPair;

import static whitewerx.com.trapos.util.CurrencyProvider.USD;
import static whitewerx.com.trapos.util.CurrencyProvider.EUR;
import static whitewerx.com.trapos.util.CurrencyProvider.CAD;
import static whitewerx.com.trapos.util.CurrencyProvider.JPY;

/**
 * Sample currency pairs for test cases.
 * 
 * @author ewhite
 */
public class CurrencyPairProvider {
    public static final CurrencyPair EURUSD = new CurrencyPair(EUR, USD);
    public static final CurrencyPair USDCAD = new CurrencyPair(USD, CAD);
    public static final CurrencyPair USDJPY = new CurrencyPair(USD, JPY);
}
