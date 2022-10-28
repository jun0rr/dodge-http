/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.dodge.tcp.TcpChannel;
import com.jun0rr.util.Host;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author F6036477
 */
public class TestEchoServer {
  
  public static final String MESSAGE = "Hello World!";
  
  @Test
  public void testEcho() {
    echoServer();
    echoClient();
  }
  
  public void echoServer() {
    try {
      Logger logger = LoggerFactory.getLogger("EchoServer");
      logger.info("------ Starting Server ------");
      TcpChannel.newServer()
          .setAddress(Host.localhost(7000))
          .setLogLevel(Level.DEBUG)
          .addHandler(ChannelEvent.Outbound.WRITE, String.class, ()->x->{
            ByteBuf msg = x.context().alloc().buffer(x.message().length());
            msg.writeCharSequence(x.message(), StandardCharsets.UTF_8);
            x.writeAndFlush(msg)
                .acceptNext(f->f.close())
                .acceptNext(f->f.shutdownMasterGroup());
          })
          .addHandler(ChannelEvent.Inbound.ACTIVE, ()->x->{
            logger.info("Client connected: {}", x.context().channel().remoteAddress());
            x.context().fireChannelActive();
          })
          .addHandler(ChannelEvent.Inbound.READ, ByteBuf.class, ()->x->{
            String msg = x.message().readCharSequence(x.message().readableBytes(), StandardCharsets.UTF_8).toString();
            ReferenceCountUtil.release(x.message());
            x.read(msg);
          })
          .addHandler(ChannelEvent.Inbound.READ, ()->x->{
            logger.info("<< {}", x.message());
            x.write(x.message());
          })
          .start()
          .acceptNext(f->logger.info("TcpServer listening at: {}", f.channel().localAddress()))
          .syncUninterruptibly()
          .acceptOnClose(f->{
            logger.info("TcpServer Stopped");
            f.shutdownWorkerGroup().shutdownMasterGroup();
          });
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  public void echoClient() {
    try {
      Logger logger = LoggerFactory.getLogger("EchoClient");
      logger.info("------ Starting Client ------");
      TcpChannel.newClient()
          .setAddress(Host.localhost(7000))
          .setLogLevel(Level.DEBUG)
          .addHandler(ChannelEvent.Outbound.WRITE, String.class, ()->x->{
            ByteBuf msg = x.context().alloc().buffer(x.message().length());
            msg.writeCharSequence(x.message(), StandardCharsets.UTF_8);
            x.writeAndFlush(msg);
          })
          .addHandler(ChannelEvent.Inbound.ACTIVE, ()->x->{
            logger.info("Connected to {}", x.context().channel().remoteAddress());
            x.context().fireChannelActive();
          })
          .addHandler(ChannelEvent.Inbound.READ, ByteBuf.class, ()->x->{
            String msg = x.message().readCharSequence(x.message().readableBytes(), StandardCharsets.UTF_8).toString();
            ReferenceCountUtil.release(x.message());
            x.read(msg);
          })
          .addHandler(ChannelEvent.Inbound.READ, ()->x->{
            logger.info("<< {}", x.message());
            x.context().close();
          })
          .start()
          .acceptNext(f->{
            String msg = "Hello World!";
            logger.info(">> {}", msg);
            f.write(msg);
          })
          .acceptOnClose(f->{
            logger.info("TcpClient Stopped");
            f.shutdownWorkerGroup().shutdownMasterGroup();
          }).syncUninterruptibly();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
