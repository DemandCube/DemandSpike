package com.neverwinterdp.demandspike.client;

import com.neverwinterdp.util.text.TabularFormater;

public class MonitorPrinter {
  public void print(Monitor monitor) {
    String[] header = { 
      "Method", "Count", "Response", "ClientLimitTimeout", "Timeout", "CloseChannelException", "ConnectionTimeoutException", "Unknown Error" 
    };
    TabularFormater formater = new TabularFormater(header);
    formater.setTitle("DemandSpike Monitor");
    for (MethodMonitor sel : monitor.getRequestMonitors()) {
      formater.addRow(
          sel.getMethod(),
          sel.getCount(),
          sel.getResponseCount(),
          sel.getClientLimitTimeoutCount(),
          sel.getTimeoutExceptionCount(),
          sel.getCloseChannelExceptionCount(),
          sel.getConnectionTimeoutExceptionCount(),
          sel.getUnknownErrorCount()
      );
    }
    System.out.println(formater.getFormatText());
  }
}
