/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.IHistoryTransactionProvider;

/**
 * @author Donald G. Dunne
 */
public class HistoryTransactionDateColumn extends AbstractTransactionColumn {

   public static final String ID = "framework.history.timeStamp";
   // Cache to quickly get author so don't need to load from UserManager
   private final Map<Long, String> transIdToDateStr = new HashMap<>();
   private final Map<Long, Date> transIdToDate = new HashMap<>();

   public HistoryTransactionDateColumn(IHistoryTransactionProvider txCache) {
      super(txCache, ID, "Time Stamp", 110, XViewerAlign.Left, true, SortDataType.Date, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HistoryTransactionDateColumn copy() {
      HistoryTransactionDateColumn newXCol = new HistoryTransactionDateColumn(txCache);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object obj : objects) {
         if (obj instanceof Change) {
            Change data = (Change) obj;
            TransactionRecord endTx = getTransactionRecord(data);
            String value = transIdToDateStr.get(endTx.getId());
            if (value == null) {
               Date timeStamp = endTx.getTimeStamp();
               value = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(timeStamp);
               transIdToDateStr.put(endTx.getId(), value);
               transIdToDate.put(endTx.getId(), timeStamp);
            }
            preComputedValueMap.put(data.getTxDelta().getEndTx().getId(), value);
         }
      }
   }

   public Date getTransactionDate(Long id) {
      return transIdToDate.get(id);
   }

}
