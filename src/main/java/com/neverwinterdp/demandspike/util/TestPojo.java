package com.neverwinterdp.demandspike.util;

public class TestPojo {
  @Header(name="NAME")
  private String name;
  @Header(name="VALUE")
  private long value;
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public long getValue() {
    return value;
  }
  public void setValue(long value) {
    this.value = value;
  }
  public TestPojo(String name, long value){
    this.name = name;
    this.value = value;
  }
}
