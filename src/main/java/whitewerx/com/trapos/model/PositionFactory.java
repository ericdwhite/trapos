package whitewerx.com.trapos.model;

/**
 * Creates positions so that they can be easily tested.
 * 
 * @author ewhite
 */
public class PositionFactory {

    /**
     * Note this assumes the PNL currency is always the quote currency in a real
     * system this would not be correct.
     * 
     * For example if the PNL currency is USD, and the position is USDCAD then
     * this would be incorrect.
     * 
     * @param currencyPair
     *            for the flat position.
     * @return
     */
    public Position createFlatPositionFor(CurrencyPair currencyPair) {
        Amount ccy1Amount = new Amount(0, currencyPair.getBase());
        Amount ccy2Amount = new Amount(0, currencyPair.getQuote());
        Amount ccy1EqvAmount = ccy2Amount;
        Amount ccy2EqvAmount = ccy2Amount;

        return new Position(ccy1Amount, ccy2Amount, ccy1EqvAmount, ccy2EqvAmount, currencyPair, currencyPair.getQuote());
    }

}
