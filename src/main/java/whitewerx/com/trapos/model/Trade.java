package whitewerx.com.trapos.model;

/**
 * Represents a booked trade
 * 
 * @author ewhite
 * 
 */
public class Trade {

    private TradeType buyOrSell;

    private Amount amount;

    private Rate dealRate;

    /**
     * FX deal for either a BUY or a SELL of a currency amount
     * at the specified rate.
     * <pre>
     *   e.g.
     *   Buy 5m EUR at Rate 1.3123 EURUSD.
     * </pre>
     * @param buyOrSell
     * @param amount
     * @param dealRate
     */
    public Trade(TradeType buyOrSell, Amount amount, Rate dealRate) {
        this.buyOrSell = buyOrSell;
        this.amount = amount;
        this.dealRate = dealRate;
    }
    
    public Amount getBaseAmount() {
        return amount;
    }

    public Amount getQuoteAmount() {
        return dealRate.convert(amount);
    }

    public CurrencyPair getCurrencyPair() {
        return this.dealRate.getCurrencyPair();
    }

    public boolean isPurchase() {
        return buyOrSell==TradeType.BUY;
    }
    
    @Override
    public String toString() {
        return "Trade [" + buyOrSell + " " + amount + " at " + dealRate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((buyOrSell == null) ? 0 : buyOrSell.hashCode());
        result = prime * result + ((dealRate == null) ? 0 : dealRate.hashCode());
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
        Trade other = (Trade) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (buyOrSell != other.buyOrSell)
            return false;
        if (dealRate == null) {
            if (other.dealRate != null)
                return false;
        } else if (!dealRate.equals(other.dealRate))
            return false;
        return true;
    }
}
