package com.neverwinterdp.demandspike.job.send;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class MessageDriverConfig implements Serializable {

	String driver = "sparkngin";

	Map<String, String> driverProperties = new HashMap<String, String>();

	List<String> connect = new ArrayList<String>();

	String topic = "";
	
	Method method;

	public MessageDriverConfig(String driver, List<String> connect,
			String topic, Map<String, String> driverProperties) {
		this(driver, connect, topic,Method.POST);
		this.driverProperties = driverProperties;

	}

	public MessageDriverConfig(String driver, List<String> connect, String topic,Method method) {
		this.driver = driver;
		this.connect = connect;
		this.topic = topic;
		this.method = method;
	}
	
	public MessageDriverConfig(List<String> connect,Method method) {
		this.connect = connect;
		this.method = method;
	}

	public String getDriver() {
		return this.driver;
	}

	public MessageDriver createDriver(ApplicationMonitor appMonitor) {
		MessageDriver mdriver = new HttpDriver(appMonitor);
		mdriver.init(connect, this.method, this.topic);
		return mdriver;
	}
}