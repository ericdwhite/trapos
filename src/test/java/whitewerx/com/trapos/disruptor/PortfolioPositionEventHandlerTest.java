package whitewerx.com.trapos.disruptor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import whitewerx.com.trapos.model.PortfolioPosition;
import whitewerx.com.trapos.model.Trade;

@RunWith(JMock.class)
public class PortfolioPositionEventHandlerTest {

    Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    @Test
    public void shouldUpdateThePositionForATradeEvent() throws Exception {
        
        final PortfolioPosition portfolioPosition = context.mock(PortfolioPosition.class);
        final MarketEvent tradeEvent = context.mock(MarketEvent.class);
        final Trade trade = context.mock(Trade.class);
        
        context.checking(new Expectations(){{
            oneOf(tradeEvent).isTradeEvent();
            will(returnValue(true));
            
            oneOf(tradeEvent).getTrade();
            will(returnValue(trade));
            
            oneOf(portfolioPosition).add(trade);
        }});
        
        PortfolioPositionEventHandler h = new PortfolioPositionEventHandler(portfolioPosition);
        h.onEvent(tradeEvent, 1, true);
    }
}
