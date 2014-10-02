package com.neverwinterdp.demandspike.worker;

import java.util.List;

public class DataGenerator {
  private long    idTracker = 0;
  private String  idPrefix  = "";
  
  public void setIdPrefix(String idPrefix) {
    this.idPrefix = idPrefix ;
  }
  
  public String next(List<String> stringsToReplace, String data) {
    idTracker++ ;
    
    for(String str: stringsToReplace){
      data = data.replaceAll(str, this.idPrefix+"_"+idTracker);
    }
    return data ;
  }
}
