/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.jun0rr.dodge.metrics.Counter;
import com.jun0rr.dodge.metrics.Gauge;
import com.jun0rr.dodge.metrics.Metric;
import java.lang.reflect.Type;
import java.util.Map.Entry;

/**
 *
 * @author F6036477
 */
public class JsonMetricAdapter implements JsonSerializer<Metric>, JsonDeserializer<Metric> {
  
  public static final String TYPE = "type";
  
  public static final String COUNTER = "counter";
  
  public static final String GAUGE = "gauge";
  
  public static final String NAME = "name";
  
  public static final String HELP = "help";
  
  public static final String VALUE = "value";
  
  public static final String LABELS = "labels";
  
  @Override
  public JsonElement serialize(Metric m, Type type, JsonSerializationContext jsc) {
    JsonObject met = new JsonObject();
    String stype = Counter.class.isAssignableFrom(m.getClass()) ? COUNTER : GAUGE;
    met.addProperty(TYPE, stype);
    met.addProperty(NAME, m.name());
    met.addProperty(HELP, m.help());
    met.addProperty(VALUE, m.value());
    if(!m.labels().isEmpty()) {
      JsonArray lbs = new JsonArray();
      m.labels().entrySet().forEach(e->addKeyValue(lbs, (Entry<String,String>)e));
      met.add(LABELS, lbs);
    }
    return met;
  }
  
  private void addKeyValue(JsonArray a, Entry<String,String> e) {
    JsonObject lb = new JsonObject();
    lb.addProperty(e.getKey(), e.getValue());
    a.add(lb);
  }

  @Override
  public Metric deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
    JsonObject obj = je.getAsJsonObject();
    Metric met;
    if(COUNTER.equals(je.getAsJsonObject().get(TYPE).getAsString())) {
      met = new Counter(obj.get(NAME).getAsString(), obj.get(HELP).getAsString(), obj.get(VALUE).getAsLong());
    }
    else {
      met = new Gauge(obj.get(NAME).getAsString(), obj.get(HELP).getAsString(), obj.get(VALUE).getAsDouble());
    }
    if(obj.has(LABELS)) {
      JsonArray a = obj.getAsJsonArray(LABELS);
      a.forEach(e->getKeyValue(met, e));
    }
    return met;
  }
  
  private void getKeyValue(Metric m, JsonElement elt) {
    elt.getAsJsonObject().entrySet().forEach(e->m.putLabel(e.getKey(), e.getValue().getAsString()));
  }
  
}
