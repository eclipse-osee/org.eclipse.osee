/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange;

import java.io.IOException;
import org.eclipse.osee.orcs.db.internal.exchange.export.DbTableSqlExportItem;

/**
 * @author Torin Grenda, David Miller
 */
public class ExportImportSql {

   private ExportImportSql() {
   }

   public static void openSqlInsert(Appendable appendable, String tableName, String columnNames) throws IOException {
      appendable.append("INSERT INTO ");
      appendable.append(tableName);
      appendable.append(" (");
      appendable.append(columnNames);
      appendable.append(") VALUES\n");
   }

   public static void openFirstSqlValue(Appendable appendable) throws IOException {
      appendable.append("(");
   }

   public static void openSqlValue(Appendable appendable) throws IOException {
      appendable.append(",(");
   }

   public static void addFirstSqlAttribute(Appendable appendable, Object value) throws IOException {
      appendable.append(String.valueOf(value));
   }

   public static void addSqlAttribute(Appendable appendable, Object value) throws IOException {
      appendable.append(",");
      appendable.append(String.valueOf(value));
   }

   public static void addFirstSqlStringAttribute(Appendable appendable, Object value) throws IOException {
      appendable.append("\'");
      appendable.append(DbTableSqlExportItem.escapeSql(String.valueOf(value)));
      appendable.append("\'");
   }

   public static void addSqlStringAttribute(Appendable appendable, Object value) throws IOException {
      appendable.append(",\'");
      appendable.append(DbTableSqlExportItem.escapeSql(String.valueOf(value)));
      appendable.append("\'");
   }

   public static void closeSqlValue(Appendable appendable) throws IOException {
      appendable.append(")\n");
   }

   public static void closeSqlInsert(Appendable appendable) throws IOException {
      appendable.append(";");
   }
}
