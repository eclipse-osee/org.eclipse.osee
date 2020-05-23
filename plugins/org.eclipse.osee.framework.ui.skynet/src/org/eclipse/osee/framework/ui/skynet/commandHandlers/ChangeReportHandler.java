/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportHandler extends CommandHandler {

   @Override
   public boolean isEnabledWithException(IStructuredSelection selection) {
      boolean enabled = false;

      if (!selection.isEmpty()) {
         if (selection.size() == 1) {
            Object selectedObject = selection.getFirstElement();

            if (selectedObject instanceof TransactionRecord) {
               enabled = ((TransactionRecord) selectedObject).getTxType() != TransactionDetailsType.Baselined;
            } else if (selectedObject instanceof BranchId) {
               enabled = true;
            }
         } else if (selection.size() == 2) {
            Object[] items = selection.toArray();
            if (items[0] instanceof TransactionToken && items[1] instanceof TransactionToken) {
               if (((TransactionToken) items[0]).isOnSameBranch((TransactionToken) items[1])) {
                  enabled = true;
               }
            }
         } else {
            enabled = false;
         }
      }

      return enabled;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      if (!selection.isEmpty()) {
         try {
            if (selection.size() == 2) {
               Object[] items = selection.toArray();
               if (items[0] instanceof TransactionToken && items[1] instanceof TransactionToken) {
                  TransactionToken tx1 = (TransactionToken) items[0];
                  TransactionToken tx2 = (TransactionToken) items[1];
                  TransactionToken startTx = tx1.isOlderThan(tx2) ? tx1 : tx2;
                  TransactionToken endTx = startTx.equals(tx1) ? tx2 : tx1;
                  ChangeUiUtil.open(startTx, endTx);
               }
            } else {
               Object selectedObject = selection.getFirstElement();
               if (selectedObject instanceof TransactionToken) {
                  ChangeUiUtil.open((TransactionToken) selectedObject);
               } else if (selectedObject instanceof BranchId) {
                  ChangeUiUtil.open((BranchId) selectedObject);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }
}
