/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.metrics;

import com.jun0rr.util.match.Match;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 *
 * @author F6036477
 */
public abstract class AbstractMetric<N extends Number> implements Metric<N> {
  
  protected final MetricDefinition def;
  
  protected final String id;
  
  protected final AtomicReference<N> value;
  
  protected final AtomicReference<Instant> time;
  
  protected final Map<String,String> labels;
  
  public AbstractMetric(MetricDefinition def, String id, N value, Instant time) {
    this.labels = new ConcurrentHashMap();
    this.def = Match.notNull(def).getOrFail("Bad null MetricDefinition");
    this.id = Match.notEmpty(id).getOrFail("Bad empty id String");
    this.value = new AtomicReference(value != null ? value : 0);
    this.time = new AtomicReference(time != null ? time : Instant.now());
  }
  
  public AbstractMetric(MetricDefinition def, String id, N value) {
    this(def, id, value, Instant.now());
  }
  
  public AbstractMetric(MetricDefinition def, String id) {
    this(def, id, null, Instant.now());
  }
  
  @Override
  public MetricDefinition definition() {
    return def;
  }
  
  @Override
  public String id() {
    return id;
  }
  
  @Override
  public N value() {
    return value.get();
  }
  
  @Override
  public Instant timestamp() {
    return time.get();
  }

  @Override
  public Map<String, String> labels() {
    return labels;
  }

  @Override
  public N update(UnaryOperator<N> fn) {
    time.set(Instant.now());
    return value.updateAndGet(fn);
  }

}
