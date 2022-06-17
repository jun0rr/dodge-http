/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.Unchecked;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;
import java.util.Optional;
import javax.net.ssl.KeyManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author juno
 */
public class SSLHandlerFactory {
  
  private static final Logger logger = LoggerFactory.getLogger(SSLHandlerFactory.class);
  
  private final Optional<Path> kspath;
  
  private final Optional<char[]> kspass;
  
  
  public SSLHandlerFactory(Path kspath, char[] kspass) {
    this.kspath = Optional.ofNullable(kspath);
    this.kspass = Optional.ofNullable(kspass);
  }
  
  public SSLHandlerFactory() {
    this(null, null);
  }
  
  
  public static SSLHandlerFactory forServer(Path kspath, char[] kspass) {
    return new SSLHandlerFactory(
        Objects.requireNonNull(kspath, "Bad null keystore path"), 
        Objects.requireNonNull(kspass, "Bad null keystore password")
    );
  }
  
  public static SSLHandlerFactory forClient() {
    return new SSLHandlerFactory();
  }
  
  
  public SslHandler create(ByteBufAllocator alloc) {
    return (kspath.isPresent() && kspass.isPresent()) 
        ? Unchecked.call(()->newServerHandler(alloc))
        : Unchecked.call(()->newClientHandler(alloc));
  }
  
  private SslHandler newServerHandler(ByteBufAllocator alloc) throws IOException {
    if(kspath.isEmpty() || !Files.exists(kspath.get())) {
      throw new IllegalStateException("Bad null/missing keystore file: " + kspath);
    }
    if(kspass.isEmpty() || kspass.get().length < 1) {
      throw new IllegalStateException("Bad null/empty keystore password: " + kspass);
    }
    try {
      KeyStore ks = KeyStore.getInstance(kspath.get().toFile(), kspass.get());
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, kspass.get());
      return SslContextBuilder.forServer(kmf)
          .trustManager(InsecureTrustManagerFactory.INSTANCE)
          .build()
          .newHandler(alloc);
    } 
    catch(KeyStoreException 
        | NoSuchAlgorithmException 
        | UnrecoverableKeyException 
        | CertificateException ex) {
      throw new IOException(ex.toString(), ex);
    }
  }
  
  private SslHandler newClientHandler(ByteBufAllocator alloc) throws IOException {
    return SslContextBuilder.forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .build()
        .newHandler(alloc);
  }
  
}
