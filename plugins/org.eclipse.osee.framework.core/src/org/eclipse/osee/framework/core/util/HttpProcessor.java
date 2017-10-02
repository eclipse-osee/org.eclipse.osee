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
package org.eclipse.osee.framework.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.net.proxy.IProxyChangeEvent;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class HttpProcessor {
   private static final String CONTENT_TYPE = "content-type";
   private static final String CONTENT_ENCODING = "content-encoding";
   private static final long CHECK_WINDOW = 5000;

   private static final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
   private static final HttpClient client = new HttpClient(connectionManager);

   private static IProxyService proxyService;

   private static final Map<String, IProxyData[]> proxiedData = new ConcurrentHashMap<>();
   private static int requests = 0;

   private static final Map<String, Pair<Integer, Long>> isAliveMap =
      new ConcurrentHashMap<String, Pair<Integer, Long>>();

   private HttpProcessor() {
      // Static class
   }

   private static HttpClient getHttpClient(URI uri) {
      HostConfiguration config = client.getHostConfiguration();
      configureProxyData(uri, config);
      return client;
   }

   private static void configureProxyData(URI uri, HostConfiguration config) {
      boolean proxyBypass = OseeProperties.getOseeProxyBypassEnabled();
      if (!proxyBypass) {
         if (proxyService == null) {
            BundleContext context = Activator.getBundleContext();
            if (context != null) {
               ServiceReference<IProxyService> reference = context.getServiceReference(IProxyService.class);
               proxyService = context.getService(reference);
            }
            if (proxyService != null) {
               proxyService.addProxyChangeListener(new IProxyChangeListener() {

                  @Override
                  public void proxyInfoChanged(IProxyChangeEvent event) {
                     proxiedData.clear();
                  }
               });
            }
         }

         String key = String.format("%s_%s", uri.getScheme(), uri.getHost());
         IProxyData[] datas = proxiedData.get(key);
         if (datas == null && proxyService != null) {
            datas = proxyService.select(uri);
            proxiedData.put(key, datas);
         }
         if (datas != null) {
            for (IProxyData data : datas) {
               config.setProxy(data.getHost(), data.getPort());
            }
         }
      }
      OseeLog.logf(Activator.class, Level.INFO, "Http-Request: [%s] [%s]", requests++, uri.toASCIIString());
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

   private static void cacheAlive(URL url, int code) {
      String key = toIsAliveKey(url);
      isAliveMap.put(key, new Pair<Integer, Long>(code, System.currentTimeMillis()));
   }

   private static int executeMethod(URL url, HttpMethodBase method) throws HttpException, IOException, URISyntaxException {
      int statusCode = -1;
      try {
         statusCode = getHttpClient(url.toURI()).executeMethod(method);
      } finally {
         cacheAlive(url, statusCode);
      }
      return statusCode;
   }

   private static String toIsAliveKey(URL url) {
      String host = url.getHost();
      int port = url.getPort();
      return String.format("%s_%s", host, port);
   }

   public static boolean isAlive(URL url) {
      boolean isAlive = false;
      boolean recheck = true;
      String key = toIsAliveKey(url);
      Pair<Integer, Long> lastChecked = isAliveMap.get(key);
      if (lastChecked != null) {
         long checkedOffset = System.currentTimeMillis() - lastChecked.getSecond().longValue();
         if (checkedOffset < CHECK_WINDOW) {
            recheck = false;
            isAlive = lastChecked.getFirst() != -1;
         }
      }

      if (recheck) {
         try {
            GetMethod method = new GetMethod(url.toString());
            try {
               HttpMethodParams params = new HttpMethodParams();
               params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
               params.setSoTimeout(1000);
               method.setParams(params);
               int responseCode = executeMethod(url, method);
               if (responseCode == HttpURLConnection.HTTP_NOT_FOUND || responseCode == HttpURLConnection.HTTP_ACCEPTED || responseCode == HttpURLConnection.HTTP_ACCEPTED || responseCode == HttpURLConnection.HTTP_OK) {
                  isAlive = true;
               }
            } finally {
               method.releaseConnection();
            }
         } catch (Exception ex) {
            // Do Nothing
         }
      }
      return isAlive;
   }

   public static boolean isAlive(URI uri) {
      boolean isAlive = false;
      try {
         isAlive = isAlive(uri.toURL());
      } catch (MalformedURLException ex) {
         // do nothing
      }
      return isAlive;
   }

   public static URI save(URL url, InputStream inputStream, String contentType, String encoding) throws Exception {
      String locator = put(url, inputStream, contentType, encoding);
      return new URI(locator);
   }

   public static String put(URL url, InputStream inputStream, String contentType, String encoding)  {
      int statusCode = -1;
      String response = null;
      PutMethod method = new PutMethod(url.toString());

      InputStream responseInputStream = null;
      AcquireResult result = new AcquireResult();
      try {
         method.setRequestHeader(CONTENT_ENCODING, encoding);
         method.setRequestEntity(new InputStreamRequestEntity(inputStream, contentType));

         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = executeMethod(url, method);
         responseInputStream = method.getResponseBodyAsStream();
         result.setContentType(getContentType(method));
         result.setEncoding(method.getResponseCharSet());
         if (statusCode != HttpURLConnection.HTTP_CREATED) {
            String exceptionString = Lib.inputStreamToString(responseInputStream);
            throw new OseeCoreException(exceptionString);
         } else {
            responseInputStream = method.getResponseBodyAsStream();
            response = Lib.inputStreamToString(responseInputStream);
         }

      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(responseInputStream);
         method.releaseConnection();
      }
      return response;
   }

   public static AcquireResult delete(URL url, String xml, String contentType, String encoding, OutputStream outputStream)  {
      AcquireResult result = new AcquireResult();
      int statusCode = -1;

      org.eclipse.osee.framework.core.util.DeleteMethod method = new DeleteMethod(url.toString());

      InputStream httpInputStream = null;
      try {
         method.setRequestHeader(CONTENT_ENCODING, encoding);
         method.setRequestEntity(new InputStreamRequestEntity(Lib.stringToInputStream(xml), contentType));

         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = executeMethod(url, method);
         httpInputStream = method.getResponseBodyAsStream();
         result.setContentType(getContentType(method));
         result.setEncoding(method.getResponseCharSet());
         if (statusCode == HttpStatus.SC_ACCEPTED || statusCode == HttpStatus.SC_OK) {
            Lib.inputStreamToOutputStream(httpInputStream, outputStream);
         } else {
            String exceptionString = Lib.inputStreamToString(httpInputStream);
            throw new OseeCoreException(exceptionString);
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(httpInputStream);
         result.setCode(statusCode);
         method.releaseConnection();
      }
      return result;
   }

   public static AcquireResult post(URL url, InputStream inputStream, String contentType, String encoding, OutputStream outputStream)  {
      AcquireResult result = new AcquireResult();
      int statusCode = -1;

      PostMethod method = new PostMethod(url.toString());

      InputStream httpInputStream = null;
      try {
         method.setRequestHeader(CONTENT_ENCODING, encoding);
         method.setRequestEntity(new InputStreamRequestEntity(inputStream, contentType));

         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = executeMethod(url, method);
         httpInputStream = method.getResponseBodyAsStream();
         result.setContentType(getContentType(method));
         result.setEncoding(method.getResponseCharSet());
         if (statusCode == HttpStatus.SC_ACCEPTED || statusCode == HttpStatus.SC_OK) {
            Lib.inputStreamToOutputStream(httpInputStream, outputStream);
         } else {
            String exceptionString = Lib.inputStreamToString(httpInputStream);
            throw new OseeCoreException(exceptionString);
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(httpInputStream);
         result.setCode(statusCode);
         method.releaseConnection();
      }
      return result;
   }

   public static String post(URL url) throws Exception {
      AcquireResult result = new AcquireResult();
      String response = null;
      int statusCode = -1;

      PostMethod method = new PostMethod(url.toString());

      InputStream responseInputStream = null;
      try {
         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

         statusCode = executeMethod(url, method);
         responseInputStream = method.getResponseBodyAsStream();
         result.setContentType(getContentType(method));
         result.setEncoding(method.getResponseCharSet());
         if (statusCode != HttpStatus.SC_ACCEPTED) {
            String exceptionString = Lib.inputStreamToString(responseInputStream);
            throw new Exception(exceptionString);
         } else {
            responseInputStream = method.getResponseBodyAsStream();
            response = Lib.inputStreamToString(responseInputStream);
         }
      } finally {
         Lib.close(responseInputStream);
         method.releaseConnection();
      }
      return response;
   }

   private static String getContentType(HttpMethodBase method) {
      String contentType = null;
      Header header = method.getResponseHeader(CONTENT_TYPE);
      if (header != null) {
         contentType = header.getValue();
         if (Strings.isValid(contentType)) {
            int index = contentType.indexOf(';');
            if (index > 0) {
               contentType = contentType.substring(0, index);
            }
         }
      }
      return contentType;
   }

   public static AcquireResult acquire(URL url, OutputStream outputStream) throws Exception {
      return acquire(url, outputStream, 0);
   }

   public static AcquireResult acquire(URL url, OutputStream outputStream, int soTimeout) throws Exception {
      AcquireResult result = new AcquireResult();
      int statusCode = -1;

      GetMethod method = new GetMethod(url.toString());

      InputStream inputStream = null;
      try {
         method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
         method.getParams().setSoTimeout(soTimeout);

         statusCode = executeMethod(url, method);
         result.setEncoding(method.getResponseCharSet());
         result.setContentType(getContentType(method));
         if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
            inputStream = method.getResponseBodyAsStream();
            Lib.inputStreamToOutputStream(inputStream, outputStream);
         } else {
            String response = method.getResponseBodyAsString();
            result.setResult(response);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error acquiring resource: [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         Lib.close(inputStream);
         result.setCode(statusCode);
         method.releaseConnection();
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
         statusCode = executeMethod(url, method);
         if (statusCode == HttpStatus.SC_ACCEPTED) {
            responseInputStream = method.getResponseBodyAsStream();
            response = Lib.inputStreamToString(responseInputStream);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error deleting resource: [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         Lib.close(responseInputStream);
         method.releaseConnection();
      }
      return response;
   }

   public static final class AcquireResult {
      private int code;
      private String encoding;
      private String contentType;
      private String result;

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

      public String getResult() {
         return result;
      }

      public void setResult(String result) {
         this.result = result;
      }
   }
}
