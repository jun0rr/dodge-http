/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.util.ErrMessage;
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
import io.netty.handler.codec.http.HttpContent;
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
public class HttpUserPatchCurrentHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpUserPatchCurrentHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/user/?", HttpMethod.PATCH);
  
  public static HttpUserPatchCurrentHandler get() {
    return new HttpUserPatchCurrentHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isValidHttpContent(x.message())) {
      HttpRequest req = x.attributes().get(HttpRequest.class).get();
      User user = x.attributes().get(User.class).get();
      ByteBuf cont = ((HttpContent)x.message()).content();
      String json = cont.toString(StandardCharsets.UTF_8);
      ReferenceCountUtil.safeRelease(x.message());
      try {
        CreatingUser u = ((Http)x.channel()).gson().fromJson(json, CreatingUser.class);
        if(u.getEmail() != null) {
          user.setEmail(u.getEmail());
        }
        if(u.getBirthday() != null) {
          user.setBirthday(u.getBirthday());
        }
        if(u.getName() != null) {
          user.setName(u.getName());
        }
        if(u.getPassword() != null) {
          user.setPassword(Password.of(new Login(u.getEmail(), u.getPassword())));
        }
        if(u.getGroups() != null && !u.getGroups().isEmpty()) {
          user.setGroups(u.getGroups());
        }
        x.channel().storage().set(user);
        json = ((Http)x.channel()).gson().toJson(user);
        ByteBuf buf = x.context().alloc().buffer(json.length());
        buf.writeCharSequence(json, StandardCharsets.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        res.headers()
            .add(new JsonContentHeader(buf.readableBytes()))
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
