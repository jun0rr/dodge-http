/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.util.match.Match;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongUnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public abstract class AbstractMetric<N extends Number> implements Metric<N> {
  
  static final Logger logger = LoggerFactory.getLogger(AbstractMetric.class);
  
  public static final String HELP_FORMAT = "# HELP %s %s";
  
  public static final String TYPE_FORMAT = "# TYPE %s %s";
  
  public static final String COUNTER_VALUE_LABEL_FORMAT = "%s{%s} %d";
  
  public static final String COUNTER_VALUE_FORMAT = "%s %d";
  
  public static final String GAUGE_VALUE_LABEL_FORMAT = "%s{%s} %s";
  
  public static final String GAUGE_VALUE_FORMAT = "%s %s";
  
  public static final DecimalFormat GAUGE_FORMAT = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
  
  
  protected final String name;
  
  protected final String help;
  
  protected final AtomicReference<N> value;
  
  protected final AtomicReference<Instant> time;
  
  protected final Map<String,String> labels;
  
  public AbstractMetric(String name, String help, N value, Instant time) {
    this.labels = new HashMap();
    this.name = Match.notEmpty(name).getOrFail("Bad empty name String");
    this.help = Match.notEmpty(help).getOrFail("Bad empty help String");
    this.value = new AtomicReference(Match.notNull(value).getOrFail("Bad null metric value"));
    this.time = new AtomicReference(time != null ? time : Instant.now());
  }
  
  public AbstractMetric(String name, String help, N value) {
    this(name, help, value, Instant.now());
  }
  
  @Override
  public String name() {
    return name;
  }
  
  @Override
  public String help() {
    return help;
  }
  
  public String formatHelp() {
    return String.format(HELP_FORMAT, name, help);
  }
  
  @Override
  public String formatType() {
    return String.format(TYPE_FORMAT, name, getClass().getSimpleName().toLowerCase());
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
  public Metric<N> putLabel(String key, Object val) {
    labels.put(
        Match.notEmpty(key).getOrFail("Bad null key String"), 
        Objects.toString(Match.notNull(val).getOrFail("Bad null value String"))
    );
    return this;
  }
  
  protected String labelsToString() {
    StringBuilder lbs = new StringBuilder();
    labels.entrySet().stream()
        .map(e->String.format("%s=\"%s\", ", e.getKey(), e.getValue()))
        .forEach(s->lbs.append(s));
    if(lbs.length() > 0) {
      lbs.delete(lbs.length()-2, lbs.length());
    }
    return lbs.toString();
  }
  
  
  
  @Override
  public void collect(List<String> ls) {
    ls.add(String.format(HELP_FORMAT, name, help));
    ls.add(String.format(TYPE_FORMAT, name, getClass().getSimpleName().toLowerCase()));
  }

  @Override
  public Metric<N> updateDouble(DoubleUnaryOperator fn) {
    updateAndGetDouble(fn);
    return this;
  }
  
  @Override
  public Metric<N> updateLong(LongUnaryOperator fn) {
    updateAndGetLong(fn);
    return this;
  }
  
  @Override
  public double updateAndGetDouble(DoubleUnaryOperator fn) {
    time.set(Instant.now());
    return value.updateAndGet(n->(N)Double.valueOf(fn.applyAsDouble(n.doubleValue()))).doubleValue();
  }
  
  @Override
  public long updateAndGetLong(LongUnaryOperator fn) {
    time.set(Instant.now());
    return value.updateAndGet(n->(N)Long.valueOf(fn.applyAsLong(n.longValue()))).longValue();
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + Objects.hashCode(this.name);
    hash = 23 * hash + Objects.hashCode(this.help);
    hash = 23 * hash + Objects.hashCode(this.labels);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AbstractMetric<?> other = (AbstractMetric<?>) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.help, other.help)) {
      return false;
    }
    return Objects.equals(this.labels, other.labels);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + "name=" + name + ", help=" + help + ", value=" + value + ", time=" + time + ", labels=" + labels + '}';
  }

}
