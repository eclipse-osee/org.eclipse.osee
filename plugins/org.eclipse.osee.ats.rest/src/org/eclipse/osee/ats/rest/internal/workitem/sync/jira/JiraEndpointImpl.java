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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.jira.JiraEndpoint;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */

public class JiraEndpointImpl implements JiraEndpoint {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;
   static final String JIRA_SEARCH = "/rest/api/2/search";
   static final String JIRA_ISSUE = "/rest/api/2/issue";

   public JiraEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
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
         throw new OseeAuthenticationException("JIRA Operation Failed", ex);
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
      return response.toString();
   }

   private String getPersonalAccessToken() {
      return "Bearer " + orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andIsOfType(
         CoreArtifactTypes.GeneralData).andNameEquals("JIRA Config").getArtifact().getSoleAttributeValue(
            CoreAttributeTypes.GeneralStringData);
   }

   private String getJiraUrl() {
      return orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andIsOfType(
         CoreArtifactTypes.GeneralData).andNameEquals("JIRA Config").getArtifact().getSoleAttributeValue(
            CoreAttributeTypes.Description);
   }
}