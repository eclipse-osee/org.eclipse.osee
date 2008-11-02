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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.KeyedLevelManager;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Keeps track of contributions to a database transaction. This provides a mechanism for detecting rollback conditions
 * of a transaction that includes nested levels of method calls that would normally produce their own transaction but
 * are instead being included in a higher level transaction.
 * 
 * @author Robert A. Fisher
 */
public final class DbTransactionManager extends KeyedLevelManager {
   private final static List<IDbTransactionListener> listeners = new CopyOnWriteArrayList<IDbTransactionListener>();

   private boolean transactionNeedsRollback;
   private boolean priorCommit;
   private Connection connection;

   public DbTransactionManager() {
      this.transactionNeedsRollback = false;
      this.priorCommit = false;
   }

   @Override
   protected void onInitialEntry() throws OseeDataStoreException {
      super.onInitialEntry();
      try {
         if (connection == null) connection = ConnectionHandler.getPooledConnection();
         priorCommit = connection.getAutoCommit();
         connection.setAutoCommit(false);
         transactionNeedsRollback = false;
         DbTransaction.deferConstraintChecking(connection);
      } catch (SQLException ex) {
         requestRollback();
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   protected void onLastExit() throws OseeDataStoreException {
      // This is used to signify that the commit itself was successful, and does
      // not necessarily signify that no SQLException occurred.
      boolean committed = false;

      try {
         if (connection != null) {
            if (transactionNeedsRollback) {
               connection.rollback();
            } else {
               connection.commit();
               committed = true;
            }

            connection.setAutoCommit(priorCommit);
         }
      } catch (SQLException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         transactionNeedsRollback = false;
         ConnectionHandler.repoolConnection(connection);
         connection = null;

         notifyListeners(committed);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.plugin.util.db.KeyedLevelManager#endTransactionLevel(java.lang.Object)
    */
   @Override
   public void endTransactionLevel(Object key) throws OseeDataStoreException {
      if (true != isTransactionLevelSuccess(key)) {
         requestRollback();
      }
      super.endTransactionLevel(key);
   }

   /**
    * Mark the transaction being managed as needing rollback. This may be necessary from a failed sql, loss of a
    * connection, or any other time the application deems it necessary.
    */
   public void requestRollback() {
      if (!inTransaction()) throw new IllegalStateException(
            "Can not request a rollback when no transaction is in progress");

      transactionNeedsRollback = true;
   }

   private void notifyListeners(boolean committed) {
      DbTransactionEventCompleted eventCompleted = new DbTransactionEventCompleted(committed);

      for (IDbTransactionListener listener : listeners) {
         listener.onEvent(eventCompleted);
      }
   }

   public void addListener(IDbTransactionListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("The listener can not be null");
      }
      listeners.add(listener);
   }

   public boolean removeListener(IDbTransactionListener listener) {
      return listeners.remove(listener);
   }
}
