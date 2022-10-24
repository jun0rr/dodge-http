/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.util.ErrMessage;
import com.jun0rr.dodge.http.auth.User;
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
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpStoreUserGetHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpStoreUserGetHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?store/user/?", HttpMethod.GET);
  
  public static HttpStoreUserGetHandler get() {
    return new HttpStoreUserGetHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    User usr = x.attributes().get(User.class).get();
    Gson gson = ((Http)x.channel()).gson();
    try {
      List<JsonElement> objs = x.channel().storage().objects()
          .filter(e->e.getKey().startsWith(usr.getEmail()))
          .map(e->map2json(e, usr.getEmail(), gson))
          .collect(Collectors.toList());
      if(objs.isEmpty()) {
        HttpConstants.sendError(x, new ErrMessage(HttpResponseStatus.NOT_FOUND, "User store is empty"));
      }
      else {
        String json = gson.toJson(objs);
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
    }
    catch(Exception e) {
      HttpConstants.sendError(x, new ErrMessage(HttpResponseStatus.BAD_REQUEST, e.getMessage())
          .put("type", e.getClass())
          .put("cause", e.getCause()));
    }
  }
  
  private JsonObject map2json(Entry<String,Object> entry, String email, Gson gson) {
    JsonObject obj = new JsonObject();
    String key = entry.getKey().substring(email.length() + 1);
    JsonElement elt = gson.fromJson(entry.getValue().toString(), JsonElement.class);
    obj.add(key, elt);
    return obj;
  }
  
}
