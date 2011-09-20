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
package org.eclipse.osee.cluster;

/**
 * @author Roberto E. Escobar
 */
public interface Transaction {

   public static enum TransactionStatus {
      NO_TXN,
      ACTIVE,
      PREPARED,
      COMMITTED,
      ROLLED_BACK,
      PREPARING,
      COMMITTING,
      ROLLING_BACK,
      UNKNOWN;
   }

   /**
    * Creates a new transaction and associates it with the current thread.
    * 
    * @throws IllegalStateException if transaction is already began
    */
   void begin() throws IllegalStateException;

   /**
    * Commits the transaction associated with the current thread.
    * 
    * @throws IllegalStateException if transaction didn't begin.
    */
   void commit() throws IllegalStateException;

   /**
    * Rolls back the transaction associated with the current thread.
    * 
    * @throws IllegalStateException if transaction didn't begin.
    */
   void rollback() throws IllegalStateException;

   /**
    * Returns the status of the transaction associated with the current thread.
    * 
    * @return the status
    */
   TransactionStatus getStatus();
}