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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchState;
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

   private static String getUpdatedName(String branchName, boolean inProgress) {
      String storeName = StringFormat.truncate(branchName, 100);
      return String.format("%s - %s - %s", storeName, inProgress ? "for update" : "moved by update on",
            Lib.getDateTimeString());
   }

   public IStatus run(IProgressMonitor monitor) {
      IStatus status = Status.OK_STATUS;
      monitor.beginTask(getName(), TOTAL_WORK);
      try {
         if (originalBranch != null && originalBranch.hasParentBranch()) {
            status = performUpdate(monitor, originalBranch);
         } else {
            monitor.worked(TOTAL_WORK);
         }
      } catch (OseeCoreException ex) {
         status =
               new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, String.format("Error updating branch [%s]",
                     originalBranch.getBranchShortName()), ex);
      } finally {
         monitor.done();
      }
      return status;
   }

   private Branch createTempBranch(Branch originalBranch) throws OseeCoreException {
      Branch parentBranch = originalBranch.getParentBranch();
      String branchUpdateName = getUpdatedName(originalBranch.getBranchName(), true);
      return BranchManager.createWorkingBranch(parentBranch, branchUpdateName,
            UserManager.getUser(SystemUser.OseeSystem));
   }

   private IStatus performUpdate(IProgressMonitor monitor, Branch originalBranch) throws OseeCoreException {
      IStatus status = Status.OK_STATUS;
      Branch newWorkingBranch = null;
      try {
         monitor.subTask("Creating temporary branch");
         newWorkingBranch = createTempBranch(originalBranch);
         monitor.worked(HALF_TOTAL_WORK);

         BranchManager.setBranchState(originalBranch, BranchState.CLOSED_BY_UPDATE);

         SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, QUARTER_TOTAL_WORK);
         ConflictManagerExternal conflictManager = new ConflictManagerExternal(newWorkingBranch, originalBranch);
         if (!conflictManager.remainingConflictsExist()) {
            completeUpdate(subMonitor, conflictManager, true, false);
         } else {
            status = resolver.resolveConflicts(subMonitor, conflictManager);
         }
      } finally {
         if (newWorkingBranch != null && !status.isOK()) {
            BranchManager.purgeBranch(newWorkingBranch);
         }
         monitor.worked(QUARTER_TOTAL_WORK);
      }
      return status;
   }

   public static IStatus completeUpdate(IProgressMonitor monitor, ConflictManagerExternal conflictManager, boolean archiveSourceBranch, boolean overwriteUnresolvedConflicts) {
      IStatus status = Status.OK_STATUS;
      try {
         monitor.beginTask("Merging updates", 3);
         monitor.subTask("Merging updates");
         BranchManager.commitBranch(conflictManager, archiveSourceBranch, overwriteUnresolvedConflicts);
         monitor.worked(1);

         Branch sourceBranch = conflictManager.getFromBranch();
         Branch destinationBranch = conflictManager.getToBranch();

         String originalBranchName = sourceBranch.getBranchName();
         Artifact originalAssociatedArtifact = sourceBranch.getAssociatedArtifact();

         sourceBranch.setAssociatedArtifact(UserManager.getUser(SystemUser.OseeSystem));
         sourceBranch.rename(getUpdatedName(originalBranchName, false));
         monitor.worked(1);

         destinationBranch.rename(originalBranchName);
         if (originalAssociatedArtifact != null) {
            destinationBranch.setAssociatedArtifact(originalAssociatedArtifact);
         }

         status = Status.OK_STATUS;
         monitor.worked(1);
      } catch (OseeCoreException ex) {
         status =
               new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, String.format(
                     "Error merging updates between [%s] and [%s]",
                     conflictManager.getFromBranch().getBranchShortName(),
                     conflictManager.getToBranch().getBranchShortName()), ex);
      } finally {
         monitor.done();
      }
      return status;
   }
}