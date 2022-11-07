/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.ContentRangeHeader;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.Range;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.FileUtil;
import com.jun0rr.dodge.http.util.MimeType;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.FutureEvent;
import com.jun0rr.util.Unchecked;
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
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 *
 * @author F6036477
 */
public class FileDownloadHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final String WEB_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
  
  public static final Predicate<String> WEB_DATE_PATTERN = Pattern.compile(
      "[a-zA-Z]{3}, [0-9]{2} [a-zA-Z]{3} [0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2} GMT"
  ).asMatchPredicate();
  
  private final FileUtil file;
  
  public FileDownloadHandler(Path p) {
    this.file = new FileUtil(p);
  }
  
  public FileDownloadHandler(Path p, int bufsize) {
    this.file = new FileUtil(p, bufsize);
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    try {
      if(!file.isFileExists()) {
        notFound(x);
      }
      else if(file.isNotModified(x.message())) {
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
    res.headers()
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader());
    x.writeAndFlush(res);
  }
  
  private void notModified(ChannelExchange<HttpRequest> x) throws IOException {
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
    addEtagLastModified(x, res.headers()
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader()));
    x.writeAndFlush(res);
  }
  
  private void download(ChannelExchange<HttpRequest> x) throws IOException {
    Range range = file.getRange(x.message());
    HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, (range.start() == 0 && range.total() == range.end()) 
        ? HttpResponseStatus.OK : HttpResponseStatus.PARTIAL_CONTENT);
    addEtagLastModified(x, res.headers()
        .add(HttpHeaderNames.CONTENT_TYPE, MimeType.fromFile(file.getFilePath()).orElse(MimeType.BIN).getType())
        .add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getFileName()))
        .add(HttpHeaderNames.CONTENT_LENGTH, range.length())
        .add(new ContentRangeHeader(range))
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader()));
    FutureEvent fe = x.write(res);
    try (FileChannel fc = file.openReadChannel()) {
      long total = range.start();
      while(total < range.end()) {
        ByteBuf buffer = x.context().alloc().directBuffer(file.getBufferSize());
        int read = buffer.writeBytes(fc, (int)total, (int)Math.min(buffer.writableBytes(), (range.end() - total)));
        if(read == -1) break;
        total += read;
        fe = fe.write(buffer);
      }
      fe.writeAndFlush(new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER));
    }
  }
  
  private HttpHeaders addEtagLastModified(ChannelExchange<HttpRequest> x, HttpHeaders hds) throws IOException {
    return hds.add(HttpHeaderNames.ETAG, file.getEtag())
        .add(new DateHeader(HttpHeaderNames.LAST_MODIFIED, file.getLastModified()));
  }
  
}
