/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import com.jun0rr.dodge.http.handler.EventInboundHandler;
import com.jun0rr.dodge.http.handler.EventOutboundHandler;
import com.jun0rr.dodge.http.handler.HttpMessageLogger;
import com.jun0rr.dodge.http.handler.HttpRequestNotFoundHandler;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.handler.HttpRouteHandler;
import com.jun0rr.dodge.http.handler.ReleaseInboundHandler;
import com.jun0rr.dodge.metrics.HttpRequestTimingHandler;
import com.jun0rr.dodge.metrics.HttpResponseTimingHandler;
import com.jun0rr.dodge.metrics.TcpMetricsHandler;
import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.ConsumerType;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpServerCodec;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpServer extends Http {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
  
  public HttpServer() {
    super(SERVER_BOOTSTRAP);
  }
  
  public HttpServer addRoute(HttpRoute route, Supplier<Consumer<ChannelExchange<HttpRequest>>> sup) {
    addHandler(ChannelEvent.Inbound.READ, HttpRequest.class, ()->new HttpRouteHandler(route, sup.get()));
    return this;
  }
  
  @Override
  public ChannelInitializer<SocketChannel> createInitializer() {
    Match.notEmpty(handlers).failIfNotMatch("Bad empty ChannelHandler List");
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel c) throws Exception {
        initSslHandler(c);
        if(isMetricsEnabled()) {
          c.pipeline().addLast(new TcpMetricsHandler(HttpServer.this));
        }
        c.pipeline().addLast(new HttpServerCodec());
        if(isFullHttpMessageEnabled()) {
          c.pipeline().addLast(new HttpObjectAggregator(getBufferSize()));
        }
        if(isHttpMessageLoggerEnabled()) {
          c.pipeline().addLast(new HttpMessageLogger());
        }
        if(isMetricsEnabled()) {
          c.pipeline().addLast(HttpResponseTimingHandler.class.getSimpleName().concat("#1"), 
              new EventOutboundHandler(HttpServer.this, attributes(), 
                  ChannelEvent.Outbound.WRITE, 
                  ConsumerType.of(HttpResponse.class, new HttpResponseTimingHandler())
              )
          );
          c.pipeline().addLast(HttpRequestTimingHandler.class.getSimpleName().concat("#1"),
              new EventInboundHandler(HttpServer.this, attributes(), 
                  ChannelEvent.Inbound.READ, 
                  ConsumerType.of(HttpRequest.class, new HttpRequestTimingHandler())
              )
          );
        }
        initHandlers(c);
        c.pipeline().addLast(new HttpRequestNotFoundHandler());
        c.pipeline().addLast(new ReleaseInboundHandler());
        //c.pipeline().forEach(e->logger.debug("PIPELINE: {} >> {} - {}", e.getKey(), e.getValue(), e.getValue().getClass()));
      }
    };
  }

}
