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
package org.eclipse.osee.ats.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ExtendedStatusReportJob extends Job {
   private final ArrayList<Artifact> arts;
   private IProgressMonitor monitor;
   private final String title;

   public ExtendedStatusReportJob(String title, ArrayList<Artifact> arts) {
      super("Creating " + title);
      this.title = title;
      this.arts = arts;

   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      this.monitor = monitor;
      if (arts.size() == 0) {
         OSEELog.logSevere(AtsPlugin.class, "No Artifacts Returned", false);
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "No Artifacts Returned", null);
      }
      try {

         final String html = AHTML.simplePage(getStatusReport());
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               XResultView.getResultView().addResultPage(
                     new XResultPage(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM), html,
                           Manipulations.HTML_MANIPULATIONS));
               AWorkbench.popup("Complete", title + " Complete...Results in ATS Results");
            }
         });
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
   }

   public String getStatusReport() throws OseeCoreException, SQLException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title));
      sb.append(getStatusReportBody());
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

   public String getStatusReportBody() throws OseeCoreException, SQLException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(Columns.getColumnNames()));
      int x = 1;
      for (Artifact art : arts) {
         if (art instanceof ActionArtifact) {
            ActionArtifact actionArt = (ActionArtifact) art;
            String str =
                  String.format("Processing %s/%s \"%s\"", x++ + "", arts.size(), actionArt.getDescriptiveName());
            System.out.println(str);
            monitor.subTask(str);
            for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
               addTableRow(sb, team);
               SMAManager teamSmaMgr = new SMAManager(team);
               for (TaskArtifact taskArt : teamSmaMgr.getTaskMgr().getTaskArtifacts("Implement")) {
                  addTableRow(sb, taskArt);
               }
            }
         }
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   public void addTableRow(StringBuilder sb, StateMachineArtifact sma) throws OseeCoreException, SQLException {
      List<String> values = new ArrayList<String>();
      SMAManager smaMgr = new SMAManager(sma);
      for (Columns col : Columns.values()) {
         // System.out.println("col *" + col + "*");
         if (col == Columns.ActionId)
            values.add(sma.getParentActionArtifact().getHumanReadableId());
         else if (col == Columns.TeamId) {
            if (sma instanceof TeamWorkFlowArtifact)
               values.add(sma.getHumanReadableId());
            else
               values.add(".");
         } else if (col == Columns.TaskId) {
            if (sma instanceof TaskArtifact)
               values.add(((TaskArtifact) sma).getHumanReadableId());
            else
               values.add(".");
         } else if (col == Columns.Team) {
            if (sma instanceof TeamWorkFlowArtifact)
               values.add(((TeamWorkFlowArtifact) sma).getTeamName());
            else
               values.add(".");
         } else if (col == Columns.Type)
            values.add(sma.getArtifactTypeName());
         else if (col == Columns.Priority) {
            values.add((sma.getWorldViewPriority().equals("") ? "." : sma.getWorldViewPriority()));
         } else if (col == Columns.Change_Type) {
            values.add((sma.getWorldViewChangeType().name().equals("") ? "." : sma.getWorldViewChangeType().name()));
         } else if (col == Columns.Title)
            values.add(sma.getDescriptiveName());
         else if (col == Columns.Analysis) {
            String desc = sma.getDescription();
            if (sma instanceof TaskArtifact) {
               TaskArtifact taskArt = (TaskArtifact) sma;
               desc =
                     taskArt.getDescription() + " " + taskArt.getSoleAttributeValue(
                           ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), "");
            }
            if (desc.matches("^ *$"))
               values.add(".");
            else
               values.add(desc);
         } else if (col == Columns.Originator) {
            if (smaMgr.getOriginator() == null) {
               OSEELog.logSevere(AtsPlugin.class, "Can't retrieve orig for " + sma.getHumanReadableId(), true);
               values.add(".");
            } else
               values.add(smaMgr.getOriginator().getName());
         } else if (col == Columns.Assignees)
            values.add(Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees()));
         else if (col == Columns.Status_State)
            values.add(smaMgr.getStateMgr().getCurrentStateName());
         else if (col == Columns.Date_Created)
            values.add(sma.getWorldViewCreatedDateStr());
         else if (col == Columns.Version) {
            values.add((sma.getWorldViewVersion() == null || sma.getWorldViewVersion().equals("") ? "." : sma.getWorldViewVersion()));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
   }

}
