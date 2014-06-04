package com.neverwinterdp.demandspike;

import static org.junit.Assert.*;

import org.junit.Test;

import com.beust.jcommander.JCommander;

public class DemandSpikeJobUnitTest {
  @Test
  public void testNormalTask() {
    String[] args = {
      "--type", "normal",
      "--max-duration", "500", "--num-of-task", "3", "--max-num-of-message", "30"
    };
    DemandSpikeJob job = new DemandSpikeJob() ;
    new JCommander(job, args) ;
    
    DemandSpikeTask[] task = job.createTasks() ;
    assertTrue(task[0] instanceof NormalTask) ;
    assertEquals(3, task.length) ;
    task[0].run() ;
  }

  @Test
  public void testPeriodicTask() {
    String[] args = {
      "--type", "periodic",
      "--max-duration", "500", "--num-of-task", "3", "--max-num-of-message", "30"
    };
    DemandSpikeJob job = new DemandSpikeJob() ;
    new JCommander(job, args) ;
    
    DemandSpikeTask[] task = job.createTasks() ;
    assertTrue(task[0] instanceof PeriodicTask) ;
    assertEquals(3, task.length) ;
    task[0].run() ;
  }
}
