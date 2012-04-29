Overview
========

This is a sample application which shows one way to use the disruptor to keep 
an in memory position based on trade and rate events.  I have tried to keep the
domain as separated as possible from the technical infrastructure (Netty, Disruptor).

This was all built over a weekend, so comments and feedback is welcome.  It is
unlikely I got everything right (the Domain, Disruptor, Netty, etc.).

There is one HUGE caveat with this code, it doesn't persist anything so if the original
event stream is lost, everything is LOST.  The intent of the example was to understand
the disruptor sequencing and not create a persistent application.

The second is it only handles currency pairs quoted against dollar. Namely EURUSD, GBPUSD, etc.
This is due to how the positions are calculated in the domain.

Trade and rate events are sent into the disruptor via a line based protocol.  The
project contains a Netty server that listens on 0.0.0.0:7000.  The state of the
domain is modified by sending messages to the Netty server.

The server supports the following messages

 * Trade - prefixed with 'T|'
 * Rate - prefixed with 'R|'
 * Stop - prefixed with 'C|'

Trade
-----

The trade messages are formatted as follows, using '|' as a delimiter.

    (T)rade|(B)uy/(S)ell|(Double Amount)(Multiplier)|(Rate)

    For example
    
    T|B|5.1t|R|EURUSD|1.3124 -> Buy 5.1 thousand EUR at @ 1.3124 EURUSD. 

    See: whitewerx.com.trapos.translators.TradeTranslator 

Rate
----

The rate messages are formatted as follows, using '|' as a delimiter.

    (R)rate|(CCY1)(CCY2)|(Double Rate)
    
    For example
    
    R|EURUSD|1.3124 -> a EURUSD rate @ 1.3124

    See: whitewerx.com.trapos.translators.RateTranslator

Stop
----

The server can be shutdown by sending a stop message.  This would not be
done this way in the real world, as it would allow the sender to shutdown 
the server process.

The stop message is formatted as:

    C|STOP
        
Testing with Sample Data
========================

Start the server

    mvn clean package
    mvn exec:java -Dexec.mainClass="whitewerx.com.trapos.App"

To load some message from the command line.

    cat SAMPLE-DATA.txt | nc 127.0.0.1 7000
    
You should then see some console output like:

     173 [pool-1-thread-1] INFO w.c.t.gateway.TextMessageGateway - Started the gateway. 0.0.0.0:7000
    4063 [New I/O sw #1-1] INFO w.c.t.disruptor.MarketEventPublisher - publishEvent: seq:0 event:MarketEvent [delimitedMessage=T|B|5.1t|R|EURUSD|1.3124, trade=null, rate=null]
    4063 [New I/O sw #1-1] INFO w.c.t.disruptor.MarketEventPublisher - publishEvent: seq:1 event:MarketEvent [delimitedMessage=T|S|6.1t|R|EURUSD|1.3125, trade=null, rate=null]
    4063 [New I/O sw #1-1] INFO w.c.t.disruptor.MarketEventPublisher - publishEvent: seq:2 event:MarketEvent [delimitedMessage=R|EURUSD|1.3126, trade=null, rate=null]
    4068 [pool-1-thread-3] INFO w.c.t.MarketRateEventHandler - onEvent: seq:2/true event: MarketEvent [delimitedMessage=R|EURUSD|1.3126, trade=null, rate=Rate [EUR/USD@1.3126]]
    4076 [pool-1-thread-2] INFO w.c.t.disruptor.MarketTradeEventHandler - onEvent: seq:0/true event: MarketEvent [delimitedMessage=T|B|5.1t|R|EURUSD|1.3124, trade=Trade [BUY Amount [5100.0 EUR] at Rate [EUR/USD@1.3124]], rate=null]
    4076 [pool-1-thread-4] INFO w.c.t.disruptor.PortfolioPositionEventHandler - onEvent: seq:0/true event: MarketEvent [delimitedMessage=T|B|5.1t|R|EURUSD|1.3124, trade=Trade [BUY Amount [5100.0 EUR] at Rate [EUR/USD@1.3124]], rate=null]
    4076 [pool-1-thread-2] INFO w.c.t.disruptor.MarketTradeEventHandler - onEvent: seq:1/false event: MarketEvent [delimitedMessage=T|S|6.1t|R|EURUSD|1.3125, trade=Trade [SELL Amount [6100.0 EUR] at Rate [EUR/USD@1.3125]], rate=null]

With aggregated position updates

    4080 [pool-1-thread-4] INFO w.c.t.disruptor.PortfolioPositionEventHandler - Position change. seq:0 pos:Position [ccy1Amount=Amount [5100.0 EUR], ccy2Amount=Amount [-6693.24 USD], ccy1EquivalentInPNLCurrency=Amount [6693.24 USD], ccy2EquivalentInPNLCurrency=Amount [-6693.24 USD], currencyPair=EUR/USD, pnlCurrency=USD]
    4080 [pool-1-thread-4] INFO w.c.t.disruptor.PortfolioPositionEventHandler - onEvent: seq:1/false event: MarketEvent [delimitedMessage=T|S|6.1t|R|EURUSD|1.3125, trade=Trade [SELL Amount [6100.0 EUR] at Rate [EUR/USD@1.3125]], rate=null]
    4080 [pool-1-thread-4] INFO w.c.t.disruptor.PortfolioPositionEventHandler - Position change. seq:1 pos:Position [ccy1Amount=Amount [-1000.0 EUR], ccy2Amount=Amount [1313.0100000000002 USD], ccy1EquivalentInPNLCurrency=Amount [-1313.0100000000002 USD], ccy2EquivalentInPNLCurrency=Amount [1313.0100000000002 USD], currencyPair=EUR/USD, pnlCurrency=USD]
    4080 [pool-1-thread-4] INFO w.c.t.disruptor.PortfolioPositionEventHandler - onEvent: seq:2/true event: MarketEvent [delimitedMessage=R|EURUSD|1.3126, trade=null, rate=Rate [EUR/USD@1.3126]]


To stop the server

   echo 'C|STOP' | nc 127.0.0.1 7000

Disruptor Configuration
=======================

The following diagram illustrates the key parts of the system, including two sequence
barriers.


              tB|   +----+   |pB
            +---+-->| TT |---+-----+
            |   |   +----+   |     |
            |   |            |     v
     N   +----+ |            |   +----+
    ---->| G* | |            |   | PP |----> Console Log Position Update
         +----+ |            |   +----+
            |   |            |     ^
            |   |   +----+   |     |
            +---+-->| RT |---+-----+
                |   +----+   |

(N)etty listening on 0.0.0.0:7000

The (G)ateway could be multithreaded(*), as the Netty server can accept more than one connection
and process incoming messages on multiple threads.  But it is currently configured for 1 thread.

    See: whitewerx.com.trapos.gateway.TextMessageHandler
         whitewerx.com.trapos.disruptor.MarketEventPublisher

The (TT)radeTranslator is a single threaded disruptor BatchProcessor.

    See: whitewerx.com.trapos.disruptor.MarketTradeEventHandler
    
The (RT)rateTranslator is a single threaded disruptor BatchProcessor.

    See: whitewerx.com.trapos.disruptor.MarketRateEventHandler

The (PP)ortfolio Position is a single threaded disruptor BatchProcessor.

    See: whitewerx.com.trapos.disruptor.PortfolioPositionEventHandler 

The (tB) is a sequence barrier for the two translators, both translators can execute
as soon as a new event is available in the RingBuffer.

The (tP) is a sequence barrier for the position server this is configured to not execute
until the event is processed by both the MarketTradeEventHandler, and the MarketRateEventHandler.

Contributing
============

Bootstrap Dependencies
----------------------

 * JDK 6
 * Maven (tested with 3.0.4)
 * Internet connection to download the dependent jars/plugins

Building
--------

Standard maven commands

    mvn clean
    mvn test
    mvn install
    mvn site

Checking Code Coverage
----------------------

Run mvn site in the top level directory.

   mvn site
   open target/site/cobertura/index.html

Code Formatting
---------------
The code is styled using the Eclipse formatting conventions contained within:

    trapos-java-formatting.xml
    
