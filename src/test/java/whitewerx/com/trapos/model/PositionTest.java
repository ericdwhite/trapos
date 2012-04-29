package whitewerx.com.trapos.model;

import static whitewerx.com.trapos.util.CurrencyPairProvider.EURUSD;
import static whitewerx.com.trapos.util.TradeProvider.ONE_MILLION;
import static whitewerx.com.trapos.util.TradeProvider.buy5mEURUSD;
import static whitewerx.com.trapos.util.TradeProvider.buyEURUSD;
import static whitewerx.com.trapos.util.TradeProvider.sell3mEURUSD;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import whitewerx.com.trapos.model.event.DomainEvents;
import whitewerx.com.trapos.model.event.EventHandler;
import whitewerx.com.trapos.model.event.PositionChangeEvent;

@RunWith(JMock.class)
public class PositionTest {

    private static final Currency EUR = new Currency("EUR");
    private static final Currency USD = new Currency("USD");

    Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Test
    public void shouldAddATradeToAFlatPositionAndSendPositionChangeEvent() {
        Position positionEURUSD = new PositionFactory().createFlatPositionFor(EURUSD);

        Position expectedPosition = create5point1MillionEURUSDPosition();
        final PositionChangeEvent positionChangeEvent = new PositionChangeEvent(expectedPosition);

        @SuppressWarnings("unchecked")
        final EventHandler<PositionChangeEvent> positionChangeHandler = (EventHandler<PositionChangeEvent>) context
                .mock(EventHandler.class);

        DomainEvents.registerFor(PositionChangeEvent.class, positionChangeHandler);

        context.checking(new Expectations() {
            {
                oneOf(positionChangeHandler).handle(positionChangeEvent);
            }
        });

        positionEURUSD.add(buyEURUSD);
    }

    /**
     * This position is the expected position for adding @buyEURUSD to a flat
     * position.
     * 
     * @return
     */
    private Position create5point1MillionEURUSDPosition() {
        Amount ccy1 = new Amount(5100, EUR);
        Amount ccy2 = new Amount(-1 * 5100 * 1.3124, USD);
        Amount ccy1USDEquivalent = new Amount(5100 * 1.3124, USD); // Assumes PNL in USD
        Amount ccy2USDEquivalent = new Amount(ccy2);
        Position expectedPosition = new Position(ccy1, ccy2, ccy1USDEquivalent, ccy2USDEquivalent, EURUSD, USD);
        return expectedPosition;
    }

    @Test
    public void shouldHaveATwoMillionPositionAverageRate() {
        Position positionEURUSD = new PositionFactory().createFlatPositionFor(EURUSD);

        Position expectedPosition = create2MillionEURUSDPosition();
        final PositionChangeEvent positionChangeEvent = new PositionChangeEvent(expectedPosition);

        @SuppressWarnings("unchecked")
        final EventHandler<PositionChangeEvent> positionChangeHandler = (EventHandler<PositionChangeEvent>) context
                .mock(EventHandler.class);

        DomainEvents.registerFor(PositionChangeEvent.class, positionChangeHandler);

        context.checking(new Expectations() {
            {
                oneOf(positionChangeHandler).handle(with(any(PositionChangeEvent.class)));
                oneOf(positionChangeHandler).handle(positionChangeEvent);
            }
        });

        positionEURUSD.add(buy5mEURUSD);
        positionEURUSD.add(sell3mEURUSD);
    }

    /**
     * This position is the expected position for the following trades:
     * 
     * <pre>
     *   buy5mEURUSD
     *   sell3mEURUSD
     * </pre>
     * 
     * @return
     */
    private Position create2MillionEURUSDPosition() {
        Amount ccy1 = new Amount((5 * ONE_MILLION) - (3 * ONE_MILLION), EUR);
        Amount ccy2 = new Amount((-5 * ONE_MILLION * 1.3150) + (3.0 * ONE_MILLION * 1.3160), USD);
        Amount ccy1USDEquivalent = new Amount((5 * ONE_MILLION * 1.3150) + (-3.0 * ONE_MILLION * 1.3160), USD);
        Amount ccy2USDEquivalent = new Amount(ccy2);
        Position expectedPosition = new Position(ccy1, ccy2, ccy1USDEquivalent, ccy2USDEquivalent, EURUSD, USD);
        return expectedPosition;
    }

    @After
    public void clearRegistrations() {
        DomainEvents.unregisterFor(PositionChangeEvent.class);
    }
}
