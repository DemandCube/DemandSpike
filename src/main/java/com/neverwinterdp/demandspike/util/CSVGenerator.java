package com.neverwinterdp.demandspike.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CSVGenerator<T> {
  Class<T> clazz;

  public CSVGenerator(Class<T> clazz) {
    this.clazz = clazz;
  }

  public boolean generateCSVFile(List<T> list, String fileName)
      throws NoSuchFieldException, SecurityException, IllegalArgumentException,
      IllegalAccessException, IOException {
    Field[] fields = clazz.getDeclaredFields();
    List<String> headers = new ArrayList<String>();
    List<String> values = new ArrayList<String>();
    FileWriter ofstream = new FileWriter(fileName);
    BufferedWriter out = new BufferedWriter(ofstream);

    for (Field field : fields) {
      if (field.isAnnotationPresent(Header.class)) {
        Header column = field.getAnnotation(Header.class);
        if (column.enable()) {
          headers.add(column.name());
        }
      }
    }
    out.write(StringUtils.join(headers, ","));
    out.newLine();
    for (T t : list) {
      values = new ArrayList<String>();
      for (Field field : fields) {
        if (field.isAnnotationPresent(Header.class)) {
          field.setAccessible(true);
          Header column = field.getAnnotation(Header.class);
          Object o = field.get(t);
          System.out.println(column.enable());
          if (column.enable()) {
            if (o == null) {
              o = "";
            }
            values.add(o.toString());
          }
        }
      }
      out.write(StringUtils.join(values, ","));
      out.newLine();
    }
    out.close();
    return true;
  }
}
