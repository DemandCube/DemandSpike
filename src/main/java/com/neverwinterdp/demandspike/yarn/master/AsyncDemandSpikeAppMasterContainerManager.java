package com.neverwinterdp.demandspike.yarn.master;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.result.Result;
import com.neverwinterdp.demandspike.result.ResultAggregator;
import com.neverwinterdp.demandspike.util.Header;
import com.neverwinterdp.demandspike.yarn.worker.DemandSpikeAppWorker;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.ipc.ReportData;
import com.neverwinterdp.hadoop.yarn.app.ipc.ReportHandler;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterContainerManager;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerState;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.text.TabularPrinter;

public class AsyncDemandSpikeAppMasterContainerManager implements
    AppMasterContainerManager {
  protected static final Logger LOGGER = LoggerFactory
      .getLogger(AsyncDemandSpikeAppMasterContainerManager.class);
  ConcurrentMap<Integer, ReportData> map = new ConcurrentHashMap();

  public void onInit(AppMaster appMaster) {
    appMaster.getIPCServiceServer().register("demandspike",
        new ReportHandler() {
          @Override
          public void onReport(AppMaster appMaster,
              AppWorkerContainerInfo containerInfo, ReportData data) {
            synchronized (map) {
              map.put(containerInfo.getContainerId(), data);
            }
          }
        });
  }

  public void onRequestContainer(AppMaster appMaster) {
    LOGGER.info("Start onRequestContainer(AppMaster appMaster)");
    AppInfo appConfig = appMaster.getAppInfo();
    appConfig.setWorkerByType(DemandSpikeAppWorker.class);
    System.out.println("AppInfo: "
        + JSONSerializer.INSTANCE.toString(appConfig));
    for (int i = 0; i < appConfig.appNumOfWorkers; i++) {
      ContainerRequest containerReq = appMaster
          .createContainerRequest(0/* priority */, appConfig.workerNumOfCore,
              appConfig.workerMaxMemory);
      appMaster.add(containerReq);
    }
    LOGGER.info("Finish onRequestContainer(AppMaster appMaster)");
  }

  public void onAllocatedContainer(AppMaster appMaster, Container container) {
    try {
      LOGGER.info("onAllocateContainer(...), container id = "
          + container.getId());
      appMaster.startContainer(container);
    } catch (Exception ex) {
      LOGGER.error("Start container error", ex);
    }
  }

  public void onCompleteContainer(AppMaster master, ContainerStatus status,
      AppWorkerContainerInfo containerInfo) {
    try {
      AppInfo appConfig = master.getAppInfo();
      AppMasterMonitor monitor = master.getAppMonitor();
      int complete = monitor.getCompletedContainerCount().intValue();
      master.getAMRMClient().allocate(
          complete / (float) appConfig.appNumOfWorkers);
    } catch (Exception e) {
      LOGGER.error("onCompleteContainer() report error", e);
    }
    LOGGER.info("on complete container " + status.getContainerId());
  }

  public void onFailedContainer(AppMaster master, ContainerStatus status,
      AppWorkerContainerInfo containerInfo) {
    LOGGER.info("on failed container " + status.getContainerId());
  }

  public void onShutdownRequest(AppMaster appMaster) {
    LOGGER.info("Start onShutdownRequest(AppMaster appMaster)");
    synchronized (this) {
      this.notify();
    }
    LOGGER.info("Finish onShutdownRequest(AppMaster appMaster)");
  }

  public void onExit(AppMaster appMaster) {
    LOGGER.info("Start onExit(AppMaster appMaster)");
    AppMasterMonitor appMonitor = appMaster.getAppMonitor();
    AppWorkerContainerInfo[] info = appMonitor.getContainerInfos();
    int[] colWidth = { 20, 20, 20, 20 };
    TabularPrinter printer = new TabularPrinter(System.out, colWidth);
    printer.header("Id", "Progress", "Error", "State");
    for (AppWorkerContainerInfo sel : info) {
      printer.row(sel.getContainerId(), sel.getProgressStatus().getProgress(),
          sel.getProgressStatus().getError() != null, sel.getProgressStatus()
              .getContainerState());
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");

    List<Result> results = new ArrayList<Result>();

    for (ConcurrentMap.Entry<Integer, ReportData> entry : map.entrySet()) {
      results.add(JSONSerializer.INSTANCE.fromString(entry.getValue()
          .getJsonData(), Result.class));
      System.out.println(entry.getValue().getJsonData());
    }
    ResultAggregator resultAggregator = new ResultAggregator(new Result());
    resultAggregator.merge(results);
    resultAggregator.printResult();
    Result finalResult = resultAggregator.getResult();

    Configuration conf = appMaster.getConfiguration();
    Path tmpDir = new Path("temp");
    SequenceFile.Writer writer = null;
    try {
      final FileSystem fs = FileSystem.get(conf);
      if (!fs.mkdirs(tmpDir)) {
        throw new IOException("Cannot create input directory "
            + tmpDir.getName());
      }
      Path outFile = new Path(tmpDir, "reduce-out");
      writer = SequenceFile.createWriter(fs, conf, outFile, Text.class,
          Text.class, CompressionType.NONE);
      Field[] fields = Result.class.getDeclaredFields();

      for (Field field : fields) {
        if (field.isAnnotationPresent(Header.class)) {
          field.setAccessible(true);
          Header column = field.getAnnotation(Header.class);
          Object o = null;
          try {
            o = field.get(finalResult);
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
          if (o == null) {
            o = "";
          }
          writer.append(new Text(column.name()), new Text(o.toString()));
        }
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Configuration conf1 = new Configuration(appMaster.getConfiguration());
    Path tmpDir1 = new Path("temp");
    Path outFile1 = new Path(tmpDir1, "reduce-out");
    FileSystem fileSys = null;

    Text key = new Text();
    Text value = new Text();
    SequenceFile.Reader reader = null;
    try {
      fileSys = FileSystem.get(conf1);
      reader = new SequenceFile.Reader(fileSys, outFile1, conf1);
      while (reader.next(key, value)) {
        System.out.println(key.toString() + "," + value.toString());
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    try {
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void waitForComplete(AppMaster appMaster) {
    LOGGER.info("Start waitForComplete(AppMaster appMaster)");
    AppInfo appConfig = appMaster.getAppInfo();
    try {
      boolean finished = false;
      while (!finished) {
        synchronized (this) {
          this.wait(500);
        }
        AppMasterMonitor monitor = appMaster.getAppMonitor();
        AppWorkerContainerInfo[] cinfos = monitor.getContainerInfos();
        if (cinfos.length < appConfig.appNumOfWorkers)
          continue;
        finished = true;
        for (AppWorkerContainerInfo sel : cinfos) {
          if (!sel.getProgressStatus().getContainerState()
              .equals(AppWorkerContainerState.FINISHED)) {
            finished = false;
            break;
          }
        }
      }
    } catch (InterruptedException ex) {
      LOGGER.error("wait interruption: ", ex);
    }
    LOGGER.info("Finish waitForComplete(AppMaster appMaster)");
  }
}