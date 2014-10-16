package com.neverwinterdp.demandspike.yarn.worker;

import org.junit.Test;

import com.neverwinterdp.demandspike.DemandSpike;
import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;


public class YarnWorkersTest extends AbstractMiniClusterUnitTest {
  @Test
public void testYarnContainers() throws Exception {
    String[] args = {"run",
        "--target","http://127.0.0.1:7080/message",
        "--protocol","HTTP",
        "--method","POST",
        "--time","30000",
        "--cLevel","2",
        "--nWorkers","4",
        "--maxRequests","100",
        "--input-file","/Users/peterjeroldleslie/data.json",
        "--message-size","512,1024,2048,4096,6144",
        "--mode","distributed",
        "--use-yarn","true",
        "--yarn-config","/Users/peterjeroldleslie/hadoop/etc/hadoop/core-site.xml",
        "--yarn-config","/Users/peterjeroldleslie/hadoop/etc/hadoop/hdfs-site.xml",
        "--yarn-config","/Users/peterjeroldleslie/hadoop/etc/hadoop/yarn-site.xml",
        "--yarn-config","/Users/peterjeroldleslie/hadoop/etc/hadoop/mapred-site.xml",};
    
    DemandSpike.main(args);
    
}

}