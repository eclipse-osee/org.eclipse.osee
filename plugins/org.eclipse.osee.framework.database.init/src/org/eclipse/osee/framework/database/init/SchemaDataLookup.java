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
package org.eclipse.osee.framework.database.init;

import java.util.List;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class SchemaDataLookup {

   private SchemaData schemaData;

   public SchemaDataLookup(SchemaData schemaData) {
      this.schemaData = schemaData;
   }

   public ColumnMetadata getColumnDefinition(TableElement table, String columnId) {
      Map<String, ColumnMetadata> columns = table.getColumns();
      return getColumnDefinition(columns, columnId);
   }

   public static ColumnMetadata getColumnDefinition(Map<String, ColumnMetadata> columns, String columnId) {
      if (columns.containsKey(columnId)) {
         return columns.get(columnId);
      }
      return null;
   }

   public TableElement getTableDefinition(String tableName) {
      List<TableElement> list = schemaData.getTablesOrderedByDependency();
      for (TableElement table : list) {
         return table;
      }
      return null;
   }
}
