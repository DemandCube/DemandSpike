package com.neverwinterdp.demandspike.commandline;

import com.beust.jcommander.Parameter;

public class MainCommands {
  @Parameter(names = { "-h", "--help" }, description = "Displays help message", help = true)
  public boolean help = false;

}
