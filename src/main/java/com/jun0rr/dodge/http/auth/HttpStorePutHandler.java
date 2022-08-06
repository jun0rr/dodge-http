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
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpStorePutHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpStorePutHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?store/[a-zA-Z0-9_\\.\\-@]+/?", HttpMethod.PUT);
  
  public static HttpStorePutHandler get() {
    return new HttpStorePutHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf cont = ((HttpContent)x.message()).content();
      String json = cont.toString(StandardCharsets.UTF_8);
      HttpRequest req = x.attributes().get(HttpRequest.class).get();
      RequestParam par = new UriParam(req.uri()).asRequestParam("/store/key");
      try {
        x.channel().storage().set(par.get("key"), json);
        ReferenceCountUtil.safeRelease(x.message());
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED);
        res.headers()
            .addInt(HttpHeaderNames.CONTENT_LENGTH, 0)
            .add(new ConnectionHeaders(x))
            .add(new DateHeader())
            .add(new ServerHeader());
        HttpConstants.sendAndCheckConnection(x, res);
      }
      catch(Exception e) {
        HttpConstants.sendError(x, new ErrMessage(HttpResponseStatus.BAD_REQUEST, e.getMessage())
            .put("type", e.getClass())
            .put("cause", e.getCause()));
      }
    }
  }
  
}
