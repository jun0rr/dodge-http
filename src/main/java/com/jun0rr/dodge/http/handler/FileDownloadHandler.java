/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.MimeType;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.Unchecked;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class FileDownloadHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final int DEFAULT_BUFFER_SIZE = 4096;
  
  private final Path file;
  
  private final int bufferSize;
  
  public FileDownloadHandler(Path p) {
    this(p, DEFAULT_BUFFER_SIZE);
  }
  
  public FileDownloadHandler(Path p, int bufferSize) {
    this.file = Objects.requireNonNull(p);
    this.bufferSize = bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : bufferSize;
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    try {
      if(!Files.exists(file)) {
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        ConnectionHeaders ch = new ConnectionHeaders(x);
        res.headers()
            .add(ch)
            .add(new DateHeader())
            .add(new ServerHeader());
        ch.writeAndHandleConn(x, res);
        return;
      }
      long size = Files.size(file);
      HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
      res.headers()
          .add(HttpHeaderNames.CONTENT_TYPE, MimeType.fromFile(file).orElse(MimeType.BIN).mimeType())
          .add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getFileName().toString()))
          .add(HttpHeaderNames.CONTENT_LENGTH, size)
          .add(new ConnectionHeaders(x))
          .add(new DateHeader())
          .add(new ServerHeader());
      x.write(res);
      try (FileChannel fc = FileChannel.open(file, StandardOpenOption.READ)) {
        long curSize = 0;
        int read = 0;
        while(read != -1 && curSize < size) {
          ByteBuf buffer = x.context().alloc().directBuffer(bufferSize);
          read = buffer.writeBytes(fc, (int)curSize, (int)Math.min(size, buffer.capacity()));
          curSize += read;
          x.context().write(buffer);
        }
        x.context().writeAndFlush(new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER));
      }
    }
    catch(IOException e) {
      throw Unchecked.unchecked(e);
    }
  }
  
}
