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

import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 */
public class ChangeReportEditorInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInputFactory"; //$NON-NLS-1$
   private final static String START_TX_KEY = "org.eclipse.osee.framework.ui.skynet.change.transaction.start"; //$NON-NLS-1$
   private final static String END_TX_KEY = "org.eclipse.osee.framework.ui.skynet.change.transaction.end"; //$NON-NLS-1$
   private final static String COMPARE_TYPE = "org.eclipse.osee.framework.ui.skynet.change.compareType"; //$NON-NLS-1$
   private final static String BRANCH_ID_KEY = "org.eclipse.osee.framework.ui.skynet.change.branchId"; //$NON-NLS-1$
   private final static String TRANSACTION_TAB_ACTIVE_KEY =
      "org.eclipse.osee.framework.ui.skynet.change.transactionTabActive"; //$NON-NLS-1$

   @Override
   public IAdaptable createElement(IMemento memento) {
      ChangeReportEditorInput toReturn = null;
      try {
         if (memento != null) {
            CompareType compareType = CompareType.valueOf(memento.getString(COMPARE_TYPE));

            Long startTxId = Long.parseLong(memento.getString(START_TX_KEY));
            Long endTxId = Long.parseLong(memento.getString(END_TX_KEY));

            TransactionToken startTx = TransactionManager.getTransaction(startTxId);
            TransactionToken endTx = TransactionManager.getTransaction(endTxId);
            TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
            toReturn = ChangeUiUtil.createInput(compareType, txDelta, false);
            String branchUuid = memento.getString(BRANCH_ID_KEY);
            if (Strings.isNumeric(branchUuid)) {
               try {
                  toReturn.setBranch(BranchId.valueOf(branchUuid));
               } catch (Exception ex) {
                  // do nothing
               }
            }
            Boolean transactionTabActive = memento.getBoolean(TRANSACTION_TAB_ACTIVE_KEY);
            if (transactionTabActive != null) {
               toReturn.setTransactionTabActive(transactionTabActive);
               toReturn.setNotLoaded(true);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.WARNING, "Change report error on init", ex);
      }
      return toReturn;
   }

   public static void saveState(IMemento memento, ChangeReportEditorInput input) {
      TransactionDelta txDelta = input.getChangeData().getTxDelta();
      memento.putString(START_TX_KEY, txDelta.getStartTx().getId().toString());
      memento.putString(END_TX_KEY, txDelta.getEndTx().getId().toString());
      memento.putString(COMPARE_TYPE, input.getChangeData().getCompareType().name());
      if (input.getBranch() != null) {
         memento.putString(BRANCH_ID_KEY, String.valueOf(input.getBranch().getUuid()));
         memento.putBoolean(TRANSACTION_TAB_ACTIVE_KEY, input.isTransactionTabActive());
      }
   }
}
