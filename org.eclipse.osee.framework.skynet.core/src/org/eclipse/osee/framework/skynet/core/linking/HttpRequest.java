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
package org.eclipse.osee.framework.skynet.core.linking;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Roberto E. Escobar
 */
public class HttpRequest {

   public enum HttpMethod {
      GET, POST, PUT, DELETE, RESOURCE_GET, INVALID;
   }
   private Map<String, String> parameterMap;
   private String rawRequest;
   private String urlRequest;
   private Map<String, String> httpHeader;
   private String httpProtocol;
   private InputStream inputStream;
   private HttpMethod httpMethod;
   private InetAddress remoteAddress;
   private int remotePort;
   private Socket socket;

   protected HttpRequest(Socket socket) throws Exception {
      this.socket = socket;
      this.parameterMap = new HashMap<String, String>();
      this.rawRequest = "";
      this.urlRequest = "";
      this.httpHeader = new HashMap<String, String>();
      this.httpMethod = HttpMethod.INVALID;

      this.inputStream = new BufferedInputStream(socket.getInputStream());
      this.remoteAddress = socket.getInetAddress();
      this.remotePort = socket.getPort();
      initialize();
   }

   private void initialize() throws Exception {
      InputStream inputStream = getInputStream();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int value = -1;
      char lastChar = 0;
      char currChar = 0;
      while ((value = inputStream.read()) != -1) {
         currChar = (char) value;
         if (currChar != '\n' && currChar != '\r') {
            buffer.write((byte) value);
         }

         if (lastChar == '\r' && currChar == '\n') {
            String toProcess = buffer.toString("ISO-8859-1");
            buffer.reset();
            if (Strings.isValid(toProcess)) {
               if (toProcess.contains("HTTP")) {
                  parseRequest(toProcess);
               } else {
                  parseHeader(toProcess);
               }

            } else {
               break;
            }
         }
         lastChar = currChar;
      }
   }

   private void parseHeader(String line) throws Exception {
      Matcher matcher = Pattern.compile("(.*?):\\s*(.*)").matcher(line);
      if (matcher.matches()) {
         httpHeader.put(matcher.group(1), matcher.group(2));
      }
   }

   public String getParameter(String key) {
      String toReturn = parameterMap.get(key);
      return Strings.isValid(toReturn) ? toReturn : "";
   }

   public Set<String> getParameterKeys() {
      return parameterMap.keySet();
   }

   public String getParametersAsString() {
      return parameterMap.toString();
   }

   public InputStream getInputStream() {
      return inputStream;
   }

   public HttpMethod getMethod() {
      return httpMethod;
   }

   public InetAddress getOriginatingAddress() {
      return remoteAddress;
   }

   public int getOriginatingPort() {
      return remotePort;
   }

   public String getHttpHeaderEntry(String key) {
      String toReturn = httpHeader.get(key);
      return Strings.isValid(toReturn) ? toReturn : "";
   }

   public Set<String> getHttpHeaderKeys() {
      return httpHeader.keySet();
   }

   public String getHttpProtocol() {
      return httpProtocol;
   }

   public String getUrlRequest() {
      return urlRequest;
   }

   public String getRawRequest() {
      return rawRequest;
   }

   public Socket getSocket() {
      return socket;
   }

   private void parseRequest(String entry) throws Exception {
      this.rawRequest = entry;
      SkynetActivator.getLogger().log(Level.INFO, "HttpRequest *" + rawRequest + "*");
      String[] entries = rawRequest.split("\\s");
      if (entries.length > 0 && entries.length < 4) {
         httpMethod = HttpMethod.valueOf(entries[0].trim());
         httpProtocol = entries[2].trim();

         String request = entries[1].trim();
         boolean wasOldStyle = parseOldSchoolStyleLinks(request);
         if (wasOldStyle == false) {
            parseNewStyleRequests(request);
         }
      }
   }

   /**
    * Process new style requests are of the following format: http://127.0.0.1:<port>/<ProcessType>?key1=value1&key2=value2...&key3=value3
    * 
    * @param entry
    * @throws Exception
    */
   private void parseNewStyleRequests(String request) throws Exception {
      String noHostStr = request.replaceFirst("^http:\\/\\/(.*?)\\/", "/");
      Matcher matcher = Pattern.compile("/(.*?)\\?(.*)").matcher(noHostStr);
      if (matcher.matches()) {
         urlRequest = matcher.group(1);

         if (!Strings.isValid(urlRequest)) {
            throw new IllegalArgumentException("Unknown requestType \"" + rawRequest + "\"");
         }
         String data = matcher.group(2);
         Matcher dataMatcher = Pattern.compile("([^&]*?)=([^&]*)").matcher(data);
         while (dataMatcher.find()) {
            parameterMap.put(dataMatcher.group(1), URLDecoder.decode(dataMatcher.group(2), "UTF-8"));
         }
      } else {
         if (httpMethod.equals(HttpMethod.GET)) {
            httpMethod = HttpMethod.RESOURCE_GET;
         }
         urlRequest = request;
      }
   }

   /**
    * Process old format: http://127.0.0.1:<port>/get/guid/<guid>/<ats,Define> TODO old format should be removed once
    * all legacy references are change to new format
    * 
    * @param entry
    * @return
    * @throws Exception
    */
   private boolean parseOldSchoolStyleLinks(String entry) throws Exception {
      boolean handled = false;
      Matcher oldMatcher = Pattern.compile("/(.*?)/guid/(.*?)/(.*)").matcher(entry);
      if (oldMatcher.find()) {
         handled = true;
         String guid = oldMatcher.group(2);
         if (oldMatcher.groupCount() > 2) {
            String processType = oldMatcher.group(3);
            if (processType.equals("ats")) {
               parameterMap.put("guid", guid);
               urlRequest = processType.toUpperCase();
            } else if (processType.equals("Define") || processType.equals("")) {
               parameterMap.put("guid", guid);
               urlRequest = "Define";
            } else {
               throw new IllegalArgumentException("Unnable to parse old style link: \"" + rawRequest + "\"");
            }
         }
      }
      return handled;
   }
}
