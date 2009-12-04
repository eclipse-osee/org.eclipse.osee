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
package org.eclipse.osee.framework.core.cache;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public interface ITransactionDataAccessor {

   /**
    * Loads a specific set of transaction records
    * 
    * @param cache to populate
    * @param transaction ids to load
    * @throws OseeCoreException
    */
   public void loadTransactionRecord(TransactionCache cache, Collection<Integer> transactionIds) throws OseeCoreException;

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
   public TransactionRecord loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException;

   public void load(TransactionCache transactionCache) throws OseeCoreException;

}