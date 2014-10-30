package com.neverwinterdp.demandspike.commandline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.result.Result;
import com.neverwinterdp.demandspike.result.ResultAggregator;
import com.neverwinterdp.demandspike.worker.SpikeWorker;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.util.JSONSerializer;

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

  public boolean parseCommandLine(String[] args) throws IOException, InterruptedException, ExecutionException {
    logger.info("Parsing command lines");
    JCommander jcomm = null;
    try {
      jcomm = new JCommander(mainCommands);
      jcomm.addCommand("run", runCommands);
      jcomm.addCommand("stop", stopCommands);
      jcomm.addCommand("pause", pauseCommands);
      jcomm.addCommand("list", listCommands);

      if (args == null || args.length < 2) {
        jcomm.usage();
        return true;
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

      System.err.println(e.getMessage() + "\nUse the -h option to get usage");
      return false;
    }

    return true;
  }

  public boolean run(RunCommands commands) throws IOException, InterruptedException, ExecutionException {
    if (commands.mode.equals(SpikeEnums.MODE.standalone)) {
      return launchStandAloneTest(commands);
    } else {
      if (commands.useYarn) {
        System.out.println("yarn mode started...");
        return launchYarnMode(commands);
      } else {
        return launchDistributedMode(commands);
      }
    }
  }

  private boolean launchDistributedMode(RunCommands commands) throws InterruptedException {
    // TODO Distributed mode
    return true;
  }

  private boolean launchStandAloneTest(RunCommands commands) throws IOException, InterruptedException,
      ExecutionException {
    JobConfig config = new JobConfig(commands);
    ExecutorService executor = Executors.newCachedThreadPool();
    Future<Result> future = executor.submit(new SpikeWorker(config));
    System.out.println(JSONSerializer.INSTANCE.toString(future.get()));
    return true;
  }

  private boolean launchYarnMode(RunCommands commands) {
    YarnConfiguration yarnConf = new YarnConfiguration();
    yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 64);
    yarnConf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030");

    if (commands.yarnConfig != null && commands.yarnConfig.size() > 0) {
      for (String file : commands.yarnConfig) {
        yarnConf.addResource(new Path(file));
      }
    }

    String[] args = { "--app-name", "NeverwinterDP_DemandSpike_App", "--app-container-manager",
        "com.neverwinterdp.demandspike.yarn.master.AsyncDemandSpikeAppMasterContainerManager",
        "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030", "--app-rpc-port", "63200", "--app-num-of-worker",
        "" + commands.nWorkers, "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
        "--conf:broker-connect=" + commands.targets.get(0), "--conf:max-duration=" + commands.time,
        "--conf:message-size=" + commands.messageSize, "--conf:maxNumOfRequests=" + commands.maxRequests,
        "--conf:cLevel=" + commands.cLevel, "--conf:nWorkers=" + commands.nWorkers, };

    AppClient appClient = new AppClient();
    try {
      AppClientMonitor appMonitor = appClient.run(args, yarnConf);
      appMonitor.monitor();
      appMonitor.report(System.out);

      System.out.println("finished yarn application");

      Configuration conf1 = new Configuration(yarnConf);
      Path tmpDir1 = new Path("temp");
      Path outFile1 = new Path(tmpDir1, "reduce-out");
      FileSystem fileSys = null;

      Text key = new Text();
      Text value = new Text();
      SequenceFile.Reader reader = null;
      try {
        fileSys = FileSystem.get(conf1);
        reader = new SequenceFile.Reader(fileSys, outFile1, conf1);
        List<Result> results = new ArrayList<Result>();
        while (reader.next(key, value)) {
          System.out.println(key.toString() + "," + value.toString());
          results.add(JSONSerializer.INSTANCE.fromString(value.toString(), Result.class));
        }
        ResultAggregator resultAggregator = new ResultAggregator();
        resultAggregator.merge(results);
        System.out.println(JSONSerializer.INSTANCE.toString(resultAggregator.getResult()));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      // TODO Handle exception
      e.printStackTrace();
    }

    return true;
  }
}
