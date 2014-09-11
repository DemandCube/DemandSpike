package com.neverwinterdp.demandspike.commandline;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.constants.Mode;
import com.neverwinterdp.demandspike.job.DemandSpikeJob;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.job.send.MessageDriverConfig;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

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

  public boolean parseCommandLine(String[] args) throws Exception {

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
        System.exit(0);
      }

      jcomm.parse(args);

      if (mainCommands.help) {
        jcomm.usage();
        System.exit(0);
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

    // jcomm.usage();

    return true;
  }

  public void run(RunCommands commands) throws Exception {

    if (commands.mode.equals(Mode.standalone)) {
      launchStandAloneTest(commands);
    } else {
      if (commands.useYarn) {
        launchYarnMode(commands);
      } else {
        // TODO distributed mode
      }
      System.exit(0);
    }

  }

  private void launchYarnMode(RunCommands commands) throws Exception {
    YarnConfiguration yarnConf = new YarnConfiguration();
    yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 64);
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
        "--app-rpc-port", "63200", "--app-num-of-worker",
        commands.cLevel.toString(),
        "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
        "--conf:broker-connect=" + commands.targets.get(0),
        "--conf:max-duration=" + commands.time,
        "--conf:message-size=" + commands.messageSize,
        "--conf:num-of-message=" + commands.nMessages };

    AppClient appClient = new AppClient();
    AppClientMonitor appMonitor = appClient.run(args, new YarnConfiguration(
        miniYarnCluster.getConfig()));
    appMonitor.monitor();
    appMonitor.report(System.out);
    Thread.sleep(3000);
    miniYarnCluster.stop();
    miniYarnCluster.close();
  }

  private void launchStandAloneTest(RunCommands commands) throws IOException {

    JobConfig config;
    if (commands.method.equals(Method.POST)) {
      config = new JobConfig(new MessageDriverConfig("sparkngin",
          commands.targets, "metrics.consumer", commands.method),
          commands.cLevel, commands.messageSize, commands.time,
          commands.nMessages, commands.sendPeriod);
    } else {
      config = new JobConfig(new MessageDriverConfig(commands.targets,
          commands.method), commands.cLevel, commands.messageSize,
          commands.time, commands.nMessages, commands.sendPeriod);
    }
    ApplicationMonitor appMonitor = new ApplicationMonitor();
    DemandSpikeJob job = new DemandSpikeJob("sparkngin producer", appMonitor,
        config);
    logger.info("Job started");
    job.run();
    ApplicationMonitorSnapshot snapshot = appMonitor.snapshot();
    Map<String, TimerSnapshot> timers = snapshot.getRegistry().getTimers();
    MetricFormater formater = new MetricFormater("  ");
    System.out.println(formater.format(timers));
  }
}
