package whitewerx.com.trapos;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.MultiThreadedClaimStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;

import whitewerx.com.trapos.disruptor.MarketEvent;
import whitewerx.com.trapos.disruptor.MarketEventPublisher;
import whitewerx.com.trapos.disruptor.MarketTradeEventHandler;
import whitewerx.com.trapos.disruptor.RingBufferAdapter;
import whitewerx.com.trapos.gateway.TextMessageGateway;

/**
 * Starts the gateway and configures the disruptor to handle messages.
 * 
 * Message can be sent to the gateway using NetCat
 * 
 * <pre>
 * cat test-data | nc localhost 7000
 * </pre>
 */
public class App implements ShutdownListener {
    private static final Logger l = LoggerFactory.getLogger(TextMessageGateway.class.getName());
    private CountDownLatch shutdown = new CountDownLatch(1);
    
    /** This is the number of event processors + 1 thread for the gateway */
    private static final int THREAD_POOL_SIZE = 2;
    
    private ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    Future<?>[] tasks = new Future<?>[THREAD_POOL_SIZE]; 
    private EventProcessor[] eventProcessors = new EventProcessor[THREAD_POOL_SIZE-1];

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    private void run(String[] args) throws InterruptedException {
        
        RingBuffer<MarketEvent> ringBuffer = new RingBuffer<MarketEvent>(MarketEvent.FACTORY, new MultiThreadedClaimStrategy(2^6), new YieldingWaitStrategy()); 

        // Initial barrier
        SequenceBarrier translationBarrier = ringBuffer.newBarrier();
        
        MarketTradeEventHandler tradeHandler = new MarketTradeEventHandler();
        EventProcessor tradeProcessor = new BatchEventProcessor<MarketEvent>(ringBuffer, translationBarrier, tradeHandler);
        eventProcessors[0] = tradeProcessor;
        
        MarketEventPublisher eventPublisher = new MarketEventPublisher(new RingBufferAdapter<MarketEvent>(ringBuffer));
        TextMessageGateway gateway = new TextMessageGateway(eventPublisher, this);
        
        // The producer can't move past this barrier.
        ringBuffer.setGatingSequences(tradeProcessor.getSequence());

        tasks[0] = threadPool.submit(tradeProcessor);
        tasks[1] = threadPool.submit(gateway);

        shutdown.await();
        l.info("Shutting down the app.");
    }

    public void notifyShutdown() {
        shutdownDisruptor();
        shutdownThreadPool();

        // This is the final step.
        shutdown.countDown();
    }

    private void shutdownDisruptor() {
        for(EventProcessor p : eventProcessors) {
            p.halt();
        }
        for(Future<?> task : tasks) {
            task.cancel(true);
        }
    }

    private void shutdownThreadPool() {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore as we are shutting down anyway.
        }
    }
}
