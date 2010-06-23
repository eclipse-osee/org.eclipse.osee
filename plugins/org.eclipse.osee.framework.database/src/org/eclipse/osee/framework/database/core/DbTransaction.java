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
package org.eclipse.osee.framework.database.core;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * This abstract class provides a uniform way of executing database transactions. It handles exceptions ensuring that
 * transactions are processed in the correct order and roll-backs are performed whenever errors are detected.
 * 
 * @author Roberto E. Escobar
 */
public abstract class DbTransaction {

   /**
    * Gets the name of this transaction. This is provided mainly for logging purposes.
    * 
    * @return String transaction class Name
    */
   protected String getTxName() {
      return this.getClass().getCanonicalName();
   }

   /**
    * This template method calls {@link #handleTxWork} which is provided by child classes. This method handles
    * roll-backs and exception handling to prevent transactions from being left in an incorrect state.
    * 
    * @throws OseeCoreException
    */
   public void execute() throws OseeCoreException {
      DatabaseTransactions.execute(new InternalTransactionWork());
   }

   /**
    * Provides the transaction's work implementation.
    * 
    * @param connection
    * @throws OseeCoreException
    */
   protected abstract void handleTxWork(OseeConnection connection) throws OseeCoreException;

   /**
    * When an exception is detected during transaction processing, the exception is caught and passed to this method.
    * This convenience method is provided so child classes have access to the exception. <br/>
    * <b>Override to handle transaction exception</b>
    * 
    * @param ex
    * @throws Exception
    */
   protected void handleTxException(Exception ex) {
   }

   /**
    * This convenience method is provided in case child classes have a portion of code that needs to execute always at
    * the end of the transaction, regardless of exceptions. <br/>
    * <b>Override to add additional code to finally block</b>
    * 
    * @throws OseeCoreException
    */
   protected void handleTxFinally() throws OseeCoreException {
      // override to add additional code to finally
   }

   private final class InternalTransactionWork implements IDbTransactionWork {

      @Override
      public void handleTxException(Exception ex) {
         DbTransaction.this.handleTxException(ex);
      }

      @Override
      public void handleTxFinally() throws OseeCoreException {
         DbTransaction.this.handleTxFinally();
      }

      @Override
      public void handleTxWork(OseeConnection connection) throws OseeCoreException {
         DbTransaction.this.handleTxWork(connection);
      }

      @Override
      public String getName() {
         return DbTransaction.this.getTxName();
      }
   };
}