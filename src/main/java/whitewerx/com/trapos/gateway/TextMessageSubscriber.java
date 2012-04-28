package whitewerx.com.trapos.gateway;

/**
 * The implementor will receive the text message on a Netty
 * thread as they arrive.
 * 
 * @author ewhite
 */
public interface TextMessageSubscriber {
    /**
     * @param delimitedMessage
     */
    public void accept(String delimitedMessage);
}
