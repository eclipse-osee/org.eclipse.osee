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
package org.eclipse.osee.framework.core.message.internal.translation;

import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 */
public class BranchCommitResponseTranslator implements ITranslator<BranchCommitResponse> {

   private static enum Entry {
      TRANSACTION_NUMBER
   }

   @Override
   public BranchCommitResponse convert(PropertyStore propertyStore) throws OseeCoreException {
      BranchCommitResponse response = new BranchCommitResponse();
      Integer transactionRecord = propertyStore.getInt(Entry.TRANSACTION_NUMBER.name());
      response.setTransactionId(transactionRecord);
      return response;
   }

   @Override
   public PropertyStore convert(BranchCommitResponse data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      Integer record = data.getTransactionId();
      store.put(Entry.TRANSACTION_NUMBER.name(), record);
      return store;
   }

}