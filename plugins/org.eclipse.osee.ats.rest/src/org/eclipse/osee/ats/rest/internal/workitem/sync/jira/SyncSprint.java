/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.sync.jira;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public class SyncSprint {

   IAgileSprint sprint;
   IAgileTeam team;
   Collection<IAtsTeamWorkflow> teamWfsInSprint = new ArrayList<>();
   Collection<JiraTask> jiraTasksInSprint = new ArrayList<>();
   String jiraSprintName;

   public SyncSprint() {
   }

   public IAgileSprint getSprint() {
      return sprint;
   }

   public void setSprint(IAgileSprint sprint) {
      this.sprint = sprint;
   }

   public IAgileTeam getTeam() {
      return team;
   }

   public void setTeam(IAgileTeam team) {
      this.team = team;
   }

   public Collection<IAtsTeamWorkflow> getTeamWfsInSprint() {
      return teamWfsInSprint;
   }

   public void setTeamWfsInSprint(Collection<IAtsTeamWorkflow> teamWfsInSprint) {
      this.teamWfsInSprint = teamWfsInSprint;
   }

   public String getJiraSprintName() {
      return jiraSprintName;
   }

   public void setJiraSprintName(String jiraSprintName) {
      this.jiraSprintName = jiraSprintName;
   }

   public Collection<JiraTask> getJiraTasksInSprint() {
      return jiraTasksInSprint;
   }

   public void setJiraTasksInSprint(Collection<JiraTask> jiraTasksInSprint) {
      this.jiraTasksInSprint = jiraTasksInSprint;
   }

   public void addJiraTask(JiraTask jTask) {
      jiraTasksInSprint.add(jTask);
   }

}
