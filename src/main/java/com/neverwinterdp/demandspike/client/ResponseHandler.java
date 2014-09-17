package com.neverwinterdp.demandspike.client;

import io.netty.handler.codec.http.HttpResponse;

public interface ResponseHandler {
  public void onResponse(HttpResponse response);
}
