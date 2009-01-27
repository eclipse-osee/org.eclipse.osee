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

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.OseeInfo;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class ValidateChangeReportByHrid extends XNavigateItemAction {

   /**
    * @param parent
    */
   public ValidateChangeReportByHrid(XNavigateItem parent) {
      super(parent, "Validate Change Reports by HRID");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      EntryCheckDialog ed = new EntryCheckDialog(getName(), "Enter HRID", "Display Was/Is data in Results View.");
      if (ed.open() == 0) {
         String hrid = ed.getEntry();
         if (hrid != null && !hrid.equals("")) {
            Jobs.startJob(new Report(getName(), hrid, ed.isChecked()), true);
         }
      }
   }

   public class Report extends Job {

      private final String hrid;
      private final boolean exportWasIs;

      public Report(String name, String hrid, boolean exportWasIs) {
         super(name);
         this.hrid = hrid;
         this.exportWasIs = exportWasIs;
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
            try {
               TeamWorkFlowArtifact teamArt =
                     (TeamWorkFlowArtifact) ArtifactQuery.getArtifactFromId(hrid, AtsPlugin.getAtsBranch());
               String currentDbGuid = OseeInfo.getValue("osee.db.guid");
               ValidateChangeReports.changeReportValidated(currentDbGuid, teamArt, rd, exportWasIs);
            } catch (Exception ex) {
               rd.logError(ex.getLocalizedMessage());
            }
            rd.report(getName());
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
