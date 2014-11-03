package com.neverwinterdp.demandspike;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Map;
import java.util.Random;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.RouteHandlerGeneric;
import com.neverwinterdp.util.JSONSerializer;

public class HttpServerSimulator {
  HttpServer server;
  HttpServerSimulator.MessageHandler handler ;
  
  public HttpServerSimulator(int numOfWorkers, int minProcessTime, int maxProcessTime) {
    server = new HttpServer();
    handler = new MessageHandler();
    server.add("/message", handler);
    server.setPort(7080);
    server.setNumberOfWorkers(numOfWorkers);
    handler.setMinProcessTime(minProcessTime);
    handler.setMaxProcessTime(maxProcessTime);
  }
  
  public HttpServerSimulator start() {
    server.startAsDeamon();
    return this ;
  }
  
  public HttpServerSimulator shutdown() {
    server.shutdown();
    return this ;
  }
  
  static public byte[] newPayload(String key, Map<String, String> params, byte[] data) {
    Payload payload = new Payload(key, params, data) ;
    return JSONSerializer.INSTANCE.toBytes(payload) ;
  }
  
  static public class Payload {
    private String key;
    private Map<String, String> params ;
    private byte[]   data ;
    
    public Payload() {}
    
    public Payload(String key, Map<String, String> params, byte[] data) {
      this.key = key ;
      this.params = params ;
      this.data = data ;
    }

    public String getKey() { return key; }
    public void setKey(String key) {
      this.key = key;
    }

    public Map<String, String> getParams() { return params; }
    public void setParams(Map<String, String> params) {
      this.params = params;
    }

    public byte[] getData() { return data; }
    public void setData(byte[] data) {
      this.data = data;
    }
  }
  
  static public class MessageHandler extends RouteHandlerGeneric {
    int postCount = 0;
    int getCount = 0;
    
    int minProcessTime = 0 ;
    int maxProcessTime = 0 ;
    
    Random random = new Random() ;
    
    public void setMinProcessTime(int min) { 
      this.minProcessTime = min ;
      if(maxProcessTime < minProcessTime) maxProcessTime = minProcessTime ;
    }
    
    public void setMaxProcessTime(int max) { 
      this.maxProcessTime = max ;
      if(minProcessTime > maxProcessTime) minProcessTime = maxProcessTime ;
    }
    
    @Override
    protected void doPost(ChannelHandlerContext ctx, HttpRequest httpReq) {
      postCount++;
      processTime() ;
      FullHttpRequest req = (FullHttpRequest) httpReq ;
      ByteBuf byteBuf = req.content() ;
      byte[] data = new byte[byteBuf.readableBytes()] ;
      byteBuf.readBytes(data) ;
      byteBuf.release() ;
      Payload payload = JSONSerializer.INSTANCE.fromBytes(data, Payload.class) ;
      writeJSON(ctx, httpReq, payload) ;
    }
    
    @Override
    protected void doGet(ChannelHandlerContext ctx, HttpRequest httpReq) {
      getCount++;
      processTime() ;
      QueryStringDecoder reqDecoder = new QueryStringDecoder(httpReq.getUri()) ;
      String data = reqDecoder.parameters().get("data").get(0) ;
      Payload payload = JSONSerializer.INSTANCE.fromString(data, Payload.class) ;
      writeJSON(ctx, httpReq, payload) ;
      this.writeContent(ctx, httpReq, "do get", "text/plain");
    }
    
    private void processTime() {
      try {
        int processTime = minProcessTime + random.nextInt(maxProcessTime - minProcessTime) ;
        //Simulate the process time to return the response between minProcessTime - maxProcessTime
        Thread.sleep(random.nextInt(processTime));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}