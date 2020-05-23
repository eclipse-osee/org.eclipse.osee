/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.actions;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.ITransactionRecordSelectionProvider;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.ChangeReportHandler;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowChangeReportSinceAction extends Action {

   private final ITransactionRecordSelectionProvider selectionProvider;
   private final BranchId branch;

   public ShowChangeReportSinceAction(BranchId branch, ITransactionRecordSelectionProvider selectionProvider) {
      super("Show Change Report Since");
      this.branch = branch;
      this.selectionProvider = selectionProvider;
      setToolTipText("Show changes since selected transaction");
   }

   @Override
   public void run() {
      List<TransactionId> selectedTransactionRecords = selectionProvider.getSelectedTransactionRecords();
      if (selectedTransactionRecords.size() != 1) {
         AWorkbench.popup("Must select 1 transaction to show Change Report Since");
         return;
      }
      ChangeReportHandler handler = new ChangeReportHandler();
      TransactionId headTransaction = TransactionManager.getHeadTransaction(branch);
      List<TransactionId> records = Arrays.asList(selectedTransactionRecords.iterator().next(), headTransaction);
      handler.executeWithException(null, new StructuredSelection(records));
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE);
   }

}
