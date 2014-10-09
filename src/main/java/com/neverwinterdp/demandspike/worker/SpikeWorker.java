package com.neverwinterdp.demandspike.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.neverwinterdp.demandspike.DemandSpike;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.result.Result;
import com.neverwinterdp.util.JSONSerializer;

public class SpikeWorker implements Callable<Result>, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final JobConfig config;
  public static MetricRegistry metricRegistry = new MetricRegistry();

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

  public static synchronized Timer.Context getTimerContext(String... name) {
    return SpikeWorker.metricRegistry.timer(getName(name)).time();
  }

  public static synchronized Meter getMeter(String name) {
    return SpikeWorker.metricRegistry.meter(getName(name));
  }

  public static synchronized Histogram getHistogram(String... name) {
    return SpikeWorker.metricRegistry.histogram(getName(name));
  }

  public SpikeWorker(JobConfig config) {
    this.config = config;
  }

  @Override
  public Result call() throws Exception {

    if (config.inputFile != null) {
      config.data = readFile(config.inputFile);
    }

    final CountDownLatch latch = new CountDownLatch(config.numOfThreads);
    Thread t;
    System.out.println("Test started. please wait to complete...");
    for (int i = 0; i < config.numOfThreads; i++) {
      t = new Thread(new SpikeRunner("thread_" + i, config, latch));
      t.start();
    }
    latch.await();
  
//    
//    ConsoleReporter reporter = ConsoleReporter
//        .forRegistry(SpikeWorker.metricRegistry)
//        .convertRatesTo(TimeUnit.SECONDS)
//        .convertDurationsTo(TimeUnit.MILLISECONDS).build();
//    reporter.report();
//    
//    
//    CsvReporter reporter2 = CsvReporter
//        .forRegistry(SpikeWorker.metricRegistry)
//        .build(new File("/Users/peterjeroldleslie/"));
//    reporter2.report();
    Timer timer = SpikeWorker.metricRegistry.getTimers().get(getName("responses"));
    Snapshot snapshot = timer.getSnapshot();
    Result result = new Result();
   // SpikeWorker.metricRegistry.getTimer
    
    
    
    result.setResponse2xx(SpikeWorker.response2xx);
    result.setResponse3xx(SpikeWorker.response3xx);
    result.setResponse4xx(SpikeWorker.response4xx);
    result.setResponse5xx(SpikeWorker.response5xx);
    result.setResponseOthers(SpikeWorker.responseOthers);

    result.setMin(snapshot.getMin());
    result.setMax(snapshot.getMax());
    
    result.setCount(timer.getCount());
    result.setValues(snapshot.getValues());
    result.setMax(snapshot.getMax());
    result.setMean(snapshot.getMean());
    result.setMin( snapshot.getMin());
    result.setP50( snapshot.getMedian());
    result.setP75( snapshot.get75thPercentile());
    result.setP95( snapshot.get95thPercentile());
    result.setP98( snapshot.get98thPercentile());
    result.setP99( snapshot.get99thPercentile());
    result.setP999( snapshot.get999thPercentile());
    result.setStddev( snapshot.getStdDev());
    result.setM15_rate(timer.getFifteenMinuteRate());
    result.setM1_rate(timer.getOneMinuteRate());
    result.setM5_rate(timer.getFiveMinuteRate());
    result.setMean_rate(timer.getMeanRate());
    result.setDuration_units(TimeUnit.NANOSECONDS.name());
    result.setRate_units("");
    
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
  
  private static String getName(String...name){
    return MetricRegistry.name(DemandSpike.class, name); 
  }
}
