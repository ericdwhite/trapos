package whitewerx.com.trapos.translators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whitewerx.com.trapos.model.Amount;
import whitewerx.com.trapos.model.Rate;
import whitewerx.com.trapos.model.Trade;
import whitewerx.com.trapos.model.TradeType;

/**
 * Translates String delimited trade into trade objects.
 * 
 * <pre>
 * T|B|5.1t|R|EURUSD|1.3124 -> Buy 5.1 thousand EUR at @ 1.3124 EURUSD. 
 * </pre>
 * 
 * @author ewhite
 */
public class TradeTranslator {

    private static final Logger l = LoggerFactory.getLogger(TradeTranslator.class.getName());
    
    private RateTranslator rateTranslator = new RateTranslator();
    
    /** Match TRADE|(Buy/Sell)|(double amount)(t==thousands)|(rate) */
    private static final Pattern tradeRegex = Pattern.compile("^T\\|([B|S]+)\\|(\\d+(\\.\\d+)?)([t,m]*)\\|(R\\|.*)");
    
    // Pattern Groups
    private static final int TRADE_TYPE = 1;
    private static final int AMOUNT = 2;
    private static final int MULTIPLIER = 4;
    private static final int RATE = 5;

    public Trade translate(String delimitedTrade) throws TranslateException {
        if( delimitedTrade==null )
            throw new IllegalArgumentException("A trade must be passed in to parse.");
        
        Matcher m = tradeRegex.matcher(delimitedTrade);
        if( !m.matches() )
            throw new TranslateException("Failed to translate trade.", delimitedTrade);

        TradeType buyOrSell = parseTradeTypeFrom(m.group(TRADE_TYPE));
        Rate tradeRate = parseTradeRateFrom(m.group(RATE));
        TradeMultiplier multiplier = parseTradeMultiplierFrom(m.group(MULTIPLIER));
        Amount tradeAmount = parseTradeAmountFrom(m.group(AMOUNT), multiplier, tradeRate);
        
        Trade trade = new Trade(buyOrSell, tradeAmount, tradeRate);
        if(l.isTraceEnabled())
            l.trace(delimitedTrade +"->" + trade);

        return trade;
    }
    
    /**
     * Quick verification to see if this can even handle the message.
     * @param delimitedTrade
     */
    public boolean canHandle(String delimitedTrade) {
        if(delimitedTrade.charAt(0)=='T')
            return true;
        return false;
    }
    
    private TradeType parseTradeTypeFrom(String parsedTradeType) {
        return TradeType.tradeTypeFor(parsedTradeType);
    }

    private TradeMultiplier parseTradeMultiplierFrom(String parsedMultiplier) {
        return TradeMultiplier.multiplierFor(parsedMultiplier);
    }

    private Amount parseTradeAmountFrom(String parsedAmount, TradeMultiplier multiplier, Rate tradeRate) {
        double amount = Double.valueOf(parsedAmount);
        amount *= multiplier.factor();
        return new Amount(amount, tradeRate.getBaseCurrency());
    }

    private Rate parseTradeRateFrom(String delimitedRate) throws TranslateException {
        Rate tradeRate = rateTranslator.translate(delimitedRate);
        return tradeRate;
    }
}
