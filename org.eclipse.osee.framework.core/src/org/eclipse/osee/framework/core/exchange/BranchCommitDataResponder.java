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

import org.eclipse.osee.framework.core.data.CommitTransactionRecordResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 */
public class BranchCommitDataResponder implements IDataTranslator<CommitTransactionRecordResponse> {
   private enum Entry {
      TRANSACTION_NUMBER
   }

   public BranchCommitDataResponder() {
   }

   public CommitTransactionRecordResponse convert(PropertyStore propertyStore) throws OseeCoreException {
      return new CommitTransactionRecordResponse(propertyStore.getInt(Entry.TRANSACTION_NUMBER.name()));
   }

   public PropertyStore convert(CommitTransactionRecordResponse data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.TRANSACTION_NUMBER.name(), data.getTransactionNumber());
      return store;
   }

   public byte[] convertToReponse(CommitTransactionRecordResponse data) {
      //TODO return the byte data
      return new byte[0];
   }
}