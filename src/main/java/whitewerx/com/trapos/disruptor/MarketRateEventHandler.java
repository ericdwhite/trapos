package whitewerx.com.trapos.disruptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

import whitewerx.com.trapos.model.Rate;
import whitewerx.com.trapos.translators.RateTranslator;

/**
 * EventHandler that converts raw rate messages into {@link Rate} objects.
 * 
 * @author ewhite
 */
public class MarketRateEventHandler implements EventHandler<MarketEvent> {
    private static final Logger l = LoggerFactory.getLogger(MarketRateEventHandler.class.getName());
    
    private RateTranslator translator;
    
    public MarketRateEventHandler(RateTranslator translator) {
        this.translator = translator;
    }
    
    public MarketRateEventHandler() {
        this(new RateTranslator());
    }

    public void onEvent(MarketEvent marketEvent, long sequence, boolean endOfBatch) throws Exception {
        String delmitedRate = marketEvent.getMessage();
        
        if(!translator.canHandle(delmitedRate))
            return;
        Rate rate = translator.translate(delmitedRate);

        marketEvent.accept(rate);
        
        if(l.isInfoEnabled())
            l.info("onEvent: seq:"+sequence + "/" + endOfBatch + " event: "+ marketEvent);
    }
}
