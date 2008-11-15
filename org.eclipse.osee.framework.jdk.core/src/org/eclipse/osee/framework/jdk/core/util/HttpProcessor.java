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
package org.eclipse.osee.framework.jdk.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author Roberto E. Escobar
 */
public class HttpProcessor {
   private static final int CONNECTION_TIMEOUT = 1000 * 60 * 2;
   private static final String CONTENT_LENGTH = "Content-Length";
   private static final String CONTENT_TYPE = "Content-Type";
   private static final String CONTENT_ENCODING = "Content-Encoding";

   private HttpProcessor() {
   }

   private static HttpURLConnection setupConnection(URL url) throws IOException {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(CONNECTION_TIMEOUT);
      connection.setReadTimeout(0);
      return connection;
   }

   public static URI save(URL url, InputStream inputStream, String contentType, String encoding) throws Exception {
      String locator = put(url, inputStream, contentType, encoding);
      return new URI(locator);
   }

   public static String put(URL url, InputStream inputStream, String contentType, String encoding) throws Exception {
      String response = null;
      HttpUploader uploader = new HttpUploader(url.toString(), inputStream, contentType, encoding);
      boolean wasSuccessful = uploader.execute();
      if (wasSuccessful) {
         response = uploader.getUploadResponse();
         if (response == null) {
            throw new Exception(String.format("Error uploading resource [%s]", url));
         }
      } else {
         throw new Exception(String.format("Error uploading resource [%s]", url));
      }
      return response;
   }

   public static AcquireResult post(URL url, InputStream inputStream, String contentType, String encoding, OutputStream outputStream) throws IOException {
      AcquireResult result = new AcquireResult();
      int code = -1;
      HttpURLConnection connection = null;
      InputStream httpInputStream = null;
      try {
         connection = setupConnection(url);
         connection.setRequestProperty(CONTENT_LENGTH, Integer.toString(inputStream.available()));
         connection.setRequestProperty(CONTENT_TYPE, contentType);
         connection.setRequestProperty(CONTENT_ENCODING, encoding);
         connection.setRequestMethod("POST");
         connection.setAllowUserInteraction(true);
         connection.setDoOutput(true);
         connection.setDoInput(true);
         connection.connect();
         Lib.inputStreamToOutputStream(inputStream, connection.getOutputStream());
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_ACCEPTED) {
            httpInputStream = (InputStream) connection.getContent();
            result.setContentType(connection.getContentType());
            result.setEncoding(connection.getContentEncoding());
            Lib.inputStreamToOutputStream(httpInputStream, outputStream);
         } else {
            throw new IOException(String.format("Error during POST [%s] - status code: [%s]", url, code));
         }
      } catch (IOException ex) {
         throw new IOException(String.format("Error during POST [%s] - status code: [%s]", url, code), ex);
      } finally {
         result.setCode(code);
         if (httpInputStream != null) {
            httpInputStream.close();
         }
         if (connection != null) {
            connection.disconnect();
         }
      }
      return result;
   }

   public static String post(URL url) throws Exception {
      String response = null;
      int code = -1;
      InputStream inputStream = null;
      HttpURLConnection connection = null;
      try {
         connection = setupConnection(url);
         connection.setRequestMethod("POST");
         connection.connect();
         // Wait for response
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_ACCEPTED) {
            inputStream = (InputStream) connection.getContent();
            response = Lib.inputStreamToString(inputStream);
         } else {
            throw new Exception(String.format("Error during POST [%s] - status code: [%s]", url, code));
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error during POST [%s] - status code: [%s]", url, code), ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
         if (connection != null) {
            connection.disconnect();
         }
      }
      return response;
   }

   public static AcquireResult acquire(URL url, OutputStream outputStream) throws Exception {
      AcquireResult result = new AcquireResult();
      int code = -1;
      InputStream inputStream = null;
      HttpURLConnection connection = null;
      try {
         connection = setupConnection(url);
         connection.connect();
         // Wait for response
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_OK) {
            inputStream = (InputStream) connection.getContent();
            result.setContentType(connection.getContentType());
            result.setEncoding(connection.getContentEncoding());
            Lib.inputStreamToOutputStream(inputStream, outputStream);
         }

      } catch (Exception ex) {
         throw new Exception(String.format("Error acquiring resource: [%s] - status code: [%s]", url, code), ex);
      } finally {
         result.setCode(code);
         if (inputStream != null) {
            inputStream.close();
         }
         if (connection != null) {
            connection.disconnect();
         }
      }
      return result;
   }

   public static String delete(URL url) throws Exception {
      String response = null;
      int code = -1;
      InputStream inputStream = null;
      HttpURLConnection connection = null;
      try {
         connection = setupConnection(url);
         connection.setRequestMethod("DELETE");
         connection.connect();
         // Wait for response
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_ACCEPTED) {
            inputStream = (InputStream) connection.getContent();
            response = Lib.inputStreamToString(inputStream);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error deleting resource: [%s] - status code: [%s]", url, code), ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
         if (connection != null) {
            connection.disconnect();
         }
      }
      return response;
   }

   public static final class AcquireResult {
      private int code;
      private String encoding;
      private String contentType;

      private AcquireResult() {
         super();
         this.code = -1;
         this.encoding = "";
         this.contentType = "";
      }

      public boolean wasSuccessful() {
         return code == HttpURLConnection.HTTP_OK;
      }

      public int getCode() {
         return code;
      }

      private void setCode(int code) {
         this.code = code;
      }

      public String getEncoding() {
         return encoding;
      }

      private void setEncoding(String encoding) {
         this.encoding = encoding;
      }

      public String getContentType() {
         return contentType;
      }

      private void setContentType(String contentType) {
         this.contentType = contentType;
      }
   }
}
