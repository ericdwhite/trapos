package whitewerx.com.trapos.disruptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

import whitewerx.com.trapos.model.Trade;
import whitewerx.com.trapos.translators.TradeTranslator;

/**
 * EventHandler that converts raw trade messages into {@link Trade} objects.
 * 
 * @author ewhite
 */
public class MarketTradeEventHandler implements EventHandler<MarketEvent> {
    private static final Logger l = LoggerFactory.getLogger(MarketTradeEventHandler.class.getName());
    
    private TradeTranslator translator;
    
    public MarketTradeEventHandler(TradeTranslator translator) {
        this.translator = translator;
    }
    
    public MarketTradeEventHandler() {
        this(new TradeTranslator());
    }

    public void onEvent(MarketEvent marketEvent, long sequence, boolean endOfBatch) throws Exception {
        String delmitedTrade = marketEvent.getMessage();
        
        if(!translator.canHandle(delmitedTrade))
            return;
        Trade trade = translator.translate(delmitedTrade);

        marketEvent.accept(trade);
        
        if(l.isInfoEnabled())
            l.info("onEvent: seq:"+sequence + "/" + endOfBatch + " event: "+ marketEvent);
    }
}
