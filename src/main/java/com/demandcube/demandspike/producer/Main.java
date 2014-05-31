package com.demandcube.demandspike.producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.demandcube.demandspike.kafka.metrics.MetricsManager;
import com.demandcube.demandspike.kafka.producer.KafkaProducerConfig;
import com.demandcube.demandspike.kafka.producer.KafkaRandomProducer;
import com.demandcube.demandspike.sparkengin.producer.SparkEnginProducerConfig;
import com.demandcube.demandspike.sparkengin.producer.SparkEnginRandomProducer;

/**
 * The Class TestProducer.
 */
public class Main {

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws FileNotFoundException
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
	Main main = new Main();
	main.execute(args);
    }

    public void execute(String[] args) throws FileNotFoundException, InterruptedException {
	String path = this.getClass().getResource("config.yml").getFile();
	InputStream input = new FileInputStream(new File(path));
	Yaml yaml = new Yaml();
	Map configData = (Map) yaml.load(input);
	String target = configData.get("target").toString();
	
	AbstractProducerConfig randomProducerConfig = null;
	AbstractProducer rp = null;
	MetricsManager metricsManager;
	    
	if (target.equals("Kafka")) {
	     randomProducerConfig = new KafkaProducerConfig(configData);
	     metricsManager = new MetricsManager("KafkaProducer");
	     rp = new KafkaRandomProducer(randomProducerConfig, metricsManager);
	    
	}else if (target.equals("sribengin")){
	    randomProducerConfig = new SparkEnginProducerConfig(configData);
	    metricsManager = new MetricsManager("SparkEnginProducer");
	    rp = new SparkEnginRandomProducer(randomProducerConfig, metricsManager);
	}
	    
	    Thread rpT = new Thread(rp);
	    rpT.start();
	    System.out.println("Sending in pregress ...");

    }
}
