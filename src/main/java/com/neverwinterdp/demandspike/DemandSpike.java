package com.neverwinterdp.demandspike;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.commandline.DemandSpikeParser;

public class DemandSpike {

  private static Logger logger;
  private boolean help;
  // public boolean run;
  public boolean stop;
  public boolean pause;
  public boolean list;

  public DemandSpike() {
    logger = LoggerFactory.getLogger("DemandSpike");
  }

  public static void main(String[] args) throws InterruptedException,
      IOException, ExecutionException {

    DemandSpikeParser demandSpikeParser = new DemandSpikeParser();
    if (demandSpikeParser.parseCommandLine(args)) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }
}
