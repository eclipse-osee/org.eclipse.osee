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
package org.eclipse.osee.framework.skynet.core.artifact.update;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;

/**
 * @author Roberto E. Escobar
 */
public class UpdateBranchJob extends Job {
   private final int TOTAL_WORK = Integer.MAX_VALUE;
   private final int HALF_TOTAL_WORK = TOTAL_WORK / 2;
   private final int QUARTER_TOTAL_WORK = TOTAL_WORK / 4;
   private Branch originalBranch;
   private IConflictResolver resolver;

   public UpdateBranchJob(Branch branch, IConflictResolver resolver) {
      super("Update Branch");
      this.originalBranch = branch;
      this.resolver = resolver;
   }

   private String getUpdatedName(String branchName) {
      String storeName = StringFormat.truncate(branchName, 100);
      return String.format("%s - update - %s", storeName, Lib.getDateTimeString());
   }

   public IStatus run(IProgressMonitor monitor) {
      IStatus status = Status.OK_STATUS;
      monitor.beginTask(getName(), TOTAL_WORK);
      try {
         if (originalBranch != null && originalBranch.hasParentBranch()) {
            Branch parentBranch = originalBranch.getParentBranch();
            String originalBranchName = originalBranch.getBranchName();
            Artifact originalAssociatedArtifact = originalBranch.getAssociatedArtifact();
            if (parentBranch != null) {
               try {
                  status =
                        performUpdate(monitor, parentBranch, originalBranch, originalBranchName,
                              originalAssociatedArtifact);

                  if (monitor.isCanceled()) {
                     status = restoreBranch(originalBranch, originalBranchName, originalAssociatedArtifact);
                     if (status.isOK()) {
                        status = Status.CANCEL_STATUS;
                     }
                  }
               } catch (OseeCoreException ex) {
                  status =
                        new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, String.format(
                              "Error updating branch [%s]", originalBranch.getBranchShortName()), ex);
                  restoreBranch(originalBranch, originalBranchName, originalAssociatedArtifact);
               }
            }
         }
         monitor.worked(TOTAL_WORK);
      } catch (OseeCoreException ex) {
         status =
               new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, String.format("Error updating branch [%s]",
                     originalBranch.getBranchShortName()), ex);
      } finally {
         monitor.done();
      }
      return status;
   }

   private IStatus performUpdate(IProgressMonitor monitor, Branch parentBranch, Branch originalBranch, String originalBranchName, Artifact originalAssociatedArtifact) throws OseeCoreException {
      IStatus status = Status.OK_STATUS;
      Branch newWorkingBranch = null;
      try {
         // Change Names
         originalBranch.setAssociatedArtifact(UserManager.getUser(SystemUser.OseeSystem));
         originalBranch.rename(getUpdatedName(originalBranchName));

         // Create new updated branch
         monitor.subTask("Create new branch");
         newWorkingBranch =
               BranchManager.createWorkingBranch(parentBranch, originalBranchName, originalAssociatedArtifact);
         monitor.worked(HALF_TOTAL_WORK);

         ConflictManagerExternal conflictManager = new ConflictManagerExternal(newWorkingBranch, originalBranch);
         SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, QUARTER_TOTAL_WORK);
         status = resolveConflicts(subMonitor, conflictManager);
         if (status.isOK() && !conflictManager.remainingConflictsExist()) {
            monitor.subTask("Merging Changes");
            BranchManager.commitBranch(conflictManager, true, false);
            if (originalAssociatedArtifact != null) {
               newWorkingBranch.setAssociatedArtifact(originalAssociatedArtifact);
            }
         } else {
            status =
                  new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID,
                        "All Conflict were not resolved. Unable to finish update process.");
         }
      } finally {
         if (newWorkingBranch != null && !status.isOK()) {
            BranchManager.deleteBranch(newWorkingBranch);
         }
         monitor.worked(QUARTER_TOTAL_WORK);
      }
      return status;
   }

   private IStatus restoreBranch(Branch originalBranch, String originalBranchName, Artifact originalAssociatedArtifact) {
      IStatus status = Status.OK_STATUS;
      try {
         Artifact currentArtifact = originalBranch.getAssociatedArtifact();
         if ((currentArtifact == null || !currentArtifact.equals(originalAssociatedArtifact)) && originalAssociatedArtifact != null) {
            originalBranch.setAssociatedArtifact(originalAssociatedArtifact);
         }
      } catch (OseeCoreException ex1) {
         status =
               new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, String.format(
                     "Error restoring associated artifact to [%s]", originalAssociatedArtifact.getArtId()), ex1);
      }
      try {
         if (!originalBranch.getBranchName().equals(originalBranchName)) {
            originalBranch.rename(originalBranchName);
         }
      } catch (OseeCoreException ex2) {
         IStatus error =
               new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, String.format(
                     "Error restoring branch name for: branchId[%s] - orginal name was [%s]",
                     originalBranch.getBranchId(), originalBranchName), ex2);
         if (!status.isOK()) {
            MultiStatus multiStatus =
                  new MultiStatus(status.getPlugin(), IStatus.ERROR, String.format("Error restoring branch [%s]",
                        originalBranch.getBranchId()), status.getException());
            multiStatus.add(status);
            multiStatus.add(error);
            status = multiStatus;
         } else {
            status = error;
         }
      }
      return status;
   }

   private IStatus resolveConflicts(IProgressMonitor monitor, ConflictManagerExternal conflictManager) throws OseeCoreException {
      IStatus status = Status.OK_STATUS;
      try {
         monitor.beginTask("Resolve Conflicts", 100);
         if (conflictManager.remainingConflictsExist()) {
            if (monitor.isCanceled()) {
               status = Status.CANCEL_STATUS;
            } else {
               SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 100);
               status = resolver.resolveConflicts(subMonitor, conflictManager);
            }
         }
      } finally {
         monitor.done();
      }
      return status;
   }
}