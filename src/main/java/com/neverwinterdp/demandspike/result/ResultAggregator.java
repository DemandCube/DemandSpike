package com.neverwinterdp.demandspike.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.neverwinterdp.util.text.TabularPrinter;

public class ResultAggregator {
  Result result;

  long[] values = new long[0];

  public ResultAggregator() {
    this.result = new Result();
  }

  public ResultAggregator(Result result) {
    this.result = result;
  }

  public Result getResult() {
    return result;
  }

  public Result merge(List<Result> results) {
    // merge responses
    int size = results.size();
    int response2xx = 0;
    int response3xx = 0;
    int response4xx = 0;
    int response5xx = 0;
    int responseOthers = 0;
    int count = 0;
    for (Result r : results) {
      response2xx += r.getResponse2xx();
      response3xx += r.getResponse3xx();
      response4xx += r.getResponse4xx();
      response5xx += r.getResponse5xx();
      responseOthers += r.getResponseOthers();
      count += r.getCount();
    }
    result.setResponse2xx(response2xx);
    result.setResponse3xx(response3xx);
    result.setResponse4xx(response4xx);
    result.setResponse5xx(response5xx);
    result.setResponseOthers(responseOthers);
    result.setCount(count);

    // merge mean
    double mean = 0;
    for (Result r : results) {
      mean += r.getMean();
    }
    result.setMean(mean / size);

    // merge stddev
    double stddev = 0;
    for (Result r : results) {
      stddev += r.getStddev();
    }
    result.setStddev(stddev / size);

    // merger min rates , percentiles and mean_rate
    double m15_rate = 0;
    double m1_rate = 0;
    double m5_rate = 0;
    double p50 = 0;
    double p75 = 0;
    double p95 = 0;
    double p98 = 0;
    double p99 = 0;
    double p999 = 0;
    double mean_rate = 0;

    for (Result r : results) {
      m15_rate += r.getM15_rate();
      m1_rate += r.getM1_rate();
      m5_rate += r.getM5_rate();
      p50 += r.getP50();
      p75 += r.getP75();
      p95 += r.getP95();
      p98 += r.getP98();
      p99 += r.getP99();
      p999 += r.getP999();
      mean_rate += r.getMean_rate();
    }
    result.setM1_rate(m1_rate / size);
    result.setM5_rate(m5_rate / size);
    result.setM15_rate(m15_rate / size);
    result.setP50(p50 / size);
    result.setP75(p75 / size);
    result.setP95(p95 / size);
    result.setP98(p98 / size);
    result.setP99(p99 / size);
    result.setP999(p999 / size);
    result.setMean_rate(mean_rate / size);

    // min and max
    List<Long> maxList = new ArrayList<Long>();
    List<Long> minList = new ArrayList<Long>();
    for (Result r : results) {
      maxList.add(r.getMax());
      minList.add(r.getMin());
    }
    result.setMin(Collections.min(minList));
    result.setMax(Collections.max(maxList));

    return this.result;
  }

  public void printResult() {
    int[] resultColWidth = { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
    TabularPrinter resultPrinter = new TabularPrinter(System.out, resultColWidth);
    resultPrinter.header("count", "max", "mean", "min", "p50", "p75", "p95", "p98", "p99", "p999", "stddev",
        "m15_rate", "m1_rate", "m5_rate", "mean_rate", "response2xx", "response3xx", "response4xx", "response5xx",
        "responseOthers");
    resultPrinter.row(result.getCount(), result.getMax(), result.getMean(), result.getMin(), result.getP50(),
        result.getP75(), result.getP95(), result.getP98(), result.getP99(), result.getP999(), result.getStddev(),
        result.getM15_rate(), result.getM1_rate(), result.getM5_rate(), result.getMean_rate(), result.getResponse2xx(),
        result.getResponse3xx(), result.getResponse4xx(), result.getResponse5xx(), result.getResponseOthers());
  }
}
