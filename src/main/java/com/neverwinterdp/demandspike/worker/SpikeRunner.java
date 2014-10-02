package com.neverwinterdp.demandspike.worker;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.neverwinterdp.demandspike.client.Client;
import com.neverwinterdp.demandspike.client.HttpClient;
import com.neverwinterdp.demandspike.client.ResponseHandler;
import com.neverwinterdp.demandspike.commandline.SpikeEnums;
import com.neverwinterdp.demandspike.job.JobConfig;

public class SpikeRunner implements Runnable, Serializable {
  private static Logger logger;
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String taskId;
  private DataGenerator dataGenerator;
  private final String url;

  private final CountDownLatch latch;
  private final JobConfig config;


  public SpikeRunner(String taskID, JobConfig config, CountDownLatch latch) {
    logger = LoggerFactory.getLogger("DemandSpike");
    this.taskId = taskID;
    this.url = config.getTargets().get(0);
    this.config = config;
    this.latch = latch;
    this.dataGenerator = new DataGenerator();
  }
  
  public SpikeRunner(JobConfig config) {
	    logger = LoggerFactory.getLogger("DemandSpike");
	    this.url = config.getTargets().get(0);
	    this.config = config;
	    this.latch = new CountDownLatch(1);
	    this.dataGenerator = new DataGenerator();
	  }

  @Override
  public void run() {
    if (config.sendPeriod > 0) {
      // sendPeriod();
    } else {
      try {
        final CountDownLatch localLatch = new CountDownLatch(
            config.requestsPerThread);
        Client c = null;
        try {
          c = new HttpClient(url);
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
        try {
          if (!c.start()) {
            return;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        sendContinuous(c, localLatch);
        localLatch.await(10, TimeUnit.SECONDS);

        c.stop();
        latch.countDown();

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  void sendContinuous(Client c, final CountDownLatch localLatch)
      throws InterruptedException {

    final AtomicInteger bufferSize = new AtomicInteger();

    final AtomicLong requestCounter = new AtomicLong();

    DefaultFullHttpRequest request;
    HttpMethod method = null;
    if (config.method.equals(SpikeEnums.METHOD.GET)) {
      method = HttpMethod.GET;
    } else if (config.method.equals(SpikeEnums.METHOD.POST)) {
      method = HttpMethod.POST;
    }

    if (config.data != null) {
      request = c.createRequest(method, getByteBuf(config.data));
    } else {
      request = c.createRequest(method, null);
    }
    dataGenerator.setIdPrefix(taskId);
    final long stopTime = System.currentTimeMillis() + config.maxDuration;
    for (long i = 0; i < config.requestsPerThread; i++) {
      bufferSize.incrementAndGet();
      while (bufferSize.get() >= 30) {
        Thread.sleep(500);
      }
      if (config.autoGeneratorString != null
          && config.autoGeneratorString.size() > 0) {
        request = c.createRequest(method, getByteBuf(dataGenerator.next(
            config.autoGeneratorString, config.data)));
      }
      final Timer.Context cxt = SpikeWorker.getTimerContext("responses");
      final Meter meter = SpikeWorker.getMeter("requests");
      final Histogram histogram = SpikeWorker.getHistogram("response-sizes");
      
      c.sendRequest(request, new ResponseHandler() {
        @Override
        public void onResponse(HttpResponse response) {
          cxt.close();
          meter.mark();
          HttpContent content = (HttpContent) response;
          histogram.update(content.content().readableBytes());
          String json = content.content().toString(CharsetUtil.UTF_8);
          if (json.trim() != "" && json != null) {
            logger.info("Response string : " + json);
          }
          bufferSize.decrementAndGet();
          if (response.getStatus().code() >= 200
              && response.getStatus().code() < 300) {
            SpikeWorker.increaseResponse2xx();
          } else if (response.getStatus().code() >= 300
              && response.getStatus().code() < 400) {
            SpikeWorker.increaseResponse3xx();
          } else if (response.getStatus().code() >= 400
              && response.getStatus().code() < 500) {
            SpikeWorker.increaseResponse4xx();
          } else if (response.getStatus().code() >= 500
              && response.getStatus().code() < 600) {
            SpikeWorker.increaseResponse5xx();
          } else {
            SpikeWorker.increaseResponseOthers();
          }
          requestCounter.incrementAndGet();

          if (requestCounter.get() >= config.requestsPerThread) {
            localLatch.countDown();
          }
        }
      });

      if (System.currentTimeMillis() >= stopTime) {
        localLatch.countDown();
        break;
      }

    }
  }

  // private helper
  private ByteBuf getByteBuf(String data) {
    return Unpooled.wrappedBuffer(data.getBytes());
  }

}
