/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jdbc;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

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

   public String getRegularExpMatchSql(String field) {
      if (equals(oracle)) {
         return "REGEXP_LIKE (" + field + ", ?)";
      } else if (equals(hsql)) {
         return "REGEXP_MATCHES (" + field + ", ?)";
      } else if (equals(postgresql)) {
         return "exists (select * from REGEXP_MATCHES (" + field + ", ?))";
      } else if (equals(mysql)) {
         return "(" + field + " REGEXP ?)";
      }
      throw newJdbcException("RegExp matching is not supported for db [%s]", this);
   }

   public String getRecursiveWithSql() {
      if (matches(oracle, hsql)) {
         return "";
      } else {
         return "RECURSIVE";
      }
   }

   public String getStringConversion() {
      if (matches(postgresql)) {
         return "::varchar(255)";
      } else {
         return "";
      }
   }

   public String getRowNum() {
      if (matches(postgresql)) {
         return "row_number() over ()";
      } else {
         return "rownum";
      }
   }

   public String getLimitRowsReturned(int limit) {
      if (matches(oracle, hsql)) {
         return " and rownum < " + limit;
      } else {
         return " limit " + limit;
      }
   }

   public String getExpireDateDays(int expireLengthInDays) {
      if (matches(oracle, hsql)) {
         return "TRUNC(SYSDATE) - " + expireLengthInDays;
      } else {
         return "current_timestamp - interval '" + expireLengthInDays + " day'";
      }
   }

   public String getInStringSql(String str, String searchString) {
      if (matches(oracle, hsql)) {
         return "instr(" + str + "," + searchString + ")";
      } else {
         return "strpos(" + str + "," + searchString + ")";
      }
   }

   /**
    * return union keyword in recursive query using a Common Table Expression (WITH statement). Oracle requires UNION
    * ALL (does not support UNION in a recursive CTE). HSQLDB version 2.3.2+ hangs indefinitely when UNION ALL is used.
    * In other (not a recursive CTE) UNION ALL performs better because it does not have to eliminate duplicates.
    */
   public String getCteRecursiveUnion() {
      return equals(hsql) ? "UNION" : "UNION ALL";
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

   public boolean isPaginationOrderingSupported() {
      if (matches(hsql)) {
         return false;
      }
      return true;
   }

   public String getPostgresRecurse() {
      if (matches(postgresql)) {
         return " recursive";
      }
      return " ";
   }

   public String getPostgresCastStart() {
      if (matches(postgresql)) {
         return "CAST(";
      }
      return " ";
   }

   public String getPostgresCastVarCharEnd() {
      if (matches(postgresql)) {
         return "as varchar)";
      }
      return " ";
   }

   public String getPostgresCastBigIntEnd() {
      if (matches(postgresql)) {
         return "as bigint)";
      }
      return " ";
   }

   public String json_agg(String sqlToAggregate) {
      String result = "";
      if (matches(postgresql)) {
         result += "jsonb_agg (";
      }
      if (matches(oracle)) {
         result += "json_arrayagg (";
      }
      result += sqlToAggregate;
      if (matches(oracle)) {
         result += " returning clob ";
      }
      result += ")";
      return result;
   }

   public String jsonb_array(String... data) {
      String result = "";
      if (matches(postgresql)) {
         result += "jsonb_build_array(";
         for (int i = 0; i < data.length; i++) {
            result += " " + data[i] + " ";
            if (i < data.length - 1) {
               result += ",";
            }
         }
         result += ")";
      }
      if (matches(oracle)) {
         result += "json_array(";
         for (int i = 0; i < data.length; i++) {
            result += " " + data[i] + " ";
            if (i < data.length - 1) {
               result += ",";
            }
         }
         result += " returning clob)";
      }
      return result;
   }

   public String jsonb_object(String... data) {
      if (data.length % 2 != 0) {
         throw new OseeArgumentException("Improper count of keys to create json object", (Object[]) data);
      }
      String result = "";
      if (matches(postgresql)) {
         result += "jsonb_build_object (";
         for (int i = 0; i < data.length; i++) {
            boolean keyOrValue = i % 2 != 0;
            if (!keyOrValue) {
               result += " '" + data[i] + "' ,";
            } else {
               result += " " + data[i] + " ";
               if (i < data.length - 2) {
                  result += ",";
               }
            }
         }
      }
      if (matches(oracle)) {
         result += "json_object (";
         for (int i = 0; i < data.length; i++) {
            boolean keyOrValue = i % 2 != 0;
            if (!keyOrValue) {
               result += " key '" + data[i] + "' ";
            } else {
               result += " value " + data[i] + " ";
               if (i < data.length - 2) {
                  result += ",";
               }
            }
         }
         result += " returning clob ";
      }
      result += ")";
      return result;
   }

   public String json_object(String... data) {
      if (data.length % 2 != 0) {
         throw new OseeArgumentException("Improper count of keys to create json object", (Object[]) data);
      }
      String result = "";
      if (matches(postgresql)) {
         result += "json_build_object (";
         for (int i = 0; i < data.length; i++) {
            boolean keyOrValue = i % 2 != 0;
            if (!keyOrValue) {
               result += " '" + data[i] + "' ,";
            } else {
               result += " " + data[i] + " ";
               if (i < data.length - 2) {
                  result += ",";
               }
            }
         }
      }
      if (matches(oracle)) {
         result += "json_object (";
         for (int i = 0; i < data.length; i++) {
            boolean keyOrValue = i % 2 != 0;
            if (!keyOrValue) {
               result += " key '" + data[i] + "' ";
            } else {
               result += " value " + data[i] + " ";
               if (i < data.length - 2) {
                  result += ",";
               }
            }
         }
         result += " returning clob ";
      }
      result += ")";
      return result;
   }

   public String jsonObjectContains(String value, String tableColumn, String jsonColumn) {
      String result = "";
      if (matches(postgresql)) {
         result = " exists (select 1 from jsonb_array_elements("+tableColumn+") t1 where t1->>'value'::text like ?)";
      } else {
         result = "DBMS_LOB.INSTR( "+tableColumn+", ? ) > 0"; 
      }
      return result;
   }
   
   public String getJsonObjectContainsParameter(String value) {
      if (matches(postgresql)) {
         return "%" + value + "%";
      } else {
         return value;
      }
   }
   public String cast(String value, String type) {
      String result = "";
      if (matches(postgresql) || matches(oracle)) {
         result += "CAST( " + value + " AS " + type + ")";
      }
      return result;
   }

   public String longToString() {
      String result = "";
      if (matches(postgresql)) {
         result += "TEXT";
      }
      if (matches(oracle)) {
         result += "varchar(20)";
      }
      return result;
   }

   public String split_part(String stringToSplit, String characterToSplitBy, long index) {
      String result = "";
      if (matches(postgresql)) {
         result += "split_part(" + stringToSplit + ", '" + characterToSplitBy + "', " + index + ")";
      }
      if (matches(oracle)) {
         result +=
            "substr(" + stringToSplit + ", " + index + ", " + "instr(" + stringToSplit + ", '" + characterToSplitBy + "')" + "-2" + ")";
      }
      return result;
   }

   public String booleanTrue() {
      String result = "";
      if (matches(postgresql)) {
         result += "true";
      }
      if (matches(oracle)) {
         result += "1";
      }
      return result;
   }

   public String booleanFalse() {
      String result = "";
      if (matches(postgresql)) {
         result += "false";
      }
      if (matches(oracle)) {
         result += "0";
      }
      return result;
   }

}