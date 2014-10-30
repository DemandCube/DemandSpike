package com.neverwinterdp.demandspike.cluster;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class SpikeCluster implements Callable<HazelcastInstance> {

  private final CountDownLatch latch;
  HazelcastInstance instance;

  public SpikeCluster(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public HazelcastInstance call() {
    instance = Hazelcast.newHazelcastInstance();
    latch.countDown();
    return this.instance;
  }
}
