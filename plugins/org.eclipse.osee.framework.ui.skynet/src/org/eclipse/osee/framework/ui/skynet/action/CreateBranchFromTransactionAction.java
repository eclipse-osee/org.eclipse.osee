/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.action;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CreateBranchFromTransactionAction extends Action {

   private static final String TITLE = "Create Branch from Selected Transaction";
   private final List<TransactionId> txs;
   private final BranchId branchId;

   public CreateBranchFromTransactionAction(List<TransactionId> txs, BranchId branchId) {
      super(TITLE);
      this.txs = txs;
      this.branchId = branchId;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
      setToolTipText(TITLE);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH);
   }

   @Override
   public void run() {
      try {
         if (txs.isEmpty() || txs.size() > 1) {
            AWorkbench.popup("One Transaction Record must be selected");
            return;
         }
         TransactionId txId = txs.get(0);
         TransactionToken txToken = TransactionToken.valueOf(txId, branchId);

         EntryDialog dialog = new EntryDialog("Enter branch name", "Enter branch name");

         int result = dialog.open();
         if (result == 0 && dialog.getEntry() != null) {

            IExceptionableRunnable runnable = new IExceptionableRunnable() {
               @Override
               public IStatus run(IProgressMonitor monitor) throws Exception {
                  String newBranchName = dialog.getEntry();
                  IOseeBranch newBranch = BranchManager.createWorkingBranchFromTx(txToken, newBranchName, null);
                  if (newBranch.isValid()) {
                     AWorkbench.popup("Branch: " + newBranch + " has been created.");
                     return Status.OK_STATUS;
                  } else {
                     AWorkbench.popup(
                        "Branch: " + newBranch + " has NOT been created.  Error occurred.  Contact OSEE Admin.");
                     return Status.CANCEL_STATUS;
                  }
               }
            };

            Jobs.runInJob("Create Branch", runnable, Activator.class, Activator.PLUGIN_ID);
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
