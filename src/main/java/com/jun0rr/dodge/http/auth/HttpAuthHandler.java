/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpAuthHandler implements Consumer<ChannelExchange<FullHttpRequest>> {
  
  public static final HttpRoute ROUTE = HttpRoute.of("\\/?auth\\/?", HttpMethod.POST);

  @Override
  public void accept(ChannelExchange<FullHttpRequest> x) {
    Http http = (Http) x.tcpChannel();
    JsonObject obj = http.gson().fromJson(x
        .message()
        .content()
        .toString(StandardCharsets.UTF_8), JsonElement.class
    ).getAsJsonObject();
    
  }
  
}
