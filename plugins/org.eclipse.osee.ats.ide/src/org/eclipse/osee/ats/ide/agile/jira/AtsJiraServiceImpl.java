/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.agile.jira;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.jira.JiraEndpoint;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.agile.jira.AbstractAtsJiraService;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsJiraServiceImpl extends AbstractAtsJiraService {

   public AtsJiraServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   protected String searchJira(String json) {
      JiraEndpoint jiraEndpoint = AtsApiService.get().getServerEndpoints().getJiraEndpoint();
      String searchResults = jiraEndpoint.searchJira(json);
      return searchResults;
   }

   @Override
   public XResultData transition(IAtsTeamWorkflow teamWf, int statusId, XResultData rd) {
      String transitionJson = java.lang.String.format(STATUS_ISSUE, statusId);
      String jiraStoryId = getJiraStoryLink(teamWf);
      if (Strings.isInvalid(jiraStoryId)) {
         rd.error("Team Workflow not linked to JIRA Story");
         return rd;
      }

      String responseStr =
         AtsApiService.get().getServerEndpoints().getJiraEndpoint().transitionJiraIssue(transitionJson, jiraStoryId);
      if (responseStr.contains("errorMessages")) {
         rd.errorf("%s", responseStr);
      }
      return rd;
   }

   @Override
   public String editJira(String json, String jiraStoryId) {
      return atsApi.getServerEndpoints().getJiraEndpoint().editJira(json, jiraStoryId);
   }

}
