package com.neverwinterdp.demandspike.yarn.worker;

import com.google.protobuf.RpcCallback;
import com.neverwinterdp.demandspike.client.HttpClient;
import com.neverwinterdp.netty.rpc.client.DefaultClientRPCController;
import com.neverwinterdp.netty.rpc.client.RPCClient;
import com.neverwinterdp.netty.rpc.ping.protocol.Ping;
import com.neverwinterdp.netty.rpc.ping.protocol.PingService;
import com.neverwinterdp.netty.rpc.ping.protocol.Pong;

public class FailureSubmitter extends Thread {

  static protected RPCClient client;

  public FailureSubmitter() {
    try {
      client = new RPCClient();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      PingService.Interface nonBlockingService = PingService.newStub(client.getRPCChannel());
      RpcCallback<Pong> done = new RpcCallback<Pong>() {
        public void run(Pong pong) {

        }
      };
      while (true) {

        Ping.Builder pingB = Ping.newBuilder();
        pingB.setMessage(+HttpClient.failures + "");
        nonBlockingService.ping(new DefaultClientRPCController(), pingB.build(), done);

        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

}
