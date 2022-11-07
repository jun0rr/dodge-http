/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.FutureEvent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpConnectionCloseHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpConnectionCloseHandler.class);
  
  private boolean close = false;
  
  public static HttpConnectionCloseHandler get() {
    return new HttpConnectionCloseHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isHttpResponse(x)) {
      HttpResponse res = (HttpResponse) x.message();
      String val = res.headers().get(HttpHeaderNames.CONNECTION);
      close = HttpHeaderValues.CLOSE.toString().equalsIgnoreCase(val);
      x.forwardMessage();
    }
    else if(HttpConstants.isLastHttpContent(x) && close) {
      x.writeAndFlush(x.message()).acceptNext(FutureEvent::close);
    }
    else {
      x.forwardMessage();
    }
  }
  
}
