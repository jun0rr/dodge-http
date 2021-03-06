/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.JsonArray;
import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpRolesGetAllHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/roles/?", HttpMethod.GET);
  
  public static HttpRolesGetAllHandler get() {
    return new HttpRolesGetAllHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    JsonArray array = new JsonArray();
    x.channel().storage().roles()
        .forEach(r->array.add( ((Http)x.channel()).gson().toJsonTree(r) ));
    ByteBuf buf = x.context().alloc().directBuffer();
    buf.writeCharSequence(((Http)x.channel()).gson().toJson(array), StandardCharsets.UTF_8);
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
    res.headers()
        .add(new JsonContentHeader(buf.readableBytes()))
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader());
    HttpConstants.sendAndCheckConnection(x, res);
  }
  
}
