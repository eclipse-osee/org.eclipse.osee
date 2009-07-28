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

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.internal.InternalActivator;
import org.eclipse.osee.framework.database.internal.TransactionMonitor;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeDbConnection {
   private static final Timer timer = new Timer();
   private static final Map<String, OseeConnectionPool> dbInfoToPools = new HashMap<String, OseeConnectionPool>();
   private static final TransactionMonitor txMonitor = new TransactionMonitor();

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

   public static void reportTxStart(final DbTransaction transaction) throws OseeWrappedException, OseeStateException {
      txMonitor.reportTxStart(transaction);
   }

   public static void reportTxEnd(final DbTransaction transaction) throws OseeWrappedException, OseeStateException {
      txMonitor.reportTxEnd(transaction);
   }

   public static void reportTxCreation(final DbTransaction transaction) throws OseeWrappedException {
      txMonitor.reportTxCreation(transaction);
   }
}
