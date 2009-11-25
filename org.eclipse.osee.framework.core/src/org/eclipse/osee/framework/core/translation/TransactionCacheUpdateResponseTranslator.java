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
package org.eclipse.osee.framework.core.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.TransactionCacheUpdateResponse.TxRow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class TransactionCacheUpdateResponseTranslator implements ITranslator<TransactionCacheUpdateResponse> {

   private enum Fields {
      TX_COUNT,
      TX_ROW,
      TX_TO_BRANCH;
   }

   @Override
   public TransactionCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {
      List<TxRow> rows = new ArrayList<TxRow>();
      int rowCount = store.getInt(Fields.TX_COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.TX_ROW, index));
         rows.add(TxRow.fromArray(rowData));
      }

      Map<Integer, Integer> txToBranchId = TranslationUtil.getMap(store, Fields.TX_TO_BRANCH);
      return new TransactionCacheUpdateResponse(rows, txToBranchId);
   }

   @Override
   public PropertyStore convert(TransactionCacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      List<TxRow> rows = object.getTxRows();
      for (int index = 0; index < rows.size(); index++) {
         TxRow row = rows.get(index);
         store.put(createKey(Fields.TX_ROW, index), row.toArray());
      }
      store.put(Fields.TX_COUNT.name(), rows.size());
      TranslationUtil.putMap(store, Fields.TX_TO_BRANCH, object.getTxToBranchId());
      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }
}
