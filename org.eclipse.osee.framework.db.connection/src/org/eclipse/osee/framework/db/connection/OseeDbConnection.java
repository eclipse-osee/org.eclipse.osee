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

import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.pool.OseeConnectionPool;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeDbConnection {

   private static final CompositeKeyHashMap<String, Properties, OseeConnectionPool> dbInfoToPools =
         new CompositeKeyHashMap<String, Properties, OseeConnectionPool>();

   private static String dbDriver = "";
   private static String dbUrl = "";
   private static Properties dbConnectionProperties = null;

   public static boolean hasOpenConnection() {
      OseeConnectionPool pool = dbInfoToPools.get(dbUrl, dbConnectionProperties);
      if (pool == null) {
         return false;
      }
      return pool.hasOpenConnection();
   }

   public static OseeConnection getConnection() throws OseeDataStoreException {
      return getConnection(dbDriver, dbUrl, dbConnectionProperties);
   }

   @Deprecated
   public static OseeConnection getConnection(String serviceName) throws OseeDataStoreException {
      return getConnection(OseeDbConnection.getDatabaseService(serviceName));
   }

   public static OseeConnection getConnection(DbInformation dbInformation) throws OseeDataStoreException {
      return getConnection(dbInformation.getConnectionData().getDBDriver(), dbInformation.getConnectionUrl(),
            dbInformation.getProperties());
   }

   public static OseeConnection getConnection(String dbDriver, String dbUrl, Properties dbProperties) throws OseeDataStoreException {
      OseeConnectionPool pool = dbInfoToPools.get(dbUrl, dbProperties);
      if (pool == null) {
         pool = new OseeConnectionPool(dbDriver, dbUrl, dbProperties);
         dbInfoToPools.put(dbUrl, dbProperties, pool);
      }
      return pool.getConnection();
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
      try {
         OseeConnection connection = getConnection();
         connection.close();
      } catch (OseeDataStoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
      return true;
   }

   /**
    * @param dbDriver
    * @param dbUrl
    * @param dbConnectionProperties
    */
   public static void setDefaultConnectionInfo(String dbDriver, String dbUrl, Properties dbConnectionProperties) {
      OseeDbConnection.dbDriver = dbDriver;
      OseeDbConnection.dbUrl = dbUrl;
      OseeDbConnection.dbConnectionProperties = dbConnectionProperties;
   }
}
