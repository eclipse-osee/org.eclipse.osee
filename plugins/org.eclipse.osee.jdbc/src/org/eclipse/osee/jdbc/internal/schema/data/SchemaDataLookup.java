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
package org.eclipse.osee.jdbc.internal.schema.data;

import java.util.List;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class SchemaDataLookup {

   private final SchemaData schemaData;

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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((schemaData == null) ? 0 : schemaData.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      SchemaDataLookup other = (SchemaDataLookup) obj;
      if (schemaData == null) {
         if (other.schemaData != null) {
            return false;
         }
      } else if (!schemaData.equals(other.schemaData)) {
         return false;
      }
      return true;
   }

}
