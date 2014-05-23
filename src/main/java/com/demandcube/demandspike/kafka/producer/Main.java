package com.demandcube.demandspike.kafka.producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.demandcube.demandspike.kafka.metrics.MetricsManager;

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
     */
    public static void main(String[] args) throws FileNotFoundException {
	Main main = new Main();
	main.execute(args);
    }
    public  void execute(String[] args) throws FileNotFoundException {
	String path = this.getClass().getResource("config.yml").getFile();
	InputStream input = new FileInputStream(new File(path));
	Yaml yaml = new Yaml();
	Map configData = (Map) yaml.load(input);
	RandomProducerConfig randomProducerConfig = new RandomProducerConfig(configData);
	MetricsManager metricsManager = new MetricsManager(randomProducerConfig.getClientId(),randomProducerConfig.getTopicName());
	RandomProducer rp = new RandomProducer(randomProducerConfig,metricsManager);
	Thread rpT = new Thread(rp);
	rpT.start();
	System.out.println("Sending in pregress ...");
	
    }
}
