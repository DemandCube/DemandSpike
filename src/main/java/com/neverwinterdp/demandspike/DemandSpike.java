package com.neverwinterdp.demandspike;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.commandline.DemandSpikeParser;

public class DemandSpike {


  public boolean stop;
  public boolean pause;
  public boolean list;

  public static void main(String[] args) throws InterruptedException,
      IOException, ExecutionException {

    DemandSpikeParser demandSpikeParser = new DemandSpikeParser();
    demandSpikeParser.parseCommandLine(args); 
  }
}
