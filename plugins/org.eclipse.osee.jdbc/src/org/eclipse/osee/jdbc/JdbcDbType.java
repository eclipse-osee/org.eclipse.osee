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
package org.eclipse.osee.jdbc;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author Roberto E. Escobar
 */
public enum JdbcDbType {
   h2,
   oracle,
   foxpro,
   mysql,
   postgresql,
   hsql;

   private static String getDatabaseName(DatabaseMetaData metaData) {
      String name = "";
      try {
         name = metaData.getDatabaseProductName();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
      return name;
   }

   private static JdbcDbType getDatabaseTypeAllowNull(DatabaseMetaData metaData) {
      String dbName = getDatabaseName(metaData);
      return getDatabaseTypeAllowNull(dbName);
   }

   private static JdbcDbType getDatabaseTypeAllowNull(String dbName) {
      JdbcDbType toReturn = null;
      String lowerCaseName = dbName.toLowerCase();
      if (lowerCaseName.contains(h2.toString())) {
         toReturn = h2;
      } else if (lowerCaseName.contains(oracle.toString())) {
         toReturn = oracle;
      } else if (lowerCaseName.contains(foxpro.toString())) {
         toReturn = foxpro;
      } else if (lowerCaseName.contains(mysql.toString())) {
         toReturn = mysql;
      } else if (lowerCaseName.contains(postgresql.toString())) {
         toReturn = postgresql;
      } else if (lowerCaseName.contains(hsql.toString())) {
         toReturn = hsql;
      }
      return toReturn;
   }

   public static JdbcDbType getDatabaseType(DatabaseMetaData metaData) {
      String dbName = getDatabaseName(metaData);
      JdbcDbType toReturn = getDatabaseTypeAllowNull(dbName);
      if (toReturn == null) {
         throw newJdbcException("Unsupported database type [%s] ", dbName);
      }
      return toReturn;
   }

   public static boolean isDatabaseType(DatabaseMetaData metaData, JdbcDbType... dbTypes) {
      boolean result = false;
      JdbcDbType supportedType = getDatabaseTypeAllowNull(metaData);
      for (JdbcDbType dbType : dbTypes) {
         if (dbType == supportedType) {
            result = true;
            break;
         }
      }
      return result;
   }

   public static boolean areHintsSupported(DatabaseMetaData metaData) {
      try {
         if (JdbcDbType.isDatabaseType(metaData, oracle)) {
            return metaData.getDatabaseMajorVersion() > 10;
         }
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
      return false;
   }

   public static String getRegularExpMatchSql(DatabaseMetaData metaData) {
      String pattern = "";
      JdbcDbType db = getDatabaseType(metaData);
      switch (db) {
         case oracle:
            pattern = "REGEXP_LIKE (%s, %s)";
            break;
         case postgresql:
         case hsql:
            pattern = "REGEXP_MATCHES (%s, %s)";
            break;
         case mysql:
            pattern = "(%s REGEXP %s)";
            break;
         default:
            throw newJdbcException("RegExp matching is not supported for db [%s]", db);
      }
      return pattern;
   }

   public static String getRecursiveWithSql(DatabaseMetaData metaData) {
      return isDatabaseType(metaData, oracle) ? "" : "RECURSIVE";
   }

   public static String getComplementSql(DatabaseMetaData metaData) {
      return isDatabaseType(metaData, oracle) ? "MINUS" : "EXCEPT";
   }

   public static String getValidationSql(DatabaseMetaData metaData) {
      String validation;
      if (isDatabaseType(metaData, oracle, h2)) {
         validation = "select 1 from dual";
      } else {
         validation = "select 1";
      }
      return validation;
   }
}