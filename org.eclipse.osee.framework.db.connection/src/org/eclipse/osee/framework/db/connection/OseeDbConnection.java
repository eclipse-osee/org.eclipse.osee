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
import java.util.Timer;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeDbConnection {
   private static final Timer timer = new Timer();
   private static final Map<String, OseeConnectionPool> dbInfoToPools = new HashMap<String, OseeConnectionPool>();

   public static boolean hasOpenConnection() throws OseeDataStoreException {
      IDatabaseInfo databaseInfo = getDatabaseInfoProvider();
      if (databaseInfo == null) {
         throw new OseeDataStoreException("Unable to get connection - database info was null.");
      }
      OseeConnectionPool pool = dbInfoToPools.get(databaseInfo.getId());
      if (pool == null) {
         return false;
      }
      return pool.hasOpenConnection();
   }

   public static OseeConnection getConnection() throws OseeDataStoreException {
      return getConnection(getDatabaseInfoProvider());
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
         timer.schedule(new StaleConnectionCloser(pool), 900000, 900000);
      }
      return pool.getConnection();
   }

   private static IDatabaseInfo getDatabaseInfoProvider() throws OseeDataStoreException {
      return InternalActivator.getApplicationDatabaseProvider().getDatabaseInfo();
   }

   private static final HashMap<Thread, ObjectPair<DbTransaction, Exception>> currentTxs =
         new HashMap<Thread, ObjectPair<DbTransaction, Exception>>();
   private static final HashMap<Thread, ObjectPair<DbTransaction, Exception>> txCreateds =
         new HashMap<Thread, ObjectPair<DbTransaction, Exception>>();

   public static void reportTxStart(DbTransaction transaction) throws OseeWrappedException {
      ObjectPair<DbTransaction, Exception> currentPair = currentTxs.get(Thread.currentThread());
      if (currentPair != null) {
         throw new OseeWrappedException(currentPair.object2);
      }
      currentTxs.put(Thread.currentThread(), new ObjectPair<DbTransaction, Exception>(transaction, new Exception()));
   }

   public static void reportTxEnd(DbTransaction transaction) throws OseeWrappedException, OseeStateException {
      ObjectPair<DbTransaction, Exception> currentPair = txCreateds.get(Thread.currentThread());
      if (currentPair == null) {
         throw new OseeStateException(
               "reportTxEnd called for thread: " + Thread.currentThread() + " but reportTxCreation had not been called.");
      }
      DbTransaction txCreated = currentPair.object1;
      if (txCreated == transaction) {
         txCreateds.put(Thread.currentThread(), null);
      } else {
         throw new OseeWrappedException(currentPair.object2);
      }

      currentPair = currentTxs.get(Thread.currentThread());
      DbTransaction currentTx = currentPair.object1;
      if (currentTx == transaction) {
         currentTxs.put(Thread.currentThread(), null);
      } else {
         throw new OseeWrappedException(currentPair.object2);
      }
   }

   public static void reportTxCreation(DbTransaction transaction) throws OseeWrappedException {
      ObjectPair<DbTransaction, Exception> currentPair = txCreateds.get(Thread.currentThread());
      if (currentPair != null) {
         // This log is to support debugging the case where skynet transactions are nested and should
         // use the same transaction.
         // This case may happen legitimately if an exception happened before transaction.execute(), so
         // it is only notification that this is occurring.
         OseeLog.log(InternalActivator.class, Level.SEVERE, "New transaction created over Last transaction",
               currentPair.object2);
      }

      txCreateds.put(Thread.currentThread(), new ObjectPair<DbTransaction, Exception>(transaction, new Exception()));
   }
}
