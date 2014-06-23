package com.neverwinterdp.server.shell;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.demandspike.DemandSpikeJob;
import com.neverwinterdp.server.client.DemandSpikePlugin;
import com.neverwinterdp.server.command.ServiceCommandResult;
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
    
    public void execute(ShellContext ctx) {
      DemandSpikePlugin plugin = ctx.getCluster().plugin("demandspike") ;
      ServiceCommandResult<Boolean>[] results = plugin.submit(job, job.memberSelector.timeout) ;
      
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