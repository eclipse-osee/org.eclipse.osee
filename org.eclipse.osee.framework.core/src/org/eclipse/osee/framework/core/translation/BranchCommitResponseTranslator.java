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

import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 */
public class BranchCommitResponseTranslator implements ITranslator<BranchCommitResponse> {

   private enum Entry {
      TRANSACTION_NUMBER
   }

   private final IDataTranslationService service;

   public BranchCommitResponseTranslator(IDataTranslationService service) {
      this.service = service;
   }

   public BranchCommitResponse convert(PropertyStore propertyStore) throws OseeCoreException {
      BranchCommitResponse response = new BranchCommitResponse();
      PropertyStore innerStore = propertyStore.getPropertyStore(Entry.TRANSACTION_NUMBER.name());
      TransactionRecord transactionRecord = service.convert(innerStore, CoreTranslatorId.TRANSACTION_RECORD);
      response.setTransaction(transactionRecord);
      return response;
   }

   public PropertyStore convert(BranchCommitResponse data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      TransactionRecord record = data.getTransaction();
      PropertyStore property = service.convert(record, CoreTranslatorId.TRANSACTION_RECORD);
      store.put(Entry.TRANSACTION_NUMBER.name(), property);
      return store;
   }

}