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
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
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

   public ChangeReportEditorInputFactory() {
   }

   public IAdaptable createElement(IMemento memento) {
      ChangeReportEditorInput toReturn = null;
      try {
         if (memento != null) {
            if (SkynetViews.isSourceValid(memento)) {
               CompareType compareType = CompareType.valueOf(memento.getString(COMPARE_TYPE));

               int startTxId = memento.getInteger(START_TX_KEY);
               int endTxId = memento.getInteger(END_TX_KEY);

               TransactionRecord startTx = TransactionManager.getTransactionId(startTxId);
               TransactionRecord endTx = TransactionManager.getTransactionId(endTxId);
               TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
               toReturn = ChangeUiUtil.createInput(compareType, txDelta, false);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Change report error on init", ex);
      }
      return toReturn;
   }

   public static void saveState(IMemento memento, ChangeReportEditorInput input) {
      TransactionDelta txDelta = input.getChangeData().getTxDelta();
      memento.putInteger(START_TX_KEY, txDelta.getStartTx().getId());
      memento.putInteger(END_TX_KEY, txDelta.getEndTx().getId());
      memento.putString(COMPARE_TYPE, input.getChangeData().getCompareType().name());
      SkynetViews.addDatabaseSourceId(memento);
   }
}
