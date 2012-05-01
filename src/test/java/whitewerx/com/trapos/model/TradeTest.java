package whitewerx.com.trapos.model;

import static whitewerx.com.trapos.util.CurrencyProvider.USD;
import static whitewerx.com.trapos.util.CurrencyProvider.EUR;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMock.class)
public class TradeTest {
    
    private Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    @Test
    public void shouldCalculateTheQuoteAmount() {
        final Rate rateEURUSD = context.mock(Rate.class);
        
        final Amount quoteAmount = new Amount(2624800, USD);
        final Amount tradeAmount = new Amount(1000000, EUR);
        Trade trade = new Trade(TradeType.BUY, tradeAmount, rateEURUSD);
        
        context.checking(new Expectations(){{
            oneOf(rateEURUSD).convert(tradeAmount);
            will(returnValue(quoteAmount));
        }});
        
        trade.getQuoteAmount();
    }
}
