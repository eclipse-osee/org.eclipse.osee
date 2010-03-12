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

public enum SupportedDatabase {
   oracle,
   derby,
   foxpro,
   mysql,
   postgresql;

   public static SupportedDatabase getDatabaseType(DatabaseMetaData metaData) throws OseeDataStoreException {
      try {
         SupportedDatabase toReturn = null;
         String dbName = metaData.getDatabaseProductName();
         String lowerCaseName = dbName.toLowerCase();
         if (lowerCaseName.contains(SupportedDatabase.derby.toString())) {
            toReturn = SupportedDatabase.derby;
         } else if (lowerCaseName.contains(SupportedDatabase.oracle.toString())) {
            toReturn = SupportedDatabase.oracle;
         } else if (lowerCaseName.contains(SupportedDatabase.foxpro.toString())) {
            toReturn = SupportedDatabase.foxpro;
         } else if (lowerCaseName.contains(SupportedDatabase.mysql.toString())) {
            toReturn = SupportedDatabase.mysql;
         } else if (lowerCaseName.contains(SupportedDatabase.postgresql.toString())) {
            toReturn = SupportedDatabase.postgresql;
         } else {
            throw new OseeDataStoreException("Unsupported database type: " + dbName);
         }
         return toReturn;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public static boolean isDatabaseType(DatabaseMetaData metaData, SupportedDatabase dbType) throws OseeDataStoreException {
      return getDatabaseType(metaData) == dbType;
   }

   public static boolean areHintsSupported(DatabaseMetaData metaData) throws OseeDataStoreException {
      try {
         if (SupportedDatabase.isDatabaseType(metaData, oracle)) {
            return metaData.getDatabaseMajorVersion() > 10;
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
      return false;
   }

   public static String getComplementSql(DatabaseMetaData metaData) throws OseeDataStoreException {
      return isDatabaseType(metaData, oracle) ? "MINUS" : "EXCEPT";
   }
}