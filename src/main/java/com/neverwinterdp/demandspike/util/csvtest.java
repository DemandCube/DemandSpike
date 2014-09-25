package com.neverwinterdp.demandspike.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class csvtest {

  public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {

    List<TestPojo> list = new ArrayList<TestPojo>();
    list.add(new TestPojo("leslie", 123));
    list.add(new TestPojo("leslie1", 1237));
    list.add(new TestPojo("leslie2", 1235));
    list.add(new TestPojo("leslie3", 1234));
    list.add(new TestPojo("leslie3", 1234));
    list.add(new TestPojo("leslie3", 1234));
    list.add(new TestPojo("leslie3", 1234));
    list.add(new TestPojo("leslie3", 1234));
    list.add(new TestPojo("leslie3", 1234));
    CSVGenerator<TestPojo> csvGenerator = new CSVGenerator<TestPojo>(TestPojo.class);
    csvGenerator.generateCSVFile(list,"/Users/peterjeroldleslie/myscv.csv");
  }

}
