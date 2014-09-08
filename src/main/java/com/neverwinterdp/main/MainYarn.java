package com.neverwinterdp.main;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;

import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;

public class MainYarn {
	static MiniYARNCluster miniYarnCluster;

	public static void main(String[] arguments) throws Exception {
		YarnConfiguration yarnConf = new YarnConfiguration();
		yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB,
				64);
		yarnConf.setClass(YarnConfiguration.RM_SCHEDULER, FifoScheduler.class,
				ResourceScheduler.class);
		MiniYARNCluster miniYarnCluster = new MiniYARNCluster("yarn", 1, 1, 1);
		miniYarnCluster.init(yarnConf);
		yarnConf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030");
		miniYarnCluster.start();
		// wait to make sure the server is started
		// TODO: find a way to fix this
		Thread.sleep(5000);
		String[] args = {
				"--mini-cluster-env",
				"--app-name",
				"NeverwinterDP_DemandSpike_App",
				"--app-container-manager",
				"com.neverwinterdp.demandspike.yarn.master.AsyncDemandSpikeAppMasterContainerManager",
				"--app-rpc-port", "63200", "--app-num-of-worker", "2",
				"--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
				"--conf:broker-connect=127.0.0.1:7080",
				"--conf:max-duration=30000",
				"--conf:message-size=1024",
				"--conf:num-of-message=100"};

		AppClient appClient = new AppClient();
		AppClientMonitor appMonitor = appClient.run(args,
				new YarnConfiguration(miniYarnCluster.getConfig()));
		appMonitor.monitor();
		appMonitor.report(System.out);
		Thread.sleep(3000);
		miniYarnCluster.stop();
	    miniYarnCluster.close();

	}

}
