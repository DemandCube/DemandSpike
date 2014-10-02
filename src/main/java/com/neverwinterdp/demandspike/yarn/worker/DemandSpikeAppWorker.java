package com.neverwinterdp.demandspike.yarn.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.result.Result;
import com.neverwinterdp.demandspike.worker.SpikeWorker;
import com.neverwinterdp.hadoop.yarn.app.ipc.ReportData;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;
import com.neverwinterdp.util.JSONSerializer;

public class DemandSpikeAppWorker implements AppWorker {
  protected static final Logger LOGGER = LoggerFactory
      .getLogger(DemandSpikeAppWorker.class);

  public void run(AppWorkerContainer appContainer) throws Exception {
    JobConfig config = new JobConfig(appContainer.getConfig().yarnConf);

    System.out.println("Test started. please wait to complete...");

    ReportData data = new ReportData();

    ExecutorService executor = Executors.newCachedThreadPool();

    long timeStart = System.currentTimeMillis();

    List<Result> results = new ArrayList<Result>();
    Future<Result> future = executor.submit(new SpikeWorker(config));
    long proccessingTime = System.currentTimeMillis() - timeStart;

    data.setJsonData(JSONSerializer.INSTANCE.toString(future.get()));
    appContainer.getAppMasterRPC().report("demandspike",
        appContainer.getConfig().getAppWorkerContainerId(), data);
  }
}
