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
package org.eclipse.osee.framework.branch.management.transaction;

import org.eclipse.osee.framework.branch.management.ITransactionService.TransactionVersion;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ITransactionDataAccessor {

   /**
    * Loads a specific transaction record
    * 
    * @param cache to populate
    * @param branch to load
    * @throws OseeCoreException
    */
   public void loadTransactionRecord(TransactionCache cache, int transactionId) throws OseeCoreException;

   /**
    * Loads all transactions for a specific branch
    * 
    * @param cache to populate
    * @param branch to load
    * @throws OseeCoreException
    */
   public void loadTransactionRecord(TransactionCache cache, Branch branch) throws OseeCoreException;

   /**
    * Load a specific branch transaction type
    * 
    * @see {@link TransactionVersion}
    * @param cache to populate
    * @param branch to load
    * @param transactionType transaction type to load, can be {@link TransactionVersion#HEAD} or
    *           {@link TransactionVersion#BASE}
    * @throws OseeCoreException
    */
   public void loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException;

}