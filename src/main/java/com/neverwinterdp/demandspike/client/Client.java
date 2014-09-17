package com.neverwinterdp.demandspike.client;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public interface Client {
  public boolean start() throws InterruptedException;

  public void stop();

  public void sendRequest(DefaultFullHttpRequest request,
      ResponseHandler response);

  public DefaultFullHttpRequest createRequest(HttpMethod method, ByteBuf content);
}
