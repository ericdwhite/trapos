= Testing with Sample Data

Start the server

    mvn clean package
    mvn exec:java -Dexec.mainClass="whitewerx.com.trapos.App"

To load some message from the command line.

    cat SAMPLE-DATA.txt | nc 127.0.0.1 7000
    
You should then see some console output like:

    189 [pool-1-thread-1] INFO whitewerx.com.trapos.gateway.TextMessageGateway - Started the gateway. 0.0.0.0:7000
    16582 [New I/O server worker #1-1] INFO whitewerx.com.trapos.gateway.TextMessageHandler - T|B|5.1t|R|EURUSD|1.3124
    16583 [New I/O server worker #1-1] INFO whitewerx.com.trapos.gateway.TextMessageHandler - T|S|0.1t|R|EURUSD|1.3125
    16583 [New I/O server worker #1-1] INFO whitewerx.com.trapos.gateway.TextMessageHandler - R|EURUSD|1.3126
    16589 [pool-1-thread-3] INFO whitewerx.com.trapos.disruptor.MarketRateEventHandler - onEvent: seq:2/true event: MarketEvent [delimitedMessage=R|EURUSD|1.3126, trade=null, rate=Rate [EUR/USD@1.3126]]
    16589 [pool-1-thread-2] INFO whitewerx.com.trapos.disruptor.MarketTradeEventHandler - onEvent: seq:0/true event: MarketEvent [delimitedMessage=T|B|5.1t|R|EURUSD|1.3124, trade=Trade [BUY Amount [5100.0 EUR] at Rate [EUR/USD@1.3124]], rate=null]
    16590 [pool-1-thread-2] INFO whitewerx.com.trapos.disruptor.MarketTradeEventHandler - onEvent: seq:1/false event: MarketEvent [delimitedMessage=T|S|0.1t|R|EURUSD|1.3125, trade=Trade [SELL Amount [100.0 EUR] at Rate [EUR/USD@1.3125]], rate=null]

To stop the server

   echo 'C|STOP' | nc 127.0.0.1 7000


= Contributing =

== Checking Code Coverage

Run mvn site in the top level directory.

   mvn site
   open target/site/cobertura/index.html



