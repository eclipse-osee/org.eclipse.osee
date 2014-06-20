/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.navigate.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Donald G. Dunne
 */
public class ExtendedStatusReportJob extends Job {
   private final List<Artifact> arts;

   public ExtendedStatusReportJob(String title, List<Artifact> arts) {
      super("Creating " + title);
      this.arts = arts;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      return runIt(monitor, getName(), arts);
   }

   public static IStatus runIt(IProgressMonitor monitor, final String jobName, Collection<? extends Artifact> teamArts) {
      if (teamArts.isEmpty()) {
         OseeLog.log(Activator.class, Level.SEVERE, "No Artifacts Returned");
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "No Artifacts Returned", null);
      }
      try {

         final String html = AHTML.simplePage(getStatusReport(monitor, jobName, teamArts));
         ResultsEditor.open(new XResultPage(jobName + " - " + DateUtil.getMMDDYYHHMM(), html,
            Manipulations.HTML_MANIPULATIONS));
         AWorkbench.popup("Complete", jobName + " Complete...Results in ATS Results");
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
   }

   private static String getStatusReport(IProgressMonitor monitor, String title, Collection<? extends Artifact> teamArts) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title));
      sb.append(getStatusReportBody(monitor, teamArts));
      return sb.toString();
   }

   private static enum Columns {
      Priority,
      Change_Type,
      Team,
      Type,
      ActionId,
      TeamId,
      TaskId,
      Title,
      Analysis,
      Originator,
      Assignees,
      Status_State,
      Date_Created,
      Version;

      @Override
      public String toString() {
         return name().replaceAll("_", " ");
      }

      public static String[] getColumnNames() {
         ArrayList<String> names = new ArrayList<String>();
         for (Columns col : values()) {
            names.add(col.toString());
         }
         return names.toArray(new String[names.size()]);
      }
   };

   private static String getStatusReportBody(IProgressMonitor monitor, Collection<? extends Artifact> arts) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(Columns.getColumnNames()));
      int x = 1;
      for (Artifact art : arts) {
         if (art.isOfType(AtsArtifactTypes.Action)) {
            Artifact actionArt = art;
            String str = String.format("Processing %s/%s \"%s\"", x++ + "", arts.size(), actionArt.getName());
            monitor.subTask(str);
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(actionArt)) {
               addTableRow(sb, team);
               for (TaskArtifact taskArt : team.getTaskArtifacts()) {
                  addTableRow(sb, taskArt);
               }
            }
         }
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private static void addTableRow(StringBuilder sb, AbstractWorkflowArtifact sma) throws OseeCoreException {
      List<String> values = new ArrayList<String>();
      for (Columns col : Columns.values()) {
         // System.out.println("col *" + col + "*");
         if (col == Columns.ActionId) {
            values.add(sma.getParentActionArtifact().getAtsId());
         } else if (col == Columns.TeamId) {
            handleTeamIdColumn(sma, values);
         } else if (col == Columns.TaskId) {
            handleTaskIdColumn(sma, values);
         } else if (col == Columns.Team) {
            handleTeamColumn(sma, values);
         } else if (col == Columns.Type) {
            values.add(sma.getArtifactTypeName());
         } else if (col == Columns.Priority) {
            String priStr = sma.getSoleAttributeValue(AtsAttributeTypes.PriorityType, "");
            values.add(priStr.equals("") ? "." : priStr);
         } else if (col == Columns.Change_Type) {
            ChangeType changeType = ChangeTypeUtil.getChangeType(sma);
            values.add((changeType == ChangeType.None ? "." : changeType.name()));
         } else if (col == Columns.Title) {
            values.add(sma.getName());
         } else if (col == Columns.Analysis) {
            handleAnalysisColumn(sma, values);
         } else if (col == Columns.Originator) {
            handleOriginatorColumn(sma, values);
         } else if (col == Columns.Assignees) {
            values.add(AtsObjects.toString("; ", sma.getStateMgr().getAssignees()));
         } else if (col == Columns.Status_State) {
            values.add(sma.getStateMgr().getCurrentStateName());
         } else if (col == Columns.Date_Created) {
            values.add(CreatedDateColumn.getDateStr(sma));
         } else if (col == Columns.Version) {
            values.add((!Strings.isValid(Versions.getTargetedVersionStr(sma)) ? "." : Versions.getTargetedVersionStr(sma)));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
   }

   private static void handleTeamColumn(AbstractWorkflowArtifact sma, List<String> values) {
      if (sma.isTeamWorkflow()) {
         values.add(((TeamWorkFlowArtifact) sma).getTeamName());
      } else {
         values.add(".");
      }
   }

   private static void handleTaskIdColumn(AbstractWorkflowArtifact awa, List<String> values) {
      if (awa.isOfType(AtsArtifactTypes.Task)) {
         values.add(((TaskArtifact) awa).getAtsId());
      } else {
         values.add(".");
      }
   }

   private static void handleTeamIdColumn(AbstractWorkflowArtifact sma, List<String> values) {
      if (sma.isTeamWorkflow()) {
         values.add(sma.getAtsId());
      } else {
         values.add(".");
      }
   }

   private static void handleOriginatorColumn(AbstractWorkflowArtifact sma, List<String> values) throws OseeCoreException {
      if (sma.getCreatedBy() == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't retrieve orig for " + sma.getAtsId());
         values.add(".");
      } else {
         values.add(sma.getCreatedBy().getName());
      }
   }

   private static void handleAnalysisColumn(AbstractWorkflowArtifact awa, List<String> values) {
      String desc = awa.getDescription();
      if (awa.isOfType(AtsArtifactTypes.Task)) {
         TaskArtifact taskArt = (TaskArtifact) awa;
         desc = taskArt.getDescription() + " " + taskArt.getCurrentStateName();
      }
      if (desc.matches("^ *$")) {
         values.add(".");
      } else {
         values.add(desc);
      }
   }

}
