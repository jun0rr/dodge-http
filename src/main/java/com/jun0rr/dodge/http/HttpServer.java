/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import com.jun0rr.dodge.http.auth.AllowRole;
import com.jun0rr.dodge.http.auth.HttpAccessFilter;
import com.jun0rr.dodge.http.auth.HttpAuthFilter;
import com.jun0rr.dodge.http.auth.HttpGroupsBindHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsDeleteHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsGetAllHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsGetOneHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsPutHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsUnbindHandler;
import com.jun0rr.dodge.http.auth.HttpLoginHandler;
import com.jun0rr.dodge.http.auth.HttpRolesDeleteHandler;
import com.jun0rr.dodge.http.auth.HttpRolesGetAllHandler;
import com.jun0rr.dodge.http.auth.HttpRolesGetHandler;
import com.jun0rr.dodge.http.auth.HttpRolesPostHandler;
import com.jun0rr.dodge.http.auth.HttpShutdownHandler;
import com.jun0rr.dodge.http.auth.HttpUserGetHandler;
import com.jun0rr.dodge.http.auth.HttpUserPatchCurrentHandler;
import com.jun0rr.dodge.http.auth.HttpUsersDeleteHandler;
import com.jun0rr.dodge.http.auth.HttpUsersGetAllHandler;
import com.jun0rr.dodge.http.auth.HttpUsersGetOneHandler;
import com.jun0rr.dodge.http.auth.HttpUsersPatchHandler;
import com.jun0rr.dodge.http.auth.HttpUsersPutHandler;
import com.jun0rr.dodge.http.auth.Storage;
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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObject;
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
  
  private boolean authEnabled = true;
  
  public HttpServer() {
    super(SERVER_BOOTSTRAP);
  }
  
  public HttpServer addRoute(HttpRoute route, Supplier<Consumer<ChannelExchange<HttpObject>>> sup) {
    addHandler(ChannelEvent.Inbound.READ, HttpObject.class, ()->new HttpRouteHandler(route, sup.get()));
    return this;
  }
  
  public <T> HttpServer addRoute(HttpRoute route, Class<T> c, Supplier<Consumer<ChannelExchange<T>>> sup) {
    addHandler(ChannelEvent.Inbound.READ, c, ()->new HttpRouteHandler(route, sup.get()));
    return this;
  }
  
  @Override
  public <T> HttpServer addHandler(ChannelEvent evt, Class<T> type, Supplier<Consumer<ChannelExchange<T>>> cs) {
    super.addHandler(evt, type, cs);
    return this;
  }

  @Override
  public HttpServer addHandler(ChannelEvent evt, Supplier<Consumer<ChannelExchange<Object>>> cs) {
    super.addHandler(evt, cs);
    return this;
  }

  @Override
  public HttpServer addHandler(Supplier<ChannelHandler> cih) {
    super.addHandler(cih);
    return this;
  }
  
  public boolean isAuthenticationEnabled() {
    return authEnabled;
  }
  
  public HttpServer setAuthenticationEnabled(boolean enabled) {
    this.authEnabled = enabled;
    return this;
  }
  
  private void enableAuthetication(SocketChannel c) {
    c.pipeline().addLast(HttpLoginHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpObject.class, new HttpLoginHandler())
        )
    );
    c.pipeline().addLast(HttpAuthFilter.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpAuthFilter())
        )
    );
    c.pipeline().addLast(HttpAccessFilter.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpAccessFilter())
        )
    );
    c.pipeline().addLast(HttpUserGetHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpUserGetHandler.ROUTE, new HttpUserGetHandler()))
        )
    );
    c.pipeline().addLast(HttpUsersGetAllHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpUsersGetAllHandler.ROUTE, new HttpUsersGetAllHandler()))
        )
    );
    c.pipeline().addLast(HttpUsersGetOneHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpUsersGetOneHandler.ROUTE, new HttpUsersGetOneHandler()))
        )
    );
    c.pipeline().addLast(HttpUsersPutHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpObject.class, new HttpRouteHandler(HttpUsersPutHandler.ROUTE, new HttpUsersPutHandler()))
        )
    );
    c.pipeline().addLast(HttpUsersPatchHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpObject.class, new HttpRouteHandler(HttpUsersPatchHandler.ROUTE, new HttpUsersPatchHandler()))
        )
    );
    c.pipeline().addLast(HttpUserPatchCurrentHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpObject.class, new HttpRouteHandler(HttpUserPatchCurrentHandler.ROUTE, new HttpUserPatchCurrentHandler()))
        )
    );
    c.pipeline().addLast(HttpUsersDeleteHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpUsersDeleteHandler.ROUTE, new HttpUsersDeleteHandler()))
        )
    );
    c.pipeline().addLast(HttpGroupsGetAllHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpGroupsGetAllHandler.ROUTE, new HttpGroupsGetAllHandler()))
        )
    );
    c.pipeline().addLast(HttpGroupsGetOneHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpGroupsGetOneHandler.ROUTE, new HttpGroupsGetOneHandler()))
        )
    );
    c.pipeline().addLast(HttpGroupsPutHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpObject.class, new HttpRouteHandler(HttpGroupsPutHandler.ROUTE, new HttpGroupsPutHandler()))
        )
    );
    c.pipeline().addLast(HttpGroupsBindHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpGroupsBindHandler.ROUTE, new HttpGroupsBindHandler()))
        )
    );
    c.pipeline().addLast(HttpGroupsUnbindHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpGroupsUnbindHandler.ROUTE, new HttpGroupsUnbindHandler()))
        )
    );
    c.pipeline().addLast(HttpGroupsDeleteHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpGroupsDeleteHandler.ROUTE, new HttpGroupsDeleteHandler()))
        )
    );
    c.pipeline().addLast(HttpRolesGetAllHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpRolesGetAllHandler.ROUTE, new HttpRolesGetAllHandler()))
        )
    );
    c.pipeline().addLast(HttpRolesGetHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpRolesGetHandler.ROUTE, new HttpRolesGetHandler()))
        )
    );
    c.pipeline().addLast(HttpRolesDeleteHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpRolesDeleteHandler.ROUTE, new HttpRolesDeleteHandler()))
        )
    );
    c.pipeline().addLast(HttpRolesPostHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpObject.class, new HttpRouteHandler(HttpRolesPostHandler.ROUTE, new HttpRolesPostHandler()))
        )
    );
    c.pipeline().addLast(HttpShutdownHandler.class.getSimpleName().concat("#0"),
        new EventInboundHandler(HttpServer.this, attributes(), 
            ChannelEvent.Inbound.READ, 
            ConsumerType.of(HttpRequest.class, new HttpRouteHandler(HttpShutdownHandler.ROUTE, new HttpShutdownHandler()))
        )
    );
  }
  
  @Override
  public ChannelInitializer<SocketChannel> createInitializer() {
    if(authEnabled) {
      startStorage()
          .set(new AllowRole(HttpUserGetHandler.ROUTE, Storage.GROUP_AUTH))
          .set(new AllowRole(HttpUserGetHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpUsersGetAllHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpUsersGetOneHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpUsersPutHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpUsersDeleteHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpUsersPatchHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpUserPatchCurrentHandler.ROUTE, Storage.GROUP_AUTH))
          .set(new AllowRole(HttpGroupsGetAllHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpGroupsGetOneHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpGroupsPutHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpGroupsBindHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpGroupsUnbindHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpGroupsDeleteHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpRolesGetAllHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpRolesGetHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpRolesPostHandler.ROUTE, Storage.GROUP_ADMIN))
          .set(new AllowRole(HttpShutdownHandler.ROUTE, Storage.GROUP_ADMIN));
    }
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
        if(authEnabled) {
          enableAuthetication(c);
        }
        initHandlers(c);
        c.pipeline().addLast(new HttpRequestNotFoundHandler());
        c.pipeline().addLast(new ReleaseInboundHandler());
        c.pipeline().forEach(e->logger.debug("Pipeline: {} - {}", e.getKey(), e.getValue().getClass()));
      }
    };
  }

}
