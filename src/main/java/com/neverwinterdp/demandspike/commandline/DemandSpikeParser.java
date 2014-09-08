package com.neverwinterdp.demandspike.commandline;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.constants.Mode;
import com.neverwinterdp.demandspike.job.DemandSpikeJob;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.job.send.MessageDriverConfig;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

public class DemandSpikeParser {
	private static Logger logger;
	
	public DemandSpikeParser() {
		logger = LoggerFactory.getLogger("DemandSpike");
	}
	public MainCommands mainCommands = new MainCommands();
	public RunCommands runCommands = new RunCommands();
	public StopCommands stopCommands = new StopCommands();
	public PauseCommands pauseCommands = new PauseCommands();
	public ListCommands listCommands = new ListCommands();
	public boolean parseCommandLine(String[] args) throws IOException {

		logger.info("Parsing command lines");
		JCommander jcomm = null;
		try {
			jcomm = new JCommander(mainCommands);
			jcomm.addCommand("run", runCommands);
			jcomm.addCommand("stop", stopCommands);
			jcomm.addCommand("pause", pauseCommands);
			jcomm.addCommand("list", listCommands);
			
			if(args.length <=0 || args==null){
				jcomm.usage();
				System.exit(0);
			}
			
			jcomm.parse(args);

			if (mainCommands.help) {
			 jcomm.usage();
			 System.exit(0);
			}
			
			if(jcomm.getParsedCommand().equals("run")){
				
				run(runCommands);
			}
		} catch (ParameterException e) {

			System.err.println(e.getMessage()
					+ "\nUse the -h option to get usage");
			return false;
		}

		if (args.length < 2) {
			jcomm.usage();
			return true;
		}

		//jcomm.usage();


		return true;
	}
	
	public void run(RunCommands commands) throws IOException {
		
		if(commands.mode.equals(Mode.standalone)){
			launchStandAloneTest(commands);
		}else{
			if(commands.useYarn){
				//TODO yarn mode 
			}else{
				//TODO distributed mode
			}
			System.exit(0);
		}
		
		
	}
	
	private void launchStandAloneTest(RunCommands commands) throws IOException{
		
		JobConfig config;
		if (commands.method.equals(Method.POST)) {
			config = new JobConfig(new MessageDriverConfig("sparkngin",
					commands.targets, "metrics.consumer", commands.method), commands.cLevel, commands.messageSize,  commands.time,
					commands.nMessages, commands.sendPeriod);
		} else {
			config = new JobConfig(new MessageDriverConfig(commands.targets, commands.method), commands.cLevel,
					commands.messageSize, commands.time, commands.nMessages, commands.sendPeriod);
		}
		ApplicationMonitor appMonitor = new ApplicationMonitor();
		DemandSpikeJob job = new DemandSpikeJob("sparkngin producer",
				appMonitor, config);
		logger.info("Job started");
		job.run();
		ApplicationMonitorSnapshot snapshot = appMonitor.snapshot();
		Map<String, TimerSnapshot> timers = snapshot.getRegistry().getTimers();
		MetricFormater formater = new MetricFormater("  ");
		System.out.println(formater.format(timers));
	}
}
