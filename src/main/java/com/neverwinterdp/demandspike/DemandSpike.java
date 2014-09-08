package com.neverwinterdp.demandspike;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.commandline.DemandSpikeParser;

public class DemandSpike {
	private static Logger logger;
	private boolean help;
	//public boolean run;
	public boolean stop;
	public boolean pause;
	public boolean list;
	
	
	
	public static void main(String[] args) {
		try {
			
			//String[] argv = { "run","--target","http://127.0.0.1:7080","--protocol","HTTP","--method","POST","--time","300","--cLevel","2" };
			
			DemandSpikeParser demandSpikeParser = new DemandSpikeParser();
			
			
			
			
			if (!demandSpikeParser.parseCommandLine(args)) {
				System.exit(-1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DemandSpike() {
		logger = LoggerFactory.getLogger("DemandSpike");

	}
	
}
