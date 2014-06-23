package com.neverwinterdp.demandspike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJobUnitTest {
  @Test
  public void testNormalTask() {
    ApplicationMonitor appMonitor = new ApplicationMonitor() ;
    String[] args = {
      "--type", "normal",
      "--max-duration", "500", "--num-of-task", "3", "--max-num-of-message", "30"
    };
    DemandSpikeJob job = new DemandSpikeJob() ;
    new JCommander(job, args) ;
    
    DemandSpikeTask[] task = job.createTasks(appMonitor) ;
    assertTrue(task[0] instanceof NormalTask) ;
    assertEquals(3, task.length) ;
    task[0].run() ;
  }

  @Test
  public void testPeriodicTask() {
    ApplicationMonitor appMonitor = new ApplicationMonitor() ;
    String[] args = {
      "--type", "periodic",
      "--max-duration", "500", "--num-of-task", "3", "--max-num-of-message", "30"
    };
    DemandSpikeJob job = new DemandSpikeJob() ;
    new JCommander(job, args) ;
    
    DemandSpikeTask[] task = job.createTasks(appMonitor) ;
    assertTrue(task[0] instanceof PeriodicTask) ;
    assertEquals(3, task.length) ;
    task[0].run() ;
  }
}
