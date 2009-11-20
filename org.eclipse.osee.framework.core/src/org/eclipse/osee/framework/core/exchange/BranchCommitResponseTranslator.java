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
package org.eclipse.osee.framework.core.exchange;

import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 */
public class BranchCommitResponseTranslator implements ITranslator<BranchCommitResponse> {

   private enum Entry {
      TRANSACTION_NUMBER
   }

   private final IOseeCachingServiceProvider cachingService;

   public BranchCommitResponseTranslator(IOseeCachingServiceProvider cachingService) {
      this.cachingService = cachingService;
   }

   public BranchCommitResponse convert(PropertyStore propertyStore) throws OseeCoreException {
      BranchCommitResponse response = new BranchCommitResponse();
      int txNumber = propertyStore.getInt(Entry.TRANSACTION_NUMBER.name());
      TransactionRecord transactionRecord =
            cachingService.getOseeCachingService().getTransactionCache().getById(txNumber);
      response.setTransaction(transactionRecord);
      return response;
   }

   public PropertyStore convert(BranchCommitResponse data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      TransactionRecord record = data.getTransaction();
      store.put(Entry.TRANSACTION_NUMBER.name(), record != null ? record.getId() : -1);
      return store;
   }

}