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
   static final String SESSION_AUTH = "rest/auth/1/session";

   public JiraEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String authenticate(String jiraUrl, String jsonPayload) {
      String sessionId = "";
      String websiteURL = "https://" + jiraUrl + "/" + SESSION_AUTH;

      CookieManager cookieManager = new CookieManager();
      CookieHandler.setDefault(cookieManager);

      byte[] out = jsonPayload.getBytes(StandardCharsets.UTF_8);

      try {
         URL url = new URL(websiteURL);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();

         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setRequestMethod("POST");
         conn.setFixedLengthStreamingMode(out.length);
         conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
         conn.connect();
         try (OutputStream os = conn.getOutputStream()) {
            os.write(out);
         }
         conn.getContent();
         conn.disconnect();

         // Get CookieStore
         CookieStore cookieStore = cookieManager.getCookieStore();
         for (HttpCookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals("JSESSIONID")) {
               sessionId = cookie.getValue();
            }
         }
      } catch (Exception ex) {
         throw new OseeAuthenticationException("Failed to authenticate user with JIRA", ex);
      }
      return sessionId;
   }
}