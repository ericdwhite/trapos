package whitewerx.com.trapos.model;

/**
 * Represents a currency pair in the market convention.
 * 
 * <pre>
 * EUR/USD
 * GPB/USD
 * USD/CAD
 * USD/JPY
 * </pre>
 * 
 * http://en.wikipedia.org/wiki/Currency_pair
 * 
 * @author ewhite
 */
public class CurrencyPair {

    /** CCY1 in the currency pair e.g. EUR in EURUSD */
    private Currency base;

    /** CCY2 in the currency pair e.g. USD in EURUSD */
    private Currency quote;

    /**
     * Direct quotation of the currency pair.
     * 
     * @param base
     *            Currency 1
     * @param quote
     *            Currency 2
     */
    public CurrencyPair(Currency base /* ccy1 */, Currency quote /* ccy2 */) {
        if (quote == null || base == null)
            throw new IllegalArgumentException(
                    "A non null quote and base currency must be specified. base:" + base
                            + " quote:" + quote);
        this.quote = quote;
        this.base = base;
    }
    
    /**
     * @return the base currency == CCY1
     */
    public Currency getBase() {
        return this.base;
    }

    /**
     * @return the quote currency == CCY2
     */
    public Currency getQuote() {
        return this.quote;
    }
    
    @Override
    public String toString() {
        return base + "/" + quote;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((base == null) ? 0 : base.hashCode());
        result = prime * result + ((quote == null) ? 0 : quote.hashCode());
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
        CurrencyPair other = (CurrencyPair) obj;
        if (base == null) {
            if (other.base != null)
                return false;
        } else if (!base.equals(other.base))
            return false;
        if (quote == null) {
            if (other.quote != null)
                return false;
        } else if (!quote.equals(other.quote))
            return false;
        return true;
    }
}
