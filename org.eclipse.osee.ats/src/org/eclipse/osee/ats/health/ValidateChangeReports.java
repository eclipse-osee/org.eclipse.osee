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
package org.eclipse.osee.ats.health;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ValidateChangeReports extends XNavigateItemAction {

   /**
    * @param parent
    */
   public ValidateChangeReports(XNavigateItem parent) {
      super(parent, "Validate Change Reports");
   }

   public ValidateChangeReports() {
      this(null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final XResultData rd = new XResultData();
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   // Lba SA11 Req Team Workflow
   // Lba B3 Req Team Workflow
   // Lba V11 REU Req Team Workflow
   // Lba V13 Req Team Workflow
   private void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      String[] columnHeaders =
            new String[] {"Team", "Working", "ArtChg (was/is)", "ArtNew", "ArtDel", "RelMod", "Notes"};
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      //            for (String artifactTypeName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {

      for (String artifactTypeName : new String[] {"Lba V13 Req Team Workflow"}) {
         sbFull.append(AHTML.addRowSpanMultiColumnTable(artifactTypeName, columnHeaders.length));
         try {
            int x = 1;
            Collection<Artifact> artifacts =
                  ArtifactQuery.getArtifactsFromType(artifactTypeName, AtsPlugin.getAtsBranch());
            for (Artifact artifact : artifacts) {
               String result = String.format("Processing %s/%s  - %s", x++, artifacts.size(), artifact);
               OSEELog.logInfo(AtsPlugin.class, result, false);
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               // Only work on committed branches
               if (!teamArt.getSmaMgr().getBranchMgr().isCommittedBranch()) continue;
               try {
                  ChangeData changeData = teamArt.getSmaMgr().getBranchMgr().getChangeData();
                  String notes = "";
                  String str =
                        AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(),
                              teamArt.getSmaMgr().getBranchMgr().isWorkingBranch() ? "Working" : "Committed",
                              notes.toString()});
                  sbFull.append(str);
                  OSEELog.logException(AtsPlugin.class, str, null, false);
               } catch (Exception ex) {
                  sbFull.append(AHTML.addRowSpanMultiColumnTable(
                        "Artifact " + artifact.getHumanReadableId() + " - Exception: " + ex.getLocalizedMessage(),
                        columnHeaders.length));
                  OSEELog.logException(AtsPlugin.class,
                        "Artifact " + artifact.getHumanReadableId() + " - Exception: " + ex.getLocalizedMessage(), ex,
                        false);
               }
            }
         } catch (Exception ex) {
            sbFull.append(AHTML.addRowSpanMultiColumnTable("Exception: " + ex.getLocalizedMessage(),
                  columnHeaders.length));
         }
      }
      sbFull.append(AHTML.endMultiColumnTable());
      xResultData.addRaw(sbFull.toString().replaceAll("\n", ""));
      if (monitorLog.getSevereLogs().size() > 0) {
         xResultData.logError(String.format("%d SevereLogs during test.\n", monitorLog.getSevereLogs().size()));
      }
   }
}
