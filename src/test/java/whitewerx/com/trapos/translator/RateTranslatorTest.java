package whitewerx.com.trapos.translator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import whitewerx.com.trapos.model.Rate;
import whitewerx.com.trapos.translators.RateTranslator;
import whitewerx.com.trapos.translators.TranslateException;

import static whitewerx.com.trapos.util.CurrencyPairProvider.EURUSD;

@RunWith(JMock.class)
public class RateTranslatorTest {

    Mockery context = new Mockery();
    
    @Test
    public void translatesValidEURUSDRate() throws Exception {
        String delimitedRate = "R|EURUSD|1.3124";
        Rate expected = new Rate(1.3124, EURUSD); 
                
        RateTranslator rateTranslator = new RateTranslator();
        Rate rate = rateTranslator.translate(delimitedRate);
        assertThat(rate, equalTo(expected));
    }
    
    @Test(expected=TranslateException.class)
    public void shouldNotTranslateInvalidEURUSDRate() throws Exception {
        String invalidRate = "1.3123|EURUSD";
                
        RateTranslator rateTranslator = new RateTranslator();
        rateTranslator.translate(invalidRate);
    }

    @Test
    public void shouldTransalateRateMessages(){
        String delimitedRate = "R";
        
        RateTranslator rateTranslator = new RateTranslator();
        assertThat(rateTranslator.canHandle(delimitedRate), equalTo(true));
    }
    
    @Test
    public void shouldNotTranslateAnyOtherMessageTypes(){
        String otherMessageType = "C";
        
        RateTranslator rateTranslator = new RateTranslator();
        assertThat(rateTranslator.canHandle(otherMessageType), equalTo(false));
    }
}
