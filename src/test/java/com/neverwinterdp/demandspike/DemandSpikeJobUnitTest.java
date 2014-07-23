package com.neverwinterdp.demandspike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.neverwinterdp.demandspike.job.send.MessageSenderTask;
import com.neverwinterdp.queuengin.kafka.SimplePartitioner;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJobUnitTest {
  @Test
  public void testNormalTask() throws Exception {
    ApplicationMonitor appMonitor = new ApplicationMonitor() ;
    String[] args = {
      "--driver", "dummy",
      "--driver:serializer.class=kafka.serializer.StringEncoder",
      "--driver:partitioner.class=" + SimplePartitioner.class.getName(),
      "--driver:request.required.acks=1",
      "--max-duration", "500", "--num-of-task", "3", "--max-num-of-message", "3",

      "--problem:kafka.description=kafka server randomly on/off",
      "--problem:kafka.problem=service-failure",
      "--problem:kafka.member-role=kafka",
      "--problem:kafka.module=kafka",
      "--problem:kafka.service-id=KafkaClusterService",
      "--problem:kafka.period=3000",
      "--problem:kafka.failure-time=1000",
    };
    DemandSpikeJob job = new DemandSpikeJob() ;
    new JCommander(job, args) ;
    
    MessageSenderTask[] task = job.createTasks(appMonitor) ;
    assertEquals(3, task.length) ;
    task[0].run() ;
    
    Map<String, ProblemSimulator> problemSimulators = job.problemConfig.getProblemSimulators() ;
    assertEquals(1, problemSimulators.size()) ;
    ProblemSimulator kafkaServerFailure = problemSimulators.get("kafka") ;

    assertNotNull(kafkaServerFailure) ;
    assertTrue(kafkaServerFailure instanceof ServiceFailureSimulator) ;
    kafkaServerFailure.start(); 
    Thread.sleep(5000);
    kafkaServerFailure.stop();  
  }

  @Test
  public void testPeriodicTask() {
    ApplicationMonitor appMonitor = new ApplicationMonitor() ;
    String[] args = {
      "--max-duration", "500", "--num-of-task", "3", "--max-num-of-message", "30", "--send-period", "100"
    };
    DemandSpikeJob job = new DemandSpikeJob() ;
    new JCommander(job, args) ;
    
    MessageSenderTask[] task = job.createTasks(appMonitor) ;
    assertEquals(3, task.length) ;
    task[0].run() ;
  }
}
