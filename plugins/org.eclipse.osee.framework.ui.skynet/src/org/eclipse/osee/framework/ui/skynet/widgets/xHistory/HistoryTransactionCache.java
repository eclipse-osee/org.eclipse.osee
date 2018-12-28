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
package org.eclipse.osee.framework.ui.skynet.widgets.xHistory;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Donald G. Dunne
 */
public class HistoryTransactionCache implements IHistoryTransactionProvider {

   private final Map<Long, TransactionRecord> txIdToTransRecord = new HashMap<>(500);

   public Map<Long, TransactionRecord> getTxIdToTransRecord() {
      return txIdToTransRecord;
   }

   @Override
   public TransactionRecord getTransactionRecord(Long id) {
      return txIdToTransRecord.get(id);
   }

   @Override
   public void put(Long id, TransactionRecord transaction) {
      txIdToTransRecord.put(transaction.getId(), transaction);
   }

}
