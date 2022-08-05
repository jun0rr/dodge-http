/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongUnaryOperator;

/**
 *
 * @author F6036477
 */
public interface Metric<N extends Number> {
  
  public String name();
  
  public String help();
  
  public String type();
  
  public default String helpAndType() {
    return help().concat("\n").concat(type());
  }
  
  public N value();
  
  public Instant timestamp();
  
  public Map<String,String> labels();
  
  public Metric<N> putLabel(String key, Object val);
  
  //public Metric<N> update(UnaryOperator<N> fn);
  
  public Metric<N> updateDouble(DoubleUnaryOperator fn);
  
  public Metric<N> updateLong(LongUnaryOperator fn);
  
  //public N updateAndGet(UnaryOperator<N> fn);
  
  public double updateAndGetDouble(DoubleUnaryOperator fn);
  
  public long updateAndGetLong(LongUnaryOperator fn);
  
  public void collect(List<String> ls);
  
  public Metric<N> newCopy(String key, String val);
  
  public default Counter asCounter() {
    return (Counter) this;
  }
  
  public default Gauge asGauge() {
    return (Gauge) this;
  }
  
}
