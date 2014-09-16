package com.neverwinterdp.demandspike.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {
  private ResponseHandler responseHandler;

  public HttpClientHandler(ResponseHandler handler) {
    this.responseHandler = handler;
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
      throws Exception {
    if (!(msg instanceof HttpResponse))
      return;
    responseHandler.onResponse((HttpResponse) msg);
  }
}
