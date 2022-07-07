/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.netty.handler.codec.http.HttpMethod;
import java.lang.reflect.Type;

/**
 *
 * @author F6036477
 */
public class JsonHttpMethodAdapter implements JsonSerializer<HttpMethod>, JsonDeserializer<HttpMethod> {
  
  @Override
  public JsonElement serialize(HttpMethod m, Type type, JsonSerializationContext jsc) {
    return jsc.serialize(m.name());
  }

  @Override
  public HttpMethod deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
    return HttpMethod.valueOf(je.getAsString());
  }
  
}
