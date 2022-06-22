/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import com.jun0rr.dodge.http.auth.Storage;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.handler.HttpRouteHandler;
import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.ResourceLoader;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

/**
 *
 * @author F6036477
 */
public class HttpServer extends Http {
  
  private Path storageDir = ResourceLoader.caller().loadPath("storage");
  
  private EmbeddedStorageManager storageManager;
  
  public HttpServer() {
    super();
  }
  
  public Path getStorageDir() {
    return storageDir;
  }
  
  public HttpServer setStorageDir(Path path) {
    this.storageDir = Match.notNull(path).getOrFail("Bad null Path");
    return this;
  }
  
  //public EmbeddedStorageManager getStorageManager() {
    //if(storageManager == null) {
      //storageManager = EmbeddedStorage.start(persistence, storageDir);
    //}
    //return storageManager;
  //}
//  
  //public Storage getPersistence() {
    //return persistence;
  //}
  
  public HttpServer addRoute(HttpRoute route, Supplier<Consumer<ChannelExchange<HttpRequest>>> sup) {
    addHandler(ChannelEvent.Inbound.READ, HttpRequest.class, ()->new HttpRouteHandler(route, sup.get()));
    return this;
  }
  
}
