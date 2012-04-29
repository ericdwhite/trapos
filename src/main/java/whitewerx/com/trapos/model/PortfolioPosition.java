package whitewerx.com.trapos.model;

import java.util.HashMap;
import java.util.Map;

public class PortfolioPosition {
    private Map<CurrencyPair, Position> positions = new HashMap<CurrencyPair, Position>();
    
    private PositionFactory positionFactory;
    
    public PortfolioPosition(PositionFactory positionFactory) {
        this.positionFactory = positionFactory;
    }
    
    public PortfolioPosition() {
        this(new PositionFactory());
    }

    public void add(Trade trade) {
        Position position = findPositionFor(trade.getCurrencyPair());
        position.add(trade);
    }

    /**
     * Finds the current open position or creates a flat one
     * if none exists.
     * 
     * @param currencyPair for the desired position.
     * @return the position.
     */
    private Position findPositionFor(CurrencyPair currencyPair) {
        Position p = positions.get(currencyPair);
        if( p!=null )
            return p;
        
        p = positionFactory.createFlatPositionFor(currencyPair);
        positions.put(currencyPair, p);
        return p;
    }
}
