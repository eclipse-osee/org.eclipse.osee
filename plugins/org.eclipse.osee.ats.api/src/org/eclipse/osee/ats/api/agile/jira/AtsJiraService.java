/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile.jira;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface AtsJiraService {

   public static String JIRA_BASEPATH_KEY = "JiraBasepath";

   String getStatus(IAtsTeamWorkflow teamWf);

   JiraSearch search(IAtsWorkItem workItem);

   Integer getJiraTeamId(IAtsWorkItem workItem);

   AtsUser getJiraAssignee(IAtsWorkItem workItem);

   String getTwSearchJson(IAtsWorkItem workItem, Integer teamId);

   Long getJiraSprintId(IAgileSprint sprint, XResultData rd);

   String getJiraBasePath();

   String getStorySearchJson(IAtsWorkItem workItem, String jiraStoreId);

   String getJiraStoryLink(IAtsWorkItem workItem);

   boolean isJiraStoryWorkflow(IAtsWorkItem workItem);

   XResultData transition(IAtsTeamWorkflow teamWf, int statusId, XResultData rd);

   String editJira(String json, String jiraStoryId);

}
