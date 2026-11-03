/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem.sync.gitlab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.gitlab.AtsGitLabService;
import org.eclipse.osee.ats.api.agile.gitlab.GitLabOutboundEndpoint;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Sends requests to GitLab. Modeled on JiraOutboundEndpointImpl.
 */
public class GitLabOutboundEndpointImpl implements GitLabOutboundEndpoint {

   // Config settings (the base URL key lives in AtsGitLabService)
   static final String GITLAB_PROJECT_KEY = "GitLabProject"; // e.g. mina-gamble-group/osee-kanban
   static final String GITLAB_TOKEN_KEY = "GitLabToken"; // access token with api scope

   // %s is replaced with the URL-encoded project path
   static final String GITLAB_ISSUES = "/api/v4/projects/%s/issues";

   private final AtsApi atsApi;

   public GitLabOutboundEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String createGitLabIssue(String jsonPayload) {
      return sendGitLabRequest(jsonPayload, getIssuesPath(), "POST");
   }

   private String getIssuesPath() {
      String projectPath = atsApi.getConfigValue(GITLAB_PROJECT_KEY);
      if (Strings.isInvalid(projectPath)) {
         throw new OseeCoreException("No GitLab project configured (%s)", GITLAB_PROJECT_KEY);
      }
      try {
         // mina-gamble-group/osee-kanban -> mina-gamble-group%2Fosee-kanban
         String encodedProject = URLEncoder.encode(projectPath, "UTF-8");
         return String.format(GITLAB_ISSUES, encodedProject);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to encode GitLab project path", ex);
      }
   }

   private String sendGitLabRequest(String jsonPayload, String urlExtension, String requestMethod) {

      StringBuilder response = new StringBuilder();
      String accessToken = getAccessToken();
      String websiteUrl = getGitLabUrl() + urlExtension;

      HttpURLConnection conn = null;

      try {
         URL url = new URL(websiteUrl);
         conn = (HttpURLConnection) url.openConnection();

         conn.setDoInput(true);
         conn.setRequestMethod(requestMethod);
         conn.setRequestProperty("PRIVATE-TOKEN", accessToken);
         conn.setRequestProperty("Connection", "keep-alive");

         conn.setRequestProperty("Content-Type", "application/json");
         conn.setDoOutput(true);
         try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
         }
         conn.getResponseCode(); // Populates the error stream
         if (conn.getErrorStream() != null) {
            String errorStr = Lib.inputStreamToString(conn.getErrorStream());
            if (Strings.isValid(errorStr)) {
               return errorStr;
            }
         }
         try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
               response.append(responseLine.trim());
            }
         }
      } catch (Exception ex) {
         throw new OseeCoreException("GitLab Operation Failed", ex);
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return response.toString();
   }

   private String getAccessToken() {
      String token = atsApi.getConfigValue(GITLAB_TOKEN_KEY);
      if (Strings.isInvalid(token)) {
         throw new OseeCoreException("No GitLab access token configured (%s)", GITLAB_TOKEN_KEY);
      }
      return token;
   }

   private String getGitLabUrl() {
      String baseUrl = atsApi.getConfigValue(AtsGitLabService.GITLAB_BASEPATH_KEY);
      if (Strings.isInvalid(baseUrl)) {
         throw new OseeCoreException("No GitLab base URL configured (%s)", AtsGitLabService.GITLAB_BASEPATH_KEY);
      }
      // Jira's stored URL has no scheme; accept either form here
      return baseUrl.startsWith("http") ? baseUrl : "https://" + baseUrl;
   }
}
