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

    public Trade(TradeType buyOrSell, Amount amount, Rate dealRate) {
        this.buyOrSell = buyOrSell;
        this.amount = amount;
        this.dealRate = dealRate;
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
