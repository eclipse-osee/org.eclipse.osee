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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 */
public class ShowFinalVersionHandler extends AbstractSelectionChangedHandler {
   // private static final RendererManager rendererManager = RendererManager.getInstance();
   public ShowFinalVersionHandler() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         IStructuredSelection myIStructuredSelection =
               (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
         List<Branch> mySelectedBranchList = Handlers.getBranchListFromStructuredSelection(myIStructuredSelection);
         List<TransactionData> myTransactionDataList =
               Handlers.getTransactionDataNeededFromStructuredSelection(myIStructuredSelection);
         if (mySelectedBranchList != null && mySelectedBranchList.size() == 1) {
            ChangeReportView.openViewUpon(mySelectedBranchList.get(0));
         } else if (myTransactionDataList != null && myTransactionDataList.size() == 2) {
            TransactionId transaction1 = myTransactionDataList.get(0).getTransactionId();
            TransactionId transaction2 = myTransactionDataList.get(1).getTransactionId();
            TransactionId base =
                  transaction1.getTransactionNumber() < transaction2.getTransactionNumber() ? transaction1 : transaction2;
            TransactionId to =
                  transaction1.getTransactionNumber() < transaction2.getTransactionNumber() ? transaction2 : transaction1;

            ChangeReportView.openViewUpon(new ChangeReportInput(base.getBranch().getDisplayName(), base, to));

         }
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
      }

      return null;
   }
}
