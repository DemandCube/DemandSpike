package com.neverwinterdp.demandspike.client;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.neverwinterdp.demandspike.worker.SpikeWorker;

public class HttpResponseHandler implements ResponseHandler {
	
	private Timer.Context cxt;
	private Meter meter;
	private  Histogram histogram;
			
	@Override
	public void onResponse(HttpResponse response) {
		cxt.close();
		meter.mark();
		HttpContent content = (HttpContent) response;
		histogram.update(content.content().readableBytes());
		/*String json = content.content().toString(CharsetUtil.UTF_8);
		if (json.trim() != "" && json != null) {
			logger.info("Response string : " + json);
		}*/
		if (response.getStatus().code() >= 200
				&& response.getStatus().code() < 300) {
			SpikeWorker.getMetricRegistry().counter("2xx").inc();
		} else if (response.getStatus().code() >= 300
				&& response.getStatus().code() < 400) {
			SpikeWorker.getMetricRegistry().counter("3xx").inc();
		} else if (response.getStatus().code() >= 400
				&& response.getStatus().code() < 500) {
			SpikeWorker.getMetricRegistry().counter("4xx").inc();
		} else if (response.getStatus().code() >= 500
				&& response.getStatus().code() < 600) {
			SpikeWorker.getMetricRegistry().counter("5xx").inc();
		} else {
			SpikeWorker.getMetricRegistry().counter("others").inc();
		}

	}
	public void setMeters(Timer.Context cxt, Meter meter, Histogram histogram){
		this.cxt = cxt;
		this.meter = meter;
		this.histogram = histogram; 
	}
}
