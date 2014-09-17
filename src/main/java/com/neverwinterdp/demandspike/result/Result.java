package com.neverwinterdp.demandspike.result;

import java.io.Serializable;

public class Result implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private int response2xx = 0;
  private int response3xx = 0;
  private int response4xx = 0;
  private int response5xx = 0;
  private int responseOthers = 0;
  private long count;
  private long max;
  private double mean;
  private long min;
  private double p50;
  private double p75;
  private double p95;
  private double p98;
  private double p99;
  private double p999;
  private double stddev;
  private double m15_rate;
  private double m1_rate;
  private double m5_rate;
  private double mean_rate;
  private String duration_units;
  private String rate_units;
  private long[] values;

  public int getResponse2xx() {
    return response2xx;
  }

  public void setResponse2xx(int response2xx) {
    this.response2xx = response2xx;
  }

  public int getResponse3xx() {
    return response3xx;
  }

  public void setResponse3xx(int response3xx) {
    this.response3xx = response3xx;
  }

  public int getResponse4xx() {
    return response4xx;
  }

  public void setResponse4xx(int response4xx) {
    this.response4xx = response4xx;
  }

  public int getResponse5xx() {
    return response5xx;
  }

  public void setResponse5xx(int response5xx) {
    this.response5xx = response5xx;
  }

  public int getResponseOthers() {
    return responseOthers;
  }

  public void setResponseOthers(int responseOthers) {
    this.responseOthers = responseOthers;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public long getMax() {
    return max;
  }

  public void setMax(long max) {
    this.max = max;
  }

  public double getMean() {
    return mean;
  }

  public void setMean(double mean) {
    this.mean = mean;
  }

  public long getMin() {
    return min;
  }

  public void setMin(long min) {
    this.min = min;
  }

  public double getP50() {
    return p50;
  }

  public void setP50(double p50) {
    this.p50 = p50;
  }

  public double getP75() {
    return p75;
  }

  public void setP75(double p75) {
    this.p75 = p75;
  }

  public double getP95() {
    return p95;
  }

  public void setP95(double p95) {
    this.p95 = p95;
  }

  public double getP98() {
    return p98;
  }

  public void setP98(double p98) {
    this.p98 = p98;
  }

  public double getP99() {
    return p99;
  }

  public void setP99(double p99) {
    this.p99 = p99;
  }

  public double getP999() {
    return p999;
  }

  public void setP999(double p999) {
    this.p999 = p999;
  }

  public double getStddev() {
    return stddev;
  }

  public void setStddev(double stddev) {
    this.stddev = stddev;
  }

  public double getM15_rate() {
    return m15_rate;
  }

  public void setM15_rate(double m15_rate) {
    this.m15_rate = m15_rate;
  }

  public double getM1_rate() {
    return m1_rate;
  }

  public void setM1_rate(double m1_rate) {
    this.m1_rate = m1_rate;
  }

  public double getM5_rate() {
    return m5_rate;
  }

  public void setM5_rate(double m5_rate) {
    this.m5_rate = m5_rate;
  }

  public double getMean_rate() {
    return mean_rate;
  }

  public void setMean_rate(double mean_rate) {
    this.mean_rate = mean_rate;
  }

  public String getDuration_units() {
    return duration_units;
  }

  public void setDuration_units(String duration_units) {
    this.duration_units = duration_units;
  }

  public String getRate_units() {
    return rate_units;
  }

  public void setRate_units(String rate_units) {
    this.rate_units = rate_units;
  }

  public long[] getValues() {
    return values;
  }

  public void setValues(long[] values) {
    this.values = values;
  }

}
