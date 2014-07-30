package com.neverwinterdp.server.shell;

import java.util.Map;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.demandspike.job.send.MessageSender;
import com.neverwinterdp.demandspike.job.send.MessageSenderConfig;
import com.neverwinterdp.demandspike.job.simulation.ServiceFailureSimulator;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

@ShellCommandConfig(name = "demandspike:job")
public class DemandSpikeJobCommand extends ShellCommand {
  public DemandSpikeJobCommand() {
    add("send", SendCommand.class) ;
    add("simulation", SimulationCommand.class) ;
  }
  
  static public class SendCommand extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      MessageSenderConfig config = new MessageSenderConfig() ;
      command.mapAll(config);
      ApplicationMonitor appMonitor = new ApplicationMonitor() ;
      MessageSender sender = new MessageSender(appMonitor, config, ctx.getClusterGateway()) ;
      sender.run();
      ApplicationMonitorSnapshot snapshot = appMonitor.snapshot() ;
      Map<String, TimerSnapshot> timers = snapshot.getRegistry().getTimers() ;
      MetricFormater formater = new MetricFormater("  ") ;
      ctx.console().println(formater.format(timers));
    }
  }
  
  static public class SimulationCommand extends ShellSubCommand {
    @Parameter(
        names = "--name", description = "Simulation name"
    )
    private String name ;
    
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      command.mapPartial(this);
      if("service-failure".equals(ServiceFailureSimulator.NAME)) {
        ServiceFailureSimulator simulator = new ServiceFailureSimulator(ctx) ;
        command.mapAll(simulator);
        simulator.schedule(ctx.getTimer());
      }
    }
  }
}