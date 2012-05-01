package whitewerx.com.trapos.translators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whitewerx.com.trapos.model.Currency;
import whitewerx.com.trapos.model.CurrencyPair;
import whitewerx.com.trapos.model.Rate;

/**
 * Translates String delimited rates into Rate objects.
 * 
 * <pre>
 * R|EURUSD|1.3124 -> a EURUSD rate @ 1.3124
 * </pre>
 * 
 * @author ewhite
 */
public class RateTranslator {
    
    private static final Logger l = LoggerFactory.getLogger(RateTranslator.class.getName());
        
    /** Match RATE|(CCY1)(CCY2)|(rate as a double) */
    private static final Pattern rateRegex = Pattern.compile("^R\\|([A-Z]{3})([A-Z]{3})\\|(\\d+(\\.\\d+)?)");
    
    // Pattern Groups
    private static int CCY1 = 1;
    private static int CCY2 = 2;
    private static int RATE = 3;

    public Rate translate(String delimitedRate) throws TranslateException {
        if( delimitedRate == null )
            throw new IllegalArgumentException("Rate must be passed in to be parsed.");
        
        Matcher m = rateRegex.matcher(delimitedRate);
        if( !m.matches() )
            throw new TranslateException("Failed to translate rate.", delimitedRate);

        String ccy1 = m.group(CCY1);
        String ccy2 = m.group(CCY2);
        double atRate = Double.valueOf(m.group(RATE));
        
        Rate rate = new Rate(atRate, new CurrencyPair(new Currency(ccy1), new Currency(ccy2)));
        if(l.isTraceEnabled())
            l.trace(delimitedRate +"->" + rate);
        
        return rate;
    }
    
    /**
     * Quick verification to see if this can even handle the message.
     * @param delimitedTrade
     */
    public boolean canHandle(String delimitedTrade) {
        if(delimitedTrade.charAt(0)=='R')
            return true;
        return false;
    }    
}
