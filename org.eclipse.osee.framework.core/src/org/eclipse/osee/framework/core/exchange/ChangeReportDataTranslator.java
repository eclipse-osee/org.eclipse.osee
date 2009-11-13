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

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.ChangeReportData;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportDataTranslator implements IDataTranslator<ChangeReportData> {
   private enum Entry {
      TO_TRANSACTION,
      FROM_TRANSACTION,
      IS_HISTORY
   }

   private final IDataTranslationService service;
   
   public ChangeReportDataTranslator(IDataTranslationService service) {
      super();
      this.service = service;
   }

   @Override
   public ChangeReportData convert(PropertyStore propertyStore) throws OseeCoreException {
      PropertyStore toTransactionStore = propertyStore.getPropertyStore(Entry.TO_TRANSACTION.name());
      PropertyStore fromTransactionStore = propertyStore.getPropertyStore(Entry.FROM_TRANSACTION.name());

      TransactionRecord toTransaction = service.convert(toTransactionStore, TransactionRecord.class);
      TransactionRecord fromTransaction = service.convert(fromTransactionStore, TransactionRecord.class);

      boolean isHistory = propertyStore.getBoolean(Entry.IS_HISTORY.name());
      ChangeReportData data = new ChangeReportData(toTransaction, fromTransaction, isHistory);
      return data;
   }

   @Override
   public PropertyStore convert(ChangeReportData data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.IS_HISTORY.name(), data.isHistorical());
      store.put(Entry.TO_TRANSACTION.name(), service.convert(data.getToTransactionRecord(), TransactionRecord.class));
      store.put(Entry.FROM_TRANSACTION.name(), service.convert(data.getFromTransactionRecord(), TransactionRecord.class));

      return store;
   }

}
