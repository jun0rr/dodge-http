/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.http.util.RequestParam;
import com.jun0rr.dodge.http.util.UriParam;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpUsersDeleteHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpUsersDeleteHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/users/[a-zA-Z_]+[a-zA-Z0-9_\\.\\-]*@[a-zA-Z_]+\\.[a-zA-Z0-9_.]+/?", HttpMethod.DELETE);
  
  public static HttpUsersDeleteHandler get() {
    return new HttpUsersDeleteHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    RequestParam pars = new UriParam(x.message().uri()).asRequestParam("/auth/users/email");
    String email = pars.get("email");
    if(email != null && x.channel().storage().rmUser(email)) {
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
      res.headers()
          .add(new ConnectionHeaders(x))
          .add(new DateHeader())
          .add(new ServerHeader());
      HttpConstants.sendAndCheckConnection(x, res);
    }
    else {
      HttpConstants.sendError(x, 
          new ErrMessage(HttpResponseStatus.NOT_FOUND, "User Not Found")
              .put("email", email));
    }
  }
  
}
