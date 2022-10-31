/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.header.Range;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.Unchecked;
import com.jun0rr.util.crypto.Hash;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 *
 * @author F6036477
 */
public class FileUploadHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  public static final int DEFAULT_BUFFER_SIZE = 4096;
  
  public static final String WEB_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
  
  private final Path file;
  
  private final int bufferSize;
  
  private final DateTimeFormatter formatter;
  
  public FileUploadHandler(Path p) {
    this(p, DEFAULT_BUFFER_SIZE);
  }
  
  public FileUploadHandler(Path p, int bufferSize) {
    this.file = Objects.requireNonNull(p);
    this.bufferSize = bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : bufferSize;
    this.formatter = DateTimeFormatter.ofPattern(WEB_DATE_FORMAT, Locale.US);
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
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
  
  public Upload upload(ChannelExchange<HttpObject> x) {
    try {
      FileChannel fc = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      x.context().channel().closeFuture().addListener(f->Unchecked.call(()->fc.close()));
      return null; //new Upload(fc, Range.of(x.attributes().get(HttpRequest.class).get()));
    }
    catch(IOException e) {
      throw Unchecked.unchecked(e);
    }
  }
  
  private String etag() throws IOException {
    Hash hash = Hash.sha1();
    ByteBuffer buf = ByteBuffer.allocateDirect(bufferSize);
    try (FileChannel fc = FileChannel.open(file, StandardOpenOption.READ)) {
      int read = -1;
      long total = 0;
      while((read = fc.read(buf)) != -1) {
        hash.put(buf.flip());
        buf.compact();
        total += read;
      }
      return String.format("\"%s\"", hash.put(
          buf.clear().putLong(total).flip()
      ).get());
    }
  }
  
  private LocalDateTime lastModified() throws IOException {
    LocalDateTime date = LocalDateTime.ofInstant(Files.getFileAttributeView(file, BasicFileAttributeView.class)
        .readAttributes().lastModifiedTime()
        .toInstant(), ZoneOffset.UTC);
    return date.minusNanos(date.getNano());
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
