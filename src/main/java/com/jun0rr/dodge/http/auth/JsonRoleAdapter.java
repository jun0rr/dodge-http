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
import com.google.gson.reflect.TypeToken;
import com.jun0rr.dodge.http.handler.HttpRoute;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author F6036477
 */
public class JsonRoleAdapter implements JsonSerializer<Role>, JsonDeserializer<Role> {
  
  public static final String TYPE = "type";
  
  public static final String ROUTE = "route";
  
  public static final String GROUPS = "groups";
  
  public static final String ALLOW = "allow";
  
  public static final String DENY = "deny";
  
  @Override
  public JsonElement serialize(Role t, Type type, JsonSerializationContext jsc) {
    JsonObject role = new JsonObject();
    String stype = AllowRole.class.isAssignableFrom(t.getClass()) ? ALLOW : DENY;
    role.addProperty(TYPE, stype);
    role.add(ROUTE, jsc.serialize(t.route));
    role.add(GROUPS, jsc.serialize(t.groups));
    return role;
  }

  @Override
  public Role deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
    HttpRoute route = jdc.deserialize(je.getAsJsonObject().get(ROUTE), HttpRoute.class);
    List<Group> groups = jdc.deserialize(je.getAsJsonObject().get(GROUPS), new TypeToken<LinkedList<Group>>(){}.getType());
    if(ALLOW.equals(je.getAsJsonObject().get(TYPE).getAsString())) {
      return new AllowRole(route, groups);
    }
    else {
      return new DenyRole(route, groups);
    }
  }
  
}
