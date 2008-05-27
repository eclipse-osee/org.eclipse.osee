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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.pool.OseeConnectionPool;

/**
 * @author Andrew M Finkbeiner
 */
public class OseeDbConnection {
   private static DbInformation defaultDbInformation;
   private static Map<DbInformation, OseeConnectionPool> dbInfoToPools =
         new ConcurrentHashMap<DbInformation, OseeConnectionPool>();

   public static Connection getConnection() throws SQLException {
      verifyDefaultDbInformation();
      return getConnection(defaultDbInformation);
   }

   public static Connection getConnection(DbInformation dbInformation) throws SQLException {
      return getConnection(dbInformation, true);
   }

   public static Connection getConnection(DbInformation dbInformation, boolean validityCheck) throws SQLException {
      OseeConnectionPool pool = dbInfoToPools.get(dbInformation);
      if (pool == null) {
         pool = new OseeConnectionPool(dbInformation, validityCheck);
         dbInfoToPools.put(dbInformation, pool);
      }
      return pool.getConnection();
   }

   private static void verifyDefaultDbInformation() {
      if (defaultDbInformation == null) {
         defaultDbInformation = OseeDb.getDefaultDatabaseService();
      }
   }
}
