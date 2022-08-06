/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpRequestWriteHandler implements Consumer<ChannelExchange<HttpObject>> {

  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    x.attributes().remove(HttpRequest.class).ifPresent(ReferenceCountUtil::safeRelease);
    x.forwardMessage();
  }
  
}
