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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.osee.framework.database.core.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public class MetaData {
   private final Map<String, SQL3DataType> dataConversionMap;
   private final String tableTarget;
   private String query;

   MetaData(String tableName) {
      this.dataConversionMap = new LinkedHashMap<String, SQL3DataType>();
      this.tableTarget = tableName;
      this.query = null;
   }

   private String buildQuery() {
      String toReturn = null;
      Collection<String> columnNames = getColumnNames();
      if (columnNames.isEmpty() != true) {
         String columns = columnNames.toString();
         columns = columns.substring(1, columns.length() - 1);
         StringBuffer params = new StringBuffer();
         for (int index = 0; index < columnNames.size(); index++) {
            params.append("?");
            if (index + 1 < columnNames.size()) {
               params.append(", ");
            }
         }
         toReturn = String.format("INSERT INTO %s (%s) VALUES (%s)", tableTarget, columns, params);
      }
      return toReturn;
   }

   void addColumn(String columnName, SQL3DataType sql3DataType) {
      this.dataConversionMap.put(columnName, sql3DataType);
   }

   public int getColumnSize() {
      return dataConversionMap.size();
   }

   public String getTableName() {
      return tableTarget;
   }

   public Collection<String> getColumnNames() {
      return this.dataConversionMap.keySet();
   }

   public String getQuery() {
      if (query == null) {
         query = buildQuery();
      }
      return query;
   }

   public SQL3DataType toDataType(String fieldName) {
      return this.dataConversionMap.get(fieldName);
   }

   public Class<?> toClass(String fieldName) {
      SQL3DataType dataType = this.dataConversionMap.get(fieldName);
      return dataType != null ? dataType.getJavaEquivalentClass() : null;
   }

   @Override
   public String toString() {
      return getTableName();
   }
}
