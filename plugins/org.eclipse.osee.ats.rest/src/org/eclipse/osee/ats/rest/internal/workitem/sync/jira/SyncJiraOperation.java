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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * - Goto JIRA backlog</br>
 * - Select blue sheet icon w/ wavy lines</br>
 * - Change criteria "team=20"</br>
 * - Export CSV</br>
 * - All Fields</br>
 * - Save as jira.csv to desktop</br>
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
   private final Map<String, JiraTask> atsIdToJTask = new HashMap<String, JiraTask>();
   private final Set<String> atsIds = new HashSet<>();
   private final HashCollection<String, JiraTask> sprintToTasks = new HashCollection<>();

   public SyncJiraOperation(AtsApi atsApi, boolean reportOnly) {
      this.atsApi = atsApi;
   }

   public XResultData run() {
      readTasks();
      loadWfs();
      checkJiraClosedToOseeOpen();
      checkJiraOpenToOseeClosed();
      groupSprints();
      return results;
   }

   public XResultData loadWfs() {
      teamWfArts = atsApi.getQueryService().getArtifactListFromAttributeValues(AtsAttributeTypes.AtsId, atsIds, 500);
      for (ArtifactToken teamWfArt : teamWfArts) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);
         JiraTask jTask = atsIdToJTask.get(teamWf.getAtsId());
         jTask.setTeamWf(teamWf);
      }
      return results;
   }

   private void groupSprints() {
      for (JiraTask jTask : jTasks) {
         sprintToTasks.put(jTask.getSprint(), jTask);
      }

      List<String> sprintNames = new ArrayList<>();
      for (String sprint : sprintToTasks.keySet()) {
         if (Strings.isValid(sprint)) {
            sprintNames.add(sprint);
         }
      }
      Collections.sort(sprintNames);

      for (String sprintName : sprintNames) {
         List<JiraTask> jTasks = sprintToTasks.getValues(sprintName);
         StringBuilder ids = new StringBuilder();
         if (jTasks.size() <= 0) {
            results.logf("Sprint [%s] - No Tasks", sprintName);
         } else {
            results.log("");
            results.logf("Sprint [%s]", sprintName);
            for (JiraTask jTask : jTasks) {
               results.logf("   %s", jTask.toString());
               if (Strings.isValid(jTask.getAtsId())) {
                  results.logf("      ERROR: ATS Id not set for JIRA Task %s", jTask.getSummary());
               } else {
                  IAtsTeamWorkflow teamWf = jTask.getTeamWf();
                  if (teamWf != null) {
                     IAgileSprint sprint = atsApi.getAgileService().getSprint(teamWf);
                     boolean noWfSprint = sprint == null;
                     if (noWfSprint) {
                        results.logf("      ERROR: Workflow Sprint not set for %s", teamWf.toStringWithId());
                     } else if (sprint == null || !sprint.getName().contains(jTask.getSprint())) {
                        results.logf("      ERROR: Workflow Sprint [%s] doesn't match JIRA sprint [%s] for workflow %s",
                           sprint, jTask.getSprint(), teamWf.toStringWithId());
                     }
                  }
               }
               if (Strings.isValid(jTask.getAtsId())) {
                  ids.append(jTask.getAtsId() + ",");
               }
            }
            results.logf("Ids: %s", ids.toString().replaceFirst(",$", ""));
         }
      }
   }

   private void checkJiraOpenToOseeClosed() {
      results.log("");
      results.logf("JIRA Open and OSEE Closed", teamWfArts.size());
      StringBuilder ids = new StringBuilder();
      for (JiraTask jTask : jTasks) {
         IAtsTeamWorkflow teamWf = jTask.getTeamWf();
         if (teamWf != null) {
            if (teamWf.isCompletedOrCancelled() && !jTask.getStatus().equals("Closed")) {
               results.errorf("JIRA Task Open, Team Wf Closed [%s] %s", teamWf.getStateDefinition().getName(),
                  teamWf.toStringWithId());
               ids.append(jTask.getAtsId() + ",");
            }
         }
      }
      results.logf("Ids: %s", ids.toString().replaceFirst(",$", ""));
   }

   private void checkJiraClosedToOseeOpen() {
      results.log("");
      results.logf("JIRA Closed and OSEE Open", teamWfArts.size());
      StringBuilder ids = new StringBuilder();
      for (JiraTask jTask : jTasks) {
         IAtsTeamWorkflow teamWf = jTask.getTeamWf();
         if (teamWf != null) {
            if (jTask.getStatus().equals("Closed") && teamWf.isInWork()) {
               results.errorf("JIRA Task Closed, OSEE is Open [%s] %s", teamWf.getStateDefinition().getName(),
                  teamWf.toStringWithId());
               ids.append(jTask.getAtsId() + ",");
            }
         }
      }
      results.logf("Ids: %s", ids.toString().replaceFirst(",$", ""));
   }

   public XResultData readTasks() {
      results = new XResultData();
      try {
         String home = System.getenv("HOMEPATH");
         File file2 = new File(home + "\\Desktop\\jira.csv");
         if (!file2.exists()) {
            results.errorf("File [%s] does not exist", file2.getAbsolutePath());
            return results;
         }
         String file = Lib.fileToString(file2);
         file = file.replaceAll("\r\n", "  ");
         jTasks = new ArrayList<JiraTask>();
         for (String line : file.split("[\n\r]{1,2}")) {
            System.out.println("line: " + line);
            if (Strings.isInValid(line)) {
               continue;
            }
            String[] values = line.split("\\|");
            JiraTask task = new JiraTask();
            task.setSummary(values[0]);
            task.setStatus(values[4]);
            findAtsId(task, line);
            findSprint(task, line);
            jTasks.add(task);
            System.err.println(task);
         }
      } catch (IOException ex) {
         results.errorf("Exception %s", Lib.exceptionToString(ex));
      }
      return results;
   }

   private static Pattern sprint = Pattern.compile("AMS [0-9\\.]{3,4}");

   private void findSprint(JiraTask task, String line) {
      Matcher m = sprint.matcher(line);
      if (m.find()) {
         String sprint = m.group();
         task.setSprint(sprint);
      }
   }

   private static Pattern atsId = Pattern.compile("TW[0-9]{5}");
   private List<ArtifactToken> teamWfArts;

   private void findAtsId(JiraTask task, String line) {
      Matcher m = atsId.matcher(line);
      if (m.find()) {
         String atsId = m.group();
         task.setAtsId(atsId);
         atsIdToJTask.put(atsId, task);
         atsIds.add(atsId);
      }
   }

   public static void main(String[] args) {
      SyncJiraOperation op = new SyncJiraOperation(null, false);
      op.readTasks();
   }

}
