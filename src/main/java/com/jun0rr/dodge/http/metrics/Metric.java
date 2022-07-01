/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.http.metrics;

import java.time.Instant;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 *
 * @author F6036477
 */
public interface Metric<N extends Number> {
  
  public MetricDefinition definition();
  
  public String id();
  
  public N value();
  
  public Instant timestamp();
  
  public Map<String,String> labels();
  
  public N update(UnaryOperator<N> fn);
  
  public void collect(StringBuilder sb);
  
  public Metric newCopy(String id);
  
}
