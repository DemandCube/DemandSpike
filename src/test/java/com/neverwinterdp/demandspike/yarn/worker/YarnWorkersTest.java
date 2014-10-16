package com.neverwinterdp.demandspike.yarn.worker;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.HttpConnectionUnitTest.LongTaskRouteHandler;

public class YarnWorkersTest extends AbstractMiniClusterUnitTest {

	private static MiniYARNCluster miniYarnCluster;
	private static HttpServer server;

	@BeforeClass
	public static void setup() throws Exception {
		miniYarnCluster = createMiniYARNCluster(1);
		Thread.sleep(1000);
		server = new HttpServer();
		server.add("/message", new LongTaskRouteHandler());
		server.setPort(7080);
		server.startAsDeamon();
		Thread.sleep(1000);
	}

	@AfterClass
	public static void teardown() throws Exception {
		miniYarnCluster.stop();
		miniYarnCluster.close();
		server.shutdown();

	}

	@Test
	public void testYarnContainers() throws Exception {
		YarnConfiguration yarnConf = new YarnConfiguration(
				miniYarnCluster.getConfig());
		yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB,
				64);
		yarnConf.setClass(YarnConfiguration.RM_SCHEDULER, FifoScheduler.class,
				ResourceScheduler.class);
		yarnConf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030");

		String[] args = {
				// "--app-home", "/tmp/app/demandspike",
				// "--app-home-local",
				// "./build/release/DemandSpike/libs/jarsforhadoop" ,
				"--app-name",
				"NeverwinterDP_DemandSpike_App",
				"--app-container-manager",
				"com.neverwinterdp.demandspike.yarn.master.AsyncDemandSpikeAppMasterContainerManager",
				"--app-rpc-port", "63200", "--app-num-of-worker", "2",
				"--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
				"--conf:broker-connect=http://127.0.0.1:7080/message",
				"--conf:max-duration=30000", "--conf:message-size=1024",
				"--conf:maxNumOfRequests=10000" };

		AppClient appClient = new AppClient();
		AppClientMonitor appMonitor = appClient.run(args, yarnConf);
		appMonitor.monitor();
		appMonitor.report(System.out);

	}
}