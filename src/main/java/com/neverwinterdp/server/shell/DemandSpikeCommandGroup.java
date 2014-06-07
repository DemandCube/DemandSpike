package com.neverwinterdp.server.shell;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.demandspike.DemandSpikeClusterService;
import com.neverwinterdp.demandspike.DemandSpikeJob;
import com.neverwinterdp.server.client.MemberSelector;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.util.text.TabularPrinter;

@CommandGroupConfig(name = "demandspike")
public class DemandSpikeCommandGroup extends CommandGroup {
  public DemandSpikeCommandGroup() {
    add("submit", SubmitCommand.class);
  }
  
  @Parameters(commandDescription = "execute submit job command")
  static public class SubmitCommand extends Command {
    @ParametersDelegate
    DemandSpikeJob job = new DemandSpikeJob();
    
    @ParametersDelegate
    MemberSelector memberSelector = new MemberSelector();
    
    private long waitTime = 10000;
    
    public void execute(ShellContext ctx) {
      ServiceCommand<Boolean> methodCall = 
          new ServiceCommands.MethodCall<Boolean>("submit", job, waitTime) ;
      methodCall.setTargetService("DemandSpike", DemandSpikeClusterService.class.getSimpleName());
      ServiceCommandResult<Boolean>[] results =
          memberSelector.execute(ctx.getCluster().getClusterClient(), methodCall) ;
      
      ctx.console().header("Submit job");
      TabularPrinter printer = ctx.console().tabularPrinter(30, 10, 10) ;
      printer.setIndent("  ") ;
      printer.header("Member", "Error", "Submit");
      boolean hasError = false ;
      for(int i = 0; i < results.length; i++) {
        ServiceCommandResult<Boolean> sel = results[i] ;
        printer.row(sel.getFromMember(), sel.hasError(), sel.getResult());
        if(sel.hasError()) hasError = true ;
      }
      if(hasError) {
        for(int i = 0; i < results.length; i++) {
          ServiceCommandResult<Boolean> sel = results[i] ;
          if(sel.hasError()) ctx.console().println(sel.getError());
        }
      }
    }
  }
}