package whitewerx.com.trapos.gateway;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whitewerx.com.trapos.ShutdownListener;

/**
 * This is a text gateway that accepts messages line by line.
 * 
 * It is implemented using Netty.
 * 
 * @author ewhite
 */
public class TextMessageGateway implements Runnable, ShutdownListener {
    private static final Logger l = LoggerFactory.getLogger(TextMessageGateway.class.getName());

    private static final int TCPIP_PORT = 7000;

    private static final String TCPIP_INTERFACE = "0.0.0.0";

    /**
     * This must match the producing claim strategy.
     */
    public static int PUBLISHING_THREADS = 1;

    /**
     * The maximum line length of any text message, this is used in the framing
     * of the messages as they are received over the TCP/IP socket.
     */
    private static final int MAX_LINE_LENGTH = 500;

    /** This receiver interested in the text messages (e.g. Disruptor Producer). */
    private TextMessageSubscriber textMessageSubscriber;

    private ShutdownListener shutdownListener;
    private CountDownLatch shutdown = new CountDownLatch(1);

    private final ChannelGroup allChannels = new DefaultChannelGroup("tm-gateway");
    private ChannelFactory factory;

    public TextMessageGateway(TextMessageSubscriber textMessageSubscriber, ShutdownListener shutdownListener) {
        this.shutdownListener = shutdownListener;
        this.textMessageSubscriber = textMessageSubscriber;
    }

    /**
     * Starts the Netty server up and waits for a shutdown message to come
     * through the gateway.
     */
    public void run() {
        ServerBootstrap bootstrap = createServer();

        configureTextMessageProcessingPipeline(bootstrap);
        configureTCPIPSettings(bootstrap);
        startServer(bootstrap);

        waitForShutdown();
    }

    /**
     * Creates the server with it's thread pools.
     */
    private ServerBootstrap createServer() {
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService workers = Executors.newFixedThreadPool(PUBLISHING_THREADS);

        factory = new NioServerSocketChannelFactory(boss, workers, PUBLISHING_THREADS);
        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        return bootstrap;
    }

    /**
     * Starts the server listening on the TCP/IP socket.
     * @param bootstrap
     */
    private void startServer(ServerBootstrap bootstrap) {
        Channel gateway = bootstrap.bind(new InetSocketAddress(TCPIP_INTERFACE, TCPIP_PORT));
        allChannels.add(gateway);
        l.info("Started the gateway. "+TCPIP_INTERFACE+":"+TCPIP_PORT);
    }

    public void notifyShutdown() {
        shutdown.countDown();
    }

    private void configureTCPIPSettings(ServerBootstrap bootstrap) {
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
    }

    private void waitForShutdown() {
        try {
            shutdown.await();
        } catch (InterruptedException e) {
            l.info("Gateway interupted waiting for shutdown.", e);
        }
        handleShutdown();
    }

    /**
     * Sets up a pipline that delimits lines based on CRLF/LF and a line length
     * no greater than MAX_LINE_LENGTH.
     * 
     * @param bootstrap
     * @param gatewayShutdownListener
     */
    private void configureTextMessageProcessingPipeline(ServerBootstrap bootstrap) {
        final ShutdownListener gatewayShutdownListener = this;
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast("Framer", new DelimiterBasedFrameDecoder(MAX_LINE_LENGTH, Delimiters.lineDelimiter()));
                pipeline.addLast("Decoder", new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("Gateway", new TextMessageHandler(textMessageSubscriber, gatewayShutdownListener));

                return pipeline;
            }
        });
    }

    /**
     * Properly stops the gateway releasing resources.
     */
    private void handleShutdown() {
        try {
            l.info("Stopping the gateway.");
            ChannelGroupFuture shutdown = allChannels.close();
            shutdown.awaitUninterruptibly();
            factory.releaseExternalResources();
            l.info("Stopped the gateway.");
        } finally {
            shutdownListener.notifyShutdown();
        }
    }
}
