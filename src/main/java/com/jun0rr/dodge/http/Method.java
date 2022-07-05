/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpMethod;

/**
 *
 * @author F6036477
 */
public enum Method {
  
  CONNECT,
  DELETE,
  GET,
  HEAD,
  OPTIONS,
  PATCH,
  POST,
  PUT,
  TRACE;
  
  public boolean equals(HttpMethod meth) {
    return this == of(meth);
  }
  
  public static Method of(HttpMethod meth) {
    if(HttpMethod.CONNECT == Match.notNull(meth).getOrFail("Bad null HttpMethod")) {
      return CONNECT;
    }
    else if(HttpMethod.DELETE == meth) {
      return DELETE;
    }
    else if(HttpMethod.GET == meth) {
      return GET;
    }
    else if(HttpMethod.HEAD == meth) {
      return HEAD;
    }
    else if(HttpMethod.OPTIONS == meth) {
      return OPTIONS;
    }
    else if(HttpMethod.PATCH == meth) {
      return PATCH;
    }
    else if(HttpMethod.POST == meth) {
      return POST;
    }
    else if(HttpMethod.PUT == meth) {
      return PUT;
    }
    else {
      return TRACE;
    }
  }
  
}
