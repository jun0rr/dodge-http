/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpAccessFilter implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpAccessFilter.class);
  
  public static HttpAccessFilter get() {
    return new HttpAccessFilter();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    User user = x.attributes().<User>get(HttpAuthFilter.ATTR_USER).get();
    if(x.channel().storage().roles()
        .filter(r->r.match(x.message()))
        .anyMatch(r->r.allow(user))) {
      x.forwardMessage();
    }
    else {
      send(x, new ErrMessage(HttpResponseStatus.UNAUTHORIZED, "Unauthorized resource")
          .put("method", x.message().method().name())
          .put("uri", x.message().uri()));
    }
  }
  
  private void send(ChannelExchange<HttpRequest> x, ErrMessage msg) {
    String json = ((Http)x.channel()).gson().toJson(msg);
    ByteBuf buf = x.context().alloc().heapBuffer(json.length());
    buf.writeCharSequence(json, StandardCharsets.UTF_8);
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, msg.getStatus(), buf);
    res.headers()
        .add(new ConnectionCloseHeaders())
        .add(new DateHeader())
        .add(new ServerHeader())
        .add(new JsonContentHeader(buf.readableBytes()));
    x.writeAndFlush(res).channelClose();
  }
  
}
