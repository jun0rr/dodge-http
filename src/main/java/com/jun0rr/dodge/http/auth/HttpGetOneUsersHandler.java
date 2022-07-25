/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.RequestParam;
import com.jun0rr.dodge.http.util.UriParam;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpGetOneUsersHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final HttpRoute ROUTE = HttpRoute.of("\\/?auth\\/users\\/[a-zA-Z_]+[a-zA-Z0-9_\\.\\-]*@[a-zA-Z_]+\\.[a-zA-Z0-9_.]+\\/?", HttpMethod.GET);
  
  public static HttpGetOneUsersHandler get() {
    return new HttpGetOneUsersHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    RequestParam pars = new UriParam(x.message().uri()).asRequestParam("/auth/users/email");
    String email = pars.get("email");
    Optional<User> opt = x.channel().storage().users()
        .filter(u->u.getEmail().equals(email))
        .findAny();
    HttpResponse res;
    ByteBuf cont;
    if(opt.isPresent()) {
      String json = ((Http)x.channel()).gson().toJson(opt.get());
      cont = x.context().alloc().buffer(json.length());
      cont.writeCharSequence(json, StandardCharsets.UTF_8);
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, cont);
    }
    else {
      ErrMessage msg = new ErrMessage(HttpResponseStatus.NOT_FOUND, "User Not Found")
          .put("email", email);
      String json = ((Http)x.channel()).gson().toJson(msg);
      cont = x.context().alloc().buffer(json.length());
      cont.writeCharSequence(json, StandardCharsets.UTF_8);
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, msg.getStatus(), cont);
    }
    res.headers()
        .add(new JsonContentHeader(cont.readableBytes()))
        .add(new ConnectionCloseHeaders())
        .add(new DateHeader())
        .add(new ServerHeader());
    x.writeAndFlush(res).channelClose();
  }
  
}
