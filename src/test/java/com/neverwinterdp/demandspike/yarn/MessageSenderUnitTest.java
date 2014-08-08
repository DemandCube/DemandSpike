package com.neverwinterdp.demandspike.yarn;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.demandspike.DemandSpikeClusterBuilder;
import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.message.Message;
import com.neverwinterdp.server.shell.Shell;

public class MessageSenderUnitTest extends AbstractMiniClusterUnitTest {
  static DemandSpikeClusterBuilder clusterBuilder ;
  static protected Shell shell ;
  
  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new DemandSpikeClusterBuilder() ;
    clusterBuilder.start() ;
    clusterBuilder.install() ;
    shell = clusterBuilder.shell ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy() ;
  }

  @Test
  public void testMessageSender() throws Exception {
    MessageSender sender = new MessageSender("127.0.0.1:7080", 10) ;
    for(int i = 0; i < 100; i++) {
      Message message = new Message("m" + i, "message " + i, true) ;
      sender.send(message, 3000);
    }
    Thread.sleep(1000);
    sender.close() ; 
    shell.execute("server metric");
  }
}