/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import java.time.Instant;
import java.util.List;

/**
 *
 * @author F6036477
 */
public class Counter extends AbstractMetric<Long> {
  
  public Counter(String name, String help, Long value, Instant time) {
    super(name, help, value, time);
  }
  
  public Counter(String name, String help, Long value) {
    super(name, help, value);
  }
  
  public Counter(String name, String help) {
    super(name, help, 0L);
  }
  
  @Override
  public void collect(List<String> ls) {
    //super.collect(ls);
    if(labels.isEmpty()) {
      //ls.add(String.format(COUNTER_VALUE_FORMAT, name, value.get().longValue(), time.get().getEpochSecond()));
      ls.add(String.format(COUNTER_VALUE_FORMAT, name, value.get().longValue()));
    }
    else {
      //ls.add(String.format(COUNTER_VALUE_LABEL_FORMAT, name, labelsToString(), value.get().longValue(), time.get().getEpochSecond()));
      ls.add(String.format(COUNTER_VALUE_LABEL_FORMAT, name, labelsToString(), value.get().longValue()));
    }
  }
  
  @Override
  public Counter newCopy(String key, String val) {
    return new Counter(name, help, value.get(), time.get()).putLabel(key, val).asCounter();
  }
  
}
