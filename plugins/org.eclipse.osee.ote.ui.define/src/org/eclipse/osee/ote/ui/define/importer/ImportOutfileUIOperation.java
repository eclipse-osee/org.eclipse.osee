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
package org.eclipse.osee.ote.ui.define.importer;

import java.net.URI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.define.jobs.FindCommitableJob;
import org.eclipse.osee.ote.define.jobs.OutfileToArtifactJob;
import org.eclipse.osee.ote.ui.define.jobs.CommitTestRunJob;
import org.eclipse.osee.ote.ui.define.jobs.ReportErrorsJob;
import org.eclipse.osee.ote.ui.define.utilities.CommitConfiguration;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class ImportOutfileUIOperation {

   private final URI[] fileSystemObjects;
   private final BranchId selectedBranch;

   public ImportOutfileUIOperation(BranchId selectedBranch, URI... fileSystemObjects) {
      this.fileSystemObjects = fileSystemObjects;
      this.selectedBranch = selectedBranch;
   }

   public boolean execute() {
      boolean toReturn = true;
      if (fileSystemObjects.length > 0) {
         launchImportJob();
      } else {
         toReturn = false;
         Shell shell = AWorkbench.getActiveShell();
         MessageDialog.openInformation(shell, "Information", "There were no resources currently selected for import.");
      }
      return toReturn;
   }

   private void launchImportJob() {
      OutfileToArtifactJob convertJob = new OutfileToArtifactJob(selectedBranch, fileSystemObjects);
      convertJob.addJobChangeListener(new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            if (wasJobSuccessful(event)) {
               OutfileToArtifactJob job = (OutfileToArtifactJob) event.getJob();

               URI[] itemsWithError = job.getUnparseableFiles();
               Artifact[] artifacts = job.getResults();
               // Report Parse Errors
               if (itemsWithError.length > 0) {
                  reportFilesWithErrors(itemsWithError, artifacts);
               } else {
                  launchFindCommitableJob(artifacts);
               }
            }
         }
      });
      convertJob.schedule();
   }

   private void launchCommitJob(final FindCommitableJob job) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            CommitTestRunJob newJob = new CommitTestRunJob(job.getAll(), job.getCommitAllowed(),
               job.getCommitNotAllowed(), CommitConfiguration.isCommitOverrideAllowed());
            newJob.schedule();
         }
      });
   }

   private void launchFindCommitableJob(final Artifact[] artifacts) {
      // Find Commit Allowed
      FindCommitableJob commitableJob = new FindCommitableJob(artifacts);
      commitableJob.addJobChangeListener(new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            if (wasJobSuccessful(event)) {
               FindCommitableJob job = (FindCommitableJob) event.getJob();
               launchCommitJob(job);
            }
         }
      });
      commitableJob.schedule();
   }

   private void reportFilesWithErrors(final Object[] items, final Artifact[] artifacts) {
      String title = "Outfile Import Error";
      String message = "The following file(s) had errors during the parsing operation: ";

      JobChangeAdapter listener = new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            launchFindCommitableJob(artifacts);
         }
      };

      ReportErrorsJob.openError(title, message, listener, items);
   }

   private boolean wasJobSuccessful(IJobChangeEvent event) {
      IStatus status = event.getResult();
      return status.equals(Status.OK_STATUS) || status.equals(IStatus.OK);
   }
}
