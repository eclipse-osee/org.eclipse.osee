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

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @author Paul K. Waldfogel
 */
public class DeleteTransactionHandler extends AbstractSelectionHandler {
   // private static final Logger logger =
   // ConfigUtil.getConfigFactory().getLogger(ImportOntoBranchHandler.class);
   // private static final AccessControlManager accessManager = AccessControlManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

   // BranchPersistenceManager.getInstance();
   // private static final TransactionIdManager transactionIdManager =
   // TransactionIdManager.getInstance();
   // private TreeViewer branchTable;
   // private boolean selective;

   /**
    * @param branchTable
    */
   public DeleteTransactionHandler() {
      super(new String[] {"TransactionData"});
      // this.branchTable = branchTable;
      // this.selective = selective;
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection myIStructuredSelection = super.getIStructuredSelection();
      List<TransactionData> myTransactionDataList = super.getTransactionDataList();
      TransactionData selectedTransaction =
            (TransactionData) ((JobbedNode) myIStructuredSelection.getFirstElement()).getBackingData();
      if (selectedTransaction == myTransactionDataList.get(0)) {
         System.out.println("selectedTransaction == myTransactionDataList.get(0)");
      }
      if (MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Delete Transaction",
            "Are you sure you want to delete the transaction: " + selectedTransaction.getTransactionNumber())) {
         branchManager.deleteTransaction(selectedTransaction.getTransactionNumber());
      }

      return null;

   }

   @Override
   public boolean isEnabled() {
      // IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      IStructuredSelection myIStructuredSelection = super.getIStructuredSelection();

      return SkynetSelections.oneTransactionSelected(myIStructuredSelection) && OseeProperties.getInstance().isDeveloper();
   }

}