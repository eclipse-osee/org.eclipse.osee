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
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.data.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
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

   private final IOseeModelFactoryServiceProvider provider;

   public TransactionCacheUpdateResponseTranslator(IOseeModelFactoryServiceProvider provider) {
      this.provider = provider;
   }

   private TransactionRecordFactory getFactory() throws OseeCoreException {
      return provider.getOseeFactoryService().getTransactionFactory();
   }

   @Override
   public TransactionCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {
      TransactionRecordFactory factory = getFactory();
      List<TransactionRecord> rows = new ArrayList<TransactionRecord>();
      int rowCount = store.getInt(Fields.TX_COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.TX_ROW, index));
         rows.add(fromArray(factory, rowData));
      }
      return new TransactionCacheUpdateResponse(rows);
   }

   @Override
   public PropertyStore convert(TransactionCacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      List<TransactionRecord> rows = object.getTxRows();
      for (int index = 0; index < rows.size(); index++) {
         TransactionRecord row = rows.get(index);
         store.put(createKey(Fields.TX_ROW, index), toArray(row));
      }
      store.put(Fields.TX_COUNT.name(), rows.size());
      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }

   private String[] toArray(TransactionRecord row) {
      return new String[] {String.valueOf(row.getId()), row.getTxType().name(), row.getComment(),
            String.valueOf(row.getTimeStamp().getTime()), String.valueOf(row.getAuthor()),
            String.valueOf(row.getCommit()), String.valueOf(row.getBranchId())};
   }

   private static TransactionRecord fromArray(TransactionRecordFactory factory, String[] data) throws OseeCoreException {
      int index = 0;
      int txId = Integer.valueOf(data[index++]);
      TransactionDetailsType txType = TransactionDetailsType.valueOf(data[index++]);
      String comment = data[index++];
      Date timeStamp = new Date(Long.valueOf(data[index++]));
      int authorArtId = Integer.valueOf(data[index++]);
      int commitArtId = Integer.valueOf(data[index++]);
      int branchId = Integer.valueOf(data[index++]);
      return factory.create(txId, branchId, comment, timeStamp, authorArtId, commitArtId, txType);
   }
}
