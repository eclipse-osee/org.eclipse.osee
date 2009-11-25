/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.Collection;
import org.eclipse.osee.framework.core.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Roberto E. Escobar
 */
public class ClientTransactionAccessor implements ITransactionDataAccessor {

   @Override
   public void loadTransactionRecord(TransactionCache cache, Collection<Integer> transactionIds) throws OseeCoreException {
      System.out.println("Transaction Loading");
   }

   @Override
   public void loadTransactionRecord(TransactionCache cache, Branch branch) throws OseeCoreException {
      System.out.println("Transaction Loading");
   }

   @Override
   public void loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException {
      System.out.println("Transaction Loading");
   }

}
