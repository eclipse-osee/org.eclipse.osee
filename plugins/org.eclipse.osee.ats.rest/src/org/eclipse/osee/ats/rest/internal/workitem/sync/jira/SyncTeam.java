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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class SyncTeam {

   Collection<IAtsTeamDefinition> teamDefs = new ArrayList<>();
   Map<String, SyncSprint> jiraSprintStrToSyncSprints = new HashMap<>();
   Map<String, JiraTask> atsIdToTask = new HashMap<>();
   Collection<SyncSprint> syncSprints = new ArrayList<>();
   IAgileTeam agileTeam;
   private final Long agileTeamId = 6915488L;
   private final XResultData results = new XResultData();
   private final List<IAtsTeamWorkflow> backlogTeamWfs = new ArrayList<>();
   private final List<IAtsTeamWorkflow> jiraTeamWfs = new ArrayList<>();

   public SyncTeam() {
   }

   public Long getAgileTeamId() {
      return agileTeamId;
   }

   public Collection<IAtsTeamDefinition> getTeamDefs() {
      return teamDefs;
   }

   public void setTeamDefs(Collection<IAtsTeamDefinition> teamDefs) {
      this.teamDefs = teamDefs;
   }

   public IAgileTeam getAgileTeam() {
      return agileTeam;
   }

   public void setAgileTeam(IAgileTeam agileTeam) {
      this.agileTeam = agileTeam;
   }

   public XResultData getResults() {
      return results;
   }

   public SyncSprint getOrCreateSyncSprint(String jiraSprint) {
      SyncSprint syncSprint = jiraSprintStrToSyncSprints.get(jiraSprint);
      if (syncSprint == null) {
         boolean found = false;
         for (SyncSprint sSprint : syncSprints) {
            if (sSprint.getSprint().getName().contains(jiraSprint)) {
               sSprint.setJiraSprintName(jiraSprint);
               syncSprint = sSprint;
               jiraSprintStrToSyncSprints.put(jiraSprint, sSprint);
               found = true;
               break;
            }
         }
         if (!found) {
            syncSprint = new SyncSprint();
            syncSprints.add(syncSprint);
            syncSprint.setJiraSprintName(jiraSprint);
            jiraSprintStrToSyncSprints.put(jiraSprint, syncSprint);
         }
      }
      return syncSprint;
   }

   public SyncSprint getOrCreateSyncSprint(IAgileSprint sprint) {
      SyncSprint syncSprint = new SyncSprint();
      syncSprint.setSprint(sprint);
      syncSprints.add(syncSprint);
      return syncSprint;
   }

   public void addTeamDef(IAtsTeamDefinition teamDef) {
      teamDefs.add(teamDef);
   }

   public Map<String, JiraTask> getAtsIdToTask() {
      return atsIdToTask;
   }

   public void setAtsIdToTask(Map<String, JiraTask> atsIdToTask) {
      this.atsIdToTask = atsIdToTask;
   }

   public void addAtsIdToTask(String atsId, JiraTask jTask) {
      this.getAtsIdToTask().put(atsId, jTask);
   }

   public SyncSprint getSyncSprint(String sprint) {
      return jiraSprintStrToSyncSprints.get(sprint);
   }

   public void addBacklogTeamWf(IAtsTeamWorkflow teamWf) {
      this.backlogTeamWfs.add(teamWf);
   }

   public void addJiraTeamWf(IAtsTeamWorkflow teamWf) {
      this.jiraTeamWfs.add(teamWf);
   }

   public List<IAtsTeamWorkflow> getBacklogTeamWfs() {
      return backlogTeamWfs;
   }

   public List<IAtsTeamWorkflow> getJiraTeamWfs() {
      return jiraTeamWfs;
   }

   public Collection<SyncSprint> getSyncSprints() {
      return syncSprints;
   }
}
