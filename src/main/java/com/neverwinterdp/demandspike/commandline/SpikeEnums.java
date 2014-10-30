package com.neverwinterdp.demandspike.commandline;

public class SpikeEnums {
  public static enum METHOD {
    GET, POST;
  }

  public static enum MODE {
    standalone, distributed;
  }

  public static enum OUTPUT_TYPE {
    console, csv;
  }

  public static enum STOCHASTIC {
    exponential, oscillator;
  }

  public static enum PROTOCOL {
    HTTP, HTTPS;
  }
}
