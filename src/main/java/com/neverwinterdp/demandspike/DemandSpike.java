package com.neverwinterdp.demandspike;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.neverwinterdp.demandspike.commandline.DemandSpikeParser;

public class DemandSpike {

  public boolean stop;
  public boolean pause;
  public boolean list;

  public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {

    DemandSpikeParser demandSpikeParser = new DemandSpikeParser();
    demandSpikeParser.parseCommandLine(args);
  }
}
