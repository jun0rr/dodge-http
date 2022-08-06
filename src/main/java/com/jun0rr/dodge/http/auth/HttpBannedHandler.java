/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.tcp.ChannelExchange;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpBannedHandler implements Consumer<ChannelExchange<Object>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpBannedHandler.class);

  @Override
  public void accept(ChannelExchange<Object> x) {
    AuthTry t = AuthTry.of(x);
    AuthTry tries = x.attributes().parent().<AuthTry>get(t.attributeKey()).orElse(t);
    if(tries.isBanned()) {
      x.context().channel().close();
    }
    else {
      x.context().fireChannelRegistered();
    }
  }
  
}
