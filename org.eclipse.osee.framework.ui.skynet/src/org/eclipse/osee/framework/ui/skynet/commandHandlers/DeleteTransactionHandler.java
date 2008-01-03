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

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @author Paul K. Waldfogel
 */
public class DeleteTransactionHandler extends AbstractSelectionChangedHandler {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

   /**
    * @param branchTable
    */

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      TransactionData selectedTransaction =
            (TransactionData) ((JobbedNode) myIStructuredSelection.getFirstElement()).getBackingData();
      if (MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Delete Transaction",
            "Are you sure you want to delete the transaction: " + selectedTransaction.getTransactionNumber())) {
         branchManager.deleteTransaction(selectedTransaction.getTransactionNumber());
      }

      return null;

   }

   @Override
   public boolean isEnabled() {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      return SkynetSelections.oneTransactionSelected(myIStructuredSelection) && OseeProperties.getInstance().isDeveloper();
   }

}