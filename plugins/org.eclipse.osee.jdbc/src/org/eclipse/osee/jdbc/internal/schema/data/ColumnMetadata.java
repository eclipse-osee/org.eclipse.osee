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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.ColumnFields;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.TableSections;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class ColumnMetadata {
   private final Map<ColumnFields, String> columnFields;

   public ColumnMetadata(String columnId) {
      columnFields = new HashMap<ColumnFields, String>();
      columnFields.put(ColumnFields.id, columnId);
   }

   public String getId() {
      return getColumnField(ColumnFields.id);
   }

   public Map<ColumnFields, String> getColumnFields() {
      return columnFields;
   }

   public void addColumnField(ColumnFields field, String value) {
      columnFields.put(field, value);
   }

   public String getColumnField(ColumnFields field) {
      if (columnFields.containsKey(field)) {
         return columnFields.get(field);
      }
      return "";
   }

   @Override
   public String toString() {
      String toReturn = TableSections.Column + ": ";
      Set<ColumnFields> keys = columnFields.keySet();
      for (ColumnFields field : keys) {
         String value = columnFields.get(field);
         toReturn += "\t" + field + "[" + value + "]";
      }
      return toReturn;
   }

   public Element toXml(Document doc) {
      Element columnElement = doc.createElement(TableSections.Column.toString());
      for (ColumnFields key : columnFields.keySet()) {
         columnElement.setAttribute(key.toString(), columnFields.get(key));
      }
      return columnElement;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((columnFields == null) ? 0 : columnFields.hashCode());
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
      ColumnMetadata other = (ColumnMetadata) obj;
      if (columnFields == null) {
         if (other.columnFields != null) {
            return false;
         }
      } else if (!columnFields.equals(other.columnFields)) {
         return false;
      }
      return true;
   }
}
