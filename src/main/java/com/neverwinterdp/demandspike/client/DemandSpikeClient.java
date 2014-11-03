package com.neverwinterdp.demandspike.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.QueryStringEncoder;

import java.nio.channels.ClosedChannelException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import com.neverwinterdp.netty.http.client.AsyncHttpClient;
import com.neverwinterdp.netty.http.client.ResponseHandler;

public class DemandSpikeClient {
  private String path  ;
  private AsyncHttpClient client ;
  private Map<String, Request> waitingRequests ;
  private int bufferSize ;
  private Monitor monitor = new Monitor();
  
  public DemandSpikeClient(String host, int port, String path, int bufferSize, boolean connect) throws Exception {
    client = new AsyncHttpClient(host, port, new ResponseHandlerImpl(), connect) ;
    this.path = path ;
    this.bufferSize = bufferSize ;
    waitingRequests = new ConcurrentHashMap<String, Request>() ;
  }
  
  public boolean isConnected() { return client.isConnected() ; }
  
  public void setNotConnected() { client.setNotConnected(); }
  
  public boolean connect() throws Exception {
    return client.connect();
  }
  
  public boolean connect(long timeout, long tryPeriod) throws Exception {
    return client.connect(timeout, tryPeriod);
  }
  
  public Request getWaitingRequest(String key) {
    return waitingRequests.get(key) ;
  }
  
  public Monitor getMonitor() { return this.monitor ; }
  
  public void sendGet(String key, Map<String, String> params, long timeout) throws Exception {
    Request request = new Request("GET", key, params, null) ;
    MethodMonitor methodMonitor = monitor.getMethodMonitor("GET");
    methodMonitor.incrCount(); 
    synchronized(waitingRequests) {
      if(waitingRequests.size() >= bufferSize) {
        waitingRequests.wait(timeout);
        if(waitingRequests.size() >= bufferSize) {
          methodMonitor.incrClientLimitTimeoutCount(); ;
          throw new TimeoutException("fail to send the message in " + timeout + "ms") ;
        }
      }
      try {
        QueryStringEncoder encoder = new QueryStringEncoder(path);
        if(params != null) {
          for(Map.Entry<String, String> entry : params.entrySet()) {
            encoder.addParam(entry.getKey(), entry.getValue());
          }
        }
        ChannelFuture future = client.get(encoder.toString());
        handleFuture(future, request) ;
      } catch(Exception ex) {
        methodMonitor.incrUnknownErrorCount();
        throw ex ;
      }
    }
  }
  
  public void sendPost(String key, byte[] data, long timeout) throws Exception {
    Request request = new Request("POST", key, null, data) ;
    MethodMonitor methodMonitor = monitor.getMethodMonitor(request.getMethod()) ;
    methodMonitor.incrCount();
    synchronized(waitingRequests) {
      if(waitingRequests.size() >= bufferSize) {
        waitingRequests.wait(timeout);
        if(waitingRequests.size() >= bufferSize) {
          methodMonitor.incrClientLimitTimeoutCount();
          throw new TimeoutException("fail to send the message in " + timeout + "ms") ;
        }
      }
      try {
        ChannelFuture future = client.post(path, data);
        handleFuture(future, request) ;
      } catch(Exception ex) {
        methodMonitor.incrUnknownErrorCount();
        throw ex ;
      }
    }
  }
  
  public Map<String, Request> getWaitingRequests() { return this.waitingRequests ; }
  
  /**
   * This method will wait and check periodically to make sure that all the request to get the response
   */
  public void waitAndClose(long waitTime) throws InterruptedException {
    if(waitingRequests.size() > 0) { 
      synchronized(waitingRequests) {
        long stopTime = System.currentTimeMillis() + waitTime ;
        while(stopTime > System.currentTimeMillis() && waitingRequests.size() > 0) {
          long timeToWait = stopTime - System.currentTimeMillis() ;
          waitingRequests.wait(timeToWait);
        }
      }
    }
    for(Request sel : waitingRequests.values()) {
      MethodMonitor mMonitor = monitor.getMethodMonitor(sel.getMethod()) ;
      mMonitor.incrTimeoutExceptionCount();
    }
    close();
  }
  
  public void close() {
    if(waitingRequests.size() > 0) {
      System.err.println("There are " + waitingRequests.size() + " messages waitting for ack") ;
    }
    client.close(); 
  }
  
  private void handleFuture(ChannelFuture future, final Request request) {
    waitingRequests.put(request.getKey(), request) ;
    
    //Be aware, this operation complete is called when the client the delivered sucessfully to the remote server.
    //Not when the client get the ack or response
    future.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        Throwable cause = future.cause() ;
        if(cause != null) {
          MethodMonitor mMonitor = monitor.getMethodMonitor(request.getMethod()) ;
          if(cause instanceof ClosedChannelException) {
            mMonitor.incrCloseChannelExceptionCount();
          } else if(cause instanceof ConnectTimeoutException) {
            mMonitor.incConnectionTimeoutExceptionCount(); 
          }
        }
      }
    });
  }
  
  /**
   * This method should be overrided, convert the return response data into a structure object, process and verify 
   * the response and find the Request of this response according to the key.
   * @param data
   * @return
   */
  public Request handleResponse(HttpResponse response, byte[] data) {
    return null ;
  }
  
  class ResponseHandlerImpl implements ResponseHandler {
    public void onResponse(HttpResponse response) {
      if(response instanceof HttpContent) {
        HttpContent content = (HttpContent) response;
        ByteBuf byteBuf = content.content() ;
        byte[] data = new byte[byteBuf.readableBytes()] ;
        byteBuf.readBytes(data) ;
        Request request = handleResponse(response, data) ;
        if(request == null) {
          Iterator<Map.Entry<String, Request>> iterator = waitingRequests.entrySet().iterator() ;
          iterator.next();
          iterator.remove(); 
        } else {
          MethodMonitor mMonitor = monitor.getMethodMonitor(request.getMethod()) ;
          mMonitor.incrResponseCount();
          synchronized(waitingRequests) {
            waitingRequests.remove(request.getKey()) ;
            waitingRequests.notify();
          }
        }
      }
    }
  }
  
  static public class Request {
    private String method ;
    private String key;
    private Map<String, String> params ;
    private byte[]   data ;
    private long sendTime ;
    
    public Request(String method, String key, Map<String, String> params, byte[] data) {
      this.method = method ;
      this.key = key ;
      this.params = params ;
      this.data = data ;
      this.sendTime = System.currentTimeMillis();
    }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public Map<String, String> getParams() { return params; }
    public void setParams(Map<String, String> params) { this.params = params; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    
    public long getSendTime() { return this.sendTime ; }
    public void setSendTime(long time) { this.sendTime = time ; }
  }
}
