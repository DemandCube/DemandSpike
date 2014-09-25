package com.neverwinterdp.demandspike.yarn;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;

public class YarnWorkersTest {

  

  @Test
  public void testYarnContainers() throws Exception {
		YarnConfiguration yarnConf = new YarnConfiguration();
		yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB,
				64);
		yarnConf.setClass(YarnConfiguration.RM_SCHEDULER, FifoScheduler.class,
				ResourceScheduler.class);
		yarnConf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030");

		String[] args = {
				"--mini-cluster-env",
				"--app-name",
				"NeverwinterDP_DemandSpike_App",
				"--app-container-manager",
				"com.neverwinterdp.demandspike.yarn.master.AsyncDemandSpikeAppMasterContainerManager",
				"--app-rpc-port", "63200", "--app-num-of-worker", "2",
				"--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
				"--conf:broker-connect=http://127.0.0.1:7080",
				"--conf:max-duration=30000",
				"--conf:message-size=1024",
				"--conf:maxNumOfRequests=100"};

		AppClient appClient = new AppClient();
		AppClientMonitor appMonitor = appClient.run(args,yarnConf);
		appMonitor.monitor();
		appMonitor.report(System.out);


  }
}