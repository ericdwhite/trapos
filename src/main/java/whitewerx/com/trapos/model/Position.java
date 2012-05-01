package whitewerx.com.trapos.model;

import whitewerx.com.trapos.model.event.DomainEvents;
import whitewerx.com.trapos.model.event.PositionChangeEvent;

/**
 * This is an aggregated position due to trading.
 * 
 * TODO: handle PNL changes due to rates.
 * 
 * In real system the handling of PNL would probably be elsewhere as it can be
 * complicated.
 * 
 * The two equivalent amounts can be used to calculate a book rate.
 * 
 * @author ewhite
 */
public class Position {
    
    private Amount ccy1Amount;
    private Amount ccy2Amount;
    
    /** Weighted average ccy1 amount in PNL currency */
    private Amount ccy1EquivalentInPNLCurrency;
    
    /** Weighted average ccy2 amount in PNL currency */
    private Amount ccy2EquivalentInPNLCurrency;
    
    private CurrencyPair currencyPair;
    private Currency pnlCurrency;
    
    /**
     * This constructor should be used for testing only.  Use {@PositionFactory} to
     * create positions.
     * 
     * @param ccy1Amount in ccy1 currency
     * @param ccy2Amount in ccy2 currency
     * @param ccy1EquivalentInPNLCurrency
     * @param ccy2EquivalentInPNLCurrency
     * @param currencyPair
     * @param pnlCurrency typically USD
     */
    protected Position(Amount ccy1Amount, Amount ccy2Amount, Amount ccy1EquivalentInPNLCurrency, Amount ccy2EquivalentInPNLCurrency,
            CurrencyPair currencyPair, Currency pnlCurrency) {
        this.ccy1Amount = ccy1Amount;
        this.ccy2Amount = ccy2Amount;
        this.ccy1EquivalentInPNLCurrency = ccy1EquivalentInPNLCurrency;
        this.ccy2EquivalentInPNLCurrency = ccy2EquivalentInPNLCurrency;
        this.currencyPair = currencyPair;
        this.pnlCurrency = pnlCurrency;
    }

    /**
     * Adds the trade to the position.
     * 
     * Note: This is incomplete as it assumes the rate
     *       is quoted in terms of USD.  E.g. EURUSD, GBPUSD, etc.
     *       
     * @param trade
     */
    public void add(Trade trade) {
        Amount ccy2AmountDelta = trade.getQuoteAmount();

        if( trade.isPurchase() ) {

            ccy1Amount = ccy1Amount.add(trade.getBaseAmount());
            ccy2Amount = ccy2Amount.subtract(ccy2AmountDelta);
            
            ccy1EquivalentInPNLCurrency = ccy1EquivalentInPNLCurrency.add(ccy2AmountDelta);
            ccy2EquivalentInPNLCurrency = ccy2Amount;
        } else {
            ccy1Amount = ccy1Amount.subtract(trade.getBaseAmount());
            ccy2Amount = ccy2Amount.add(ccy2AmountDelta);

            ccy1EquivalentInPNLCurrency = ccy1EquivalentInPNLCurrency.subtract(ccy2AmountDelta);
            ccy2EquivalentInPNLCurrency = ccy2Amount;
        }

        notifyChange();
    }

    private void notifyChange() {
        DomainEvents.raise(new PositionChangeEvent(this));
    }

    @Override
    public String toString() {
        return "Position [ccy1Amount=" + ccy1Amount + ", ccy2Amount=" + ccy2Amount + ", ccy1EquivalentInPNLCurrency="
                + ccy1EquivalentInPNLCurrency + ", ccy2EquivalentInPNLCurrency=" + ccy2EquivalentInPNLCurrency
                + ", currencyPair=" + currencyPair + ", pnlCurrency=" + pnlCurrency + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ccy1Amount == null) ? 0 : ccy1Amount.hashCode());
        result = prime * result + ((ccy1EquivalentInPNLCurrency == null) ? 0 : ccy1EquivalentInPNLCurrency.hashCode());
        result = prime * result + ((ccy2Amount == null) ? 0 : ccy2Amount.hashCode());
        result = prime * result + ((ccy2EquivalentInPNLCurrency == null) ? 0 : ccy2EquivalentInPNLCurrency.hashCode());
        result = prime * result + ((currencyPair == null) ? 0 : currencyPair.hashCode());
        result = prime * result + ((pnlCurrency == null) ? 0 : pnlCurrency.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;
        if (ccy1Amount == null) {
            if (other.ccy1Amount != null)
                return false;
        } else if (!ccy1Amount.equals(other.ccy1Amount))
            return false;
        if (ccy1EquivalentInPNLCurrency == null) {
            if (other.ccy1EquivalentInPNLCurrency != null)
                return false;
        } else if (!ccy1EquivalentInPNLCurrency.equals(other.ccy1EquivalentInPNLCurrency))
            return false;
        if (ccy2Amount == null) {
            if (other.ccy2Amount != null)
                return false;
        } else if (!ccy2Amount.equals(other.ccy2Amount))
            return false;
        if (ccy2EquivalentInPNLCurrency == null) {
            if (other.ccy2EquivalentInPNLCurrency != null)
                return false;
        } else if (!ccy2EquivalentInPNLCurrency.equals(other.ccy2EquivalentInPNLCurrency))
            return false;
        if (currencyPair == null) {
            if (other.currencyPair != null)
                return false;
        } else if (!currencyPair.equals(other.currencyPair))
            return false;
        if (pnlCurrency == null) {
            if (other.pnlCurrency != null)
                return false;
        } else if (!pnlCurrency.equals(other.pnlCurrency))
            return false;
        return true;
    }
}
