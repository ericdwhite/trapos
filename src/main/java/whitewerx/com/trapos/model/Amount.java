package whitewerx.com.trapos.model;

/**
 * Immutable amount value object.
 * 
 * @author ewhite
 */
public class Amount {
    /** The raw immutable amount */
    private double raw;

    private Currency currency;

    public Amount(double amount, Currency currency) {
        if (currency == null)
            throw new IllegalArgumentException("You must specify a currency.");

        this.raw = amount;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Amount [" + raw + " " + currency + "]";
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        long temp;
        temp = Double.doubleToLongBits(raw);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Amount other = (Amount) obj;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (Double.doubleToLongBits(raw) != Double.doubleToLongBits(other.raw))
            return false;
        return true;
    }
}
