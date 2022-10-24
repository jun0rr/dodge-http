/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.util.List;

/**
 *
 * @author F6036477
 */
public class JsonIgnoreStrategy implements ExclusionStrategy {

  @Override
  public boolean shouldSkipField(FieldAttributes fa) {
    return fa.getAnnotations().stream()
        .filter(a->JsonIgnore.class == a.annotationType())
        .findAny()
        .isPresent();
  }

  @Override
  public boolean shouldSkipClass(Class<?> type) {
    return List.of(type.getAnnotations()).stream()
        .filter(a->JsonIgnore.class == a.annotationType())
        .findAny()
        .isPresent();
  }
  
}
