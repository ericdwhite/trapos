package whitewerx.com.trapos.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whitewerx.com.trapos.gateway.TextMessageSubscriber;

/** 
 * Publishes events from the gateway to the ring buffer.
 * 
 * This is done through an adapter to support testing via JMock
 * 
 * @author ewhite
 */
public class MarketEventPublisher implements TextMessageSubscriber {
    private static final Logger l = LoggerFactory.getLogger(MarketEventPublisher.class.getName());
    
    private RingBufferAdapter<MarketEvent> ringBuffer;
    
    public MarketEventPublisher(RingBufferAdapter<MarketEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void accept(String delimitedMessage) {
        long sequence = ringBuffer.next();
        MarketEvent event = ringBuffer.get(sequence);
        event.setMessage(delimitedMessage);
        
        if(l.isInfoEnabled())
            l.info("publishEvent: seq:"+sequence+" event:"+ event);
        
        ringBuffer.publish(sequence);
    }
}
