package com.neverwinterdp.demandspike.yarn.master;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.neverwinterdp.netty.rpc.ping.protocol.Ping;
import com.neverwinterdp.netty.rpc.ping.protocol.PingService;
import com.neverwinterdp.netty.rpc.ping.protocol.Pong;
import com.neverwinterdp.netty.rpc.server.RPCServer;

public class FailureCollector extends Thread {

  RPCServer server;
  long totalFailure;

  public FailureCollector(RPCServer server) {
    this.server = server;

  }

  @Override
  public void run() {
    server.getServiceRegistry().register(PingService.newReflectiveService(new PingServImpl()));

    while (true) {
      System.out.println("totalFailure " + totalFailure);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }

  }

  public class PingServImpl implements PingService.Interface {

    @Override
    public void ping(RpcController controller, Ping request, RpcCallback<Pong> done) {
      totalFailure += Long.parseLong(request.getMessage());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
      if (controller.isCanceled()) {
        done.run(null);
        return;
      }
      Pong pong = Pong.newBuilder().setMessage("failuire received").build();
      done.run(pong);
    }
  }
}
