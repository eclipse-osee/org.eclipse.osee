/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.client.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class HttpResponse {
   private static final String ENTRY_SEPARATOR = "\r\n";
   private static final String FIELD_VALUE_SEPARATOR = ": ";
   private static final String STATUS_LINE = "HTTP/1.1 ";
   private static final String SERVER_ENTRY = "Server: SkynetHttpServer";
   private static final String DATE_ENTRY = "Date";
   private static final String CONTENT_LENGTH = "Content-Length";
   private static final String CONTENT_ENCODING = "Content-Encoding";
   private static final String CONTENT_TYPE = "Content-Type";
   private static final String CONTENT_DISPOSITION = "Content-Disposition";

   private static Map<Integer, String> codes;

   static {
      HttpResponse.codes = new HashMap<>();
      codes.put(100, StatusCodes.STATUS_100);
      codes.put(101, StatusCodes.STATUS_101);
      codes.put(200, StatusCodes.STATUS_200);
      codes.put(201, StatusCodes.STATUS_201);
      codes.put(202, StatusCodes.STATUS_202);
      codes.put(203, StatusCodes.STATUS_203);
      codes.put(204, StatusCodes.STATUS_204);
      codes.put(205, StatusCodes.STATUS_205);
      codes.put(206, StatusCodes.STATUS_206);
      codes.put(300, StatusCodes.STATUS_300);
      codes.put(301, StatusCodes.STATUS_301);
      codes.put(302, StatusCodes.STATUS_302);
      codes.put(303, StatusCodes.STATUS_303);
      codes.put(304, StatusCodes.STATUS_304);
      codes.put(305, StatusCodes.STATUS_305);
      codes.put(307, StatusCodes.STATUS_307);
      codes.put(401, StatusCodes.STATUS_401);
      codes.put(402, StatusCodes.STATUS_402);
      codes.put(403, StatusCodes.STATUS_403);
      codes.put(404, StatusCodes.STATUS_404);
      codes.put(405, StatusCodes.STATUS_405);
      codes.put(406, StatusCodes.STATUS_406);
      codes.put(407, StatusCodes.STATUS_407);
      codes.put(408, StatusCodes.STATUS_408);
      codes.put(409, StatusCodes.STATUS_409);
      codes.put(410, StatusCodes.STATUS_410);
      codes.put(411, StatusCodes.STATUS_411);
      codes.put(412, StatusCodes.STATUS_412);
      codes.put(413, StatusCodes.STATUS_413);
      codes.put(414, StatusCodes.STATUS_414);
      codes.put(415, StatusCodes.STATUS_415);
      codes.put(416, StatusCodes.STATUS_416);
      codes.put(417, StatusCodes.STATUS_417);
      codes.put(500, StatusCodes.STATUS_500);
      codes.put(501, StatusCodes.STATUS_501);
      codes.put(502, StatusCodes.STATUS_502);
      codes.put(503, StatusCodes.STATUS_503);
      codes.put(504, StatusCodes.STATUS_504);
      codes.put(505, StatusCodes.STATUS_505);
      codes.put(400, StatusCodes.STATUS_400);
   }

   private final Map<String, String> responseHeaderMap;
   private final PrintStream printStream;
   private final OutputStream outputStream;
   private final Socket socket;

   protected HttpResponse(Socket socket) throws Exception {
      this.socket = socket;
      this.outputStream = socket.getOutputStream();
      this.responseHeaderMap = new LinkedHashMap<>();
      this.printStream = new PrintStream(outputStream, true, "UTF-8");
   }

   public OutputStream getOutputStream() {
      return outputStream;
   }

   public PrintStream getPrintStream() {
      return printStream;
   }

   public void setContentEncoding(String encoding) {
      setReponseHeader(CONTENT_ENCODING, encoding);
   }

   public void setContentType(String contentType) {
      setReponseHeader(CONTENT_TYPE, contentType);
   }

   public void setContentDisposition(String disposition) {
      setReponseHeader(CONTENT_DISPOSITION, disposition);
   }

   public void setReponseHeader(String field, String value) {
      responseHeaderMap.put(field, value);
   }

   public String getResponseHeaderField(String field) {
      String toReturn = responseHeaderMap.get(field);
      return Strings.isValid(toReturn) ? toReturn : "";
   }

   public Set<String> getResponseHeaderFields() {
      return responseHeaderMap.keySet();
   }

   public static String getStatus(int value) {
      return codes.get(value);
   }

   public String getResponseHeader(int errorCode, long responseLength) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(STATUS_LINE);
      buffer.append(getStatus(errorCode));
      buffer.append(ENTRY_SEPARATOR);

      buffer.append(SERVER_ENTRY);
      buffer.append(ENTRY_SEPARATOR);

      buffer.append(DATE_ENTRY);
      buffer.append(FIELD_VALUE_SEPARATOR);
      buffer.append(new Date().toString());
      buffer.append(ENTRY_SEPARATOR);

      Set<String> fields = responseHeaderMap.keySet();
      for (String field : fields) {
         String values = responseHeaderMap.get(field);
         buffer.append(field);
         buffer.append(FIELD_VALUE_SEPARATOR);
         buffer.append(values);
         buffer.append(ENTRY_SEPARATOR);
      }
      buffer.append(ENTRY_SEPARATOR);
      return buffer.toString();
   }

   public void sendResponseHeaders(int errorCode, long responseLength) throws IOException {
      setReponseHeader(CONTENT_LENGTH, Long.toString(responseLength));
      byte[] header = getResponseHeader(errorCode, responseLength).getBytes();
      sendInputStream(new ByteArrayInputStream(header));
   }

   public void sendBody(InputStream inputStream) throws IOException {
      sendInputStream(inputStream);
   }

   private void sendInputStream(InputStream inputStream) throws IOException {
      byte[] buf = new byte[10000];
      int count = -1;
      while ((count = inputStream.read(buf)) != -1) {
         outputStream.write(buf, 0, count);
      }
      inputStream.close();
   }

   public void outputStandardError(int errorCode, String message, Throwable ex) {
      StringWriter sw = new StringWriter();
      if (ex != null) {
         ex.printStackTrace(new PrintWriter(sw));
      }
      outputStandardError(errorCode, message + sw.toString());
   }

   public void outputStandardError(int errorCode, String reason) {
      String errorStr = getStatus(errorCode);
      String reasonStr = reason != null ? "Reason: " + reason : "";
      String html = AHTML.simplePage(
         "<h1>Error " + errorStr + "</h1><h2>OSEE was unable to handle the request.</h2>" + reasonStr + "<form><input type=button onClick='window.opener=self;window.close()' value='Close'></form>");
      OseeLog.log(Activator.class, Level.SEVERE, "HttpServer Request failed. " + reasonStr);
      try {
         printStream.println(html);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error sending error string", ex);
      }
   }

   public Socket getSocket() {
      return socket;
   }

   private final class StatusCodes {
      private static final String STATUS_100 = "100 Continue";
      private static final String STATUS_101 = "101 Switching Protocols";
      private static final String STATUS_200 = "200 OK";
      private static final String STATUS_201 = "201 Created";
      private static final String STATUS_202 = "202 Accepted";
      private static final String STATUS_203 = "203 Non-Authoritative Information";
      private static final String STATUS_204 = "204 No Content";
      private static final String STATUS_205 = "205 Reset Content";
      private static final String STATUS_206 = "206 Partial Content";
      private static final String STATUS_300 = "300 Multiple Choices";
      private static final String STATUS_301 = "301 Moved Permanently";
      private static final String STATUS_302 = "302 Found";
      private static final String STATUS_303 = "303 See Other";
      private static final String STATUS_304 = "304 Not Modified";
      private static final String STATUS_305 = "305 Use Proxy";
      private static final String STATUS_307 = "307 Temporary Redirect";
      private static final String STATUS_400 = "400 Bad Request";
      private static final String STATUS_401 = "401 Unauthorized";
      private static final String STATUS_402 = "402 Payment Required";
      private static final String STATUS_403 = "403 Forbidden";
      private static final String STATUS_404 = "404 Not Found";
      private static final String STATUS_405 = "405 Method Not Allowed";
      private static final String STATUS_406 = "406 Not Acceptable";
      private static final String STATUS_407 = "407 Proxy Authentication Required";
      private static final String STATUS_408 = "408 Request Time-out";
      private static final String STATUS_409 = "409 Conflict";
      private static final String STATUS_410 = "410 Gone";
      private static final String STATUS_411 = "411 Length Required";
      private static final String STATUS_412 = "412 Precondition Failed";
      private static final String STATUS_413 = "413 Request Entity Too Large";
      private static final String STATUS_414 = "414 Request-URI Too Large";
      private static final String STATUS_415 = "415 Unsupported Media Type";
      private static final String STATUS_416 = "416 Requested range not satisfiable";
      private static final String STATUS_417 = "417 Expectation Failed";
      private static final String STATUS_500 = "500 Internal Server Error";
      private static final String STATUS_501 = "501 Not Implemented";
      private static final String STATUS_502 = "502 Bad Gateway";
      private static final String STATUS_503 = "503 Service Unavailable";
      private static final String STATUS_504 = "504 Gateway Time-out";
      private static final String STATUS_505 = "505 HTTP Version not supported";
   }
}
