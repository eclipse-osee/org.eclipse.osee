/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * - Goto JIRA backlog</br>
 * - Select blue sheet icon w/ wavy lines</br>
 * - Change criteria "team=20"</br>
 * - Export XML</br>
 * - Right-click in browser > Save As > Desktop/jira.xml</br>
 * - Run report from ats/action/sync/jira url</br>
 * - Run persist from ats/action/sync/jira/persist url</br>
 * </br>
 *
 * @author Donald G. Dunne
 */
public class SyncJiraOperation {

   private final AtsApi atsApi;
   private XResultData results;
   private List<JiraTask> jTasks;
   private final Set<String> atsIds = new HashSet<>();
   private final SyncTeam syncTeam;
   private final boolean fixSprint = false;
   private final boolean debugJiraTextCleanup = false;
   private final String SKIP_JIRA_SYNC = "skipJiraSync";

   public SyncJiraOperation(AtsApi atsApi, SyncTeam syncTeam, boolean reportOnly) {
      this.atsApi = atsApi;
      this.syncTeam = syncTeam;
      this.results = syncTeam.getResults();
   }

   public XResultData run() {

      ElapsedTime allTime = new ElapsedTime(getClass().getSimpleName(), true);

      ElapsedTime time = new ElapsedTime("loadTeamDefsAndSprints", true);
      loadTeamDefsAndSprints();
      time.end();
      if (results.isErrors()) {
         return results;
      }

      time.start("loadJiraTasks");
      loadJiraTasks();
      time.end();
      if (results.isErrors()) {
         return results;
      }

      time.start("loadWfs");
      loadWfs();
      time.end();
      if (results.isErrors()) {
         return results;
      }

      time.start("checkJiraClosedToOseeOpen");
      checkJiraClosedToOseeOpen();
      time.end();

      time.start("checkJiraOpenToOseeClosed");
      checkJiraOpenToOseeClosed();
      time.end();

      time.start("validateWorkflowsNotInJira");
      validateWorkflowsNotInJira();
      time.end();

      time.start("validateSprints");
      validateSprints();
      time.end();

      time.start("printSprints");
      printSprints();
      time.end();

      allTime.end(Units.MIN);
      return results;
   }

   private void printSprints() {
      results.logf("");
      results.logf("");
      results.logf("================================================================");
      results.logf("Sprints");
      results.logf("");
      for (SyncSprint sSprint : syncTeam.getSyncSprints()) {
         if (sSprint.sprint == null) {
            results.errorf("Sprint [%s] has null sprint", sSprint.getJiraSprintName());
         } else if (!atsApi.getAttributeResolver().getAttributesToStringList(sSprint.sprint.getStoreObject(),
            AtsAttributeTypes.Category1).contains(SKIP_JIRA_SYNC)) {
            results.logf("");
            results.logf("Sprint [%s]", sSprint.getJiraSprintName());
            Set<String> ids = new HashSet<>();
            for (JiraTask jTask : sSprint.getJiraTasksInSprint()) {
               results.logf("[%s] - %s-[%s]", jTask.getjSprint(), jTask.getAtsIds(), jTask.getSummary());
               ids.addAll(jTask.getAtsIds());
            }
            results.logf("Ids: %s", Collections.toString(",", ids));
         }
      }
      results.logf("================================================================");
   }

   private void validateWorkflowsNotInJira() {
      results.logf("");
      results.logf("");
      results.logf("================================================================");
      results.logf("Validate Workflows Not in JIRA don't belong to Sprint");
      results.logf("");
      Collection<IAtsTeamWorkflow> teamWfsNotInJira =
         Collections.setComplement(syncTeam.getBacklogTeamWfs(), syncTeam.getJiraTeamWfs());
      StringBuilder ids = new StringBuilder();
      for (IAtsTeamWorkflow teamWf : teamWfsNotInJira) {
         if (!skipJiraSync(teamWf)) {
            IAgileSprint sprint = atsApi.getAgileService().getSprint(teamWf);
            if (sprint != null) {
               results.logf("   ERROR: Workflow shouldn't belong to sprint [%s] - %s", sprint.getName(),
                  teamWf.toStringWithId());
               ids.append(teamWf.getAtsId() + ",");
            }
         }
      }
      results.logf("Ids: %s", ids.toString().replaceFirst(",$", ""));
      results.logf("================================================================");
   }

   private void loadTeamDefsAndSprints() {
      IAgileTeam aTeam = atsApi.getAgileService().getAgileTeam(syncTeam.getAgileTeamId());
      syncTeam.setAgileTeam(aTeam);
      for (IAgileSprint sprint : atsApi.getAgileService().getAgileSprints(aTeam)) {
         if (sprint.getName().contains("AMS")) {
            syncTeam.getOrCreateSyncSprint(sprint);
         }
      }

      for (IAtsTeamDefinition teamDef : atsApi.getAgileService().getAtsTeams(aTeam)) {
         syncTeam.addTeamDef(teamDef);
      }
   }

   public XResultData loadWfs() {
      IAgileBacklog backlog = atsApi.getAgileService().getAgileBacklog(syncTeam.getAgileTeam());
      for (IAgileItem aItem : atsApi.getAgileService().getItems(backlog)) {
         ArtifactToken art = aItem.getArtifactToken();
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(art);
         if (teamWf != null) {
            syncTeam.addBacklogTeamWf(teamWf);
         }
      }
      StringBuilder ids = new StringBuilder();

      results.logf("");
      results.logf("");
      results.logf("================================================================");
      results.log("Ignoring non OSEE Workflows (spot check): ");
      results.logf("");
      for (ArtifactToken teamWfArt : atsApi.getQueryService().getArtifactListFromAttributeValues(
         AtsAttributeTypes.AtsId, atsIds, 500)) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);
         if (teamWf != null && !skipJiraSync(teamWf)) {
            syncTeam.addJiraTeamWf(teamWf);
            if (syncTeam.getTeamDefs().contains(teamWf.getTeamDefinition())) {
               JiraTask jTask = getJiraTask(teamWf.getAtsId());
               jTask.setTeamWf(teamWf);
            } else {
               results.logf("   INFO: Ignoring non OSEE wf %s", teamWf.toStringWithId());
               ids.append(teamWf.getAtsId() + ",");
            }
         }
      }
      results.logf("Ids: %s", ids.toString().replaceFirst(",$", ""));
      results.logf("================================================================");
      return results;
   }

   private JiraTask getJiraTask(String atsId) {
      return syncTeam.getAtsIdToTask().get(atsId);
   }

   private void validateSprints() {
      results.logf("");
      results.logf("");
      results.logf("================================================================");
      results.logf("Sprints Do Not Match, update ATS");
      results.logf("");
      IAtsChangeSet changes = atsApi.createChangeSet("Sync OSEE with JIRA Sprints");
      Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
      for (JiraTask jTask : syncTeam.atsIdToTask.values()) {
         if (jTask.getAtsIds().isEmpty()) {
            results.logf("   ERROR: ATS Id not set for JIRA Task %s", jTask.getSummary());
         } else {
            IAtsTeamWorkflow teamWf = jTask.getTeamWf();
            if (teamWf != null) {
               if (!teamWfs.contains(teamWf)) {
                  teamWfs.add(teamWf);
                  IAgileSprint atsSprint = atsApi.getAgileService().getSprint(teamWf);
                  jTask.setaSprint(atsSprint);
                  SyncSprint sSprint = null;
                  if (Strings.isValid(jTask.getjSprint())) {
                     sSprint = syncTeam.getSyncSprint(jTask.getjSprint());
                  }
                  if (atsSprint == null && sSprint != null) {

                     results.logf("   ERROR: Workflow Sprint [%s] doesn't match JIRA sprint [%s] for workflow %s",
                        atsSprint, jTask.getjSprint(), teamWf.toStringWithId());
                     results.logf("      FIX: SET ATS Sprint to %s", jTask.getjSprint());
                     if (fixSprint) {
                        if (sSprint.getSprint() == null) {
                           results.errorf("No matching ATS Sprint for JIRA Sprint [%s]", sSprint.getJiraSprintName());
                        } else {
                           atsApi.getAgileService().setSprint(teamWf, sSprint.getSprint(), changes);
                        }
                     }

                  } else if (sSprint == null && atsSprint != null) {

                     results.logf("   ERROR: Workflow Sprint [%s] doesn't match JIRA sprint [%s] for workflow %s",
                        atsSprint, jTask.getjSprint(), teamWf.toStringWithId());
                     results.logf("      FIX: REMOVE ATS workflow from sprint %s", atsSprint);
                     if (fixSprint) {
                        atsApi.getAgileService().setSprint(teamWf, null, changes);
                     }

                  } else if (sSprint != null && atsSprint != null) {
                     if (sSprint.getSprint() == null) {
                        results.errorf("   ERROR: sSprint != null but getSprint() == null for %s", sSprint);
                     }
                     // If match, it is good; else error that wrong sprint
                     else if (!sSprint.getSprint().equals(atsSprint)) {
                        results.logf("   ERROR: Workflow Sprint [%s] doesn't match JIRA sprint [%s] for workflow %s",
                           atsSprint, jTask.getjSprint(), teamWf.toStringWithId());
                        results.logf("      FIX: MOVE ATS workflow to sprint %s", sSprint.getSprint());
                        if (fixSprint) {
                           if (sSprint.getSprint() == null) {
                              results.errorf("No matching ATS Sprint for JIRA Sprint [%s]",
                                 sSprint.getJiraSprintName());
                           } else {
                              atsApi.getAgileService().setSprint(teamWf, sSprint.getSprint(), changes);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      if (results.isErrors()) {
         return;
      }
      if (fixSprint) {
         changes.executeIfNeeded();
         results.log("Sprints Fixed");
      }
      results.logf("================================================================");
   }

   private void checkJiraOpenToOseeClosed() {
      results.logf("");
      results.logf("");
      results.logf("================================================================");
      results.logf("JIRA Open and OSEE Closed (close in JIRA)", syncTeam.getJiraTeamWfs().size());
      results.logf("");
      StringBuilder ids = new StringBuilder();
      for (JiraTask jTask : jTasks) {
         IAtsTeamWorkflow teamWf = jTask.getTeamWf();
         if (teamWf != null) {
            if (!skipJiraSync(teamWf) && teamWf.isCompletedOrCancelled() && !jTask.getStatus().equals("Closed")) {
               results.logf("   ERROR: JIRA Task Open, Team Wf Closed [%s] %s", teamWf.getStateDefinition().getName(),
                  teamWf.toStringWithId());
               for (String atsId : jTask.getAtsIds()) {
                  ids.append(atsId + ",");
               }
            }
         }
      }
      results.logf("Ids: %s", ids.toString().replaceFirst(",$", ""));
      results.logf("================================================================");
   }

   private void checkJiraClosedToOseeOpen() {
      results.logf("");
      results.logf("");
      results.logf("================================================================");
      results.logf("JIRA Closed and OSEE Open (spot check; close in OSEE)", syncTeam.getJiraTeamWfs().size());
      results.logf("");
      StringBuilder ids = new StringBuilder();
      for (JiraTask jTask : jTasks) {
         IAtsTeamWorkflow teamWf = jTask.getTeamWf();
         if (teamWf != null) {
            if (!skipJiraSync(teamWf) && jTask.getStatus().equals("Closed") && teamWf.isInWork()) {
               results.logf("   ERROR: JIRA Task Closed, OSEE is Open [%s] %s", teamWf.getStateDefinition().getName(),
                  teamWf.toStringWithId());
               for (String atsId : jTask.getAtsIds()) {
                  ids.append(atsId + ",");
               }
            }
         }
      }
      results.logf("Ids: %s", ids.toString().replaceFirst(",$", ""));
      results.logf("================================================================");
   }

   private boolean skipJiraSync(IAtsTeamWorkflow teamWf) {
      return atsApi.getAttributeResolver().getAttributesToStringList(teamWf, AtsAttributeTypes.Category1).contains(
         SKIP_JIRA_SYNC);
   }

   private final static Pattern ITEM_CHECKED_PATTERN =
      Pattern.compile("<item checked.*?</item>", Pattern.MULTILINE | Pattern.DOTALL);
   private final static Pattern ITEM_PATTERN = Pattern.compile("<item(.*?)</item>", Pattern.MULTILINE | Pattern.DOTALL);

   public XResultData loadJiraTasks() {
      results = syncTeam.getResults();
      try {
         String home = System.getenv("HOMEPATH");
         File file2 = new File(home + "\\Desktop\\jira.xml");
         if (!file2.exists()) {
            results.errorf("File [%s] does not exist", file2.getAbsolutePath());
            return results;
         }

         // Get rid of all tags/contents that include "item" tags so "item" matcher matches correctly
         String fileXml = Lib.fileToString(file2);
         int preSize = fileXml.length();
         Matcher m = ITEM_CHECKED_PATTERN.matcher(fileXml);
         fileXml = m.replaceAll("");

         // Write to a file that can be compared
         if (debugJiraTextCleanup) {
            File file3 = new File(home + "\\Desktop\\jira2.xml");
            Lib.writeStringToFile(fileXml, file3);
         }

         // These collide with search for <item></item> blocks
         int postSize = fileXml.length();
         if (preSize == postSize) {
            results.errorf("Does not look like checked items were removed");
            return results;
         }

         jTasks = new ArrayList<JiraTask>();
         m = ITEM_PATTERN.matcher(fileXml);
         int lineNum = 1;
         while (m.find()) {
            try {
               String xmlStr = m.group();
               String title = AXml.getTagData(xmlStr, "title");
               System.out.println(String.format("line %s : %s", lineNum, title));
               JiraTask task = new JiraTask();
               task.setSummary(title);
               findStatus(task, xmlStr);
               findAtsId(task, xmlStr);
               findSprint(task, xmlStr);
               jTasks.add(task);
            } catch (Exception ex) {
               results.errorf("Error on line %s: Exception %s", lineNum, ex.getLocalizedMessage());
            }
            lineNum++;
         }
      } catch (IOException ex) {
         results.errorf("Exception %s", Lib.exceptionToString(ex));
      }
      if (jTasks.isEmpty()) {
         results.error("No tasks found");
      }
      return results;
   }

   private static Pattern sprintPattern = Pattern.compile("AMS [0-9\\.]{3,4}");

   private void findSprint(JiraTask jTask, String line) {
      Matcher m = sprintPattern.matcher(line);
      if (m.find()) {
         String jiraSprint = m.group();
         jTask.setjSprint(jiraSprint);
         SyncSprint syncSprint = syncTeam.getOrCreateSyncSprint(jiraSprint);
         syncSprint.addJiraTask(jTask);
      }
   }

   private static Pattern statusPattern = Pattern.compile(">(.*?)</status>");

   private void findStatus(JiraTask task, String line) {
      Matcher m = statusPattern.matcher(line);
      if (m.find()) {
         String status = m.group(1);
         if (Strings.isValid(status)) {
            task.setStatus(status);
         } else {
            results.errorf("Can't retrive status for task [%s]", line);
         }
      }
      return;
   }
   private static Pattern atsTwIdPattern = Pattern.compile("TW[0-9]{5}");
   private static Pattern atsIdPattern = Pattern.compile("ATS[0-9]{6}");

   private void findAtsId(JiraTask task, String line) {
      Matcher m = atsIdPattern.matcher(line);
      String atsId = null;
      while (m.find()) {
         atsId = m.group();
         task.addAtsId(atsId);
         atsIds.add(atsId);
         syncTeam.addAtsIdToTask(atsId, task);
      }
      m = atsTwIdPattern.matcher(line);
      while (m.find()) {
         atsId = m.group();
         task.addAtsId(atsId);
         atsIds.add(atsId);
         syncTeam.addAtsIdToTask(atsId, task);
      }
   }

   public static String getAtsId(String str) {
      Matcher m = atsIdPattern.matcher(str);
      String atsId = null;
      while (m.find()) {
         atsId = m.group();
      }
      m = atsTwIdPattern.matcher(str);
      while (m.find()) {
         atsId = m.group();
      }
      return atsId;
   }

}
