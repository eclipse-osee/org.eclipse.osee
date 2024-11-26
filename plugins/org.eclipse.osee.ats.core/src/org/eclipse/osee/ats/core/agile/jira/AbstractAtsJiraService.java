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

package org.eclipse.osee.ats.core.agile.jira;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.jira.AtsJiraService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsJiraService implements AtsJiraService {

   protected String TW_DESCRIPTION_SEARCH =
      "{ \"jql\": \"team = %s AND description ~ %s \", \"startAt\": 0, \"maxResults\": 4, " //
         + "\"fields\": [ \"summary\", \"description\", \"status\", \"assignee\" ] }";
   protected String STORY_ID_SEARCH = "{ \"jql\": \"key = %s\" , \"startAt\": 0, \"maxResults\": 4, " //
      + "\"fields\": [ \"summary\", \"description\", \"status\", \"assignee\" ] }";
   // Fields found at: rest/api/2/issue/createmeta/<project>/issuetypes/10001
   protected final String STATUS_ISSUE = "{\"transition\":{\"id\":\"%s\"}}";

   protected final AtsApi atsApi;

   public AbstractAtsJiraService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getStatus(IAtsTeamWorkflow teamWf) {
      String status = "";
      JiraSearch twSearch = search(teamWf);
      if (twSearch != null && twSearch.issues != null && !twSearch.issues.isEmpty()) {
         status = twSearch.issues.iterator().next().fields.status.name;
      }
      return status;
   }

   @Override
   public boolean isJiraStoryWorkflow(IAtsWorkItem workItem) {
      return Strings.isValid(getJiraStoryLink(workItem));
   }

   @Override
   public String getJiraBasePath() {
      return AtsApiService.get().getConfigValue(JIRA_BASEPATH_KEY);
   }

   @Override
   public Long getJiraSprintId(IAgileSprint sprint, XResultData rd) {
      Long jiraSprintId =
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(sprint, AtsAttributeTypes.JiraSprintId, 0L);
      if (jiraSprintId == 0) {
         // Temporary storage till JiraSprintId can move through releases
         String sprintConfigTag = AtsAttributeTypes.JiraSprintId.getName() + "_" + sprint.getIdString();
         String sprintIdStr = AtsApiService.get().getConfigValue(sprintConfigTag);
         if (Strings.isNumeric(sprintIdStr)) {
            jiraSprintId = Long.valueOf(sprintIdStr);
         } else {
            rd.errorf("No JIRA SprintId Configured for Sprint %s", sprint.toStringWithId());
            return null;
         }
      }
      return jiraSprintId;
   }

   @Override
   public AtsUser getJiraAssignee(IAtsWorkItem workItem) {
      JiraSearch srch = search(workItem);
      if (!srch.issues.isEmpty() && srch.issues != null) {
         String userId = srch.issues.iterator().next().getAssigneeUserId();
         if (Strings.isValid(userId)) {
            AtsUser user = AtsApiService.get().getUserService().getUserByLoginId(userId);
            return user;
         }
      }
      return AtsCoreUsers.UNASSIGNED_USER;
   }

   /**
    * Search by JIRA Story Id if available. Else, search by TW num in description
    */
   @Override
   public JiraSearch search(IAtsWorkItem workItem) {
      try {
         String jiraStoryId = getJiraStoryLink(workItem);
         String json = "";
         if (Strings.isValid(jiraStoryId)) {
            json = getStorySearchJson(workItem, jiraStoryId);
         } else {
            json = getTwSearchJson(workItem, getJiraTeamId(workItem));
         }
         String searchResults = searchJira(json);
         if (searchResults.contains("errorMessages")) {
            JiraSearch srch = new JiraSearch();
            srch.getRd().errorf(searchResults);
            return srch;
         }
         JiraSearch srch = JsonUtil.readValue(searchResults, JiraSearch.class);
         return srch;
      } catch (Exception ex) {
         JiraSearch srch = new JiraSearch();
         srch.getRd().errorf(Lib.exceptionToString(ex));
         return srch;
      }
   }

   @Override
   public String getJiraStoryLink(IAtsWorkItem workItem) {
      String jiraStoryId =
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.JiraStoryId, "");
      return jiraStoryId;
   }

   protected abstract String searchJira(String json);

   @Override
   public String getStorySearchJson(IAtsWorkItem workItem, String jiraStoreId) {
      String json = String.format(STORY_ID_SEARCH, jiraStoreId);
      return json;
   }

   @Override
   public String getTwSearchJson(IAtsWorkItem workItem, Integer teamId) {
      String json = String.format(TW_DESCRIPTION_SEARCH, teamId.toString(), workItem.getAtsId());
      return json;
   }

   @Override
   public Integer getJiraTeamId(IAtsWorkItem workItem) {
      Integer teamId = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(
         workItem.getParentTeamWorkflow().getTeamDefinition(), AtsAttributeTypes.JiraTeamId, -1L).intValue();
      if (teamId <= 0) {
         throw new OseeArgumentException("Not JIRA Team Id specified for %s",
            workItem.getParentTeamWorkflow().getTeamDefinition().toStringWithId());
      }
      return teamId;
   }

}
