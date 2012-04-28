package whitewerx.com.trapos.translators;

/**
 * Thrown when a translation fails, the caller should provide
 * a context string (e.g. trade parsing, rate parsing) and the 
 * delimited string trying to be parsed.
 * 
 * @author ewhite
 */
public class TranslateException extends Exception {

    private static final long serialVersionUID = 1L;

    public TranslateException(String context, String delimited) {
        super(context + " Delimited String: " + delimited);
    }
}
