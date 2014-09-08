package com.neverwinterdp.demandspike.commandline;

import com.beust.jcommander.Parameter;

public class MainCommands {
//	@Parameter(names = { "start" }, description = "Run test job")
//	public boolean run = false;
//
//	@Parameter(names = { "stop" }, description = "Stop test job")
//	public boolean stop = false;
//
//	@Parameter(names = { "list" }, description = "List test job")
//	public boolean list = false;
//
//	@Parameter(names = { "pause" }, description = "Pause test job")
//	public boolean pause = false;
//	
	@Parameter(names={"-h","--help"}, description="Displays help message",help = true)
    public boolean help = false;
	
//	@Parameter(names={"demandspike"})
//    public boolean demandspike = false;
}
