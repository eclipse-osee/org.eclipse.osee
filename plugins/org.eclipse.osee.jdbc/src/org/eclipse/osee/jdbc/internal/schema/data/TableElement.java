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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class TableElement {

   public enum TableTags {
      Table,
      ColumnInfo,
      Row;
   }

   public enum TableSections {
      Column,
      Constraint,
      Index;
   };

   public enum TableDescriptionFields {
      name,
      schema,
      backupData,
      importData,
      importFrom,
      tablespace;
   };

   public enum ColumnFields {
      id,
      type,
      limits,
      identity,
      defaultValue;
      ;
   };

   private final Map<TableDescriptionFields, String> tableDescription;
   private final Map<String, ColumnMetadata> columns;
   private final List<ConstraintElement> constraints;
   private final List<ForeignKey> foreignKeys;
   private final List<IndexElement> indices;

   public TableElement() {
      this.tableDescription = new HashMap<TableDescriptionFields, String>();
      this.columns = new HashMap<String, ColumnMetadata>();
      this.constraints = new ArrayList<ConstraintElement>();
      this.foreignKeys = new ArrayList<ForeignKey>();
      this.indices = new ArrayList<IndexElement>();
   }

   public void addColumn(ColumnMetadata column) {
      this.columns.put(column.getId(), column);
   }

   public void addConstraint(ConstraintElement constraint) {
      if (constraint instanceof ForeignKey) {
         foreignKeys.add((ForeignKey) constraint);
      } else {
         constraints.add(constraint);
      }
   }

   public void addIndexData(IndexElement indexData) {
      indices.add(indexData);
   }

   public String getSchema() {
      return this.tableDescription.get(TableDescriptionFields.schema);
   }

   public String getName() {
      return this.tableDescription.get(TableDescriptionFields.name);
   }

   public boolean isBackupDataSet() {
      if (this.tableDescription.containsKey(TableDescriptionFields.backupData)) {
         return Boolean.parseBoolean(this.tableDescription.get(TableDescriptionFields.backupData));
      }
      return false;
   }

   public boolean isImportDataSet() {
      if (this.tableDescription.containsKey(TableDescriptionFields.importData)) {
         return Boolean.parseBoolean(this.tableDescription.get(TableDescriptionFields.importData));
      }
      return false;
   }

   public String getImportFrom() {
      if (this.tableDescription.containsKey(TableDescriptionFields.importFrom)) {
         return this.tableDescription.get(TableDescriptionFields.importFrom);
      }
      return null;
   }

   public void addTableDescription(TableDescriptionFields field, String value) {
      if (!field.equals(TableDescriptionFields.importFrom)) {
         value = value.toUpperCase();
      }
      value = value.trim();
      this.tableDescription.put(field, value);
   }

   public Map<String, ColumnMetadata> getColumns() {
      return this.columns;
   }

   public Map<TableDescriptionFields, String> getDescription() {
      return this.tableDescription;
   }

   public boolean hasForeignKey() {
      return foreignKeys.size() > 0;
   }

   public String getFullyQualifiedTableName() {
      if (getSchema() == null) {
         return getName();
      } else {
         return getSchema() + "." + getName();
      }
   }

   public List<ConstraintElement> getConstraints() {
      return constraints;
   }

   public List<ForeignKey> getForeignKeyConstraints() {
      return foreignKeys;
   }

   public List<IndexElement> getIndexData() {
      return indices;
   }

   @Override
   public String toString() {
      StringBuilder toReturn = new StringBuilder();
      toReturn.append(" Table : \n");
      Set<TableDescriptionFields> keys = tableDescription.keySet();
      for (TableDescriptionFields key : keys) {
         toReturn.append(" \t" + key.toString() + ": " + tableDescription.get(key));
      }
      toReturn.append("\n");
      int count = 0;
      Set<String> columnKeys = columns.keySet();
      for (String key : columnKeys) {
         toReturn.append("\t[" + ++count + "] " + columns.get(key).toString() + "\n");
      }
      count = 0;
      for (ConstraintElement constraint : constraints) {
         toReturn.append("\t[" + ++count + "] " + constraint.toString() + "\n");
      }
      for (ForeignKey fkeys : foreignKeys) {
         toReturn.append("\t[" + ++count + "] " + fkeys.toString() + "\n");
      }
      count = 0;
      for (IndexElement iData : indices) {
         toReturn.append("\t[" + ++count + "] " + iData.toString() + "\n");
      }
      return toReturn.toString();
   }

   /**
    * @param doc The XML document we're creating the XML for
    * @return The XML Element corresponding to this
    */
   public Element toXml(Document doc) {
      Element tableElement = doc.createElement(TableTags.Table.name());
      for (TableDescriptionFields key : tableDescription.keySet()) {
         tableElement.setAttribute(key.toString(), tableDescription.get(key));
      }
      for (String key : columns.keySet()) {
         tableElement.appendChild(columns.get(key).toXml(doc));
      }
      for (ConstraintElement constraint : constraints) {
         tableElement.appendChild(constraint.toXml(doc));
      }
      for (ForeignKey constraint : foreignKeys) {
         tableElement.appendChild(constraint.toXml(doc));
      }
      for (IndexElement iData : indices) {
         tableElement.appendChild(iData.toXml(doc));
      }
      return tableElement;
   }

   public Set<String> getTableDependency() {
      Set<String> dependency = new TreeSet<String>();
      for (ForeignKey fk : foreignKeys) {
         Set<String> refTables = fk.getReferencedTables();
         for (String tableName : refTables) {
            dependency.add(tableName);
         }
      }
      return dependency;
   }

   public String getTablespace() {
      String toReturn = getDescription().get(TableDescriptionFields.tablespace);
      return Strings.isValid(toReturn) ? toReturn : "";
   }

   public void setTablespace(String value) {
      getDescription().put(TableDescriptionFields.tablespace, value);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((columns == null) ? 0 : columns.hashCode());
      result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
      result = prime * result + ((foreignKeys == null) ? 0 : foreignKeys.hashCode());
      result = prime * result + ((indices == null) ? 0 : indices.hashCode());
      result = prime * result + ((tableDescription == null) ? 0 : tableDescription.hashCode());
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
      TableElement other = (TableElement) obj;
      if (columns == null) {
         if (other.columns != null) {
            return false;
         }
      } else if (!columns.equals(other.columns)) {
         return false;
      }
      if (constraints == null) {
         if (other.constraints != null) {
            return false;
         }
      } else if (!constraints.equals(other.constraints)) {
         return false;
      }
      if (foreignKeys == null) {
         if (other.foreignKeys != null) {
            return false;
         }
      } else if (!foreignKeys.equals(other.foreignKeys)) {
         return false;
      }
      if (indices == null) {
         if (other.indices != null) {
            return false;
         }
      } else if (!indices.equals(other.indices)) {
         return false;
      }
      if (tableDescription == null) {
         if (other.tableDescription != null) {
            return false;
         }
      } else if (!tableDescription.equals(other.tableDescription)) {
         return false;
      }
      return true;
   }

}
