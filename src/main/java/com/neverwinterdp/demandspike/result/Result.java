package com.neverwinterdp.demandspike.result;

import java.io.Serializable;

import com.neverwinterdp.demandspike.util.Header;

public class Result implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Header(name = "2XX_Response")
  private long response2xx = 0;
  @Header(name = "3XX_Response")
  private long response3xx = 0;
  @Header(name = "4XX_Response")
  private long response4xx = 0;
  @Header(name = "5XX_Response")
  private long response5xx = 0;
  @Header(name = "Other_Response")
  private long responseOthers = 0;
  @Header(name = "count")
  private long count;
  @Header(name = "max")
  private long max;
  @Header(name = "mean")
  private double mean;
  @Header(name = "min")
  private long min;
  @Header(name = "p50")
  private double p50;
  @Header(name = "p75")
  private double p75;
  @Header(name = "p95")
  private double p95;
  @Header(name = "p98")
  private double p98;
  @Header(name = "p99")
  private double p99;
  @Header(name = "p999")
  private double p999;
  @Header(name = "stddev")
  private double stddev;
  @Header(name = "m15_rate")
  private double m15_rate;
  @Header(name = "m1_rate")
  private double m1_rate;
  @Header(name = "m5_rate")
  private double m5_rate;
  @Header(name = "mean_rate")
  private double mean_rate;
  @Header(name = "duration_units")
  private String duration_units;
  @Header(name = "rate_units", enable = false)
  private String rate_units;
  @Header(name = "values", enable = false)
  private long[] values;

  public long getResponse2xx() {
    return response2xx;
  }

  public void setResponse2xx(long response2xx) {
    this.response2xx = response2xx;
  }

  public long getResponse3xx() {
    return response3xx;
  }

  public void setResponse3xx(long response3xx) {
    this.response3xx = response3xx;
  }

  public long getResponse4xx() {
    return response4xx;
  }

  public void setResponse4xx(long response4xx) {
    this.response4xx = response4xx;
  }

  public long getResponse5xx() {
    return response5xx;
  }

  public void setResponse5xx(long response5xx) {
    this.response5xx = response5xx;
  }

  public long getResponseOthers() {
    return responseOthers;
  }

  public void setResponseOthers(long responseOthers) {
    this.responseOthers = responseOthers;
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
