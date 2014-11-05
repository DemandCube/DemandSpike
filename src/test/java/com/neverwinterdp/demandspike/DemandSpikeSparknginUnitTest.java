package com.neverwinterdp.demandspike;

import org.junit.After;
import org.junit.Before;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.util.JSONSerializer;

public class DemandSpikeSparknginUnitTest {
  //TODO: check the SparknginHttpConnectorServerUnitTest to init the sparkngin server
  //TODO: init the DemandSpikeClient
  
  @Before
  public void setup() {
  }
  
  @After
  public void teardown() {
  }
  
  public void testSend() {
    Message message = new Message() ;
    //TODO: check SparknginHttpConnectorServerUnitTest  to init the message object
    byte[] jsonData = JSONSerializer.INSTANCE.toBytes(message) ;
    //TODO: use the client post to send the message data to the sparkngin
  }
  
}
