package whitewerx.com.trapos.disruptor;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;

/**
 * Used to by the publisher to allow for mocking of an underlying {@link RingBuffer}.
 * 
 * @author ewhite
 */
public class RingBufferAdapter<T> {
    private RingBuffer<T> delegate;
    
    public RingBufferAdapter(RingBuffer<T> delegate) {
        this.delegate = delegate;
    }
    
    /**
     * Claim the next event in sequence for publishing.
     *
     * @return the claimed sequence value
     */
    public long next() {
        return delegate.next();
    }
    
    /**
     * Get the event for a given sequence in the RingBuffer.
     *
     * @param sequence for the event
     * @return event for the sequence
     */
    public T get(final long sequence) {
        return delegate.get(sequence);
    }
    
    /**
     * Publish an event and make it visible to {@link EventProcessor}s
     *
     * @param sequence to be published
     */
    public void publish(final long sequence) {
        delegate.publish(sequence);
    }
}
