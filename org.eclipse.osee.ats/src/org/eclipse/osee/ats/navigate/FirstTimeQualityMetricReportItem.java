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
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.VersionMetrics;
import org.eclipse.osee.ats.util.VersionTeamMetrics;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem#getDescription()
    */
   @Override
   public String getDescription() {
      return "This report will genereate a metric comprised of:\n\n# of priority 1 and 2 OSEE problem actions orginated between release\n__________________________________\n# of non-support actions in that released";
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException, SQLException {
      TeamDefinitionArtifact useTeamDef = teamDef;
      if (useTeamDef == null && teamDefName != null) {
         useTeamDef = AtsCache.getSoleArtifactByName(teamDefName, TeamDefinitionArtifact.class);
      }
      if (useTeamDef == null) {
         TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
         ld.setTitle(getName());
         try {
            ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Both));
         } catch (MultipleAttributesExist ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
         int result = ld.open();
         if (result == 0) {
            if (ld.getResult().length == 0) {
               AWorkbench.popup("ERROR", "You must select a team to operate against.");
               return;
            }
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
            String html = getTeamWorkflowReport(getName(), teamDef, monitor);
            resultData.addRaw(html);
            resultData.report(getName(), Manipulations.RAW_HTML);
         } catch (Exception ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private static String[] HEADER_STRINGS =
         new String[] {"Version", "StartDate", "RelDate", "Num 1 + 2 Orig During Next Release Cycle",
               "Num Non-Support Released", "Ratio Orig 1 and 2 Bugs/Number Released"};

   /**
    * Ratio of # of priority 1 and 2 OSEE problem actions (non-cancelled) that were orginated between a release and the
    * next release / # of non-support actions released in that release
    * 
    * @param teamDef
    * @param monitor
    * @return
    * @throws SQLException
    */
   public static String getTeamWorkflowReport(String title, TeamDefinitionArtifact teamDef, IProgressMonitor monitor) throws OseeCoreException, SQLException {
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
         Integer numOrigDurningNextReleaseCycle = null;
         if (nextReleaseStartDate != null && nextReleaseEndDate != null) {
            if (numOrigDurningNextReleaseCycle == null) {
               numOrigDurningNextReleaseCycle = 0;
            }
            Collection<TeamWorkFlowArtifact> arts =
                  teamMet.getWorkflowsOriginatedBetween(nextReleaseStartDate, nextReleaseEndDate);
            for (TeamWorkFlowArtifact team : arts) {
               if (!team.getSmaMgr().isCancelled() && team.getChangeType() == ChangeType.Problem && (team.getPriority() == PriorityType.Priority_1 || team.getPriority() == PriorityType.Priority_2)) {
                  numOrigDurningNextReleaseCycle++;
               }
            }
         }
         Integer numNonSupportReleased = null;
         if (thisReleaseEndDate != null) {
            numNonSupportReleased = 0;
            for (TeamWorkFlowArtifact team : verMet.getTeamWorkFlows(ChangeType.Problem, ChangeType.Improvement)) {
               if (!team.getSmaMgr().isCancelled()) {
                  numNonSupportReleased++;
               }
            }
         }
         sb.append(AHTML.addRowMultiColumnTable(new String[] {
               verMet.getVerArt().getDescriptiveName(),
               XDate.getDateStr(thisReleaseStartDate, XDate.MMDDYY),
               XDate.getDateStr(thisReleaseEndDate, XDate.MMDDYY),
               numOrigDurningNextReleaseCycle == null ? "N/A" : String.valueOf(numOrigDurningNextReleaseCycle),
               numNonSupportReleased == null ? "N/A" : String.valueOf(numNonSupportReleased),
               numOrigDurningNextReleaseCycle == null || numNonSupportReleased == 0 || numNonSupportReleased == null ? "N/A" : AtsLib.doubleToStrString(new Double(
                     numOrigDurningNextReleaseCycle) / numNonSupportReleased)}));
         monitor.worked(1);
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }
}
