/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.util.ErrMessage;
import com.jun0rr.util.match.Match;

/**
 *
 * @author F6036477
 */
public class HttpRequestException extends RuntimeException {
  
  private final ErrMessage message;
  
  public HttpRequestException(ErrMessage msg) {
    super(Match.notNull(msg).getOrFail("Bad null ErrMessage").getMessage());
    this.message = msg;
  }
  
  public ErrMessage errMessage() {
    return message;
  }
  
}
