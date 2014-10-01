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

      System.err.println(e.getMessage() + "\nUse the -h option to get usage");
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

  
  private boolean launchYarnMode(RunCommands commands) throws Exception{
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
        "--conf:broker-connect=http://www.facebook.com:80",
        "--conf:max-duration=30000",
        "--conf:message-size=1024",
        "--conf:maxNumOfRequests=200"};

    AppClient appClient = new AppClient();
    AppClientMonitor appMonitor = appClient.run(args,yarnConf);
    appMonitor.monitor();
    appMonitor.report(System.out);
    
    return true;
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
    final CountDownLatch latch = new CountDownLatch(1);
    FutureTask<HazelcastInstance> clusterTask = new FutureTask<HazelcastInstance>(
        new SpikeCluster(latch));
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.execute(clusterTask);
    latch.await();
    hazelcastInstance = clusterTask.get();
    //System.out.println("Demandspike cluster started");

    IExecutorService eS = hazelcastInstance.getExecutorService("default");

    Set<Member> members = new HashSet<Member>();
    for (Member member : hazelcastInstance.getCluster().getMembers()) {
      members.add(member);
      if (members.size() == config.nWorkers) {
        break;
      }
    }

    long timeStart = System.currentTimeMillis();
    Map<Member, Future<Result>> futures = eS.submitToMembers(new SpikeWorker(
        config), members);

    List<Result> results = new ArrayList<Result>();
    for (Future<Result> future : futures.values()) {
      results.add(future.get());
    }
    long proccessingTime = System.currentTimeMillis() - timeStart;
    
    ResultAggregator resultAggregator = new ResultAggregator(new Result());
    resultAggregator.merge(results);
    resultAggregator.printResult();
    
    
//    Result finalResult = resultAggregator.getResult();
//    System.out.println("2xx response               : " + finalResult.getResponse2xx());
//    System.out.println("3xx response               : " + finalResult.getResponse3xx());
//    System.out.println("4xx response               : " + finalResult.getResponse4xx());
//    System.out.println("5xx response               : " + finalResult.getResponse5xx());
//    System.out.println("Other response             : " + finalResult.getResponseOthers());
//   
//    String pTime = String.format(
//        "%02d:%02d:%02d",
//        TimeUnit.MILLISECONDS.toHours(proccessingTime),
//        TimeUnit.MILLISECONDS.toMinutes(proccessingTime)
//            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
//                .toHours(proccessingTime)),
//        TimeUnit.MILLISECONDS.toSeconds(proccessingTime)
//            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
//                .toMinutes(proccessingTime)));
//    System.out.println("Number of threads used     : " + config.numOfThreads);
//    System.out.println("Processing time            : " + pTime);

    if(config.outputFile!=null){
      CSVGenerator<Result> csvGenerator = new CSVGenerator<Result>(Result.class);
      try {
        csvGenerator.generateCSVFile(results, config.outputFile);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
        return false;
      } catch (SecurityException e) {
        e.printStackTrace();
        return false;
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return false;
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        return false;
      }
    }
   
    return true;
  }
}
