package whitewerx.com.trapos;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.MultiThreadedClaimStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;

import whitewerx.com.trapos.disruptor.MarketEvent;
import whitewerx.com.trapos.disruptor.MarketEventPublisher;
import whitewerx.com.trapos.disruptor.MarketRateEventHandler;
import whitewerx.com.trapos.disruptor.MarketTradeEventHandler;
import whitewerx.com.trapos.disruptor.RingBufferAdapter;
import whitewerx.com.trapos.gateway.TextMessageGateway;

/**
 * Starts the gateway and configures the disruptor to handle messages.
 * 
 * Message can be sent to the gateway using Netcat
 * 
 * <pre>
 * cat SAMPLE-DATA.txt | nc localhost 7000
 * echo 'C|STOP' | nc 127.0.0.1 7000
 * </pre>
 * See: README.md for more details about how to start and interact with
 *      with the server.
 */
public class App implements ShutdownListener {
    private static final Logger l = LoggerFactory.getLogger(TextMessageGateway.class.getName());
    
    /** This is the number of event processors + 1 thread for the gateway */
    private static final int THREAD_POOL_SIZE = 3;

    private static final int RINGBUFFER_SIZE = 2^6;
    
    private ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    Future<?>[] tasks = new Future<?>[THREAD_POOL_SIZE]; 
    private EventProcessor[] eventProcessors = new EventProcessor[THREAD_POOL_SIZE-1];

    private CountDownLatch shutdown = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    private void run(String[] args) throws InterruptedException {
        
        // This is to keep my MBA from catching on fire...
        WaitStrategy waitStrategy = new BlockingWaitStrategy();
        
        RingBuffer<MarketEvent> ringBuffer = new RingBuffer<MarketEvent>(MarketEvent.FACTORY,
                new MultiThreadedClaimStrategy(RINGBUFFER_SIZE), waitStrategy); 

        // Initial barrier
        SequenceBarrier translationBarrier = ringBuffer.newBarrier();
        
        MarketTradeEventHandler tradeHandler = new MarketTradeEventHandler();
        EventProcessor tradeProcessor = new BatchEventProcessor<MarketEvent>(ringBuffer, translationBarrier, tradeHandler);
        eventProcessors[0] = tradeProcessor;
        
        MarketRateEventHandler rateHandler = new MarketRateEventHandler();
        EventProcessor rateProcessor = new BatchEventProcessor<MarketEvent>(ringBuffer, translationBarrier, rateHandler);
        eventProcessors[1] = rateProcessor;
        
        MarketEventPublisher eventPublisher = new MarketEventPublisher(new RingBufferAdapter<MarketEvent>(ringBuffer));
        TextMessageGateway gateway = new TextMessageGateway(eventPublisher, this);
        
        // The producer can't move past this barrier.
        ringBuffer.setGatingSequences(tradeProcessor.getSequence(), rateProcessor.getSequence());

        // Start the threads
        tasks[0] = threadPool.submit(gateway);
        tasks[1] = threadPool.submit(tradeProcessor);
        tasks[2] = threadPool.submit(rateProcessor);

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
