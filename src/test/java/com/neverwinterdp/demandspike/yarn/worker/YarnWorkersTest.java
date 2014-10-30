package com.neverwinterdp.demandspike.yarn.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.demandspike.result.Result;
import com.neverwinterdp.demandspike.result.ResultAggregator;
import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.HttpConnectionUnitTest.LongTaskRouteHandler;
import com.neverwinterdp.util.JSONSerializer;

public class YarnWorkersTest extends AbstractMiniClusterUnitTest {

	private static MiniYARNCluster miniYarnCluster;
	private static HttpServer server;

	@BeforeClass
	public static void setup() throws Exception {
		server = new HttpServer();
		server.add("/message", new LongTaskRouteHandler());
		server.setPort(7080);
		server.startAsDeamon();
		Thread.sleep(1000);
		miniYarnCluster = createMiniYARNCluster(1);
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
				"--app-rpc-port", "63200", "--app-num-of-worker", "1",
				"--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
				"--conf:broker-connect=http://127.0.0.1:7080/message",
				"--conf:max-duration=3000000", "--conf:message-size=1024",
				"--conf:maxNumOfRequests=10000" };

		AppClient appClient = new AppClient();
		try {
			AppClientMonitor appMonitor = appClient.run(args, yarnConf);
		appMonitor.monitor();
			appMonitor.report(System.out);

			System.out.println("finished yarn application");
			/*
			 * Configuration conf1 = new Configuration(yarnConf); Path tmpDir1 =
			 * new Path("temp"); Path outFile1 = new Path(tmpDir1,
			 * "reduce-out"); FileSystem fileSys = null;
			 * 
			 * Text key = new Text(); Text value = new Text();
			 * SequenceFile.Reader reader = null; try { fileSys =
			 * FileSystem.get(conf1); reader = new SequenceFile.Reader(fileSys,
			 * outFile1, conf1); List<Result> results = new ArrayList<Result>();
			 * while (reader.next(key, value)) {
			 * System.out.println(key.toString() + "," + value.toString());
			 * results.add(JSONSerializer.INSTANCE.fromString( value.toString(),
			 * Result.class)); } ResultAggregator resultAggregator = new
			 * ResultAggregator(); resultAggregator.merge(results);
			 * System.out.println(JSONSerializer.INSTANCE
			 * .toString(resultAggregator.getResult())); } catch (IOException
			 * e1) { e1.printStackTrace(); } try { reader.close(); } catch
			 * (IOException e) { e.printStackTrace(); }
			 */
		} catch (Exception e) {
			// TODO Handle exception
			e.printStackTrace();
		}

	}
}