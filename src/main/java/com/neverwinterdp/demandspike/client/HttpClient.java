package com.neverwinterdp.demandspike.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ClosedChannelException;
import com.neverwinterdp.demandspike.job.JobConfig;

public class HttpClient implements Client {

  private URL url;
  private Channel channel;
  private EventLoopGroup group;
  public  static long failures;
  JobConfig config;

  public HttpClient(String url, JobConfig config) throws MalformedURLException {
    this.url = new URL(url);
    this.config = config;
  }
  @Override
  public  long getFailures() {
		return failures;
	}
  @Override
  public  void initFailure() {
  	failures = 0;
  	
  }
  @Override
  public boolean start() throws InterruptedException {
    ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
      public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("codec", new HttpClientCodec());
        p.addLast("inflater", new HttpContentDecompressor());
        p.addLast("aggregator", new HttpObjectAggregator(1048576));
      }
    };

    group = new NioEventLoopGroup();
    Bootstrap b = new Bootstrap();
    b.group(group).channel(NioSocketChannel.class).handler(initializer);
    channel = b.connect(url.getHost(), url.getPort()).sync().channel();
    return this.channel.isActive();
  }

  @Override
  public void stop() {
    if (this.channel != null) {
      group.shutdownGracefully();
      this.channel.close();
    }
  }

  @Override
  public void sendRequest(DefaultFullHttpRequest request, ResponseHandler response)
  {
    if (this.channel.isActive()) {	
      if (this.channel.pipeline().get("handler") == null) {
        this.channel.pipeline().addLast("handler", new HttpClientHandler(response));
      }
      
      ChannelFuture future = this.channel.writeAndFlush(request);
      
      if(config.stopOnConditionName.equals("All")){
    	  future.addListener(new ChannelFutureListener() {

              @Override
              public void operationComplete(ChannelFuture future) throws Exception {
                  if ( !future.isSuccess() || (future.isSuccess() && future.cause()!=null )) {
                	  failures++;
                  }
              }
       });
      }
      if(config.stopOnConditionName.equals("Latency")){
    	  final long start = System.currentTimeMillis();
    	  final long maxLatency = Integer.parseInt(config.stopOnConditionValue);
    	  future.addListener(new ChannelFutureListener() {

              @Override
              public void operationComplete(ChannelFuture future) throws Exception {      	  
            	  if (System.currentTimeMillis()- start > maxLatency )
            		  failures++;
              }
       });
      }
      
      
      if(config.stopOnConditionName.equals("FailedConnexion")){
    	  future.addListener(new ChannelFutureListener() {
              @Override
              public void operationComplete(ChannelFuture future) throws Exception {      	  
            	  if ( future.cause() != null && (future.cause() instanceof ClosedChannelException  || future.cause() instanceof ConnectTimeoutException) ) {
                	  failures++;
                  }
              }
       });    
      }
      
      if(config.stopOnConditionName.equals("FailedIO")){
    	  future.addListener(new ChannelFutureListener() {
              @Override
              public void operationComplete(ChannelFuture future) throws Exception {      	  
            	  if ( future.cause() != null && (future.cause() instanceof ChannelException ) ) {
                	  failures++;
                  }
              }
       });    
      }
      
    }
  }

  public DefaultFullHttpRequest createRequest(HttpMethod method, ByteBuf content) {
    DefaultFullHttpRequest request = null;
    if (content == null) {
      request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, url.toString());
    } else {
      request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url.toString(), content);
      request.headers().set(HttpHeaders.Names.CONTENT_LENGTH,content.readableBytes());
    }
    request.headers().set(HttpHeaders.Names.HOST, url.getHost());
    request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
    return request;
  }





}
