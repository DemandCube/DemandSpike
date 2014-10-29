package com.neverwinterdp.demandspike.worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.neverwinterdp.demandspike.DemandSpike;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.result.Result;

public class SpikeWorker implements Callable<Result>, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final JobConfig config;
  private static MetricRegistry metricRegistry = new MetricRegistry();

  public static synchronized MetricRegistry getMetricRegistry(){
    return metricRegistry;
  }
  
  public static synchronized Timer.Context getTimerContext(String... name) {
    return metricRegistry.timer(getName(name)).time();
  }

  public static synchronized Meter getMeter(String name) {
    return metricRegistry.meter(getName(name));
  }

  public static synchronized Histogram getHistogram(String... name) {
    return metricRegistry.histogram(getName(name));
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
    for (int i = 0; i < config.numOfThreads; i++) {
      t = new Thread(new SpikeRunner("thread_" + i, config, latch));
      t.start();
    }
    latch.await();

     ConsoleReporter reporter = ConsoleReporter
     .forRegistry(metricRegistry)
     .convertRatesTo(TimeUnit.SECONDS)
     .convertDurationsTo(TimeUnit.MILLISECONDS).build();
     reporter.report();
   
    Timer timer = metricRegistry.getTimers().get(getName("responses"));
    Snapshot snapshot = timer.getSnapshot();
    Result result = new Result();
    
    result.setResponse2xx(metricRegistry.counter("2xx").getCount());
    result.setResponse3xx(metricRegistry.counter("3xx").getCount());
    result.setResponse4xx(metricRegistry.counter("4xx").getCount());
    result.setResponse5xx(metricRegistry.counter("5xx").getCount());
    result.setResponseOthers(metricRegistry.counter("others").getCount());

    result.setMin(snapshot.getMin());
    result.setMax(snapshot.getMax());

    result.setCount(timer.getCount());
    result.setValues(snapshot.getValues());
    result.setMax(snapshot.getMax());
    result.setMean(snapshot.getMean());
    result.setMin(snapshot.getMin());
    result.setP50(snapshot.getMedian());
    result.setP75(snapshot.get75thPercentile());
    result.setP95(snapshot.get95thPercentile());
    result.setP98(snapshot.get98thPercentile());
    result.setP99(snapshot.get99thPercentile());
    result.setP999(snapshot.get999thPercentile());
    result.setStddev(snapshot.getStdDev());
    result.setM15_rate(timer.getFifteenMinuteRate());
    result.setM1_rate(timer.getOneMinuteRate());
    result.setM5_rate(timer.getFiveMinuteRate());
    result.setMean_rate(timer.getMeanRate());
    result.setDuration_units(TimeUnit.NANOSECONDS.name());
    result.setRate_units("messages/second");

    return result;
  }

  // private helper - Reads data from a file ----------------------
  private String readFile(String path) {
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
        if (br != null)
          br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return outData;
  }

  private static String getName(String... name) {
    return MetricRegistry.name(DemandSpike.class, name);
  }
}
