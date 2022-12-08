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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 */
public class ServerUtils {

   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   public static final String OSEE_HEALTH_SERVERS_KEY = "osee.health.servers";
   public static final String OSEE_HEALTH_CURL_SERVER = "osee.health.curl.server";
   public static final String GREEN_DOT = "greenDot.png";
   public static final String RED_DOT = "redDot.png";

   private ServerUtils() {
   }

   public static String getOseeInfoValue(JdbcClient jdbcClient, String key) {
      String toReturn = jdbcClient.fetch("", GET_VALUE_SQL, key);
      return toReturn;
   }

   public static List<String> getServers(JdbcClient jdbcClient) {
      List<String> servers = new ArrayList<>();
      // Retrieve servers from OseeInfo
      String serversStr = ServerUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_SERVERS_KEY);
      serversStr = serversStr.replaceAll(" ", "");
      for (String server : serversStr.split(",")) {
         servers.add(server);
      }
      return servers;
   }

   public static String getImage(String imageName, String url) {
      return String.format("<a href=\"%s\" target=_blank><img src=\"/server/health/images/%s\"></img></a>", url,
         imageName);
   }

   public static boolean isCurlServerSet(JdbcClient jdbcClient) {
      String curlServer = ServerUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_CURL_SERVER);
      return Strings.isValid(curlServer);
   }

   public static String getCurlExecUrl(String cmd, JdbcClient jdbcClient) {
      String curlServer = ServerUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_CURL_SERVER);
      if (Strings.isInValid(curlServer)) {
         return "osee.health.curl.server not set in osee_info";
      }
      String urlStr = getCurlUrl(curlServer, cmd);
      return urlStr;
   }

   public static String runCurlExecFromCurlServer(String cmd, JdbcClient jdbcClient) {
      String curlServer = ServerUtils.getOseeInfoValue(jdbcClient, OSEE_HEALTH_CURL_SERVER);
      if (Strings.isInValid(curlServer)) {
         return null;
      }
      String rStr = "";
      try {
         String urlStr = getCurlUrl(curlServer, cmd);
         String results = getUrlResults(urlStr);
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

   public static String getUrlResults(String urlStr) {
      StringBuilder response = new StringBuilder();
      HttpURLConnection conn = null;
      try {
         URL url = new URL(urlStr);
         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Connection", "keep-alive");
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(5000);
         conn.setDoOutput(true);
         try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
               response.append(responseLine.trim());
            }
            br.close();
         }
         conn.getContent();
         conn.disconnect();
      } catch (Exception ex) {
         throw new OseeCoreException("Operation Failed: " + ex.getLocalizedMessage(), ex);
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return response.toString();
   }

}
