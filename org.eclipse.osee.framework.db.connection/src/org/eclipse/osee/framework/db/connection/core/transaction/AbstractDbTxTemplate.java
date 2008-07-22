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
package org.eclipse.osee.framework.db.connection.core.transaction;

import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This abstract class provides a uniform way of executing database transactions. It handles exceptions ensuring that
 * transactions are processed in the correct order and roll-backs are performed whenever errors are detected.
 * 
 * @author Roberto E. Escobar
 */
public abstract class AbstractDbTxTemplate {

   /**
    * Transaction Constructor
    * 
    * @param branch The branch this transaction should operate on
    */
   public AbstractDbTxTemplate() {
   }

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
    * @throws Exception
    */
   public void execute() throws Exception {
      try {
         ConnectionHandler.startTransactionLevel(this);
         OseeLog.log(Activator.class, Level.FINEST, String.format("Start Transaction: [%s]", getTxName()));

         handleTxWork();

         ConnectionHandler.setTransactionLevelAsSuccessful(this);
         OseeLog.log(Activator.class, Level.FINEST, String.format("End Transaction: [%s]", getTxName()));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.FINEST, ex);
         handleTxException(ex);
      } finally {
         ConnectionHandler.endTransactionLevel(this);
         handleTxFinally();
      }
   }

   /**
    * Provides the transaction's work implementation.
    * 
    * @throws Exception
    */
   protected abstract void handleTxWork() throws Exception;

   /**
    * When an exception is detected during transaction processing, the exception is caught and passed to this method.
    * This convenience method is provided so child classes have access to the exception. <br/> <b>Override to handle
    * transaction exception</b>
    * 
    * @param ex
    * @throws Exception
    */
   protected void handleTxException(Exception ex) throws Exception {
      // override to handle transaction exception
      throw ex;
   }

   /**
    * This convenience method is provided in case child classes have a portion of code that needs to execute always at
    * the end of the transaction, regardless of exceptions. <br/><b>Override to add additional code to finally block</b>
    * 
    * @throws Exception
    */
   protected void handleTxFinally() throws Exception {
      // override to add additional code to finally
   }
}