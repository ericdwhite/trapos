package whitewerx.com.trapos.model;

/**
 * Immutable rate value object.
 * 
 * @author ewhite
 */
public class Rate {

    private double rate;

    private CurrencyPair quotedPair;

    public Rate(double rate, CurrencyPair quotedPair) {
        if (quotedPair == null)
            throw new IllegalArgumentException("You must specify a currency pair. rate:" + rate + " currency pair:"
                    + quotedPair);

        this.rate = rate;
        this.quotedPair = quotedPair;
    }

    @Override
    public String toString() {
        return "Rate [" + quotedPair + "@" + rate + "]";
    }

    /**
     * @return the base currency == CCY1
     */
    public Currency getBaseCurrency() {
        return this.quotedPair.getBase();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((quotedPair == null) ? 0 : quotedPair.hashCode());
        long temp;
        temp = Double.doubleToLongBits(rate);
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
        Rate other = (Rate) obj;
        if (Double.doubleToLongBits(rate) != Double.doubleToLongBits(other.rate))
            return false;

        if (quotedPair == null) {
            if (other.quotedPair != null)
                return false;
        } else if (!quotedPair.equals(other.quotedPair))
            return false;

        return true;
    }
}
