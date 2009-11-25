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

import java.sql.Timestamp;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public final class TransactionRecordTranslator implements ITranslator<TransactionRecord> {

   private enum Entry {
      TRANSACTION_ID,
      TRANSACTION_BRANCH,
      TRANSACTION_TX_TYPE,
      TRANSACTION_COMMENT,
      TRANSACTION_TIMESTAMP,
      TRANSACTION_AUTHOR_ART_ID,
      TRANSACTION_COMMIT_ART_ID;
   }

   private final IOseeModelFactoryServiceProvider factoryProvider;

   public TransactionRecordTranslator(IOseeModelFactoryServiceProvider factoryProvider) {
      this.factoryProvider = factoryProvider;
   }

   public TransactionRecord convert(PropertyStore store) throws OseeCoreException {
      int transactionNumber = store.getInt(Entry.TRANSACTION_ID.name());
      TransactionDetailsType txType = TransactionDetailsType.valueOf(store.get(Entry.TRANSACTION_TX_TYPE.name()));
      String comment = store.get(Entry.TRANSACTION_COMMENT.name());
      Date time = new Timestamp(store.getLong(Entry.TRANSACTION_TIMESTAMP.name()));
      int authorArtId = store.getInt(Entry.TRANSACTION_AUTHOR_ART_ID.name());
      int commitArtId = store.getInt(Entry.TRANSACTION_COMMIT_ART_ID.name());
      int branchId = store.getInt(Entry.TRANSACTION_BRANCH.name());
      TransactionRecordFactory factory = factoryProvider.getOseeFactoryService().getTransactionFactory();
      return factory.create(transactionNumber, branchId, comment, time, authorArtId, commitArtId, txType);
   }

   public PropertyStore convert(TransactionRecord data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.TRANSACTION_ID.name(), data.getId());
      store.put(Entry.TRANSACTION_TX_TYPE.name(), data.getTxType().name());
      store.put(Entry.TRANSACTION_COMMENT.name(), data.getComment());
      store.put(Entry.TRANSACTION_TIMESTAMP.name(), data.getTimeStamp().getTime());
      store.put(Entry.TRANSACTION_AUTHOR_ART_ID.name(), data.getAuthor());

      store.put(Entry.TRANSACTION_COMMIT_ART_ID.name(), data.getCommit());

      store.put(Entry.TRANSACTION_BRANCH.name(), data.getBranchId());
      return store;
   }
}
