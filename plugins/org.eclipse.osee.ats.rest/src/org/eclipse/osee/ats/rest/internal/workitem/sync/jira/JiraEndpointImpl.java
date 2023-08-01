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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.jira.JiraEndpoint;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Stephen J. Molaro
 */

public class JiraEndpointImpl implements JiraEndpoint {

   private final AtsApi atsApi;
   static final String JIRA_SEARCH = "/rest/api/2/search";
   static final String JIRA_ISSUE = "/rest/api/2/issue";

   Pattern TOTAL_RESULTS = Pattern.compile("\"total\":\\s*([0-9]+)");
   Pattern MAX_RESULTS = Pattern.compile("\"maxResults\":\\s*([0-9]+)");
   Pattern ISSUE_KEY = Pattern.compile("\"key\":\\s*\"(.+-[0-9]+)\"");
   Pattern ISSUE_NAME = Pattern.compile("\"summary\":\\s*\"(.*)\"");

   public JiraEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String searchJira(String jsonPayload) {
      return sendJiraRequest(jsonPayload, JIRA_SEARCH, "POST");
   }

   @Override
   public String createJiraIssue(String jsonPayload) {
      return sendJiraRequest(jsonPayload, JIRA_ISSUE, "POST");
   }

   @Override
   public String transitionJiraIssue(String jsonPayload, String issueId) {
      String urlPath = JIRA_ISSUE + "/" + issueId + "/transitions";
      return sendJiraRequest(jsonPayload, urlPath, "POST");
   }

   @Override
   public String editJira(String jsonPayload, String issueId) {
      String urlExtension = JIRA_ISSUE + "/" + issueId;
      return sendJiraRequest(jsonPayload, urlExtension, "PUT");
   }

   private String sendJiraRequest(String jsonPayload, String urlExtension, String requestMethod) {

      StringBuilder response = new StringBuilder();
      String personalAccessToken = getPersonalAccessToken();
      String jiraUrl = getJiraUrl();
      String websiteURL = jiraUrl + urlExtension;

      HttpURLConnection conn = null;

      try {
         URL url = new URL("https://" + websiteURL);
         conn = (HttpURLConnection) url.openConnection();

         conn.setDoInput(true);
         conn.setRequestMethod(requestMethod);
         conn.setRequestProperty("Authorization", personalAccessToken);
         conn.setRequestProperty("Connection", "keep-alive");

         conn.setRequestProperty("Content-Type", "application/json");
         conn.setDoOutput(true);
         try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes();
            os.write(input, 0, input.length);
         }
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
         throw new OseeCoreException("JIRA Operation Failed", ex);
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return response.toString();
   }

   private String getPersonalAccessToken() {
      ArtifactToken jiraConfig =
         atsApi.getQueryService().getArtifact(AtsArtifactToken.JiraConfig, atsApi.getAtsBranch());
      return "Bearer " + atsApi.getAttributeResolver().getSoleAttributeValue(jiraConfig,
         CoreAttributeTypes.GeneralStringData, "");
   }

   private String getJiraUrl() {
      ArtifactToken jiraConfig =
         atsApi.getQueryService().getArtifact(AtsArtifactToken.JiraConfig, atsApi.getAtsBranch());
      return atsApi.getAttributeResolver().getSoleAttributeValue(jiraConfig, CoreAttributeTypes.Description, "");
   }

   public int getNumberOfResults(String searchResults) {
      Matcher m = TOTAL_RESULTS.matcher(searchResults);
      if (m.find()) {
         return Integer.parseInt(m.group(1));
      } else {
         throw new OseeCoreException("Failed to parse number of JIRA results");
      }
   }

   public int getMaxResults(String searchResults) {
      Matcher m = MAX_RESULTS.matcher(searchResults);
      if (m.find()) {
         return Integer.parseInt(m.group(1));
      } else {
         throw new OseeCoreException("Failed to parse max possible number of JIRA results");
      }
   }

   public String getSoleIssueKey(String searchResults) {
      int numResults = getNumberOfResults(searchResults);
      if (numResults != 1) {
         throw new OseeCoreException("More than one results for issue");
      }
      Matcher m = ISSUE_KEY.matcher(searchResults);
      if (m.find()) {
         return m.group(1);
      } else {
         throw new OseeCoreException("Could not find the issue key");
      }
   }

   public Map<String, String> getIssueNameToKey(String searchResults) {
      HashMap<String, String> nameToKey = new HashMap<String, String>();
      int numResults = Math.min(getNumberOfResults(searchResults), getMaxResults(searchResults));

      String issueName = "";
      String issueKey = "";
      Matcher nameMatcher = ISSUE_NAME.matcher(searchResults);
      Matcher keyMatcher = ISSUE_KEY.matcher(searchResults);

      for (int i = 1; i <= numResults; i++) {
         if (nameMatcher.find()) {
            issueName = nameMatcher.group(1);
         }
         if (keyMatcher.find()) {
            issueKey = keyMatcher.group(1);
         }
         nameToKey.put(issueName, issueKey);
      }
      return nameToKey;
   }
}