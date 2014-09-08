package com.neverwinterdp.demandspike.commandline;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.constants.Mode;
import com.neverwinterdp.demandspike.constants.Protocol;

public class RunCommands {
	@Parameter(names = "--target", description = "Target url. Multiple urls can be given by giving -target option multiple times.", required = true)
	public List<String> targets;

	@Parameter(names = { "--name" }, description = "Naming test job")
	public String name;

	@Parameter(names = { "--mode" }, description = "Mode of testing environment standalone|distributed")
	public Mode mode = Mode.standalone;

	@Parameter(names = { "--useYarn" }, description = "Run test using yarn or without yarn. works only on distributed mode", arity = 1)
	public boolean useYarn = true;

	@Parameter(names = { "--protocol" }, description = "Protocol (For now only HTTP supports)", required = true)
	public Protocol protocol = Protocol.HTTP;

	@Parameter(names = { "--method" }, description = "GET|POST", required = true)
	public Method method = Method.POST;

	@Parameter(names = { "--cLevel" }, description = "Concurrency level.Number of threads/containers per machine")
	public Integer cLevel = 1;

	@Parameter(names = { "--messageSize" }, description = "Size of the message in bytes.")
	public Integer messageSize = 1024;

	@Parameter(names = { "--time" }, description = "Time duration for test. Should be in seconds.")
	public Integer time = 30000;
	
	@Parameter(names = { "--nMessages" }, description = "Number of messages to test")
	public Integer nMessages = 100000;
	
	@Parameter(names = { "--sendPeriod" }, description = "")
	public Integer sendPeriod = 0;

}
