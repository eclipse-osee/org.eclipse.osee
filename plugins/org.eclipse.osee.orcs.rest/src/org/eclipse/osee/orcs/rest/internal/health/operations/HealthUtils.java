/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 * @author Jaden W. Puckett
 */
public class HealthUtils {

   private static final Logger LOGGER = Logger.getLogger(HealthUtils.class.getName());

   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   public static final String OSEE_HEALTH_SERVERS_KEY = "osee.health.servers";
   public static final String OSEE_HEALTH_BALANCERS_KEY = "osee.health.balancers";
   public static final String OSEE_HEALTH_CURL_SERVER = "osee.health.curl.server";
   private static String errorMsg = ""; //clear the errorMsg before setting it in a method

   private HealthUtils() {
   }

   public static String getOseeInfoValue(JdbcClient jdbcClient, String key) {
      String toReturn = jdbcClient.fetch("", GET_VALUE_SQL, key);
      return toReturn;
   }

   public static List<String> getServers(JdbcClient jdbcClient) {
      List<String> servers = new ArrayList<>();
      // Retrieve servers from osee_info
      String serversStr = HealthUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_SERVERS_KEY);
      serversStr = serversStr.replaceAll(" ", "");
      for (String server : serversStr.split(",")) {
         servers.add(server);
      }
      return servers;
   }

   public static List<String> getBalancers(JdbcClient jdbcClient) {
      List<String> balancers = new ArrayList<>();
      // Retrieve balancers from osee_info
      String balancersStr = HealthUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_BALANCERS_KEY);
      balancersStr = balancersStr.replaceAll(" ", "");
      for (String balancer : balancersStr.split(",")) {
         balancers.add(balancer);
      }
      return balancers;
   }

   public static String getImage(String imageName, String url) {
      return String.format("<a href=\"%s\" target=_blank><img src=\"/server/health/images/%s\"></img></a>", url,
         imageName);
   }

   public static boolean isCurlServerSet(JdbcClient jdbcClient) {
      String curlServer = HealthUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_CURL_SERVER);
      return Strings.isValid(curlServer);
   }

   public static String getCurlExecUrl(String cmd, JdbcClient jdbcClient) {
      String curlServer = HealthUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_CURL_SERVER);
      if (Strings.isInValid(curlServer)) {
         return "osee.health.curl.server not set in osee_info";
      }
      String urlStr = getCurlUrl(curlServer, cmd);
      return urlStr;
   }

   public static String runCurlExecFromCurlServer(String cmd, JdbcClient jdbcClient, String auth) {
      String curlServer = HealthUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_CURL_SERVER);
      if (Strings.isInValid(curlServer)) {
         return null;
      }
      String rStr = "";
      try {
         String urlStr = getCurlUrl(curlServer, cmd);
         String results = makeHttpRequestWithStringResult(urlStr, auth);
         if (results.contains("<html>cmd [ps -ef]")) {
            rStr = results;
         }
      } catch (Exception ex) {
         rStr = Lib.exceptionToString(ex);
      }
      return rStr;
   }

   private static String getCurlUrl(String curlServer, String cmd) {
      return String.format("http://%s/server/health/exec?cmd=%s", curlServer, cmd);
   }

   public static boolean isUrlReachable(String urlStr, String authId) {
      clearErrorMsg();
      HttpURLConnection conn = null;
      try {
         URL url = new URL(urlStr);

         // Set up SSLContext
         SSLContext sslContext = getSSLContext();

         // Open connection with SSLContext
         conn = (HttpURLConnection) url.openConnection();
         if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
         }

         conn.setDoInput(true);
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Connection", "keep-alive");
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setRequestProperty("Authorization", "Basic " + authId);
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(5000);

         int responseCode = conn.getResponseCode();
         return (responseCode >= 200 && responseCode < 300);
      } catch (Exception ex) {
         LOGGER.log(Level.WARNING, "Exception checking URL reachability: " + urlStr, ex);
         setErrorMsg("Exception occurred: " + ex.getMessage());
         return false;
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
   }

   public static String makeHttpRequestWithStringResult(String urlStr, String authId) {
      clearErrorMsg();
      StringBuilder response = new StringBuilder();
      HttpURLConnection conn = null;
      try {
         URL url = new URL(urlStr);

         // Set up SSLContext
         SSLContext sslContext = getSSLContext();

         // Open connection with SSLContext
         conn = (HttpURLConnection) url.openConnection();
         if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
         }

         conn.setDoInput(true);
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Connection", "keep-alive");
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setRequestProperty("Authorization", "Basic " + authId);
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(5000);

         int responseCode = conn.getResponseCode();

         if (responseCode == 401) {
            setErrorMsg("401 Unauthorized error. AuthId: " + authId + ". ");
         }

         try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
               response.append(responseLine.trim());
            }
         }
      } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
         LOGGER.log(Level.WARNING, "Exception during HTTP request to: " + urlStr, ex);
         try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
            ex.printStackTrace(pw);
            String stackTrace = sw.toString();
            setErrorMsg("Exception occurred: " + ex.getMessage() + "\nStackTrace:\n" + stackTrace);
         } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error writing stack trace", e);
            setErrorMsg("StringWriter exception occurred: " + ex.getMessage());
         }
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return response.toString();
   }

   public static <T> T makeHttpRequest(String urlStr, String authId, Class<T> responseType, T defaultValue) {
      clearErrorMsg();
      StringBuilder response = new StringBuilder();
      HttpURLConnection conn = null;

      try {
         URL url = new URL(urlStr);

         // Set up SSLContext
         SSLContext sslContext = getSSLContext();

         // Open connection with SSLContext
         conn = (HttpURLConnection) url.openConnection();
         if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
         }

         conn.setDoInput(true);
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Connection", "keep-alive");
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setRequestProperty("Authorization", "Basic " + authId);
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(5000);

         int responseCode = conn.getResponseCode();

         if (responseCode == 401) {
            setErrorMsg("401 Unauthorized error. AuthId: " + authId);
         }

         try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
               response.append(responseLine.trim());
            }
         }
      } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
         LOGGER.log(Level.WARNING, "Exception during HTTP request to: " + urlStr, ex);
         try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
            ex.printStackTrace(pw);
            String stackTrace = sw.toString();
            setErrorMsg("Exception occurred: " + ex.getMessage() + "\nStackTrace:\n" + stackTrace);
            return defaultValue;
         } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error writing stack trace", e);
            setErrorMsg("StringWriter exception occurred: " + ex.getMessage());
         }
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }

      try {
         // Parse JSON response
         ObjectMapper objectMapper = new ObjectMapper();
         return objectMapper.readValue(response.toString(), responseType);
      } catch (IOException e) {
         setErrorMsg("Failed to parse JSON response: " + e.getMessage());
         return defaultValue;
      }
   }

   /*
    * Returns an SSLContext using the JVM's default trust store for certificate validation.
    * Uses TLS 1.3 for secure internal OSEE health-check communication.
    */
   private static SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
      try {
         TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         tmf.init((KeyStore) null); // Uses the JVM default trust store (cacerts)

         SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
         sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
         return sslContext;
      } catch (java.security.KeyStoreException ex) {
         LOGGER.log(Level.WARNING, "Failed to initialize default trust store, falling back to TLS default", ex);
         SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
         sslContext.init(null, null, new java.security.SecureRandom());
         return sslContext;
      }
   }

   public static void setErrorMsg(String message) {
      if (!errorMsg.isEmpty()) {
         errorMsg += "\n";
      }
      errorMsg += message;
   }

   public static void clearErrorMsg() {
      errorMsg = "";
   }

   public static String getErrorMsg() {
      return errorMsg;
   }

}
