package whitewerx.com.trapos.model;

/**
 * A Rate specified in terms of the quote currency.
 * 
 * So a EURUSD rate of 1.3123 means that 1 EUR buys 1.3123 USD. Where EUR is the
 * base currency, and USD is the quote currency.
 * 
 * http://en.wikipedia.org/wiki/Currency_pair
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

    /**
     * @return the quote currency == CCY2
     */
    private Currency getQuoteCurrency() {
        return this.quotedPair.getQuote();
    }

    public CurrencyPair getCurrencyPair() {
        return this.quotedPair;
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

    /**
     * Convert the CCY1 amount to a CCY2 amount using the rate.
     * 
     * This assumes that everything is foreign in terms of USD e.g. EURUSD,
     * GBPUSD, etc.
     * 
     * @param baseAmount to convert
     * @return
     */
    public Amount convert(Amount baseAmount) {
        if (!baseAmount.currencyMatches(getBaseCurrency()))
            throw new IllegalArgumentException(
                    "The base amount currency does not match the rate base currency. Amount: " + baseAmount
                            + " Rate Base Currency: " + getBaseCurrency());
        
        double atRate = this.rate;
        return baseAmount.convertToQuote(getQuoteCurrency(), atRate);
    }
}
