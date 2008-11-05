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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.db.connection.internal.OseeConnectionPool;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeDbConnection {

   private static final Map<String, OseeConnectionPool> dbInfoToPools = new HashMap<String, OseeConnectionPool>();

   private static IDatabaseInfo databaseInfo = null;

   public static boolean hasOpenConnection() {
      OseeConnectionPool pool = dbInfoToPools.get(databaseInfo.getId());
      if (pool == null) {
         return false;
      }
      return pool.hasOpenConnection();
   }

   public static OseeConnection getConnection() throws OseeDataStoreException {
      return getConnection(databaseInfo);
   }

   public static OseeConnection getConnection(IDatabaseInfo databaseInfo) throws OseeDataStoreException {
      if (databaseInfo == null) {
         throw new OseeDataStoreException("Unable to get connection - database info was null.");
      }
      OseeConnectionPool pool = dbInfoToPools.get(databaseInfo.getId());
      if (pool == null) {
         pool =
               new OseeConnectionPool(databaseInfo.getDriver(), databaseInfo.getConnectionUrl(),
                     databaseInfo.getConnectionProperties());
         dbInfoToPools.put(databaseInfo.getId(), pool);
      }
      return pool.getConnection();
   }

   /**
    * @return whether a successful connection has been had to the database
    */
   public static boolean isConnectionValid() {
      try {
         OseeConnection connection = getConnection();
         connection.close();
      } catch (OseeDataStoreException ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
         return false;
      }
      return true;
   }

   /**
    * @param dbDriver
    * @param dbUrl
    * @param dbConnectionProperties
    */
   public static void setDatabaseInfo(IDatabaseInfo databaseInfo) {
      OseeDbConnection.databaseInfo = databaseInfo;
   }

   private static final HashMap<Thread, DbTransaction> currentTxs = new HashMap<Thread, DbTransaction>();
   private static final HashMap<Thread, DbTransaction> txCreateds = new HashMap<Thread, DbTransaction>();

   public static void reportTxStart(DbTransaction transaction) throws OseeStateException {
      DbTransaction currentTx = currentTxs.get(Thread.currentThread());
      if (currentTx != null) {
         throw new OseeStateException(String.format("Attempted to start executing %s but %s not finished",
               transaction.getClass().getName(), currentTx.getClass().getName()));
      }
      currentTxs.put(Thread.currentThread(), transaction);
   }

   public static void reportTxEnd(DbTransaction transaction) throws OseeStateException {
      DbTransaction txCreated = txCreateds.get(Thread.currentThread());
      if (txCreated == transaction) {
         txCreateds.put(Thread.currentThread(), null);
      } else {
         throw new OseeStateException(String.format("Attempted to finish %s but found %s instead",
               transaction.getClass().getName(), txCreated == null ? "null" : txCreated.getClass().getName()));
      }
      DbTransaction currentTx = currentTxs.get(Thread.currentThread());
      if (currentTx == transaction) {
         currentTxs.put(Thread.currentThread(), null);
      } else {
         throw new OseeStateException(String.format("Attempted to finish %s but found %s instead",
               transaction.getClass().getName(), currentTx.getClass().getName()));
      }
   }

   public static void reportTxCreation(DbTransaction transaction) throws OseeStateException {
      DbTransaction txCreated = txCreateds.get(Thread.currentThread());
      if (txCreated != null) {
         OseeLog.log(InternalActivator.class, Level.WARNING, String.format("Created %s but %s not finished",
               transaction.getClass().getName(), txCreated.getClass().getName()));
      }
      txCreateds.put(Thread.currentThread(), transaction);
   }
}
