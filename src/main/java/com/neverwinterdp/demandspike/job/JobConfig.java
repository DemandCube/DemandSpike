package com.neverwinterdp.demandspike.job;

import java.io.Serializable;
import java.util.List;

import com.neverwinterdp.demandspike.commandline.RunCommands;
import com.neverwinterdp.demandspike.commandline.SpikeEnums;

public class JobConfig implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public List<String> targets;
  public int numOfThreads = 1;
  public int messageSize = 1024;
  public long maxDuration = 30 * 1000; // 30s
  public int maxNumOfRequests = 1000000; // 1 million messages by default
  public long sendPeriod = 0; // Every 100 ms
  public String data;
  public String inputFile;
  public String inputTemplate;
  public int requestsPerThread = 0;
  public int nWorkers = 1;
  public SpikeEnums.METHOD method;
  public List<String> autoGeneratorString;
  public String outputFile;

  public List<String> getTargets() {
    return targets;
  }


  public JobConfig(RunCommands commands) {
    this.numOfThreads = commands.cLevel;
    this.messageSize = commands.dataSize;
    this.maxDuration = commands.time;
    this.maxNumOfRequests = commands.maxRequests;
    this.sendPeriod = commands.sendPeriod;
    this.data = commands.inputData;
    this.nWorkers = commands.nWorkers;
    this.method = commands.method;
    this.targets = commands.targets;
    this.requestsPerThread = maxNumOfRequests / nWorkers / numOfThreads;
    this.data = commands.inputData;
    this.autoGeneratorString = commands.autoAutoString;
    this.inputFile = commands.inputFile;
    this.inputTemplate = commands.inputTemplate;
    this.outputFile = commands.outputFile;
  }
  
}