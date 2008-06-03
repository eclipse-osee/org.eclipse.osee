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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;

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

   public static void ensureDatabaseCompatability(Connection connection) throws Exception {
      // If runtime parameter to override is set, All Ok
      if (OseeProperties.getInstance().isOverrideVersionCheck()) return;
      // If this is runtime development, All Ok
      if (OseeCodeVersion.getInstance().isDevelopmentVersion()) return;
      // If this check is overridden in OSEE_LOG, All Ok
      if (!getOseeDbCheckVersion(connection)) return;

      String dbVersionStr = getOseeDbVersion(connection);
      String codeVersionStr = OseeCodeVersion.getInstance().get();

      // If match, All Ok
      if (dbVersionStr.equals(codeVersionStr)) return;

      // Otherwise, extract date from code and db versions
      Date codeVersionDate = extractDateFromVersion(codeVersionStr);
      Date dbVersionDate = extractDateFromVersion(dbVersionStr);

      // Throw exception if code version is earlier than dbVersion;  all later or equal versions are Ok
      if (codeVersionDate == null || dbVersionDate == null || codeVersionDate.before(dbVersionDate)) {
         String errorStr =
               String.format(
                     "This installation of OSEE \"%s\" is out of date and is not compatible with database version \"%s\".  Please restart OSEE and accept all updates.",
                     codeVersionStr, dbVersionStr);
         if (!OseeProperties.getInstance().isOverrideVersionCheck())
            throw new IllegalArgumentException(errorStr);
         else
            OseeLog.log(Activator.class.getName(), Level.SEVERE, "Overriding Version Check - " + errorStr);
      }
   }

   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
   private static Pattern pattern = Pattern.compile(" (\\d+-.*?-.*? .*?:.*?)$");

   /**
    * Extract date from release string of format 0.1.3 M2 2007-12-04 17:13
    * 
    * @param versionStr
    * @return Date
    */
   private static Date extractDateFromVersion(String versionStr) {
      Matcher m = pattern.matcher(versionStr);
      if (m.find()) {
         String timestampStr = m.group(1);
         if (timestampStr == null || timestampStr.equals("")) return null;
         try {
            return (Date) formatter.parse(timestampStr);
         } catch (Exception ex) {
            OseeLog.log(Activator.class.getName(), Level.SEVERE, "Couldn't process date: " + timestampStr);
         }
      }
      return null;
   }

   public static void initializeDbVersion(Connection connection) throws SQLException {
      setOseeDbVersion(connection, OseeCodeVersion.DEFAULT_DEVELOPMENT_VERSION);
      setOseeDbCheckVersion(connection, false);
   }
}
