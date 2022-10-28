/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.header.ProxyAuthorizationHeader;
import com.jun0rr.dodge.http.HttpClient;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.FutureEvent;
import com.jun0rr.util.match.Match;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author F6036477
 */
public abstract class HttpConstants {
  
  public static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0";
  
  public static enum Header {
    
    ACCEPT(HttpHeaderNames.ACCEPT),
    PROXY_CONNECTION("Proxy-Connection"),
    X_ERROR_MESSAGE("x-error-message"),
    X_ERROR_CLASS("x-error-class"),
    X_ERROR_CAUSE("x-error-cause"),
    X_ERROR_TRACE("x-error-trace")
    ;
    
    private Header(String name) {
      this(new AsciiString(name));
    }
    
    private Header(AsciiString name) {
      this.name = new AsciiString(name);
    }
    
    private final AsciiString name;
    
    public HttpHeaders with(Value val) {
      return with(Match.notNull(val).getOrFail("Bad null value").toString());
    }
    
    public HttpHeaders with(String val) {
      return with(new AsciiString(val));
    }
    
    public HttpHeaders with(AsciiString val) {
      return new DefaultHttpHeaders().add(name, Match.notNull(val).getOrFail("Bad null value"));
    }
    
    @Override
    public String toString() {
      return name.toString();
    }
    
    public AsciiString toAsciiString() {
      return name;
    }
    
  }
  
  public static enum Value {
    ALL("*/*"),
    INLINE("inline");

    private Value(String val) {
      this.val = val;
    }

    private final String val;

    @Override
    public String toString() {
      return val;
    }

  }

  
  public static boolean isHttpRequest(Object o) {
    return HttpRequest.class.isAssignableFrom(o.getClass());
  }
  
  public static boolean isFullHttpRequest(Object o) {
    return FullHttpRequest.class.isAssignableFrom(o.getClass());
  }
  
  public static boolean isHttpResponse(Object o) {
    return HttpResponse.class.isAssignableFrom(o.getClass());
  }
  
  public static boolean isHttpContent(Object o) {
    return HttpContent.class.isAssignableFrom(o.getClass());
  }
  
  public static boolean isLastHttpContent(Object o) {
    return LastHttpContent.class.isAssignableFrom(o.getClass());
  }
  
  public static boolean isEmptyHttpContent(Object o) {
    return HttpContent.class.isAssignableFrom(o.getClass()) 
        && !((HttpContent)o).content().isReadable();
  }
  
  public static boolean isValidHttpContent(Object o) {
    return HttpContent.class.isAssignableFrom(o.getClass()) 
        && ((HttpContent)o).content().isReadable();
  }
  
  public static boolean isByteBuf(Object o) {
    return ByteBuf.class.isAssignableFrom(o.getClass());
  }
  
  public static boolean isRequestDelete(Object o) {
    return isHttpRequest(o) && ((HttpRequest)o).method() == HttpMethod.DELETE;
  }
  
  public static boolean isRequestGet(Object o) {
    return isHttpRequest(o) && ((HttpRequest)o).method() == HttpMethod.GET;
  }
  
  public static boolean isRequestHead(Object o) {
    return isHttpRequest(o) && ((HttpRequest)o).method() == HttpMethod.HEAD;
  }
  
  public static boolean isRequestOptions(Object o) {
    return isHttpRequest(o) && ((HttpRequest)o).method() == HttpMethod.OPTIONS;
  }
  
  public static boolean isRequestPatch(Object o) {
    return isHttpRequest(o) && ((HttpRequest)o).method() == HttpMethod.PATCH;
  }
  
  public static boolean isRequestPost(Object o) {
    return isHttpRequest(o) && ((HttpRequest)o).method() == HttpMethod.POST;
  }
  
  public static boolean isRequestPut(Object o) {
    return isHttpRequest(o) && ((HttpRequest)o).method() == HttpMethod.PUT;
  }
  
  public static HttpHeaders adduserAgentAndProxyAuth(HttpHeaders hds, HttpClient cli) {
    hds.add(HttpHeaderNames.USER_AGENT, USER_AGENT_VALUE);
    if(cli.getProxyAddress() != null
        && cli.getProxyUser() != null
        && !cli.getProxyUser().isBlank()
        && cli.getProxyPassword() != null
        && !cli.getProxyPassword().isBlank()) {
      hds.add(new ProxyAuthorizationHeader(cli.getProxyUser(), cli.getProxyPassword()));
    }
    return hds;
  }
  
  public static void sendError(ChannelExchange<?> x, ErrMessage msg) {
    String json = ((Http)x.channel()).gson().toJson(msg);
    ByteBuf buf = x.context().alloc().buffer(json.length());
    buf.writeCharSequence(json, StandardCharsets.UTF_8);
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, msg.getStatus(), buf);
    res.headers()
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader())
        .add(new JsonContentHeader(buf.readableBytes()));
    sendAndCheckConnection(x, res);
  }
  
  public static boolean isHttpConnectionClose(Object o) {
    if(isHttpResponse(o)) {
      HttpResponse res = (HttpResponse) o;
      String conn = res.headers().get(HttpHeaderNames.CONNECTION);
      return conn == null || HttpHeaderValues.CLOSE.toString().equalsIgnoreCase(conn);
    }
    return false;
  }
  
  public static void sendAndCheckConnection(ChannelExchange<?> x, Object o) {
    FutureEvent fe = x.writeAndFlush(o);
    if(isHttpConnectionClose(o)) {
      fe.close();
    }
  }
  
}
