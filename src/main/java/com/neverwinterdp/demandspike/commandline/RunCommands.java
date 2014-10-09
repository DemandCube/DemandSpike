package com.neverwinterdp.demandspike.commandline;

import java.util.List;

import com.beust.jcommander.Parameter;

public class RunCommands {
  @Parameter(names = "--target", description = "Target url. Multiple urls can be given by giving -target option multiple times.", required = true)
  public List<String> targets;

  @Parameter(names = { "--name" }, description = "Naming test job")
  public String name;

  @Parameter(names = { "--mode" }, description = "Mode of testing environment standalone|distributed")
  public SpikeEnums.MODE mode = SpikeEnums.MODE.standalone;

  @Parameter(names = { "--useYarn" }, description = "Run test using yarn or without yarn. works only on distributed mode", arity = 1)
  public boolean useYarn = true;

  @Parameter(names = { "--protocol" }, description = "Protocol (For now only HTTP supports)", required = true)
  public SpikeEnums.PROTOCOL protocol = SpikeEnums.PROTOCOL.HTTP;

  @Parameter(names = { "--method" }, description = "GET|POST", required = true)
  public SpikeEnums.METHOD method = SpikeEnums.METHOD.POST;

  @Parameter(names = { "--cLevel" }, description = "Concurrency level.Number of threads/containers per worker")
  public Integer cLevel = 1;

  @Parameter(names = { "--message-size" }, description = "Size of the message in bytes.")
  public Integer messageSize = 1024;

  @Parameter(names = { "--time" }, description = "Time duration for test. Should be in seconds.")
  public Integer time = 30000;

  @Parameter(names = { "--maxRequests" }, description = "Maximum number of requests to send")
  public Integer maxRequests = 1000;

  @Parameter(names = { "--sendPeriod" }, description = "")
  public Integer sendPeriod = 0;

  @Parameter(names = { "--input-data" }, description = "Input data. (Reads data from the commanline)")
  public String inputData;

  @Parameter(names = { "--input-file" }, description = "Path of the input file")
  public String inputFile;

  @Parameter(names = { "--output-file" }, description = "CSV export file path.")
  public String outputFile;
  
  @Parameter(names = { "--nWorkers" }, description = "Number of workers to handle the job")
  public Integer nWorkers = 1;

  @Parameter(names = "--auto-generator-string", description = "Auto generation data string. This string will be replaced with auto generated value for data")
  public List<String> autoAutoString;

}
