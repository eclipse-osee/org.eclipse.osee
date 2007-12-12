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
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 */
public class ShowFinalVersionHandler extends AbstractSelectionHandler {
   // private static final RendererManager rendererManager = RendererManager.getInstance();
   public ShowFinalVersionHandler() {
      super(new String[] {"Branch", "TransactionData"});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         List<Branch> mySelectedBranchList = super.getBranchList();
         List<TransactionData> myTransactionDataList = super.getTransactionDataList();
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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractArtifactSelectionHandler#permissionLevel()
    */
   @Override
   protected PermissionEnum permissionLevel() {
      return PermissionEnum.READ;
   }

}
