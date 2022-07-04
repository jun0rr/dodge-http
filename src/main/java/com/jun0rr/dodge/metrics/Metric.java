/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.util.match.Match;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 *
 * @author F6036477
 */
public interface Metric<N extends Number> {
  
  public String name();
  
  public String help();
  
  public N value();
  
  public Instant timestamp();
  
  public Map<String,String> labels();
  
  public Metric putLabel(String key, String val);
  
  public N update(UnaryOperator<N> fn);
  
  public void collect(List<String> ls);
  
  public Metric newCopy(String key, String val);
  
  public default Counter asCounter() {
    return (Counter) Match.of(this, o->Counter.class.isAssignableFrom(o.getClass())).getOrFail("Not a Counter instance");
  }
  
  public default Gauge asGauge() {
    return (Gauge) Match.of(this, o->Counter.class.isAssignableFrom(o.getClass())).getOrFail("Not a Gauge instance");
  }
  
}
