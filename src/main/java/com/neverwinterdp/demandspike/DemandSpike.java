package com.neverwinterdp.demandspike;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.neverwinterdp.demandspike.commandline.DemandSpikeParser;

public class DemandSpike {
	private static Logger logger;
	public boolean stop;
	public boolean pause;
	public boolean list;

	public static void main(String[] args) {
		try {
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
