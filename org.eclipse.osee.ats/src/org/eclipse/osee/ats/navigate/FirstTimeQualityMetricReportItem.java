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
package org.eclipse.osee.ats.navigate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.VersionMetrics;
import org.eclipse.osee.ats.util.VersionTeamMetrics;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class FirstTimeQualityMetricReportItem extends XNavigateItemAction {

   private final TeamDefinitionArtifact teamDef;
   private final String teamDefName;

   public FirstTimeQualityMetricReportItem(XNavigateItem parent, String name, String teamDefName) {
      super(parent, name);
      this.teamDefName = teamDefName;
      this.teamDef = null;
   }

   public FirstTimeQualityMetricReportItem(XNavigateItem parent) {
      this(parent, "First Time Quality Metric Report", null);
   }

   @Override
   public void run() throws SQLException {
      TeamDefinitionArtifact useTeamDef = teamDef;
      if (useTeamDef == null && teamDefName != null) {
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch(TeamDefinitionArtifact.ARTIFACT_NAME, teamDefName,
                     BranchPersistenceManager.getInstance().getAtsBranch());
         useTeamDef = srch.getSingletonArtifactOrException(TeamDefinitionArtifact.class);
      }
      if (useTeamDef == null) {
         TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
         ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Both));
         int result = ld.open();
         if (result == 0) {
            useTeamDef = (TeamDefinitionArtifact) ld.getResult()[0];
         } else
            return;
      } else if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      ReportJob job = new ReportJob(getName(), useTeamDef);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

   private class ReportJob extends Job {

      private final TeamDefinitionArtifact teamDef;

      public ReportJob(String title, TeamDefinitionArtifact teamDef) {
         super(title);
         this.teamDef = teamDef;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.ats.util.ReleaseReportJob#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            XResultData resultData = new XResultData(AtsPlugin.getLogger());
            String html = getTeamWorkflowReport(teamDef, monitor);
            resultData.addRaw(html);
            resultData.report(getName(), Manipulations.RAW_HTML);
         } catch (Exception ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   /**
    * Ratio of # of priority 1 and 2 OSEE problem actions (non-cancelled) that were orginated between a release and the
    * next release / # of non-support actions released in that release
    * 
    * @param teamDef
    * @param monitor
    * @return
    * @throws SQLException
    */
   public static String getTeamWorkflowReport(TeamDefinitionArtifact teamDef, IProgressMonitor monitor) throws Exception {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Version", "StartDate", "RelDate",
            "Num 1 + 2 Orig During Release", "Num Non-Support Released", "Ratio Orig/Released"}));
      VersionTeamMetrics teamMet = new VersionTeamMetrics(teamDef);
      Collection<VersionMetrics> verMets = teamMet.getReleasedOrderedVersions();
      monitor.beginTask("Processing Versions", verMets.size());
      for (VersionMetrics verMet : verMets) {
         Date startDate = verMet.getReleaseStartDate();
         Date endDate = verMet.getVerArt().getReleaseDate();
         Integer numOrigDurning = null;
         if (startDate != null && endDate != null) {
            Collection<TeamWorkFlowArtifact> arts = teamMet.getWorkflowsOriginatedBetween(startDate, endDate);
            for (TeamWorkFlowArtifact team : arts) {
               if (team.getPriority() == PriorityType.Priority_1 || team.getPriority() == PriorityType.Priority_2) {
                  if (numOrigDurning == null) numOrigDurning = new Integer(0);
                  numOrigDurning++;
               }
            }
         }
         Integer numNonSupportReleased = null;
         if (endDate != null) {
            numNonSupportReleased = verMet.getTeamWorkFlows(ChangeType.Problem, ChangeType.Improvement).size();
         }
         sb.append(AHTML.addRowMultiColumnTable(new String[] {
               verMet.getVerArt().getDescriptiveName(),
               XDate.getDateStr(startDate, XDate.MMDDYY),
               XDate.getDateStr(endDate, XDate.MMDDYY),
               numOrigDurning == null ? "N/A" : String.valueOf(numOrigDurning),
               numNonSupportReleased == null ? "N/A" : String.valueOf(numNonSupportReleased),
               numOrigDurning == null || numNonSupportReleased == 0 || numNonSupportReleased == null ? "N/A" : AtsLib.doubleToStrString(new Double(
                     numOrigDurning) / numNonSupportReleased)}));
         monitor.worked(1);
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }
}
