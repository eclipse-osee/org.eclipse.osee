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
package org.eclipse.osee.framework.db.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This abstract class provides a uniform way of executing database transactions. It handles exceptions ensuring that
 * transactions are processed in the correct order and roll-backs are performed whenever errors are detected.
 * 
 * @author Roberto E. Escobar
 */
public abstract class DbTransaction {

   /**
    * Transaction Constructor
    * 
    * @throws OseeCoreException TODO
    */
   public DbTransaction() throws OseeCoreException {
      OseeDbConnection.reportTxCreation(this);
   }

   /**
    * Gets the name of this transaction. This is provided mainly for logging purposes.
    * 
    * @return String transaction class Name
    */
   private String getTxName() {
      return this.getClass().getCanonicalName();
   }

   /**
    * This template method calls {@link #handleTxWork} which is provided by child classes. This method handles
    * roll-backs and exception handling to prevent transactions from being left in an incorrect state.
    * 
    * @throws Exception
    */
   public void execute() throws OseeCoreException {
      execute(OseeDbConnection.getConnection(), true);
   }

   public void execute(OseeConnection connection, boolean commit) throws OseeCoreException {
      boolean initialAutoCommit = true;
      try {
         OseeLog.log(InternalActivator.class, Level.FINEST, String.format("Start Transaction: [%s]", getTxName()));
         initialAutoCommit = connection.getAutoCommit();
         connection.setAutoCommit(false);
         ConnectionHandler.deferConstraintChecking(connection);
         if (commit) {
            OseeDbConnection.reportTxStart(this);
         }
         handleTxWork(connection);
         if (commit) {
            connection.commit();
         }
         OseeLog.log(InternalActivator.class, Level.FINEST, String.format("End Transaction: [%s]", getTxName()));
      } catch (Exception ex) {
         OseeLog.log(InternalActivator.class, Level.FINEST, ex);
         try {
            connection.rollback();
            connection.destroy();
         } catch (SQLException ex1) {
            throw new OseeWrappedException(ex1);
         }
         handleTxException(ex);
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         }
         throw new OseeWrappedException(ex);
      } finally {
         try {
            connection.setAutoCommit(initialAutoCommit);
         } catch (SQLException ex) {
            OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
         }
         if (commit) {
            connection.close();
            OseeDbConnection.reportTxEnd(this);
         }

         handleTxFinally();
      }
   }

   /**
    * Provides the transaction's work implementation.
    * 
    * @param connection
    * @throws OseeCoreException TODO
    */
   protected abstract void handleTxWork(Connection connection) throws OseeCoreException;

   /**
    * When an exception is detected during transaction processing, the exception is caught and passed to this method.
    * This convenience method is provided so child classes have access to the exception. <br/> <b>Override to handle
    * transaction exception</b>
    * 
    * @param ex
    * @throws Exception
    */
   protected void handleTxException(Exception ex) {
   }

   /**
    * This convenience method is provided in case child classes have a portion of code that needs to execute always at
    * the end of the transaction, regardless of exceptions. <br/><b>Override to add additional code to finally block</b>
    * 
    * @throws Exception
    */
   protected void handleTxFinally() throws OseeCoreException {
      // override to add additional code to finally
   }
}