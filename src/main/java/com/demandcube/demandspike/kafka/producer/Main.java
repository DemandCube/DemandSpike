package com.demandcube.demandspike.kafka.producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

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
	/*InputStream input = new FileInputStream(new File(
		"config.yml"));
	Yaml yaml = new Yaml();
	Map data = (Map) yaml.load(input);*/
	int maxSize = 255;//Integer.parseInt(data.get("maxSize").toString());
	int period =1; //Integer.parseInt(data.get("period").toString());
	String ip = "localhost";//data.get("ip").toString();
        String port = "9092";// data.get("port").toString();
        String topicName = "test";//data.get("topic_name").toString();
	RandomProducer rp = new RandomProducer("rp", period, maxSize, ip, port,topicName);
	Statistic stat = new Statistic(period);
	stat.monitor(rp);
	Thread rpT = new Thread(rp);
	Thread st = new Thread(stat);
	rpT.start();
	st.start();
    }
}
