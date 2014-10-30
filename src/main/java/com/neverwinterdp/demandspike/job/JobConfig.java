package com.neverwinterdp.demandspike.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neverwinterdp.demandspike.commandline.RunCommands;
import com.neverwinterdp.demandspike.commandline.SpikeEnums;

public class JobConfig implements Serializable {
	/**
   * 
   */
	private static final long serialVersionUID = 1L;

	public List<String> targets;
	public int numOfThreads = 1;
	public String messageSize = "1024";
	public long maxDuration = 30 * 1000; // 30s
	public int maxNumOfRequests = 1000000; // 1 million messages by default
	public long sendPeriod = 0; // Every 100 ms
	public String data;
	public String inputFile;
	public String inputTemplate;
	public int requestsPerThread = 0;
	public int nWorkers = 1;
	public int rate = 1000;
	public float stopOnFailure = 10f;
	public String stopOnConditionName="";
	public String stopOnConditionValue="";
	public SpikeEnums.METHOD method;
	public String outputFile;

	public List<String> getTargets() {
		return targets;
	}

	public JobConfig(RunCommands commands) {
		this.numOfThreads = commands.cLevel;
		this.messageSize = commands.messageSize;
		this.maxDuration = commands.time;
		this.maxNumOfRequests = commands.maxRequests;
		this.sendPeriod = commands.sendPeriod;
		this.data = commands.inputData;
		this.nWorkers = commands.nWorkers;
		this.method = commands.method;
		this.targets = commands.targets;
		this.requestsPerThread = maxNumOfRequests / nWorkers / numOfThreads;
		this.data = commands.inputData;
		this.inputFile = commands.inputFile;
		this.outputFile = commands.outputFile;
		this.rate = commands.rate;
		this.stopOnFailure = commands.stopOnFailure;
		if (!commands.stopOnCondition.equals("")) {
			String[] stopOnConditionNameArray = commands.stopOnCondition
					.split(":");
			this.stopOnConditionName = stopOnConditionNameArray[0];
			if (stopOnConditionNameArray.length > 1)
				this.stopOnConditionValue = stopOnConditionNameArray[1];
		}
	}

	public JobConfig(Map<String, String> yarnConf) {
		String brokerConnect = getString(yarnConf, "broker-connect",
				"http://127.0.0.1:7080");
		this.messageSize = getString(yarnConf, "message-size", "1024");
		;
		this.numOfThreads = getInt(yarnConf, "cLevel", 1);
		this.maxDuration = getLong(yarnConf, "max-duration", (3 * 60 * 1000l));
		this.maxNumOfRequests = getInt(yarnConf, "maxNumOfRequests", 1024);
		this.sendPeriod = 0;
		this.nWorkers = getInt(yarnConf, "nWorkers", 1);
		this.targets = new ArrayList<>();
		this.targets.add(brokerConnect);
		this.requestsPerThread = maxNumOfRequests / nWorkers / numOfThreads;
		this.method = SpikeEnums.METHOD.POST;

	}

	private String getString(Map<String, String> map, String name,
			String defaultValue) {
		String val = map.get(name);
		if (val == null)
			return defaultValue;
		return val;
	}

	private int getInt(Map<String, String> map, String name, int defaultValue) {
		String val = map.get(name);
		if (val == null)
			return defaultValue;
		return Integer.parseInt(val);
	}

	private long getLong(Map<String, String> map, String name, long defaultValue) {
		String val = map.get(name);
		if (val == null)
			return defaultValue;
		return Long.parseLong(val);
	}
}