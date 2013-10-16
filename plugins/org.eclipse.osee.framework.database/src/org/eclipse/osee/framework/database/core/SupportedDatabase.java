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
import java.sql.SQLException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public enum SupportedDatabase {
   h2,
   oracle,
   foxpro,
   mysql,
   postgresql,
   hsql;

   public static String getDatabaseName(DatabaseMetaData metaData) throws OseeCoreException {
      String name = "";
      try {
         name = metaData.getDatabaseProductName();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return name;
   }

   public static SupportedDatabase getDatabaseTypeAllowNull(DatabaseMetaData metaData) throws OseeCoreException {
      String dbName = getDatabaseName(metaData);
      return getDatabaseTypeAllowNull(dbName);
   }

   private static SupportedDatabase getDatabaseTypeAllowNull(String dbName) {
      SupportedDatabase toReturn = null;
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

   public static SupportedDatabase getDatabaseType(DatabaseMetaData metaData) throws OseeCoreException {
      String dbName = getDatabaseName(metaData);
      SupportedDatabase toReturn = getDatabaseTypeAllowNull(dbName);
      if (toReturn == null) {
         throw new OseeDataStoreException("Unsupported database type [%s] ", dbName);
      }
      return toReturn;
   }

   public static boolean isDatabaseType(DatabaseMetaData metaData, SupportedDatabase... dbTypes) throws OseeCoreException {
      boolean result = false;
      SupportedDatabase supportedType = getDatabaseTypeAllowNull(metaData);
      for (SupportedDatabase dbType : dbTypes) {
         if (dbType == supportedType) {
            result = true;
            break;
         }
      }
      return result;
   }

   public static boolean areHintsSupported(DatabaseMetaData metaData) throws OseeCoreException {
      try {
         if (SupportedDatabase.isDatabaseType(metaData, oracle)) {
            return metaData.getDatabaseMajorVersion() > 10;
         }
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return false;
   }

   public static String getRegularExpMatchSql(DatabaseMetaData metaData) throws OseeCoreException {
      String pattern = "";
      SupportedDatabase db = getDatabaseType(metaData);
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
            throw new OseeArgumentException("RegExp matching is not supported for db [%s]", db);
      }
      return pattern;
   }

   public static String getRecursiveWithSql(DatabaseMetaData metaData) throws OseeCoreException {
      return isDatabaseType(metaData, oracle) ? "" : "RECURSIVE";
   }

   public static String getComplementSql(DatabaseMetaData metaData) throws OseeCoreException {
      return isDatabaseType(metaData, oracle) ? "MINUS" : "EXCEPT";
   }

   public static String getValidationSql(DatabaseMetaData metaData) throws OseeCoreException {
      String validation;
      if (isDatabaseType(metaData, oracle, h2)) {
         validation = "select 1 from dual";
      } else {
         validation = "select 1";
      }
      return validation;

   }
}