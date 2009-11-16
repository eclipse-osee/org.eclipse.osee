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

import org.eclipse.osee.framework.core.data.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCache {

   private final ITransactionDataAccessor accessor;

   public TransactionCache(ITransactionDataAccessor accessor) {
      this.accessor = accessor;
   }

   public void cache(TransactionRecord record) {

   }

   public TransactionRecord getById(int txId) {
      return null;
   }
}
