/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import static com.jun0rr.dodge.http.Http.DEFAULT_BUFFER_SIZE;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.ContentRangeHeader;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.Range;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.FileUtil;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.Unchecked;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 *
 * @author F6036477
 */
public class FileUploadHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  private final FileUtil file;
  
  public FileUploadHandler(Path p) {
    this(p, DEFAULT_BUFFER_SIZE);
  }
  
  public FileUploadHandler(Path p, int bufsize) {
    this.file = new FileUtil(p, bufsize);
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    if(HttpMethod.HEAD == req.method()) {
      head(x);
    }
    else if(file.isFileExists() && file.isPreconditionFailed(req)) {
      preconditionFailed(x);
    }
    else {
      put(x);
    }
    Upload up = x.attributes().get(Upload.class).orElseGet(()->upload(x));
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf content = ((HttpContent)x.message()).content();
      x.attributes().put(Upload.class, up.write(content));
      ReferenceCountUtil.safeRelease(content);
    }
    if(HttpConstants.isLastHttpContent(x.message())) {
      Unchecked.call(()->up.channel().close());
      x.attributes().remove(Upload.class);
    }
  }
  
  public void head(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    ContentRangeHeader range = ContentRangeHeader.parse(req.headers());
    LocalDateTime lastModified = req.headers().contains(HttpHeaderNames.LAST_MODIFIED) 
        ? FileUtil.getDateHeader(req.headers(), HttpHeaderNames.LAST_MODIFIED) 
        : LocalDateTime.now();
    HttpResponse res;
    if(range.range().isEmpty()) {
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }
    else {
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
      res.headers()
          .add(HttpHeaderNames.ETAG, file.getWeakEtag(range.range(), lastModified))
          .add(new DateHeader(HttpHeaderNames.LAST_MODIFIED, lastModified))
          .add(range);
    }
    ConnectionHeaders ch = new ConnectionHeaders(x);
    res.headers()
        .add(ch)
        .add(new DateHeader())
        .add(new ServerHeader());
    ch.writeAndHandleConn(x, res);
  }
  
  public void preconditionFailed(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PRECONDITION_FAILED);
    LocalDateTime lastModified = file.getLastModified();
    res.headers()
        .add(HttpHeaderNames.ETAG, file.getWeakEtag())
        .add(new DateHeader(HttpHeaderNames.LAST_MODIFIED, file.getLastModified()))
        .add(new ContentRangeHeader(new Range(0, file.getSize())))
        .add(new DateHeader())
        .add(new ServerHeader());
    ConnectionHeaders ch = new ConnectionHeaders(x);
    res.headers().add(ch);
    ch.writeAndHandleConn(x, res);
  }
  
  public void put(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    Range range = x.attributes().get(Range.class).orElseGet(()->range(x));
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf content = ((HttpContent)x.message()).content();
      Upload up = x.attributes().get(Upload.class).orElseGet(upload(x));
      x.attributes().put(Upload.class, up.write(content));
      ReferenceCountUtil.safeRelease(content);
    }
    if(HttpConstants.isLastHttpContent(x.message())) {
      Upload up = x.attributes().remove(Upload.class).get();
      Unchecked.call(()->up.channel().close());
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED);
      res.headers()
          .add(HttpHeaders.EMPTY_HEADERS)
    }
  }
  
  public Range range(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    Range range = ContentRangeHeader.parse(req.headers()).range();
    String length = req.headers().get(HttpHeaderNames.CONTENT_LENGTH);
    if(range.isEmpty()) {
      if(file.isFileExists()) {
        range = new Range(0, file.getSize());
      }
      else if(length != null) {
        range = new Range(0, Long.parseLong(length));
      }
      else {
        range = new Range(0, Long.MAX_VALUE);
      }
    }
    x.attributes().put(Range.class, range);
    return range;
  }
  
  public FileChannel fileChannel(ChannelExchange<HttpObject> x) {
    FileChannel fc = file.openWriteChannel();
    x.context().channel().closeFuture().addListener(f->Unchecked.call(()->fc.close()));
    x.attributes().put(FileChannel.class, fc);
    return fc;
  }
  
  public Upload upload(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    FileChannel fc = file.openWriteChannel();
    x.context().channel().closeFuture().addListener(f->Unchecked.call(()->fc.close()));
    Range range = ContentRangeHeader.parse(req.headers()).range();
    String length = req.headers().get(HttpHeaderNames.CONTENT_LENGTH);
    if(range.isEmpty()) {
      if(file.isFileExists()) {
        range = new Range(0, file.getSize());
      }
      else if(length != null) {
        range = new Range(0, Long.parseLong(length));
      }
      else {
        range = new Range(0, Long.MAX_VALUE);
      }
    }
    return new Upload(fc, range);
  }
  
  
  public static class Upload {
    
    private final FileChannel channel;
    
    private final Range range;
    
    public Upload(FileChannel fc, Range ran) {
      this.channel = Objects.requireNonNull(fc);
      this.range = ran;
    }
    
    public FileChannel channel() {
      return this.channel;
    }
    
    public Range range() {
      return range;
    }
    
    public Upload range(UnaryOperator<Range> op) {
      return new Upload(channel, op.apply(range));
    }
    
    public Upload write(ByteBuf buf) {
      return range(r->r.incrementStart(Unchecked.call(()->
          buf.readBytes(channel, r.start(), buf.readableBytes())))
      );
    }
    
  }
  
}
