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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.pool.OseeConnectionPool;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeDbConnection {
   private static DbInformation defaultDbInformation;
   private static Map<DbInformation, OseeConnectionPool> dbInfoToPools =
         new ConcurrentHashMap<DbInformation, OseeConnectionPool>();

   public static OseeConnection getConnection() throws OseeDataStoreException {
      verifyDefaultDbInformation();
      return getConnection(defaultDbInformation);
   }

   public static boolean hasOpenConnection() {
      verifyDefaultDbInformation();
      OseeConnectionPool pool = dbInfoToPools.get(defaultDbInformation);
      if (pool == null) {
         return false;
      }
      return pool.hasOpenConnection();
   }

   public static OseeConnection getConnection(String serviceName) throws OseeDataStoreException {
      return getConnection(OseeDbConnection.getDatabaseService(serviceName));
   }

   public static OseeConnection getConnection(DbInformation dbInformation) throws OseeDataStoreException {
      OseeConnectionPool pool = dbInfoToPools.get(dbInformation);
      if (pool == null) {
         pool = new OseeConnectionPool(dbInformation);
         dbInfoToPools.put(dbInformation, pool);
      }
      return pool.getConnection();
   }

   private static void verifyDefaultDbInformation() {
      if (defaultDbInformation == null) {
         defaultDbInformation = OseeDbConnection.getDefaultDatabaseService();
      }
   }

   public static DbInformation getDefaultDatabaseService() {
      return Activator.getDbConnectionInformation().getSelectedDatabaseInfo();
   }

   public static DbInformation getDatabaseService(String id) {
      return Activator.getDbConnectionInformation().getDatabaseInfo(id);
   }

   /**
    * @return whether a successful connection has been had to the database
    */
   public static boolean isConnectionValid() {
      OseeConnection connection = null;
      try {
         connection = getConnection();
      } catch (OseeDataStoreException ex) {
         return false;
      } finally {
         ConnectionHandler.close(connection);
      }
      return true;
   }
}
