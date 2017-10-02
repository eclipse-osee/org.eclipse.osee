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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.MergeInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchOptionsEnum;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public abstract class CommitHandler extends CommandHandler {
   protected final boolean useParentBranch;

   public CommitHandler(boolean useParentBranch) {
      this.useParentBranch = useParentBranch;
   }

   public static boolean commitBranch(final ConflictManagerExternal conflictManager, final boolean archiveSourceBranch)  {
      return commitBranch(conflictManager, archiveSourceBranch, false);
   }

   public static boolean commitBranch(final ConflictManagerExternal conflictManager, final boolean archiveSourceBranch, boolean skipPrompts)  {
      boolean toReturn = false;
      BranchState state = BranchManager.getState(conflictManager.getSourceBranch());
      if (!state.isRebaselineInProgress() && !state.isRebaselined()) {
         if (conflictManager.getOriginalConflicts().size() > 0) {
            toReturn = MergeInProgressHandler.handleMergeInProgress(conflictManager, archiveSourceBranch, skipPrompts);
         } else {
            final MutableBoolean dialogResult = new MutableBoolean(false);
            if (!skipPrompts) {
               Displays.pendInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     dialogResult.setValue(
                        MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                           "Commit Branch", String.format("Commit branch\n\n\"[%s]\" onto destination\n\n\"[%s]\"",
                              conflictManager.getSourceBranch(), conflictManager.getDestinationBranch())));
                  }
               });
            } else {
               dialogResult.setValue(true);
            }

            if (dialogResult.booleanValue()) {
               BranchManager.commitBranch(null, conflictManager, archiveSourceBranch, false);
               toReturn = true;
            }
         }
      }

      return toReturn;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection)  {
      try {
         List<IOseeBranch> branches = Handlers.getBranchesFromStructuredSelection(selection);
         Iterator<IOseeBranch> iterator = branches.iterator();
         if (iterator.hasNext()) {
            BranchId sourceBranch = iterator.next();

            BranchId destinationBranch = null;
            if (useParentBranch) {
               destinationBranch = BranchManager.getParentBranch(sourceBranch);
            } else {
               destinationBranch = BranchId.valueOf(event.getParameter(BranchOptionsEnum.BRANCH_ID.origKeyName));
            }
            Jobs.startJob(new CommitJob(sourceBranch, destinationBranch,
               Boolean.parseBoolean(event.getParameter(CommitBranchParameter.ARCHIVE_PARENT_BRANCH))));
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection)  {
      List<IOseeBranch> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);

      if (branches.size() == 1) {
         IOseeBranch branch = branches.iterator().next();
         return useParentBranchValid(branch) || !useParentBranch && AccessControlManager.isOseeAdmin();
      }
      return false;
   }

   protected boolean useParentBranchValid(IOseeBranch branch)  {
      return branch.notEqual(CoreBranches.SYSTEM_ROOT) && useParentBranch && !BranchManager.isChangeManaged(
         branch) && !BranchManager.isArchived(branch);
   }
   protected class CommitJob extends Job {
      private final BranchId sourceBranch;
      private final BranchId destinationBranch;
      private final boolean archiveSourceBranch;

      public CommitJob(BranchId sourceBranch, BranchId destinationBranch, boolean archiveSourceBranch) {
         super("Commit Branch");
         this.destinationBranch = destinationBranch;
         this.sourceBranch = sourceBranch;
         this.archiveSourceBranch = archiveSourceBranch;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            commitBranch(new ConflictManagerExternal(destinationBranch, sourceBranch), archiveSourceBranch);
         } catch (OseeCoreException ex) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
         }
         return Status.OK_STATUS;
      }
   }
}
