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
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 * @author Jaden W. Puckett
 */
public class HealthUtils {

   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   public static final String OSEE_HEALTH_SERVERS_KEY = "osee.health.servers";
   public static final String OSEE_HEALTH_BALANCERS_KEY = "osee.health.balancers";
   public static final String OSEE_HEALTH_CURL_SERVER = "osee.health.curl.server";
   public static final String GREEN_DOT = "greenDot.png";
   public static final String RED_DOT = "redDot.png";
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
      setErrorMsg("");
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
         setErrorMsg("Exception occurred: " + ex.getMessage());
         return false;
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
   }

   public static String makeHttpRequestWithStringResult(String urlStr, String authId) {
      setErrorMsg("");
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

         try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
               response.append(responseLine.trim());
            }
         }
      } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
         setErrorMsg("Exception occurred: " + ex.getMessage());
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return response.toString();
   }

   public static <T> T makeHttpRequest(String urlStr, String authId, Class<T> responseType, T defaultValue) {
      setErrorMsg("");
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

         try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
               response.append(responseLine.trim());
            }
         }
      } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
         setErrorMsg("Exception occurred: " + ex.getMessage());
         return defaultValue;
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
    * Returns TrustManager that trusts all certificates. Only use when the HTTPS URLs are owned by OSEE.
    */
   private static SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
      TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
         @Override
         public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
         }

         @Override
         public void checkClientTrusted(X509Certificate[] certs, String authType) {
         }

         @Override
         public void checkServerTrusted(X509Certificate[] certs, String authType) {
         }
      }};

      SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      return sslContext;
   }

   public static void setErrorMsg(String message) {
      errorMsg = message;
   }

   public static String getErrorMsg() {
      return errorMsg;
   }

}
