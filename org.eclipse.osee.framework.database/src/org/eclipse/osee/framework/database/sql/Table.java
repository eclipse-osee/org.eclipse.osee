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
package org.eclipse.osee.framework.database.sql;

public class Table {
   protected String name;

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

   private void createValuesList(StringBuilder strB, int parameterCount) {
      strB.append("VALUES (");
      for (int i = 1; i < parameterCount; i++) {
         strB.append("?, ");
      }
      strB.append("?)");
   }

   private void buildColumnsList(StringBuilder strB, boolean qualify, String... columns) {
      for (int i = 0; i < columns.length - 1; i++) {
         if (qualify) {
            qualifyColumnName(strB, columns[i]);
         } else {
            strB.append(columns[i]);
         }
         strB.append(", ");
      }
      if (qualify) {
         qualifyColumnName(strB, columns[columns.length - 1]);
      } else {
         strB.append(columns[columns.length - 1]);
      }
   }

   private void qualifyColumnName(StringBuilder strB, String columnName) {
      strB.append(name);
      strB.append(".");
      strB.append(columnName);
   }

   @Override
   public String toString() {
      return name;
   }

   public String join(Table joinTable, String joinColumn) {
      return column(joinColumn) + "=" + joinTable.column(joinColumn);
   }
}