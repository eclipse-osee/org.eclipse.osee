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
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.KeyedLevelManager;
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
   protected void onInitialEntry() throws SQLException {
      super.onInitialEntry();
      try {
         if (connection == null) connection = ConnectionHandler.getPooledConnection();
         priorCommit = connection.getAutoCommit();
         connection.setAutoCommit(false);
         transactionNeedsRollback = false;
         String dbName = connection.getMetaData().getDatabaseProductName();
         dbName = dbName.toLowerCase();
         if (dbName.contains("mysql") || dbName.contains("derby")) {
            OseeLog.log(
                  Activator.class,
                  Level.WARNING,
                  String.format(
                        "We are doing a naughty thing in DbTransactionManager line:66.  We skip defering constraint checking because we're using %s, " + "and we're checking the metaData to see if we are using %s.",
                        dbName, dbName));
         } else {
            DbUtil.deferConstraintChecking(connection);
         }
      } catch (SQLException e) {
         requestRollback();
         throw e;
      }
   }

   @Override
   protected void onLastExit() {
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

   /**
    * @return Returns the connection.
    */
   public Connection getConnection() {
      return connection;
   }

   /**
    * If the manager is already managing a transaction and is given a new connection, then that transaction will be
    * marked as corrupted, and thusly all changes will be rolledback upon completion of the transaction.
    * 
    * @param connection The connection to set.
    */
   public void setConnection(Connection connection) {
      if (this.connection == connection) return;

      if (this.connection != null) {
         try {
            try {
               this.connection.rollback();
            } finally {
               this.connection.close();
            }
         } catch (SQLException ex) {
            OseeLog.log(Activator.class, Level.WARNING, ex);
         }
      }

      this.connection = connection;

      if (inTransaction()) {
         requestRollback();
         if (connection != null) {
            try {
               connection.setAutoCommit(false);
            } catch (SQLException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.plugin.util.db.KeyedLevelManager#endTransactionLevel(java.lang.Object)
    */
   @Override
   public void endTransactionLevel(Object key) throws SQLException {
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
