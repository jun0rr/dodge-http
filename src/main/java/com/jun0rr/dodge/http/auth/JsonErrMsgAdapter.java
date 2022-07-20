/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

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
public class JsonErrMsgAdapter implements JsonSerializer<ErrMessage>, JsonDeserializer<ErrMessage> {
  
  @Override
  public JsonElement serialize(ErrMessage m, Type type, JsonSerializationContext jsc) {
    JsonObject obj = new JsonObject();
    obj.addProperty("status", m.getStatus().code());
    obj.addProperty("message", m.getMessage());
    m.properties().forEach(e->obj.add(e.getKey(), jsc.serialize(e.getValue())));
    return obj;
  }

  @Override
  public ErrMessage deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
    JsonObject obj = je.getAsJsonObject();
    ErrMessage msg = new ErrMessage(
        HttpResponseStatus.valueOf(obj.get("status").getAsInt()), 
        obj.get("message").getAsString()
    );
    obj.entrySet().stream()
        .filter(e->!e.getKey().equals("status"))
        .filter(e->!e.getKey().equals("message"))
        .forEach(e->msg.put(e.getKey(), e.getValue().getAsString()));
    return msg;
  }
  
}
