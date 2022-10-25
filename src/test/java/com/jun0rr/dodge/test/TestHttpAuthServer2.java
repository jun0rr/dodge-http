/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.dodge.http.handler.FileDownloadHandler;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.UriParam;
import com.jun0rr.util.Host;
import com.jun0rr.util.Unchecked;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class TestHttpAuthServer2 {
  
  private static final Logger logger = LoggerFactory.getLogger(TestHttpAuthServer2.class);
  
  @Test
  public void test() {
    try {
      HttpServer server = new HttpServer();
      //server.setLogLevel(Level.INFO);
      if(Files.exists(server.getStoragePath())) {
        Files.walk(server.getStoragePath())
            .filter(p->!Files.isDirectory(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
        Files.walk(server.getStoragePath())
            .filter(p->!server.getStoragePath().equals(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
      }
      server.addRoute(HttpRoute.of("/?download/.*", HttpMethod.GET), HttpRequest.class, ()->x->{
        UriParam par = new UriParam(x.message().uri());
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < par.size(); i++) {
          sb.append(par.getParam(i)).append("/");
        }
        if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
        System.out.println("** file=" + sb.toString());
        if(sb.toString().isBlank()) {
          HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
          ConnectionHeaders ch = new ConnectionHeaders(x);
          res.headers()
              .add(ch)
              .add(new DateHeader())
              .add(new ServerHeader());
          ch.writeAndHandleConn(x, res);
          return;
        }
        new FileDownloadHandler(Paths.get(sb.toString())).accept(x);
      });
      server.setAddress(Host.of("0.0.0.0", 8090))
      //server.setAddress(Host.localhost(8090))
          //.setSslEnabled(true)
          .start()
          .acceptNext(f->logger.info("HttpServer started and listening on {}", f.channel().localAddress()))
          .acceptOnClose(f->logger.info("HttpServer stopped!"));
      server.getMasterGroup().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
