/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.ContentRangeHeader;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.Range;
import com.jun0rr.dodge.http.header.RangeHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.MimeType;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.FutureEvent;
import com.jun0rr.util.Unchecked;
import com.jun0rr.util.crypto.Hash;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class FileDownloadHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final int DEFAULT_BUFFER_SIZE = 4096;
  
  public static final String WEB_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
  
  private final Path file;
  
  private final int bufferSize;
  
  private final DateTimeFormatter formatter;
  
  public FileDownloadHandler(Path p) {
    this(p, DEFAULT_BUFFER_SIZE);
  }
  
  public FileDownloadHandler(Path p, int bufferSize) {
    this.file = Objects.requireNonNull(p);
    this.bufferSize = bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : bufferSize;
    this.formatter = DateTimeFormatter.ofPattern(WEB_DATE_FORMAT, Locale.US);
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    try {
      if(!Files.exists(file)) {
        notFound(x);
      }
      else if(isNotModified(x)) {
        notModified(x);
      }
      else {
        download(x);
      }
    }
    catch(IOException e) {
      throw Unchecked.unchecked(e);
    }
  }
  
  private void notFound(ChannelExchange<HttpRequest> x) {
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    ConnectionHeaders ch = new ConnectionHeaders(x);
    res.headers()
        .add(ch)
        .add(new DateHeader())
        .add(new ServerHeader());
    ch.writeAndHandleConn(x, res);
  }
  
  private boolean isNotModified(ChannelExchange<HttpRequest> x) throws IOException {
    RangeHeader hdr = RangeHeader.parse(x.message());
    Range r = hdr.ranges().isEmpty() 
        ? new Range(0, Files.size(file)) 
        : hdr.ranges().get(0).total(Files.size(file));
    String etag = etag();
    LocalDateTime lastModified = lastModified();
    x.attributes().put(Range.class, r);
    x.attributes().put(HttpHeaderNames.ETAG.toString(), etag);
    x.attributes().put(HttpHeaderNames.LAST_MODIFIED.toString(), lastModified);
    String modifiedSince = x.message().headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
    return x.message().headers().contains(HttpHeaderNames.IF_NONE_MATCH, etag, true)
        || modifiedSince != null 
        && !lastModified.isAfter(LocalDateTime.parse(modifiedSince, formatter));
  }
  
  private void notModified(ChannelExchange<HttpRequest> x) throws IOException {
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
    ConnectionHeaders ch = new ConnectionHeaders(x);
    addEtagLastModified(x, res.headers()
        .add(ch)
        .add(new DateHeader())
        .add(new ServerHeader()));
    ch.writeAndHandleConn(x, res);
  }
  
  private void download(ChannelExchange<HttpRequest> x) throws IOException {
    Range r = x.attributes().get(Range.class).get();
    String etag = x.attributes().<String>get(HttpHeaderNames.ETAG.toString()).get();
    String ifrange = x.message().headers().get(HttpHeaderNames.IF_RANGE);
    if(ifrange != null && !ifrange.equals(etag)) {
      r = new Range(0, r.total(), r.total());
    }
    HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, (r.start() == 0 && r.total() == r.end()) 
        ? HttpResponseStatus.OK : HttpResponseStatus.PARTIAL_CONTENT);
    addEtagLastModified(x, res.headers()
        .add(HttpHeaderNames.CONTENT_TYPE, MimeType.fromFile(file).orElse(MimeType.BIN).getType())
        .add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getFileName().toString()))
        .add(HttpHeaderNames.CONTENT_LENGTH, (r.end() - r.start()))
        .add(new ContentRangeHeader(r))
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader()));
    FutureEvent fe = x.write(res);
    try (FileChannel fc = FileChannel.open(file, StandardOpenOption.READ)) {
      long total = r.start();
      while(total < r.end()) {
        ByteBuf buffer = x.context().alloc().directBuffer(bufferSize);
        int read = buffer.writeBytes(fc, (int)total, (int)Math.min(buffer.writableBytes(), (r.end() - total)));
        if(read == -1) break;
        total += read;
        fe = fe.write(buffer);
      }
      fe.writeAndFlush(new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER));
    }
  }
  
  public String etag() throws IOException {
    long size = Files.size(file);
    Hash hash = Hash.sha1();
    ByteBuffer buf = ByteBuffer.allocateDirect(bufferSize);
    try (FileChannel fc = FileChannel.open(file, StandardOpenOption.READ)) {
      long total = 0;
      while(total < size) {
        buf.limit(buf.position() + Math.min(buf.remaining(), (int)(size - total)));
        int read = fc.read(buf);
        if(read == -1) break;
        total += read;
        hash.put(buf.flip());
        buf.compact();
      }
      String etag = String.format("\"%s\"", hash.put(
          buf.clear().putLong(total).flip()
      ).get());
      System.out.printf("* size=%d, etag: %s%n", total, etag);
      return etag;
    }
  }
  
  public LocalDateTime lastModified() throws IOException {
    LocalDateTime date = LocalDateTime.ofInstant(Files.getFileAttributeView(file, BasicFileAttributeView.class)
        .readAttributes().lastModifiedTime()
        .toInstant(), ZoneOffset.UTC);
    return date.minusNanos(date.getNano());
  }
  
  private HttpHeaders addEtagLastModified(ChannelExchange<HttpRequest> x, HttpHeaders hds) throws IOException {
    return hds.add(HttpHeaderNames.ETAG, 
        x.attributes().get(HttpHeaderNames.ETAG.toString()).get()
    ).add(HttpHeaderNames.LAST_MODIFIED, 
        formatter.format(x.attributes().<LocalDateTime>get(
            HttpHeaderNames.LAST_MODIFIED.toString()
        ).get())
    );
  }
  
}
