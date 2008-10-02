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

package org.eclipse.osee.framework.db.connection.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * Provides the necessary methods to ensure old versions of OSEE code do not get run against versions of the database
 * that they shouldn't. The check for database compatibility is provided through ensureDatabaseCompatability.
 * 
 * @author Donald G. Dunne
 */
public class OseeDbVersion {

   public static final String OSEE_DB_VERSION_KEY = "osee.db.version";
   public static final String OSEE_DB_CHECK_VERSION_KEY = "osee.db.version.check.enabled";

   public static String getOseeDbVersion(Connection connection) throws SQLException {
      return OseeInfo.getValue(OSEE_DB_VERSION_KEY);
   }

   public static boolean getOseeDbCheckVersion(Connection connection) throws SQLException {
      boolean normalOverrideValue = OseeProperties.getInstance().isOverrideVersionCheck();

      // turn off version check to avoid recursion
      OseeProperties.getInstance().setOverrideVersionCheck("");
      String value = OseeInfo.getValue(OSEE_DB_CHECK_VERSION_KEY);

      // set override value back to original value
      OseeProperties.getInstance().setOverrideVersionCheck(normalOverrideValue ? "" : null);

      if (value == null) return false;
      return value.equals("true");
   }

   public static void setOseeDbVersion(Connection connection, String oseeDbVersion) throws SQLException {
      setDatabaseValue(connection, OSEE_DB_VERSION_KEY, oseeDbVersion);
   }

   public static void setOseeDbCheckVersion(Connection connection, boolean checkVersion) throws SQLException {
      setDatabaseValue(connection, OSEE_DB_CHECK_VERSION_KEY, String.valueOf(checkVersion));
   }

   private static void setDatabaseValue(Connection connection, String key, String value) throws SQLException {
      String query = null;
      String currentValue = OseeInfo.getValue(key);
      if (currentValue == null)
         query = "INSERT INTO OSEE_INFO (OSEE_KEY, OSEE_VALUE) VALUES ( \'" + key + "\',\'" + value + "\')";
      else
         query = "UPDATE OSEE_INFO SET OSEE_VALUE = \'" + value + "\' WHERE OSEE_KEY = \'" + key + "\'";

      Statement statement = null;
      try {
         statement = connection.createStatement();
         statement.executeUpdate(query);
      } finally {
         if (statement != null) {
            statement.close();
         }
      }
   }

}
