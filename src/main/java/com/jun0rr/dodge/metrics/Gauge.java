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
public class Gauge extends AbstractMetric<Double> {
  
  public Gauge(String name, String help, Double value, Instant time) {
    super(name, help, value, time);
  }
  
  public Gauge(String name, String help, Double value) {
    super(name, help, value);
  }
  
  public Gauge(String name, String help) {
    super(name, help, 0.0);
  }
  
  @Override
  public void collect(List<String> ls) {
    //super.collect(ls);
    if(labels.isEmpty()) {
      //ls.add(String.format(GAUGE_VALUE_FORMAT, name, GAUGE_FORMAT.format(value.get().doubleValue()), time.get().getEpochSecond()));
      ls.add(String.format(GAUGE_VALUE_FORMAT, name, GAUGE_FORMAT.format(value.get().doubleValue())));
    }
    else {
      //ls.add(String.format(GAUGE_VALUE_LABEL_FORMAT, name, labelsToString(), GAUGE_FORMAT.format(value.get().doubleValue()), time.get().getEpochSecond()));
      ls.add(String.format(GAUGE_VALUE_LABEL_FORMAT, name, labelsToString(), GAUGE_FORMAT.format(value.get().doubleValue())));
    }
  }
  
  @Override
  public Gauge newCopy(String key, String val) {
    return new Gauge(name, help, value.get(), time.get()).putLabel(key, val).asGauge();
  }
  
}
