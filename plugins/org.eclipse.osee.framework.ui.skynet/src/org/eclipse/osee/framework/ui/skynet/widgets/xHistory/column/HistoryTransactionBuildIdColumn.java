/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.IHistoryTransactionProvider;

/**
 * @author Donald G. Dunne
 */
public class HistoryTransactionBuildIdColumn extends AbstractTransactionColumn {

   public static final String ID = "framework.history.buildId";

   public HistoryTransactionBuildIdColumn(IHistoryTransactionProvider txCache) {
      super(txCache, ID, "Build Id", 90, XViewerAlign.Left, true, SortDataType.Integer, false,
         "Since the build is mostly a long anyway (eg: 0.25.2.v201708012108-NR), \n build_id " //
            + "will be stored in the database that represents build as\n \n nn rr yyyy mm dd hh mm b\n \n " //
            + "where \"nn rr\" is 2502 representing 0.25.2 build\n \n and b is \n \n 0 - unknown\n 1 - " //
            + "rel\n 2 - nr beta\n 3 - nr alpha\n 4 - dev beta\n 5 - dev alpha\n \n For runtime transactions, " //
            + "only \"nn rr\" will show cause there is no build id.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HistoryTransactionBuildIdColumn copy() {
      HistoryTransactionBuildIdColumn newXCol = new HistoryTransactionBuildIdColumn(txCache);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object obj : objects) {
         if (obj instanceof Change) {
            Change data = (Change) obj;
            TransactionRecord endTx = getTransactionRecord(data);
            preComputedValueMap.put(data.getTxDelta().getEndTx().getId(), String.valueOf(endTx.getBuildId()));
         }
      }
   }

}
