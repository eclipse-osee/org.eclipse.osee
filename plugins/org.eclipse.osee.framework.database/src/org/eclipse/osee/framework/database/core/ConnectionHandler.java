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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.internal.ServiceUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Handles connection recovery in the event of database connection being lost
 * 
 * @author Jeff C. Phillips
 */
public final class ConnectionHandler {

   protected static IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return ServiceUtil.getDatabaseService();
   }

   public static IOseeSequence getSequence() throws OseeDataStoreException {
      return getDatabase().getSequence();
   }

   /**
    * Avoid using if possible
    */
   public static OseeConnection getConnection() throws OseeCoreException {
      return getDatabase().getConnection();
   }

   /**
    * Avoid using if possible
    */
   public static OseeConnection getConnection(IDatabaseInfo info) throws OseeCoreException {
      return getDatabase().getConnection(info);
   }

   public static IOseeStatement getStatement() throws OseeDataStoreException {
      return getDatabase().getStatement();
   }

   public static IOseeStatement getStatement(OseeConnection connection) throws OseeDataStoreException {
      return getDatabase().getStatement(connection);
   }

   public static IOseeStatement getStatement(OseeConnection connection, boolean autoClose) throws OseeDataStoreException {
      return getDatabase().getStatement(connection, autoClose);
   }

   /**
    * This method should only be used when not contained in a DB transaction
    * 
    * @return number of records updated
    */
   public static int runPreparedUpdate(String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedUpdate(query, data);
   }

   /**
    * This method should only be used when not contained in a DB transaction
    * 
    * @return number of records updated
    */
   public static <O extends Object> int runBatchUpdate(String query, List<O[]> dataList) throws OseeCoreException {
      return getDatabase().runBatchUpdate(query, dataList);
   }

   /**
    * This method should only be used when contained in a DB transaction
    * 
    * @return number of records updated
    */
   public static int runPreparedUpdate(OseeConnection connection, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedUpdate(connection, query, data);
   }

   public static <O extends Object> int runBatchUpdate(OseeConnection connection, String query, List<O[]> dataList) throws OseeCoreException {
      return getDatabase().runBatchUpdate(connection, query, dataList);
   }

   public static int runPreparedQueryFetchInt(int defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(defaultValue, query, data);
   }

   public static int runPreparedQueryFetchInt(OseeConnection connection, int defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(connection, defaultValue, query, data);
   }

   public static int runCallableStatementFetchInt(String query, Object... data) throws OseeCoreException {
      return runCallableStatementFetchInt(getStatement(), query, data);
   }

   public static int runCallableStatementFetchInt(OseeConnection connection, String query, Object... data) throws OseeCoreException {
      return runCallableStatementFetchInt(getStatement(connection), query, data);
   }

   public static int runCallableStatementFetchInt(IOseeStatement chStmt, String query, Object... data) throws OseeCoreException {
      try {
         chStmt.runCallableStatement(query, data);
         return chStmt.getCallableInt(1);
      } finally {
         chStmt.close();
      }
   }

   public static double runCallableStatementFetchDouble(String query, Object... data) throws OseeCoreException {
      return runCallableStatementFetchDouble(getStatement(), query, data);
   }

   public static double runCallableStatementFetchDouble(OseeConnection connection, String query, Object... data) throws OseeCoreException {
      return runCallableStatementFetchDouble(getStatement(connection), query, data);
   }

   private static double runCallableStatementFetchDouble(IOseeStatement chStmt, String query, Object... data) throws OseeCoreException {
      try {
         chStmt.runCallableStatement(query, data);
         return chStmt.getCallableDouble(1);
      } finally {
         chStmt.close();
      }
   }

   public static long runPreparedQueryFetchLong(long defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(defaultValue, query, data);
   }

   public static long runPreparedQueryFetchLong(OseeConnection connection, long defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(connection, defaultValue, query, data);
   }

   public static String runPreparedQueryFetchString(String defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(defaultValue, query, data);
   }

   public static String runPreparedQueryFetchString(OseeConnection connection, String defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(connection, defaultValue, query, data);
   }

   public static DatabaseMetaData getMetaData() throws OseeCoreException {
      OseeConnection connection = getDatabase().getConnection();
      try {
         return connection.getMetaData();
      } finally {
         connection.close();
      }
   }

   public static boolean doesTableExist(String targetTable) {
      return doesTableExist(null, targetTable);
   }

   public static boolean doesTableExist(String targetSchema, String targetTable) {
      ResultSet resultSet = null;
      try {
         resultSet = getMetaData().getTables(null, null, null, new String[] {"TABLE"});
         if (resultSet != null) {
            while (resultSet.next()) {
               String tableName = resultSet.getString("TABLE_NAME");
               String schemaName = resultSet.getString("TABLE_SCHEM");
               if (targetTable.equalsIgnoreCase(tableName)) {
                  if (targetSchema == null || targetSchema.equalsIgnoreCase(schemaName)) {
                     return true;
                  }
               }
            }
         }
      } catch (Exception ex) {
         // Do nothing
      } finally {
         if (resultSet != null) {
            try {
               resultSet.close();
            } catch (SQLException ex) {
               // Do nothing
            }
         }
      }
      return false;
   }
}