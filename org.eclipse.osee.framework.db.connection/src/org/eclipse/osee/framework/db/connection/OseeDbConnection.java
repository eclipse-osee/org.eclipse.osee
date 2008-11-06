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
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.db.connection.internal.OseeConnectionPool;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;

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
    * @param dbDriver
    * @param dbUrl
    * @param dbConnectionProperties
    */
   public static void setDatabaseInfo(IDatabaseInfo databaseInfo) {
      OseeDbConnection.databaseInfo = databaseInfo;
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

   public static void reportTxEnd(DbTransaction transaction) throws OseeWrappedException {
      ObjectPair<DbTransaction, Exception> currentPair = txCreateds.get(Thread.currentThread());
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
         throw new OseeWrappedException(currentPair.object2);
      }

      txCreateds.put(Thread.currentThread(), new ObjectPair<DbTransaction, Exception>(transaction, new Exception()));
   }
}
