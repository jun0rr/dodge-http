/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.lang.reflect.Type;

/**
 *
 * @author F6036477
 */
public class JsonHttpResponseStatusAdapter implements JsonSerializer<HttpResponseStatus>, JsonDeserializer<HttpResponseStatus> {
  
  public static final String CODE = "code";
  
  public static final String REASON = "reason";
  
  @Override
  public JsonElement serialize(HttpResponseStatus s, Type type, JsonSerializationContext jsc) {
    JsonObject obj = new JsonObject();
    obj.addProperty(CODE, s.code());
    obj.addProperty(REASON, s.reasonPhrase());
    return obj;
  }

  @Override
  public HttpResponseStatus deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
    return HttpResponseStatus.valueOf(je.getAsJsonObject().get(CODE).getAsInt());
  }
  
}
