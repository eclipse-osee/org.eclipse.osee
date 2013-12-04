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

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.client.workflow.PriorityUtil;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.ats.version.VersionMetrics;
import org.eclipse.osee.ats.version.VersionTeamMetrics;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Donald G. Dunne
 */
public class FirstTimeQualityMetricReportItem extends XNavigateItemAction {

   public FirstTimeQualityMetricReportItem(XNavigateItem parent) {
      super(parent, "First Time Quality Metric Report", AtsImage.REPORT);
   }

   @Override
   public String getDescription() {
      return "This report will genereate a metric comprised of:\n\n# of priority 1 and 2 OSEE problem actions orginated between release\n__________________________________\n# of non-support actions in that released";
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      IAtsTeamDefinition useTeamDef = null;
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      ld.setTitle(getName());

      Set<IAtsTeamDefinition> teamReleaseableDefinitions = null;
      try {
         teamReleaseableDefinitions = TeamDefinitions.getTeamReleaseableDefinitions(Active.Both);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading team definitions", ex);
      }

      ld.setInput(teamReleaseableDefinitions);
      int result = ld.open();
      if (result == 0) {
         if (ld.getResult().length == 0) {
            AWorkbench.popup("ERROR", "You must select a team to operate against.");
            return;
         }
         useTeamDef = (IAtsTeamDefinition) ld.getResult()[0];
      } else {
         return;
      }

      ReportJob job = new ReportJob(getName(), useTeamDef);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

   private static class ReportJob extends Job {

      private final IAtsTeamDefinition teamDef;

      public ReportJob(String title, IAtsTeamDefinition teamDef) {
         super(title);
         this.teamDef = teamDef;
      }

      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            XResultData resultData = new XResultData();
            String html = getTeamWorkflowReport(getName(), teamDef, monitor);
            resultData.addRaw(html);
            XResultDataUI.report(resultData, getName(), Manipulations.RAW_HTML);
         } catch (Exception ex) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.toString(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private final static String[] HEADER_STRINGS = new String[] {
      "Version",
      "StartDate",
      "RelDate",
      "Num 1 + 2 Orig During Next Release Cycle",
      "Num Non-Support Released",
      "Ratio Orig 1 and 2 Bugs/Number Released"};

   /**
    * Ratio of # of priority 1 and 2 OSEE problem actions (non-cancelled) that were orginated between a release and the
    * next release / # of non-support actions released in that release
    */
   public static String getTeamWorkflowReport(String title, IAtsTeamDefinition teamDef, IProgressMonitor monitor) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title));
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addRowSpanMultiColumnTable(
         "This report shows the ratio of 1+2 problem workflows created during next release cycle due to current release over the total non-support workflows during this release.",
         HEADER_STRINGS.length));
      sb.append(AHTML.addHeaderRowMultiColumnTable(HEADER_STRINGS));
      VersionTeamMetrics teamMet = new VersionTeamMetrics(teamDef);
      Collection<VersionMetrics> verMets = teamMet.getReleasedOrderedVersions();
      monitor.beginTask("Processing Versions", verMets.size());
      for (VersionMetrics verMet : verMets) {
         Date thisReleaseStartDate = verMet.getReleaseStartDate();
         Date thisReleaseEndDate = verMet.getVerArt().getReleaseDate();
         Date nextReleaseStartDate = null;
         Date nextReleaseEndDate = null;
         VersionMetrics nextVerMet = verMet.getNextVerMetViaReleaseDate();
         if (nextVerMet != null) {
            nextReleaseStartDate = nextVerMet.getReleaseStartDate();
            nextReleaseEndDate = nextVerMet.getVerArt().getReleaseDate();
         }
         Integer numOrigDurningNextReleaseCycle = 0;
         if (nextReleaseStartDate != null && nextReleaseEndDate != null) {
            Collection<TeamWorkFlowArtifact> arts =
               teamMet.getWorkflowsOriginatedBetween(nextReleaseStartDate, nextReleaseEndDate);
            for (TeamWorkFlowArtifact team : arts) {
               if (!team.isCancelled() && ChangeTypeUtil.getChangeType(team) == ChangeType.Problem && (PriorityUtil.getPriorityStr(
                  team).equals("1") || PriorityUtil.getPriorityStr(team).equals("2"))) {
                  numOrigDurningNextReleaseCycle++;
               }
            }
         }
         Integer numNonSupportReleased = null;
         if (thisReleaseEndDate != null) {
            numNonSupportReleased = 0;
            for (TeamWorkFlowArtifact team : verMet.getTeamWorkFlows(ChangeType.Problem, ChangeType.Improvement)) {
               if (!team.isCancelled()) {
                  numNonSupportReleased++;
               }
            }
         }
         sb.append(AHTML.addRowMultiColumnTable(new String[] {
            verMet.getVerArt().getName(),
            DateUtil.getMMDDYY(thisReleaseStartDate),
            DateUtil.getMMDDYY(thisReleaseEndDate),
            numOrigDurningNextReleaseCycle == 0 ? "N/A" : String.valueOf(numOrigDurningNextReleaseCycle),
            numNonSupportReleased == null ? "N/A" : String.valueOf(numNonSupportReleased),
            numOrigDurningNextReleaseCycle == 0 || numNonSupportReleased == null || numNonSupportReleased == 0 ? "N/A" : AtsUtilCore.doubleToI18nString((double) numOrigDurningNextReleaseCycle / (double) numNonSupportReleased)}));
         monitor.worked(1);
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }
}
