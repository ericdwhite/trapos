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

    /**
     * Copy constructor.
     */
    public Amount(Amount toCopy) {
        this.raw = toCopy.raw;
        this.currency = toCopy.currency;
    }
    
    public boolean currencyMatches(Currency other) {
        return this.currency.equals(other);
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

    public Amount add(Amount addend) {
        if (!this.currency.equals(addend.currency))
            throw new IllegalArgumentException("Currency mismatch.  Trying to add " + this + " to " + addend);

        return new Amount(this.raw + addend.raw, this.currency);
    }

    public Amount subtract(Amount subtrahend) {
        if (!this.currency.equals(subtrahend.currency))
            throw new IllegalArgumentException("Currency mismatch.  Trying to subtract " + this + " with " + subtrahend);

        return new Amount(this.raw - subtrahend.raw, this.currency);
    }
    
    /**
     * Converts an amount to a quote currency at the specified rate.
     * 
     * <pre>
     * this == 5m EUR
     * becomes == 6m USD (with a rate of 1.2)
     * </pre>
     * 
     * @param quoteCurrency of the underlying rate.
     * @param atRate the conversion rate in terms of the base/quote.
     * @return
     */
    public Amount convertToQuote(Currency quoteCurrency, double atRate) {
        return new Amount(raw*atRate, quoteCurrency);
    }
}
