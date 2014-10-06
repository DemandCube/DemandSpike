package com.neverwinterdp.demandspike.commandline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.neverwinterdp.demandspike.cluster.SpikeCluster;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.result.Result;
import com.neverwinterdp.demandspike.result.ResultAggregator;
import com.neverwinterdp.demandspike.util.CSVGenerator;
import com.neverwinterdp.demandspike.worker.SpikeWorker;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.hadoop.yarn.app.ipc.ReportData;
import com.neverwinterdp.util.JSONSerializer;

public class DemandSpikeParser {
	private static Logger logger;
	HazelcastInstance hazelcastInstance;

	public DemandSpikeParser() {
		logger = LoggerFactory.getLogger("DemandSpike");
	}

	public MainCommands mainCommands = new MainCommands();
	public RunCommands runCommands = new RunCommands();
	public StopCommands stopCommands = new StopCommands();
	public PauseCommands pauseCommands = new PauseCommands();
	public ListCommands listCommands = new ListCommands();

	public boolean parseCommandLine(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		logger.info("Parsing command lines");
		JCommander jcomm = null;
		try {
			jcomm = new JCommander(mainCommands);
			jcomm.addCommand("run", runCommands);
			jcomm.addCommand("stop", stopCommands);
			jcomm.addCommand("pause", pauseCommands);
			jcomm.addCommand("list", listCommands);

			if (args.length <= 0 || args == null) {
				jcomm.usage();
			}

			jcomm.parse(args);

			if (mainCommands.help) {
				jcomm.usage();
				return true;
			}

			if (jcomm.getParsedCommand().equals("run")) {
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
		return true;
	}

	public boolean run(RunCommands commands) throws IOException,
			InterruptedException, ExecutionException {
		if (commands.mode.equals(SpikeEnums.MODE.standalone)) {
			return launchStandAloneTest(commands);
		} else {
			if (commands.useYarn) {
				return true;
			} else {
				return launchDistributedMode(commands);
			}
		}
	}

	private boolean launchDistributedMode(RunCommands commands)
			throws InterruptedException {
		// final CountDownLatch latch = new CountDownLatch(1);
		// Thread clusterThread = new Thread(new SpikeCluster(latch));
		// clusterThread.start();
		// latch.await();
		System.out.println("Cluster started...");
		return true;
	}

	private boolean launchStandAloneTest(RunCommands commands)
			throws IOException, InterruptedException, ExecutionException {
		JobConfig config = new JobConfig(commands);
	    ReportData data = new ReportData();

	    ExecutorService executor = Executors.newCachedThreadPool();

	    Future<Result> future = executor.submit(new SpikeWorker(config));

	    data.setJsonData(JSONSerializer.INSTANCE.toString(future.get()));
		return true;
	}

	private boolean launchYarnMode(RunCommands commands) {
		YarnConfiguration yarnConf = new YarnConfiguration();
		yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB,
				64);
		// yarnConf.setClass(YarnConfiguration.RM_SCHEDULER,
		// FifoScheduler.class,ResourceScheduler.class);
		yarnConf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030");

		String[] args = {
				"--app-name",
				"NeverwinterDP_DemandSpike_App",
				"--app-container-manager",
				"com.neverwinterdp.demandspike.yarn.master.AsyncDemandSpikeAppMasterContainerManager",
				"--app-rpc-port", "63200", "--app-num-of-worker",
				"" + commands.cLevel,
				"--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
				"--conf:broker-connect=" + commands.targets.get(0),
				"--conf:max-duration=" + commands.time,
				"--conf:message-size=" + commands.dataSize,
				"--conf:maxNumOfRequests=" + commands.maxRequests };

		AppClient appClient = new AppClient();
		try {
			AppClientMonitor appMonitor = appClient.run(args, yarnConf);
			appMonitor.monitor();
			appMonitor.report(System.out);
		} catch (Exception e) {
			// TODO Handle exception
			e.printStackTrace();
		}

		return true;
	}
}
