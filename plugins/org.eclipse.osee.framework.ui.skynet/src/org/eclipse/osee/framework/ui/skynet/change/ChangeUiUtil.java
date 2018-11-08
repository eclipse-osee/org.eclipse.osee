/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.change.view.ChangeReportEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

public final class ChangeUiUtil {
   public static void open(BranchId branch) throws OseeCoreException {
      open(branch, false);
   }

   public static void open(BranchId branch, boolean showTransactionTab) throws OseeCoreException {
      Conditions.checkNotNull(branch, "Branch");
      if (permissionsDeniedWithDialog(branch)) {
         return;
      }

      if (BranchManager.getType(branch).isBaselineBranch()) {
         if (!MessageDialog.openConfirm(AWorkbench.getActiveShell(), "Show Change Report",
            "You have chosen to show a " + (showTransactionTab ? "transaction report" : "change report") + " for a BASLINE branch.\n\n" + //
               "This could be a very long running task and consume large resources.\n\nAre you sure?")) {
            return;
         }
      }
      ChangeReportEditorInput editorInput = createInput(branch, true);
      editorInput.setTransactionTabActive(showTransactionTab);
      open(editorInput);
   }

   /**
    * Check if have at least read-only for given branch. Open dialog if denied.
    *
    * @return true if permissions denied
    */
   public static boolean permissionsDeniedWithDialog(BranchId branch) {
      boolean hasPermission = AccessControlManager.hasPermission(branch, PermissionEnum.READ);
      if (!hasPermission) {
         AWorkbench.popup("Access Denied",
            String.format("Access denied to branch:\n\n%s", BranchManager.toStringWithId(branch)));
         return true;
      }
      return false;
   }

   public static void open(TransactionToken transaction) throws OseeCoreException {
      Conditions.checkNotNull(transaction, "TransactionId");
      Branch branch = BranchManager.getBranch(transaction);
      if (branch.isInvalid() || permissionsDeniedWithDialog(transaction.getBranch())) {
         return;
      }
      open(createInput(transaction, true));
   }

   public static void open(TransactionToken startTx, TransactionToken endTx) throws OseeCoreException {
      Conditions.checkNotNull(startTx, "First TransactionId");
      Conditions.checkNotNull(endTx, "Second TransactionId");
      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      if (!txDelta.areOnTheSameBranch()) {
         throw new OseeArgumentException("Invalid selection - transactions art not on the same branch.", txDelta);
      }
      Branch branch = BranchManager.getBranch(startTx);
      if (branch.isInvalid() || permissionsDeniedWithDialog(startTx.getBranch())) {
         return;
      }
      open(createInput(CompareType.COMPARE_SPECIFIC_TRANSACTIONS, txDelta, true));
   }

   private static ChangeReportEditorInput createInput(TransactionToken transactionId, boolean loadOnOpen) throws OseeCoreException {
      TransactionToken startTx = TransactionManager.getPriorTransaction(transactionId);
      TransactionToken endTx = transactionId;
      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      return createInput(CompareType.COMPARE_SPECIFIC_TRANSACTIONS, txDelta, loadOnOpen);
   }

   private static ChangeReportEditorInput createInput(BranchId branch, boolean loadOnOpen) throws OseeCoreException {
      if (BranchManager.isArchived(branch) || BranchManager.getState(branch).equals(BranchState.COMMITTED)) {
         TransactionToken startTx = BranchManager.getBaseTransaction(branch);
         TransactionToken endTx = TransactionManager.getHeadTransaction(branch);
         TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
         ChangeReportEditorInput input = createInput(CompareType.COMPARE_BASE_TO_HEAD, txDelta, loadOnOpen);
         input.setBranch(branch);
         return input;
      } else {
         BranchId parentBranch = BranchManager.getParentBranch(branch);
         return createInput(branch, parentBranch, loadOnOpen);
      }
   }

   private static ChangeReportEditorInput createInput(BranchId branch, BranchId parentBranch, boolean loadOnOpen) throws OseeCoreException {
      TransactionToken startTx = TransactionManager.getHeadTransaction(branch);
      TransactionToken endTx = TransactionManager.getHeadTransaction(parentBranch);
      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      ChangeReportEditorInput input = createInput(CompareType.COMPARE_CURRENTS_AGAINST_PARENT, txDelta, loadOnOpen);
      input.setBranch(branch);
      return input;
   }

   public static ChangeReportEditorInput createInput(CompareType compareType, TransactionDelta txDelta, boolean loadOnOpen) {
      ChangeUiData uiData = new ChangeUiData(compareType, txDelta);
      uiData.setLoadOnOpen(loadOnOpen);
      return new ChangeReportEditorInput(uiData);
   }

   public static void open(final ChangeReportEditorInput editorInput) {
      Job job = new UIJob("Open Change Report") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status = Status.OK_STATUS;
            try {
               AWorkbench.getActivePage().openEditor(editorInput, ChangeReportEditor.EDITOR_ID);
            } catch (PartInitException ex) {
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error opening change report", ex);
            }
            return status;
         }
      };
      Jobs.startJob(job, true);
   }

   public static void open(IOseeBranch workingBranch, BranchId parentBranch, boolean b) {
      Branch branch = BranchManager.getBranch(workingBranch);
      if (branch.isInvalid() || permissionsDeniedWithDialog(workingBranch)) {
         return;
      }
      branch = BranchManager.getBranch(parentBranch);
      if (branch.isInvalid() || permissionsDeniedWithDialog(parentBranch)) {
         return;
      }
      ChangeReportEditorInput input = createInput(workingBranch, parentBranch, true);
      open(input);
   }
}
