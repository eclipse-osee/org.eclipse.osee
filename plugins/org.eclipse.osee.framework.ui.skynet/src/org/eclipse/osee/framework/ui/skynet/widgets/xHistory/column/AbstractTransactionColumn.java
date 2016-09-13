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

import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.IHistoryTransactionProvider;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractTransactionColumn extends XViewerColumn implements IXViewerPreComputedColumn {

   protected final IHistoryTransactionProvider txCache;

   public AbstractTransactionColumn(IHistoryTransactionProvider txCache, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.txCache = txCache;
   }

   protected TransactionRecord getTransactionRecord(Change data) {
      return txCache.getTransactionRecord(data.getTxDelta().getEndTx().getId());
   }

   @Override
   public Long getKey(Object obj) {
      if (obj instanceof Change) {
         Change data = (Change) obj;
         return data.getTxDelta().getEndTx().getId();
      }
      return 0L;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return cachedValue;
   }

}
