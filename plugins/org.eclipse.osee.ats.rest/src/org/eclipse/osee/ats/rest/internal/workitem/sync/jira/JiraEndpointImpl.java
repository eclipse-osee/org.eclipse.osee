/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.sync.jira;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.jira.JiraEndpoint;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;

/**
 * @author Stephen J. Molaro
 */

public class JiraEndpointImpl implements JiraEndpoint {

   private final AtsApi atsApi;
   private String sessionId;
   private final String jiraUrl;
   static final String SESSION_AUTH = "rest/auth/1/session";
   static final String SEARCH_JIRA = "rest/api/2/search";

   public JiraEndpointImpl(AtsApi atsApi, String jiraUrl) {
      this.atsApi = atsApi;
      this.jiraUrl = linkParser(jiraUrl);
   }

   @Override
   public String authenticate(String jsonPayload) {
      String websiteURL = jiraUrl + SESSION_AUTH;

      CookieManager cookieManager = new CookieManager();
      CookieHandler.setDefault(cookieManager);

      byte[] out = jsonPayload.getBytes(StandardCharsets.UTF_8);
      HttpURLConnection conn = null;

      try {
         URL url = new URL(websiteURL);
         conn = (HttpURLConnection) url.openConnection();

         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setRequestMethod("POST");
         conn.setFixedLengthStreamingMode(out.length);
         conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
         conn.connect();
         try (OutputStream os = conn.getOutputStream()) {
            os.write(out, 0, out.length);
            os.close();
         }
         conn.getContent();

         // Get CookieStore
         CookieStore cookieStore = cookieManager.getCookieStore();
         for (HttpCookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals("JSESSIONID")) {
               sessionId = cookie.getValue();
            }
         }
      } catch (Exception ex) {
         throw new OseeAuthenticationException("Failed to authenticate user with JIRA", ex);
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return sessionId;
   }

   @Override
   public String searchJira(String jsonPayload) {
      String searchResults = "";
      String websiteURL = jiraUrl + SEARCH_JIRA;

      CookieManager cookieManager = new CookieManager();
      CookieHandler.setDefault(cookieManager);

      byte[] out = jsonPayload.getBytes(StandardCharsets.UTF_8);
      HttpURLConnection conn = null;

      try {
         URL url = new URL(websiteURL);
         conn = (HttpURLConnection) url.openConnection();

         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setRequestMethod("POST");
         conn.setFixedLengthStreamingMode(out.length);
         conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
         conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
         conn.connect();
         try (OutputStream os = conn.getOutputStream()) {
            os.write(out, 0, out.length);
         }
         if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String readLine = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((readLine = in.readLine()) != null) {
               searchResults += readLine;
            }
            in.close();
         }
         conn.getContent();
      } catch (Exception ex) {
         throw new OseeAuthenticationException("Failed to authenticate user with JIRA", ex);
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return searchResults;
   }

   private String linkParser(String link) {
      String newLink = link;
      if (!link.startsWith("https://")) {
         newLink = "https://" + link;
      }
      if (!link.endsWith("/")) {
         newLink += "/";
      }
      return newLink;
   }

}