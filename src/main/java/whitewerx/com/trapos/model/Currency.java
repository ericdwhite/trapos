package whitewerx.com.trapos.model;

/**
 * A currency defined by an ISO code.
 * 
 * e.g. EUR, USD, GBP, etc.
 * 
 * http://en.wikipedia.org/wiki/ISO_4217
 * 
 * Note: this would probably be expanded to include rounding, significant digits, etc.
 * 
 * @author ewhite
 */
public class Currency {

    private String isoCode;

    public Currency(String isoCode) {
        if (isoCode == null)
            throw new IllegalArgumentException("You must specify an ISO code");
        this.isoCode = isoCode;
    }

    @Override
    public String toString() {
        return isoCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isoCode == null) ? 0 : isoCode.hashCode());
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
        Currency other = (Currency) obj;
        if (isoCode == null) {
            if (other.isoCode != null)
                return false;
        } else if (!isoCode.equals(other.isoCode))
            return false;
        return true;
    }
}
