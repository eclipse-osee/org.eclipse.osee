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
package org.eclipse.osee.ats.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class VersionReportJob extends Job {

   protected final String title;
   protected final VersionArtifact verArt;

   public VersionReportJob(String title, VersionArtifact verArt) {
      super("Creating Release Report");
      this.title = title;
      this.verArt = verArt;
   }

   public IStatus run(IProgressMonitor monitor) {
      try {
         final String html = getReleaseReportHtml(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM), verArt, monitor);
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               XResultView.getResultView().addResultPage(new XResultPage(title, html, Manipulations.HTML_MANIPULATIONS));
               AWorkbench.popup("Complete", getName() + " Complete...Results in ATS Results");
            }
         });

      } catch (SQLException ex) {
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      }
      monitor.done();
      return Status.OK_STATUS;
   }

   public static String getReleaseReportHtml(String title, VersionArtifact verArt, IProgressMonitor monitor) throws SQLException {
      if (verArt == null) {
         AWorkbench.popup("ERROR", "Must select product, config and version.");
         return null;
      }
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title + getReleasedString(verArt), verArt.getDescriptiveName()));
      sb.append(getTeamWorkflowReport(verArt.getTargetedForTeamArtifacts(), null, monitor));
      return sb.toString();
   }

   public static String getFullReleaseReport(TeamDefinitionArtifact teamDef, IProgressMonitor monitor) throws SQLException {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.heading(2, teamDef + " Releases as of " + XDate.getDateNow()));
      for (VersionArtifact verArt : teamDef.getVersionsArtifacts()) {
         if (verArt.isReleased())
            sb.append(AHTML.getHyperlink("#" + verArt.getDescriptiveName(),
                  verArt.getDescriptiveName() + VersionReportJob.getReleasedString(verArt)) + AHTML.newline());
         else
            sb.append(verArt.getDescriptiveName() + " - Un-Released" + AHTML.newline());
      }
      sb.append(AHTML.addSpace(5));
      int x = 1;
      for (VersionArtifact verArt : teamDef.getVersionsArtifacts()) {
         if (monitor != null) {
            String str = "Processing version " + x++ + "/" + teamDef.getVersionsArtifacts().size();
            monitor.subTask(str);
         }
         if (verArt.isReleased()) {
            String html = VersionReportJob.getReleaseReportHtml(verArt.getDescriptiveName(), verArt, null);
            sb.append(html);
         }
      }
      return sb.toString();
   }

   public static String getReleasedString(VersionArtifact verArt) throws IllegalStateException, SQLException {
      String released = "";
      if (verArt.getSoleDateAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName()) != null) {
         released = " - " + "Released: " + XDate.getDateStr(
               verArt.getSoleDateAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName()), XDate.MMDDYY);
      }
      return released;
   }

   public static String getTeamWorkflowReport(Collection<TeamWorkFlowArtifact> teamArts, Integer backgroundColor, IProgressMonitor monitor) throws SQLException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(100, 1, backgroundColor));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Team", "Priority", "Change", "Title", "HRID"}));
      int x = 1;
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (TeamWorkFlowArtifact team : teamArts)
         teamDefs.add(team.getTeamDefinition());
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (team.getTeamDefinition().equals(teamDef)) {
               String str = "Processing team " + x++ + "/" + teamArts.size();
               if (monitor != null) monitor.subTask(str);
               System.out.println(str);
               sb.append(AHTML.addRowMultiColumnTable(new String[] {"Action", team.getTeamName(),
                     team.getWorldViewPriority(), team.getWorldViewChangeTypeStr(), team.getDescriptiveName(),
                     team.getHumanReadableId()}, null, (x % 2 == 0 ? null : "#cccccc")));

               SMAManager smaMgr = new SMAManager(team);
               for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts()) {
                  sb.append(AHTML.addRowMultiColumnTable(new String[] {"Task", "", "", "",
                        taskArt.getDescriptiveName(), taskArt.getHumanReadableId()}, null,
                        (x % 2 == 0 ? null : "#cccccc")));
               }
            }
         }
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

}
