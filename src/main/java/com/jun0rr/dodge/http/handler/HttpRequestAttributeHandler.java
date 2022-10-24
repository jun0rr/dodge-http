/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpRequest;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpRequestAttributeHandler implements Consumer<ChannelExchange<HttpRequest>> {

  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    x.attributes().put(HttpRequest.class, x.message());
    x.forwardMessage();
  }
  
}
