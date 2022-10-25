/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author F6036477
 */
public enum MimeType {
  
  AAC(".aac", "AAC audio", "audio/aac"),
  ABW(".abw", "document (AbiWord)", "application/x-abiword"),
  ARC(".arc", "Archive document (multiple files embedded)", "application/x-freearc"),
  AVIF(".avi", "Audio Video Interleave (AVI)", "video/x-msvideo"),
  AVI(".aac", "AAC audio", "audio/aac"),
  AZW(".azw", "Amazon Kindle eBook format", "application/vnd.amazon.ebook"),
  BIN(".bin", "Any kind of binary data", "application/octet-stream"),
  BMP(".bmp", "Windows OS/2 Bitmap Graphics", "image/bmp"),
  BZ(".bz", "BZip archive", "application/x-bzip"),
  BZ2(".bz2", "BZip2 archive", "application/x-bzip2"),
  CAB(".cab", "Microsoft Cabinet archive", "application/vnd.ms-cab-compressed"),
  CDA(".cda", "CD audio", "application/x-cdf"),
  CER(".cer", "Internet Security Certificate file", "application/x-x509-ca-cert"),
  CLASS(".class", "Java bytecode file", "application/x-java"),
  CPP(".cpp", "C++ source file", "text/x-c++src"),
  CRT(".crt", "Certificate File", "application/x-x509-ca-cert"),
  CSH(".csh", "C-Shell script", "application/x-csh"),
  CSS(".css", "Cascading Style Sheets (CSS)", "text/css"),
  CSV(".csv", "Comma-separated values (CSV)", "text/csv"),
  DLL(".dll", "Dynamic Link Library", "application/x-msdownload"),
  DOC(".doc", "Microsoft Word", "application/msword"),
  DOCX(".docx", "Microsoft Word (OpenXML)", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
  EOT(".eot", "MS Embedded OpenType fonts", "application/vnd.ms-fontobject"),
  EPUB(".epub", "Electronic publication (EPUB)", "application/epub+zip"),
  EXE(".exe", "Windows executable file", "application/x-msdos-program"),
  GZ(".gz", "GZip Compressed Archive", "application/gzip"),
  GIF(".gif", "Graphics Interchange Format (GIF)", "image/gif"),
  HTM(".htm", "HyperText Markup Language (HTML)", "text/html"),
  HTML(".html", "HyperText Markup Language (HTML)", "text/html"),
  ICO(".ico", "Icon format", "image/vnd.microsoft.icon"),
  ICS(".ics", "iCalendar format", "text/calendar"),
  JAR(".jar", "Java Archive (JAR)", "application/java-archive"),
  JAVA(".java", "Java source file", "text/x-java"),
  JPEG(".jpeg", "JPEG images", "image/jpeg"),
  JPG(".jpg", "JPEG images", "image/jpeg"),
  JS(".js", "JavaScript", "text/javascript"),
  JSON(".json", "JSON format", "application/json"),
  JSONLD(".jsonld", "JSON-LD format", "application/ld+json"),
  MDB(".mdb", "Microsoft Access file", "application/vnd.ms-access"),
  MID(".mid", "Musical Instrument Digital Interface (MIDI)", "audio/midi"),
  MIDI(".midi", "Musical Instrument Digital Interface (MIDI)", "audio/x-midi"),
  MJS(".mjs", "JavaScript module", "text/javascript"),
  MP3(".mp3", "MP3 audio", "audio/mpeg"),
  MP4(".mp4", "MP4 video", "video/mp4"),
  MPEG(".mpeg", "MPEG Video", "video/mpeg"),
  MPKG(".mpkg", "Apple Installer Package", "application/vnd.apple.installer+xml"),
  ODP(".odp", "OpenDocument presentation document", "application/vnd.oasis.opendocument.presentation"),
  ODS(".ods", "OpenDocument spreadsheet document", "application/vnd.oasis.opendocument.spreadsheet"),
  ODT(".odt", "OpenDocument text document", "application/vnd.oasis.opendocument.text"),
  OGA(".oga", "OGG audio", "audio/ogg"),
  OGV(".ogv", "OGG video", "video/ogg"),
  OGX(".ogx", "OGG", "application/ogg"),
  OPUS(".opus", "Opus audio", "audio/opus"),
  OTF(".otf", "OpenType font", "font/otf"),
  PNG(".png", "Portable Network Graphics", "image/png"),
  PDF(".pdf", "Adobe Portable Document Format (PDF)", "application/pdf"),
  PHP(".php", "Hypertext Preprocessor", "application/x-httpd-php"),
  PPT(".ppt", "Microsoft PowerPoint", "application/vnd.ms-powerpoint"),
  PPTX(".pptx", "Microsoft PowerPoint (OpenXML)", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
  PS(".ps", "PostScript file", "application/postscript"),
  PY(".py", "Python source file", "text/x-python"),
  RAR(".rar", "RAR archive", "application/vnd.rar"),
  RTF(".rtf", "Rich Text Format (RTF)", "application/rtf"),
  SH(".sh", "Bourne shell script", "application/x-sh"),
  SQL(".sql", "SQL file", "text/x-sql"),
  SVG(".svg", "Scalable Vector Graphics (SVG)", "image/svg+xml"),
  TAR(".tar", "Tape Archive (TAR)", "application/x-tar"),
  TGZ(".tgz", "Compressed gzip archive", "application/gzip"),
  TIF(".tif", "Tagged Image File Format (TIFF)", "image/tiff"),
  TIFF(".tiff", "Tagged Image File Format (TIFF)", "image/tiff"),
  TS(".ts", "MPEG transport stream", "video/mp2t"),
  TTF(".ttf", "TrueType Font", "font/ttf"),
  TXT(".txt", "Text", "text/plain"),
  VSD(".vsd", "Microsoft Visio", "application/vnd.visio"),
  WAR(".war", "KDE Web archive", "application/x-webarchive"),
  WAV(".wav", "Waveform Audio Format", "audio/wav"),
  WEBA(".weba", "WEBM audio", "audio/webm"),
  WEBM(".webm", "WEBM video", "video/webm"),
  WEBP(".webp", "WEBP image", "image/webp"),
  WMA(".wma", "Windows Media file", "audio/x-ms-wma"),
  WOFF(".woff", "Web Open Font Format (WOFF)", "font/woff"),
  WOFF2(".woff2", "Web Open Font Format (WOFF)", "font/woff2"),
  XHTML(".xhtml", "XHTML", "application/xhtml+xml"),
  XLM(".xlm", "Excel Macro File", "application/vnd.ms-excel"),
  XLS(".xls", "Microsoft Excel", "application/vnd.ms-excel"),
  XLSX(".xlsx", "Microsoft Excel (OpenXML)", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
  XML(".xml", "XML", "application/xml"),
  XPS(".xps", "Microsoft XPS file", "application/vnd.ms-xpsdocument"),
  XUL(".xul", "XUL", "application/vnd.mozilla.xul+xml"),
  ZIP(".zip", "ZIP archive", "application/zip"),
  Z(".z", "UNIX Compressed Archive File", "application/x-compress"),
  _3GP(".3gp", "audio/video container (3GPP)", "video/3gpp"),
  _3G2(".3g2", "audio/video container (3GPP2)", "video/3gpp2"),
  _7Z(".7z", "archive (7-zip)", "application/x-7z-compressed");
  
  private MimeType(String ext, String desc, String mime) {
    this.ext = Objects.requireNonNull(ext);
    this.desc = Objects.requireNonNull(desc);
    this.mime = Objects.requireNonNull(mime);
  }
  
  private final String ext;
  
  private final String desc;
  
  private final String mime;
  
  public String extension() {
    return ext;
  }
  
  public String description() {
    return desc;
  }
  
  public String mimeType() {
    return mime;
  }
  
  @Override
  public String toString() {
    return String.format("%s[extension=%s, mimeType=%s, description=%s]", name(), extension(), mimeType(), description());
  }
  
  public static Optional<MimeType> fromFile(Path path) {
    return fromFileName(Objects.requireNonNull(path).getFileName().toString());
  }
  
  public static Optional<MimeType> fromFileName(String name) {
    int i = name.lastIndexOf(".");
    if(i <= 0) {
      throw new IllegalArgumentException("Bad file name: " + name);
    }
    return fromExtension(name.substring(i));
  }
  
  public static Optional<MimeType> fromExtension(String ext) {
    return List.of(values()).stream()
        .filter(m->ext.equalsIgnoreCase(m.extension()))
        .findAny();
  }
  
}
