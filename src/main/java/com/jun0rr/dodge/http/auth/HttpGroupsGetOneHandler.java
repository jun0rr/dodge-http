/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
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
public class HttpGroupsGetOneHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/groups/.+/?", HttpMethod.GET);
  
  public static HttpGroupsGetOneHandler get() {
    return new HttpGroupsGetOneHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    RequestParam pars = new UriParam(x.message().uri()).asRequestParam("/auth/groups/name");
    String name = pars.get("name");
    Optional<Group> opt = x.channel().storage().groups()
        .filter(o->o.getName().equals(name))
        .findAny();
    if(opt.isPresent()) {
      String json = ((Http)x.channel()).gson().toJson(opt.get());
      ByteBuf cont = x.context().alloc().buffer(json.length());
      cont.writeCharSequence(json, StandardCharsets.UTF_8);
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, cont);
      res.headers()
          .add(new JsonContentHeader(cont.readableBytes()))
          .add(new ConnectionHeaders(x))
          .add(new DateHeader())
          .add(new ServerHeader());
      HttpConstants.sendAndCheckConnection(x, res);
    }
    else {
      HttpConstants.sendError(x, 
          new ErrMessage(HttpResponseStatus.NOT_FOUND, "Group Not Found")
              .put("name", name));
    }
  }
  
}
