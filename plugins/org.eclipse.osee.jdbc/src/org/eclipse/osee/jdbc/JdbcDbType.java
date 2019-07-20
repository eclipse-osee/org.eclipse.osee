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
import org.eclipse.osee.framework.jdk.core.type.BaseId;

/**
 * @author Roberto E. Escobar
 */
public class JdbcDbType extends BaseId {
   public static final JdbcDbType h2 = new JdbcDbType(1);
   public static final JdbcDbType oracle = new JdbcDbType(2);
   public static final JdbcDbType foxpro = new JdbcDbType(3);
   public static final JdbcDbType mysql = new JdbcDbType(4);
   public static final JdbcDbType postgresql = new JdbcDbType(5);
   public static final JdbcDbType hsql = new JdbcDbType(6);
   public static final JdbcDbType sqlite = new JdbcDbType(7);

   private final boolean hintsSupported;

   private JdbcDbType(int id) {
      this(Long.valueOf(id), false);
   }

   private JdbcDbType(Long id, boolean hintsSupported) {
      super(id);
      this.hintsSupported = hintsSupported;
   }

   public static JdbcDbType getDbType(JdbcConnection connection) {
      return getDbType(connection.getMetaData());
   }

   public static JdbcDbType getDbType(DatabaseMetaData metaData) {
      try {
         String dbName = metaData.getDatabaseProductName();
         JdbcDbType dbType;
         String lowerCaseName = dbName.toLowerCase();
         if (lowerCaseName.contains("h2")) {
            dbType = h2;
         } else if (lowerCaseName.contains("oracle")) {
            dbType = oracle;
         } else if (lowerCaseName.contains("foxpro")) {
            dbType = foxpro;
         } else if (lowerCaseName.contains("mysql")) {
            dbType = mysql;
         } else if (lowerCaseName.contains("postgresql")) {
            dbType = postgresql;
         } else if (lowerCaseName.contains("hsql")) {
            dbType = hsql;
         } else if (lowerCaseName.contains("sqlite")) {
            dbType = sqlite;
         } else {
            throw newJdbcException("Unsupported database type [%s] ", dbName);
         }
         boolean hintsSupported = dbType.equals(oracle) && metaData.getDatabaseMajorVersion() > 10;
         return new JdbcDbType(dbType.getId(), hintsSupported);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   public boolean areHintsSupported() {
      return hintsSupported;
   }

   public String getRegularExpMatchSql() {
      if (equals(oracle)) {
         return "REGEXP_LIKE (%s, %s)";
      }
      if (matches(postgresql, hsql)) {
         return "REGEXP_MATCHES (%s, %s)";
      }
      if (equals(mysql)) {
         return "(%s REGEXP %s)";
      }
      throw newJdbcException("RegExp matching is not supported for db [%s]", this);
   }

   public String getRecursiveWithSql() {
      return equals(oracle) ? "" : "RECURSIVE";
   }

   public String getComplementSql() {
      return equals(oracle) ? "MINUS" : "EXCEPT";
   }

   public String getValidationSql() {
      if (matches(oracle, h2)) {
         return "select 1 from dual";
      } else if (matches(hsql)) {
         return "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
      }
      return "select 1";
   }

   public String getConstraintCheckingSql(boolean enable) {
      String cmd;
      String value;

      if (equals(h2)) {
         cmd = "SET REFERENTIAL_INTEGRITY = %s";
         value = Boolean.toString(enable).toUpperCase();
      } else if (equals(hsql)) {
         cmd = "SET DATABASE REFERENTIAL INTEGRITY %s";
         value = Boolean.toString(enable).toUpperCase();
      } else {
         cmd = "SET CONSTRAINTS ALL %s";
         value = enable ? "IMMEDIATE" : "DEFERRED";
      }

      return String.format(cmd, value);
   }

   public String getFunctionCallSql(String function) {
      if (equals(oracle)) {
         return String.format("{ ? = call %s }", function);
      }
      return String.format("call %s", function);
   }
}