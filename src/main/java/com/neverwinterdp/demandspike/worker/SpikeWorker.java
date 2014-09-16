package com.neverwinterdp.demandspike.worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.result.Result;

public class SpikeWorker implements Callable<Result>, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final JobConfig config;
  public static MetricRegistry timerRegistry = new MetricRegistry();

  public static Integer response2xx = 0;
  public static Integer response3xx = 0;
  public static Integer response4xx = 0;
  public static Integer response5xx = 0;
  public static Integer responseOthers = 0;
  
  public static synchronized void increaseResponse2xx() {
    response2xx++;
  }

  public static synchronized void increaseResponse3xx() {
    response3xx++;
  }

  public static synchronized void increaseResponse4xx() {
    response4xx++;
  }

  public static synchronized void increaseResponse5xx() {
    response5xx++;
  }

  public static synchronized void increaseResponseOthers() {
    responseOthers++;
  }

  
  public static synchronized Timer.Context getContext() {
   return SpikeWorker.timerRegistry.timer("request").time();
  }
  
  
  public SpikeWorker(JobConfig config) {
    this.config = config;
  }

  @Override
  public Result call() throws Exception {

    if(config.inputTemplate != null){
       config.data = readFile(config.inputTemplate);
     }
    
    final CountDownLatch latch = new CountDownLatch(config.numOfThreads);
    Thread t;
    System.out.println("Test started. please wait to complete...");
    for (int i = 0; i < config.numOfThreads; i++) {
      t = new Thread(new SpikeRunner("thread_" + i, config, latch));
      t.start();
    }
    latch.await();
  
    
    ConsoleReporter reporter = ConsoleReporter
        .forRegistry(SpikeWorker.timerRegistry)
        .convertRatesTo(TimeUnit.MILLISECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS).build();
    reporter.report();

    Snapshot snapshot = SpikeWorker.timerRegistry.getTimers().get("request")
        .getSnapshot();
    Result result = new Result();
    
    result.setResponse2xx(SpikeWorker.response2xx);
    result.setResponse3xx(SpikeWorker.response3xx);
    result.setResponse4xx(SpikeWorker.response4xx);
    result.setResponse5xx(SpikeWorker.response5xx);
    result.setResponseOthers(SpikeWorker.responseOthers);

    result.setMin(snapshot.getMin());
    result.setMax(snapshot.getMax());
    return result;
  }

  //private helper - Reads data from a file ----------------------
  private String readFile(String path){
    String outData = "";
    String data;
    BufferedReader br = null;
    
    try {
      br = new BufferedReader(new FileReader(path));
      while ((data = br.readLine()) != null) {
        outData += data;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null)br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return outData;
  }
}
