package com.neverwinterdp.demandspike;

import java.util.Timer;
import java.util.TimerTask;

import com.neverwinterdp.message.Message;

public class PeriodicTask extends DemandSpikeTask {
  long sendPeriod = 100;
  
  public void setSendPeriod(long sendPeriod) {
    this.sendPeriod = sendPeriod ;
  }
  
  public void run() {
    long stopTime = System.currentTimeMillis() + maxDuration ;
    TimerTask timerTask = new TimerPeriodicTask();
    // running timer task as daemon thread
    Timer timer = new Timer(false);
    timer.scheduleAtFixedRate(timerTask, 0, sendPeriod);
    
    try {
      while(System.currentTimeMillis() < stopTime) {
        Thread.sleep(500);
      }
      timer.cancel(); 
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      onFinish() ;
    }
  }
  
  public class TimerPeriodicTask extends TimerTask {
    long messageSent ;
    public void run() {
      try {
        Message message = messageGenerator.next();
        messageDriver.send(message);
        messageSent++ ;
        if(messageSent >= maxNumOfMessage) {
          cancel() ;
        }
      } catch (Exception e) {
        logger.error("Task Error", e) ;
        this.cancel() ;
      }
    }
  }
}
