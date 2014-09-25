package com.neverwinterdp.demandspike.yarn.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.worker.SpikeRunner;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;

public class DemandSpikeAppWorker implements AppWorker {
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(DemandSpikeAppWorker.class);

	public void run(AppWorkerContainer appContainer) throws Exception {
		JobConfig config = new JobConfig(appContainer.getConfig().yarnConf);
		
		System.out.println("Test started. please wait to complete...");
		
		Thread t = new Thread(new SpikeRunner(config));
		t.start();
	}
}
