/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class ConnectionHeaders extends DefaultHttpHeaders {
  
  static final Logger logger = LoggerFactory.getLogger(ConnectionHeaders.class);
  
  private final boolean keepAlive;
  
  public ConnectionHeaders(ChannelExchange<?> x) {
    super();
    Optional<HttpRequest> req = x.attributes().get(HttpRequest.class);
    AsciiString cval = HttpHeaderValues.CLOSE;
    if(req.isPresent()) {
      String conn = req.get().headers().get(HttpHeaderNames.CONNECTION);
      keepAlive = conn != null && HttpHeaderValues.KEEP_ALIVE.toString().equalsIgnoreCase(conn);
      if(keepAlive) {
        cval = HttpHeaderValues.KEEP_ALIVE;
      }
    }
    else {
      keepAlive = false;
    }
    add(HttpHeaderNames.CONNECTION, cval);
    add(HttpConstants.Header.PROXY_CONNECTION.with(cval));
  }
  
  public boolean isKeepAlive() {
    return keepAlive;
  }
  
  public void writeAndHandleConn(ChannelExchange x, Object o) {
    if(!isKeepAlive()) {
      ChannelPromise p = x.context().newPromise();
      p.addListener(f->x.context().close());
      x.context().writeAndFlush(o, p);
    }
    else {
      x.context().writeAndFlush(o);
    }
  }
  
}
