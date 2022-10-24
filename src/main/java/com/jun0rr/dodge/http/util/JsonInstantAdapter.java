/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.Instant;

/**
 *
 * @author F6036477
 */
public class JsonInstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
  
  @Override
  public JsonElement serialize(Instant t, Type type, JsonSerializationContext jsc) {
    return jsc.serialize(t.toEpochMilli());
  }

  @Override
  public Instant deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
    return Instant.ofEpochMilli(je.getAsLong());
  }
  
}
