package com.neverwinterdp.demandspike.job.config;

import org.junit.Test;

import com.neverwinterdp.util.IOUtil;
import com.neverwinterdp.util.JSONSerializer;

public class DemandSpikeJobUnitTest {
  @Test
  public void testConfig() throws Exception {
    String json = IOUtil.getFileContentAsString("src/test/resources/demandspikejob.json") ;
    DemandSpikeJob job = JSONSerializer.INSTANCE.fromString(json, DemandSpikeJob.class) ;
   
    json = JSONSerializer.INSTANCE.toString(job) ;
    System.out.println(json);
  }
}
