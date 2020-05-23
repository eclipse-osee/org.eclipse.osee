/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column;

import java.util.Collection;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.IHistoryTransactionProvider;

/**
 * @author Donald G. Dunne
 */
public class HistoryTransactionIdColumn extends AbstractTransactionColumn {

   public static final String ID = "framework.history.transaction";

   public HistoryTransactionIdColumn(IHistoryTransactionProvider txCache) {
      super(txCache, ID, "Transaction", 90, XViewerAlign.Left, true, SortDataType.Integer, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HistoryTransactionIdColumn copy() {
      HistoryTransactionIdColumn newXCol = new HistoryTransactionIdColumn(txCache);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object obj : objects) {
         if (obj instanceof Change) {
            Change data = (Change) obj;
            TransactionRecord endTx = getTransactionRecord(data);
            preComputedValueMap.put(data.getTxDelta().getEndTx().getId(), String.valueOf(endTx.getId()));
         }
      }
   }

}
