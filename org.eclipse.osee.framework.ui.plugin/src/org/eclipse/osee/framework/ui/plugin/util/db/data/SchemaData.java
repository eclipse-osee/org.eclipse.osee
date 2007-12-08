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

package org.eclipse.osee.framework.ui.plugin.util.db.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement.ColumnFields;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class SchemaData implements Xmlizable {

   public static final String ROOT_TAG = "TableConfig";
   private List<TableElement> tableDefinitions;
   private SchemaDataLookup schemaLookup;

   @SuppressWarnings("unused")
   private boolean isSorted;

   public SchemaData() {
      this.tableDefinitions = new ArrayList<TableElement>();
      this.isSorted = false;
      this.schemaLookup = new SchemaDataLookup(this);
   }

   public SchemaDataLookup getLookUpStrategy() {
      return schemaLookup;
   }

   public List<TableElement> getTablesOrderedByDependency() {
      sortTablesForConstraints();
      markIdentityColumns();
      return tableDefinitions;
   }

   public Set<String> getTablesToBackup() {
      Set<String> backupTables = new TreeSet<String>();
      for (TableElement table : tableDefinitions) {
         if (table.isBackupDataSet()) {
            backupTables.add(table.getFullyQualifiedTableName());
         }
      }
      return backupTables;
   }

   public Map<String, Set<String>> getTablesToImport() {
      Map<String, Set<String>> importTables = new HashMap<String, Set<String>>();
      for (TableElement table : tableDefinitions) {
         if (table.isImportDataSet()) {
            String importFrom = table.getImportFrom();
            Set<String> tableSet;
            if (importTables.containsKey(importFrom)) {
               tableSet = importTables.get(importFrom);
            } else {
               tableSet = new TreeSet<String>();
            }
            tableSet.add(table.getFullyQualifiedTableName());
            importTables.put(importFrom, tableSet);
         }
      }
      return importTables;
   }

   public void addTableDefinition(TableElement table) {
      tableDefinitions.add(table);
   }

   public String toString() {
      String toReturn = "";
      for (TableElement table : tableDefinitions) {
         toReturn += table.toString();
      }
      return toReturn;
   }

   public Document getXmlDocument() throws ParserConfigurationException {
      Document doc = Jaxp.newDocument();
      Element root = doc.createElement(ROOT_TAG);
      doc.appendChild(root);
      for (TableElement table : tableDefinitions) {
         root.appendChild(table.toXml(doc));
      }
      return doc;
   }

   public Element toXml(Document doc) {
      return null;
   }

   private void sortTablesForConstraints() {
      this.tableDefinitions = sortTablesForConstraints(this.tableDefinitions);
   }

   private boolean canCreate(TableElement table, Set<String> canCreate) {
      Set<String> dependencies = table.getTableDependency();
      if (canCreate.containsAll(dependencies)) {
         return true;
      }
      return false;
   }

   private void markIdentityColumns() {
      for (TableElement aTable : tableDefinitions) {
         determineIdentityColumns(aTable);
      }
   }

   private void determineIdentityColumns(TableElement tableDef) {
      List<String> primaryKeys = new ArrayList<String>();

      // first get all of the vars used for the primary key
      List<ConstraintElement> constraints = tableDef.getConstraints();
      for (ConstraintElement constraint : constraints) {
         if (constraint.getConstraintType().equals(ConstraintTypes.PRIMARY_KEY)) {
            List<String> columns = constraint.getColumns();
            for (String column : columns) {
               primaryKeys.add(column);
            }
         }
      }

      // now go through and remove any of the primary key vars that are foreign keys
      List<ForeignKey> foreignKeys = tableDef.getForeignKeyConstraints();
      for (ForeignKey fkConstraint : foreignKeys) {
         List<String> columns = fkConstraint.getColumns();
         for (String column : columns) {
            primaryKeys.remove(column);
         }
      }

      // now we should only be left with those primary keys that are identities.
      // we set them by setting their corresponding identity column to true, a little hokey but that
      // way we don't have to create special data structures for the column
      Map<String, ColumnMetadata> columns = tableDef.getColumns();
      Set<String> columnKeys = columns.keySet();
      for (String key : columnKeys) {
         ColumnMetadata column = columns.get(key);
         if (primaryKeys.contains(column.getId())) {
            column.addColumnField(ColumnFields.identity, Boolean.TRUE.toString());
         }
      }
   }

   private List<TableElement> sortTablesForConstraints(List<TableElement> tables) {
      List<TableElement> sorted = new ArrayList<TableElement>();
      Set<String> canCreate = new HashSet<String>();

      Map<TableElement, Boolean> toSort = new HashMap<TableElement, Boolean>();

      for (TableElement aTable : tables) {
         if (aTable.hasForeignKey()) {
            toSort.put(aTable, false);
         } else {
            sorted.add(aTable);
            canCreate.add(aTable.getFullyQualifiedTableName());
         }
      }

      // Prevent for endless loops caused by
      // foreign/primary key discrepancies
      int guard = toSort.size() * 2;
      int count = 0;
      while (toSort.containsValue(false) && (count < guard)) {
         Set<TableElement> elements = toSort.keySet();

         for (TableElement aTable : elements) {

            if (!toSort.get(aTable)) {
               if (canCreate(aTable, canCreate)) {
                  canCreate.add(aTable.getFullyQualifiedTableName());
                  sorted.add(aTable);
                  toSort.put(aTable, true);
               }
            }
         }
         count++;
      }

      // If we were stuck in an endless loop copy all unsortable tables
      // to the end of the sorted array and return
      if (toSort.containsValue(false)) {
         Set<TableElement> elements = toSort.keySet();
         for (TableElement aTable : elements) {
            if (!toSort.get(aTable)) {
               canCreate.add(aTable.getFullyQualifiedTableName());
               sorted.add(aTable);
               toSort.put(aTable, true);
            }
         }
      }

      return sorted;
   }

   @Override
   public boolean equals(Object otherObject) {
      if (otherObject instanceof SchemaData == false) {
         return false;
      }
      if (this == otherObject) {
         return true;
      }
      SchemaData that = (SchemaData) otherObject;
      return hasEqualState(that);
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(37, 11).append(tableDefinitions).append(isSorted).append(schemaLookup).toHashCode();
   }

   public Map<String, TableElement> getTableMap() {
      Map<String, TableElement> tableMap = new HashMap<String, TableElement>();
      for (TableElement table : tableDefinitions) {
         tableMap.put(table.getFullyQualifiedTableName(), table);
      }
      return tableMap;
   }

   public boolean hasEqualState(SchemaData that) {
      EqualsBuilder equalsBuilder = new EqualsBuilder();
      equalsBuilder.appendSuper(super.equals(that));

      Map<String, TableElement> thisTableMap = this.getTableMap();
      Map<String, TableElement> thatTableMap = that.getTableMap();

      Set<String> thisKey1 = thisTableMap.keySet();
      Set<String> thatKey2 = thatTableMap.keySet();

      boolean toReturn = true;
      if (thisKey1.equals(thatKey2)) {
         for (String key : thisKey1) {
            equalsBuilder.append(thisTableMap.get(key), thatTableMap.get(key));
         }
         toReturn &= equalsBuilder.isEquals();
      } else {
         toReturn = false;
      }
      return toReturn;
   }
}
