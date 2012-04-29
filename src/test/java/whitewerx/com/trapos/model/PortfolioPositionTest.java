package whitewerx.com.trapos.model;

import static whitewerx.com.trapos.util.CurrencyPairProvider.EURUSD;
import static whitewerx.com.trapos.util.TradeProvider.buyEURUSD;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMock.class)
public class PortfolioPositionTest {
    
    Mockery context = new Mockery() {{
       setImposteriser(ClassImposteriser.INSTANCE); 
    }};
    
    @Test
    public void shouldAddATradeToAFlatPosition() {

        final Position flatPosition = context.mock(Position.class);
        final PositionFactory positionFactory = context.mock(PositionFactory.class);
        
        context.checking(new Expectations(){{
            oneOf(positionFactory).createFlatPositionFor(EURUSD);
            will(returnValue(flatPosition));
            
            oneOf(flatPosition).add(buyEURUSD);
        }});
        
        PortfolioPosition portfolioPosition = new PortfolioPosition(positionFactory);
        portfolioPosition.add(buyEURUSD);
    }
    
    @Test
    public void shouldAddATradeToAnExistingPositions() {

        final Position positionEURUSD = context.mock(Position.class);
        final PositionFactory positionFactory = context.mock(PositionFactory.class);
        
        context.checking(new Expectations(){{
            oneOf(positionFactory).createFlatPositionFor(EURUSD);
            will(returnValue(positionEURUSD));
            
            exactly(2).of(positionEURUSD).add(buyEURUSD);
        }});
        
        PortfolioPosition portfolioPosition = new PortfolioPosition(positionFactory);
        portfolioPosition.add(buyEURUSD);
        portfolioPosition.add(buyEURUSD);
    }    
}
