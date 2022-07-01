/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.metrics;

import java.time.Instant;

/**
 *
 * @author F6036477
 */
public class Counter extends AbstractMetric<Long> {

  public Counter(MetricDefinition def, String id, Long value, Instant time) {
    super(def, id, value, time);
  }

  public Counter(MetricDefinition def, String id, Long value) {
    super(def, id, value);
  }

  public Counter(MetricDefinition def, String id) {
    super(def, id);
  }

  @Override
  public void collect(StringBuilder sb) {
    sb.app
  }

  @Override
  public Metric newCopy(String id) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }
  
}
