package whitewerx.com.trapos.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whitewerx.com.trapos.model.PortfolioPosition;
import whitewerx.com.trapos.model.Trade;
import whitewerx.com.trapos.model.event.DomainEvents;
import whitewerx.com.trapos.model.event.PositionChangeEvent;

import com.lmax.disruptor.EventHandler;

/**
 * This event processor holds a position in memory and receives position updates
 * via {@link DomainEvents}.
 * 
 * @author ewhite
 */
public class PortfolioPositionEventHandler implements EventHandler<MarketEvent>,
        whitewerx.com.trapos.model.event.EventHandler<PositionChangeEvent> {
    private static Logger l = LoggerFactory.getLogger(PortfolioPositionEventHandler.class.getName());

    /**
     * Cached portfolio position.
     */
    private PortfolioPosition portfolioPosition;

    private long currentSequence;

    public PortfolioPositionEventHandler(PortfolioPosition position) {
        this.portfolioPosition = position;
    }

    public PortfolioPositionEventHandler() {
        this(new PortfolioPosition());
    }

    public void onEvent(MarketEvent marketEvent, long sequence, boolean endOfBatch) throws Exception {
        if (l.isInfoEnabled())
            l.info("onEvent: seq:" + sequence + "/" + endOfBatch + " event: " + marketEvent);

        currentSequence = sequence;
        try {
            DomainEvents.registerFor(PositionChangeEvent.class, this);

            if (!marketEvent.isTradeEvent())
                return;

            Trade t = marketEvent.getTrade();
            portfolioPosition.add(t);

        } finally {
            DomainEvents.unregisterFor(PositionChangeEvent.class);
        }
    }

    /**
     * Log the positions as they are changed, in the real world this might
     * notify something else.
     */
    public void handle(PositionChangeEvent event) {
        l.info("Position change. seq:" + currentSequence + " pos:" + event.getPosition());
    }
}
