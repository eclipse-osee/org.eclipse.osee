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
package org.eclipse.osee.framework.db.connection.core.schema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

public class Table {
   protected String name;
   private static final String aliassyntax;
   private static final Matcher matcher = Pattern.compile(" *PUT_TABLE_ALIAS_HERE *").matcher("");

   static {
      SupportedDatabase db = OseeDb.getDefaultDatabaseService().getDatabaseDetails().getDbType();
      switch (db) {
         case postgresql:
            aliassyntax = " as ";
            break;
         default:
            aliassyntax = " ";
      }
   }

   /**
    * Remove all PUT_TABLE_ALIAS_HERE tags and replace with table alias specific to this DB
    * 
    * @param sql string with replace tag PUT_TABLE_ALIAS_HERE embedded
    * @return sql with corresponding table alias replaced
    */
   public static String generateTableAliasedSql(String sql) {
      matcher.reset(sql);
      return matcher.replaceAll(aliassyntax);
   }

   /**
    * @param name
    */
   public Table(String name) {
      this.name = name;
   }

   public String columnsForInsert(String... cols) {
      StringBuilder strB = new StringBuilder(200);
      strB.append(name);
      strB.append(" (");
      buildColumnsList(strB, false, cols);
      strB.append(") ");
      createValuesList(strB, cols.length);
      return strB.toString();
   }

   public String columns(String... columns) {
      StringBuilder strB = new StringBuilder(200);
      buildColumnsList(strB, true, columns);
      return strB.toString();
   }

   public String column(String columnName) {
      StringBuilder strB = new StringBuilder(60);
      qualifyColumnName(strB, columnName);
      return strB.toString();
   }

   public String max(String columnName, String alias) {
      return function("max", columnName, alias);
   }

   public String max(String columnName) {
      return function("max", columnName, null);
   }

   public String min(String columnName, String alias) {
      return function("min", columnName, alias);
   }

   public String min(String columnName) {
      return function("min", columnName, null);
   }

   public static String alias(String sql, String alias) {
      StringBuilder strB = new StringBuilder();
      strB.append(sql);
      alias(strB, alias);
      return strB.toString();
   }

   private static void alias(StringBuilder strB, String alias) {
      strB.append(aliassyntax);
      strB.append(alias);
   }

   private String function(String function, String columnName, String alias) {
      StringBuilder strB = new StringBuilder(60);
      strB.append(function);
      strB.append("(");
      qualifyColumnName(strB, columnName);
      strB.append(")");
      if (alias != null && !alias.equals("")) {
         alias(strB, alias);
      }
      return strB.toString();
   }

   private void createValuesList(StringBuilder strB, int parameterCount) {
      strB.append("VALUES (");
      for (int i = 1; i < parameterCount; i++) {
         strB.append("?, ");
      }
      strB.append("?)");
   }

   private void buildColumnsList(StringBuilder strB, boolean qualify, String... columns) {
      for (int i = 0; i < columns.length - 1; i++) {
         if (qualify)
            qualifyColumnName(strB, columns[i]);
         else
            strB.append(columns[i]);
         strB.append(", ");
      }
      if (qualify)
         qualifyColumnName(strB, columns[columns.length - 1]);
      else
         strB.append(columns[columns.length - 1]);
   }

   private void qualifyColumnName(StringBuilder strB, String columnName) {
      strB.append(name);
      strB.append(".");
      strB.append(columnName);
   }

   public String toString() {
      return name;
   }

   public String join(Table joinTable, String joinColumn) {
      return column(joinColumn) + "=" + joinTable.column(joinColumn);
   }

   public LocalAliasTable aliasAs(String aliasName) {
      return new LocalAliasTable(this, aliasName);
   }
}