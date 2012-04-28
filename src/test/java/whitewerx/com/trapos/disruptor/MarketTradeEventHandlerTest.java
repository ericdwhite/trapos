package whitewerx.com.trapos.disruptor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import whitewerx.com.trapos.disruptor.MarketEvent;
import whitewerx.com.trapos.disruptor.MarketTradeEventHandler;
import whitewerx.com.trapos.model.Trade;
import whitewerx.com.trapos.translators.TradeTranslator;

@RunWith(JMock.class)
public class MarketTradeEventHandlerTest {
    
    Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    @Test
    public void consumesTradeEvent() throws Exception {
        
        final String delimitedTrade = "T|S|2.3m|R|GBPUSD|1.6324";
        final TradeTranslator tradeTranslator = context.mock(TradeTranslator.class);
        final MarketEvent marketEvent = context.mock(MarketEvent.class);
        final Trade trade = context.mock(Trade.class);
        
        final MarketTradeEventHandler h = new MarketTradeEventHandler(tradeTranslator);
        context.checking(new Expectations(){{
            oneOf(marketEvent).getMessage();
            will(returnValue(delimitedTrade));
            
            oneOf(tradeTranslator).canHandle(delimitedTrade);
            will(returnValue(true));
            oneOf(tradeTranslator).translate(delimitedTrade);
            will(returnValue(trade));

            oneOf(marketEvent).accept(with(any(Trade.class)));
        }});
        
        h.onEvent(marketEvent, 1, true);
    }
    
    @Test
    public void shouldNotConsumeRateEvents() throws Exception {
        final String delimitedRate = "R|GBPUSD|1.6324";
        
        final TradeTranslator tradeTranslator = context.mock(TradeTranslator.class);
        final MarketEvent marketEvent = context.mock(MarketEvent.class);
        
        final MarketTradeEventHandler h = new MarketTradeEventHandler(tradeTranslator);
        context.checking(new Expectations(){{
            oneOf(marketEvent).getMessage();
            will(returnValue(delimitedRate));
            
            oneOf(tradeTranslator).canHandle(delimitedRate);
            will(returnValue(false));
        }});
        
        h.onEvent(marketEvent, 1, true);
    }
}
