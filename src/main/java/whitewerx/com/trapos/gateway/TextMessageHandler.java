package whitewerx.com.trapos.gateway;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whitewerx.com.trapos.ShutdownListener;

/**
 * The handler determines what to do with each text line received. In all cases
 * but the stop case it passes on the line to the {@link TextMessageSubscriber}.
 * 
 * See: {@link TextMessageSubscriber}
 * See: Special Stop Command SHUTDOWN_COMMAND
 * 
 * @author ewhite
 */
public class TextMessageHandler extends SimpleChannelUpstreamHandler {

    private static final Logger l = LoggerFactory.getLogger(TextMessageHandler.class.getName());

    /** The special shutdown command */
    private static final String SHUTDOWN_COMMAND = "C|STOP";

    private ShutdownListener shutdownListener;
    private TextMessageSubscriber textMessageSubscriber;

    public TextMessageHandler(TextMessageSubscriber textMessageSubscriber, ShutdownListener shutdownListener) {
        this.shutdownListener = shutdownListener;
        this.textMessageSubscriber = textMessageSubscriber;
    }

    /**
     * At this point the message will be lined delimited and these messages can
     * be directly sent to disruptor for processing.
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String delimitedMessage = (String) e.getMessage();
        delimitedMessage = delimitedMessage.trim();
        if ("".equals(delimitedMessage))
            return;

        if(l.isTraceEnabled())
            l.trace(delimitedMessage);
        if(shutdownRequestedIn(delimitedMessage))
            return;
        
        publish(delimitedMessage);
    }

    /**
     * This would never be the case in a real gateway as it provides the ability
     * for the sender to stop the gateway on the data port.
     * 
     * @param delimitedMessage
     */
    private boolean shutdownRequestedIn(String delimitedMessage) {
        if (SHUTDOWN_COMMAND.equals(delimitedMessage)) {
            shutdownListener.notifyShutdown();
            return true;
        }
        return false;
    }

    /**
     * Finally send the message to the subscriber.
     * 
     * @param delimitedMessage
     */
    private void publish(String delimitedMessage) {
        textMessageSubscriber.accept(delimitedMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        l.error("Unexpected exception in the text message gateway.  Closing the channel.", e);
        e.getChannel().close();
        shutdownListener.notifyShutdown();
    }
}
