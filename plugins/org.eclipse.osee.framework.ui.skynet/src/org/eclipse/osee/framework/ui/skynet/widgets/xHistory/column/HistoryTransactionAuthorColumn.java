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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.IHistoryTransactionProvider;

/**
 * @author Donald G. Dunne
 */
public class HistoryTransactionAuthorColumn extends AbstractTransactionColumn {

   // Cache to quickly get author so don't need to load from UserManager
   private final Map<Long, String> transIdToAuthor = new HashMap<>();

   public HistoryTransactionAuthorColumn(IHistoryTransactionProvider txCache) {
      super(txCache, "framework.history.author", "Author", 100, XViewerAlign.Left, true, SortDataType.String, false,
         null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HistoryTransactionAuthorColumn copy() {
      HistoryTransactionAuthorColumn newXCol = new HistoryTransactionAuthorColumn(txCache);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object obj : objects) {
         if (obj instanceof Change) {
            Change data = (Change) obj;
            TransactionRecord endTx = getTransactionRecord(data);
            String value = transIdToAuthor.get(endTx.getId());
            if (value == null) {
               value = UserManager.getSafeUserNameById(endTx.getAuthor());
               transIdToAuthor.put(endTx.getId(), value);
            }
            preComputedValueMap.put(data.getTxDelta().getEndTx().getId(), value);
         }
      }
      // don't need anymore
      transIdToAuthor.clear();
   }

}
