/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public class ErrMessage {
  
  private final HttpResponseStatus status;
  
  private final String message;
  
  private final Map<String,Object> properties;

  public ErrMessage(HttpResponseStatus status, String message) {
    this.status = status;
    this.message = message;
    this.properties = new HashMap<>();
  }
  
  public ErrMessage(HttpResponseStatus status, String message, Object... args) {
    this.status = status;
    this.message = String.format(message, args);
    this.properties = new HashMap<>();
  }
  
  public HttpResponseStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
  
  public ErrMessage put(String key, Object obj) {
    if(key != null && !key.isBlank() && obj != null) {
      this.properties.put(key, Objects.toString(obj));
    }
    return this;
  }
  
  public Stream<Map.Entry<String,Object>> properties() {
    return properties.entrySet().stream();
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + Objects.hashCode(this.status);
    hash = 29 * hash + Objects.hashCode(this.message);
    hash = 29 * hash + Objects.hashCode(this.properties);
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
    final ErrMessage other = (ErrMessage) obj;
    if (!Objects.equals(this.message, other.message)) {
      return false;
    }
    if (!Objects.equals(this.status, other.status)) {
      return false;
    }
    return Objects.equals(this.properties, other.properties);
  }

  @Override
  public String toString() {
    return "ErrMessage{" + "status=" + status + ", message=" + message + ", properties=" + properties + '}';
  }
  
}
