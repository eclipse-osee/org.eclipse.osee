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
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.db.connection.internal.OseeConnectionPool;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeDbConnection {

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
      checkThread();
      return getConnection(getDatabaseInfoProvider());
   }

   private static void checkThread() {/*
                           String threadName = Thread.currentThread().getName();
                           
                           if (Display.getCurrent() == null) return false;
                           return Display.getCurrent().getThread() == Thread.currentThread();
                           
                           if (threadName.equals("main") || threadName.equals("Start Level Event Dispatcher")) {
                              OseeLog.log(InternalActivator.class, Level.SEVERE, "Making db calls in display threads.");
                           }*/
   }

   public static OseeConnection getConnection(IDatabaseInfo databaseInfo) throws OseeDataStoreException {
      checkThread();
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
         OseeLog.log(InternalActivator.class, Level.SEVERE, currentPair.object2);
      }

      txCreateds.put(Thread.currentThread(), new ObjectPair<DbTransaction, Exception>(transaction, new Exception()));
   }
}
