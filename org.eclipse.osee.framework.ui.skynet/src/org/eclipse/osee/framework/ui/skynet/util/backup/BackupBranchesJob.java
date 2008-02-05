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
package org.eclipse.osee.framework.ui.skynet.util.backup;

import java.io.File;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.io.Zip;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.export.ExportBranchJob;
import org.eclipse.osee.framework.ui.skynet.util.EmailableJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;

/**
 * @author Donald G. Dunne
 */
public class BackupBranchesJob extends EmailableJob {

   public static String JOB_NAME = "Backup OSEE Database";
   XResultData rd = new XResultData(SkynetGuiPlugin.getLogger());
   private final String path;
   private final Collection<Branch> branches;

   /**
    * @param name
    */
   public BackupBranchesJob(Collection<Branch> branches, String path) {
      super(JOB_NAME);
      this.branches = branches;
      this.path = path;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         backup(branches);
         if (rd.toString().equals("")) rd.log("No Problems Found");
         rd.report(getName());
         XResultPage page = rd.getReport(getName());
         notifyOfCompletion(JOB_NAME, page.getManipulatedHtml());
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex.getLocalizedMessage(), ex, false);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, "Failed", ex);
      }
   }

   public void backup(Collection<Branch> branches) {
      XResultData rd = new XResultData(SkynetGuiPlugin.getLogger());
      try {
         rd = new XResultData(SkynetGuiPlugin.getLogger());
         rd.log(getName());
         rd.log("Starting OSEE DB Backup - " + XDate.getDateNow());
         for (Branch branch : branches) {
            rd.log("Backing up \"" + branch.getBranchShortName() + "\" - " + XDate.getDateNow());
            String backupName = StringFormat.truncate(branch.getBranchName(), 25);
            backupName = backupName.replaceAll("\\W+", "_");
            File xmlFile =
                  new File(
                        path + "/OSEE_Branch_Backup__" + XDate.getDateNow("yyyy_MM_dd_HH_MM__") + backupName + ".xml");
            if (xmlFile != null) {
               Job job = new ExportBranchJob(xmlFile, branch, false);
               job.setUser(true);
               job.setPriority(Job.LONG);
               job.schedule();
               try {
                  job.join();
               } catch (InterruptedException ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, false);
                  rd.logError(ex.getLocalizedMessage());
               }
            }
            rd.log("Zipping up \"" + branch.getBranchShortName() + "\" - " + XDate.getDateNow());
            Zip.zip(new String[] {xmlFile.getAbsolutePath()}, xmlFile.getAbsolutePath().replaceFirst(".xml", ".zip"));
            // Delete backup file
            xmlFile.delete();
            rd.log("Finished with \"" + branch.getBranchShortName() + "\" - " + XDate.getDateNow());
         }
         rd.log("Completed - " + XDate.getDateNow());
         rd.report(getName());

      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         rd.logError(ex.getLocalizedMessage());
      }

   }

}
