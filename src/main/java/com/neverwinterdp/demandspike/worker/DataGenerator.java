package com.neverwinterdp.demandspike.worker;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import com.neverwinterdp.demandspike.job.JobConfig;

public class DataGenerator {
  
  private final JobConfig config;
  public static final String AUTO_INCREMENT_INT_KEY = "%AUTO-INCREMENT-INT%";
  public static final String RANDOM_STRING_KEY = "%RANDOM-STRING%";
  private static final char[] DEFAULT_CODEC = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
      .toCharArray();

  private Random random = new SecureRandom();
  private String randomData;

  // private int length;
  private long idTracker = 0;
  private String idPrefix = "";

  public void setIdPrefix(String idPrefix) {
    this.idPrefix = idPrefix;
  }

  
  public DataGenerator(JobConfig config){
    this.config = config;
  }
  public String next(List<String> stringsToReplace, String data) {
    idTracker++;
    for (String str : stringsToReplace) {
      data = data.replaceAll(str, this.idPrefix + "_" + idTracker);
    }
    return data;
  }
  
  public String next(String data){
    idTracker++;
    data = data.replaceAll(AUTO_INCREMENT_INT_KEY, this.idPrefix + "_" + idTracker);
    if (randomData == null || randomData.trim() == "") {
      randomData = generate(this.config.messageSize);
    }
    data = data.replaceAll(RANDOM_STRING_KEY, randomData);
    System.out.println(data);
    return data;
  }

  public String generate(int length) {
      System.out.println("randomData is null");
      byte[] verifierBytes = new byte[length];
      random.nextBytes(verifierBytes);
    return getAuthorizationCodeString(verifierBytes);
  }

  protected String getAuthorizationCodeString(byte[] verifierBytes) {
    System.out.println("generating data...");
    char[] chars = new char[verifierBytes.length];
    for (int i = 0; i < verifierBytes.length; i++) {
      chars[i] = DEFAULT_CODEC[((verifierBytes[i] & 0xFF) % DEFAULT_CODEC.length)];
    }
    return new String(chars);
  }
}
