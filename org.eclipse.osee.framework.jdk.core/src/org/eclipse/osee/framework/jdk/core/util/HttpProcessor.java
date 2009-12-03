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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * @author Roberto E. Escobar
 */
public class HttpProcessor {
   private static final String CONTENT_TYPE = "content-type";
   private static final String CONTENT_ENCODING = "content-encoding";

   private static final HttpProcessor instance = new HttpProcessor();

   private final MultiThreadedHttpConnectionManager connectionManager;
   private final HttpClient httpClient;

   private HttpProcessor() {
      connectionManager = new MultiThreadedHttpConnectionManager();
      httpClient = new HttpClient(connectionManager);
   }

   private static HttpClient getHttpClient() {
      return instance.httpClient;
   }

   public static String acquireString(URL url) throws Exception {
      ByteArrayOutputStream sourceOutputStream = new ByteArrayOutputStream();
      try {
         AcquireResult result = HttpProcessor.acquire(url, sourceOutputStream);
         if (result.getCode() == HttpURLConnection.HTTP_OK) {
            return sourceOutputStream.toString();
         }
      } finally {
         sourceOutputStream.close();
      }
      return null;
   }

   public static boolean isAlive(String serverAddress, int port) {
      boolean result = false;
      try {
         String portString = Strings.emptyString();
         if (port > -1) {
            portString = String.format(":%s", String.valueOf(port));
         }
         URL url = new URL(String.format("http://%s%s", serverAddress, portString));
         GetMethod method = new GetMethod(url.toString());

         try {
            HttpMethodParams params = new HttpMethodParams();
            params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
            method.setParams(params);
            int responseCode = getHttpClient().executeMethod(method);
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
               result = true;
            }
         } finally {
            method.releaseConnection();
         }
      } catch (IOException ex) {
         // Do Nothing
      }
      return result;
   }

   public static URI save(URL url, InputStream inputStream, String contentType, String encoding) throws Exception {
      String locator = put(url, inputStream, contentType, encoding);
      return new URI(locator);
   }

   public static String put(URL url, InputStream inputStream, String contentType, String encoding) throws Exception {
      int statusCode = -1;
      String response = null;
      PutMethod method = new PutMethod(url.toString());

      InputStream responseInputStream = null;
      try {
         method.setRequestHeader(CONTENT_ENCODING, encoding);
         method.setRequestEntity(new InputStreamRequestEntity(inputStream, contentType));

         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = getHttpClient().executeMethod(method);
         if (statusCode != HttpURLConnection.HTTP_CREATED) {
            throw new Exception(method.getStatusLine().toString());
         } else {
            responseInputStream = method.getResponseBodyAsStream();
            response = Lib.inputStreamToString(responseInputStream);
         }

      } catch (Exception ex) {
         throw new IOException(String.format("Error during POST [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         try {
            if (responseInputStream != null) {
               responseInputStream.close();
            }
         } catch (Exception ex) {
            // Do Nothing;
         } finally {
            method.releaseConnection();
         }
      }
      return response;
   }

   public static AcquireResult post(URL url, InputStream inputStream, String contentType, String encoding, OutputStream outputStream) throws IOException {
      AcquireResult result = new AcquireResult();
      int statusCode = -1;

      PostMethod method = new PostMethod(url.toString());

      InputStream httpInputStream = null;
      try {
         method.setRequestHeader(CONTENT_ENCODING, encoding);
         method.setRequestEntity(new InputStreamRequestEntity(inputStream, contentType));

         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = getHttpClient().executeMethod(method);
         if (statusCode != HttpStatus.SC_ACCEPTED) {
            throw new Exception(method.getStatusLine().toString());
         } else {
            httpInputStream = method.getResponseBodyAsStream();
            result.setContentType(getContentType(method));
            result.setEncoding(method.getResponseCharSet());
            Lib.inputStreamToOutputStream(httpInputStream, outputStream);
         }
      } catch (Exception ex) {
         throw new IOException(String.format("Error during POST [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         try {
            result.setCode(statusCode);
            if (httpInputStream != null) {
               httpInputStream.close();
            }
         } catch (Exception ex) {
            // Do Nothing;
         } finally {
            method.releaseConnection();
         }
      }
      return result;
   }

   public static String post(URL url) throws Exception {
      String response = null;
      int statusCode = -1;

      PostMethod method = new PostMethod(url.toString());

      InputStream responseInputStream = null;
      try {
         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = getHttpClient().executeMethod(method);
         if (statusCode != HttpStatus.SC_ACCEPTED) {
            throw new Exception(method.getStatusLine().toString());
         } else {
            responseInputStream = method.getResponseBodyAsStream();
            response = Lib.inputStreamToString(responseInputStream);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error during POST [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         try {
            if (responseInputStream != null) {
               responseInputStream.close();
            }
         } catch (Exception ex) {
            // Do Nothing;
         } finally {
            method.releaseConnection();
         }
      }
      return response;
   }

   private static String getContentType(HttpMethodBase method) {
      String contentType = method.getResponseHeader(CONTENT_TYPE).getValue();
      if (Strings.isValid(contentType)) {
         int index = contentType.indexOf(';');
         if (index > 0) {
            contentType = contentType.substring(0, index);
         }
      }
      return contentType;
   }

   public static AcquireResult acquire(URL url, OutputStream outputStream) throws Exception {
      AcquireResult result = new AcquireResult();
      int statusCode = -1;

      GetMethod method = new GetMethod(url.toString());

      InputStream inputStream = null;
      try {
         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = getHttpClient().executeMethod(method);
         if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
            inputStream = method.getResponseBodyAsStream();
            result.setEncoding(method.getResponseCharSet());
            result.setContentType(getContentType(method));
            Lib.inputStreamToOutputStream(inputStream, outputStream);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error acquiring resource: [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         try {
            result.setCode(statusCode);
            if (inputStream != null) {
               inputStream.close();
            }
         } catch (Exception ex) {
            // Do Nothing;
         } finally {
            method.releaseConnection();
         }
      }
      return result;
   }

   public static String delete(URL url) throws Exception {
      String response = null;
      int statusCode = -1;
      DeleteMethod method = new DeleteMethod(url.toString());

      InputStream responseInputStream = null;
      try {
         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
         statusCode = getHttpClient().executeMethod(method);
         if (statusCode == HttpStatus.SC_ACCEPTED) {
            responseInputStream = method.getResponseBodyAsStream();
            response = Lib.inputStreamToString(responseInputStream);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error deleting resource: [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         try {
            if (responseInputStream != null) {
               responseInputStream.close();
            }
         } catch (Exception ex) {
            // Do Nothing;
         } finally {
            method.releaseConnection();
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
         return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_ACCEPTED;
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
