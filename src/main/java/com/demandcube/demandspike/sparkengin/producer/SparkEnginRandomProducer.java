package com.demandcube.demandspike.sparkengin.producer;

import com.demandcube.demandspike.kafka.metrics.MetricsManager;
import com.demandcube.demandspike.producer.AbstractProducer;
import com.demandcube.demandspike.producer.AbstractProducerConfig;
import com.neverwinterdp.message.Message;
import com.neverwinterdp.message.SampleEvent;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.HttpClient;


public class SparkEnginRandomProducer extends AbstractProducer {


    private HttpClient client;

    String host;
    int port;
    public SparkEnginRandomProducer(AbstractProducerConfig randomProducerConfig, MetricsManager metricsManager) throws InterruptedException {
	super(randomProducerConfig,metricsManager);
	host = randomProducerConfig.getIp();
	port = Integer.parseInt(randomProducerConfig.getPort());
    }

    public void send() throws Exception {

	DumpResponseHandler handler = new DumpResponseHandler();
	client = new HttpClient(host, port, handler);
	SampleEvent event = new SampleEvent("event-", "event ");
	Message message = new Message("m", event, true);
	client.post("/message", message);

    }

  

    @Override
    public void producerDestroyed() {
	client.close();
	
    }

}
