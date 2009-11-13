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

import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class TransactionRecordDataTranslator implements IDataTranslator<TransactionRecord> {
   private enum Entry {
      TRANSACTION_NUMBER
   }

   public TransactionRecordDataTranslator() {
   }

   public TransactionRecord convert(PropertyStore propertyStore) throws OseeCoreException {
      String transactionNumber = propertyStore.get(Entry.TRANSACTION_NUMBER.name());
      //need to get a transaction record from a transaction number?
      return null;
   }

   public PropertyStore convert(TransactionRecord transactionRecord) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.TRANSACTION_NUMBER.name() , transactionRecord.getId());

      return store;
   }
}
