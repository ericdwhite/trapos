package whitewerx.com.trapos.model;

import static whitewerx.com.trapos.util.CurrencyPairProvider.EURUSD;
import static whitewerx.com.trapos.util.CurrencyProvider.EUR;
import static whitewerx.com.trapos.util.CurrencyProvider.USD;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RateTest {

    /**
     * Foreign to USD, e.g. EURUSD.
     */
    @Test
    public void shouldConverntToQuoteAmountWhenTheRateIsForeign() {

        final Amount baseAmount = new Amount(2000000, EUR);
        Rate rateEURUSD = new Rate(1.3124, EURUSD);

        final Amount expectedQuoteAmount = new Amount(2624800, USD);
        assertThat(rateEURUSD.convert(baseAmount), equalTo(expectedQuoteAmount));
    }
    
}
