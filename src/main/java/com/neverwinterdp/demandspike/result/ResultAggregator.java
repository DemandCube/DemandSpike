package com.neverwinterdp.demandspike.result;

import java.util.List;

public class ResultAggregator {
  Result result;
  
  long[] values = new long[0];
  
  public ResultAggregator( Result result){
    this.result = result;
  }
  public Result getResult() {
    return result;
  }
  public Result merge(List<Result> results) {

    int size = results.size();
    //merge response
    int response2xx = 0;
    int response3xx = 0;
    int response4xx = 0;
    int response5xx = 0;
    int responseOthers = 0;
    for (Result r : results) {
      response2xx += r.getResponse2xx();
      response3xx += r.getResponse3xx();
      response4xx += r.getResponse4xx();
      response5xx += r.getResponse5xx();
      responseOthers += r.getResponseOthers();
    }
    result.setResponse2xx(response2xx);
    result.setResponse3xx(response3xx);
    result.setResponse4xx(response4xx);
    result.setResponse5xx(response5xx);
    result.setResponseOthers(responseOthers);
    
    //merge mean
    double mean = 0;
    for (Result r : results) {
      mean += r.getMean();
    }
    result.setMean(mean / size);
    
    //merge stddev
    double stddev = 0;
    for (Result r : results) {
      stddev += r.getStddev();
    }
    result.setStddev(stddev / size);
    
    //merger min rates
    double m15_rate = 0;
    double m1_rate = 0;
    double m5_rate = 0;
    for (Result r : results) {
      m15_rate += r.getM15_rate();
      m1_rate += r.getM1_rate();
      m5_rate += r.getM5_rate();
    }
    result.setM1_rate(m1_rate / size);
    result.setM5_rate(m5_rate / size);
    result.setM15_rate(m15_rate / size);
    
    
    
    
    return this.result;
  }
  
}
