package com.neverwinterdp.demandspike.yarn.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.result.Result;
import com.neverwinterdp.demandspike.worker.SpikeWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;

public class DemandSpikeAppWorker implements AppWorker {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeAppWorker.class);

  public void run(AppWorkerContainer appContainer) throws Exception {
    JobConfig config = new JobConfig(appContainer.getConfig().yarnConf);

    ExecutorService executor = Executors.newCachedThreadPool();
    SpikeWorker spikeWorker = new SpikeWorker(config);
    Future<Result> future = executor.submit(spikeWorker);
    FailureSubmitter failureSubmitter = new FailureSubmitter();
    failureSubmitter.start();
    /*ReportData data = new ReportData(appContainer.getConfig().getAppWorkerContainerId()+"",future.get());
    appContainer.getAppMasterRPC().report("demandspike",
        appContainer.getConfig().getAppWorkerContainerId(), data);*/
    

  }
}
