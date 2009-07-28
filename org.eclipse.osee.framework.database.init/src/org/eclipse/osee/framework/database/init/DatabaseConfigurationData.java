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

import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.init.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class DatabaseConfigurationData {

   private List<URL> filesToProcess;

   public DatabaseConfigurationData(List<URL> filesToProcess) {
      this.filesToProcess = filesToProcess;
   }

   public List<URL> getUserSchemaFilesToProcess() {
      return filesToProcess;
   }

   public Map<String, SchemaData> getUserSpecifiedSchemas() throws OseeCoreException {
      Map<String, SchemaData> schemasFromUserFiles =
            SchemaConfigUtility.getUserDefinedConfig(getUserSchemaFilesToProcess());
      if (!useFileSpecifiedSchemas()) {
         try {
            DatabaseMetaData meta = ConnectionHandler.getMetaData();
            if (meta != null) {
               String userName = meta.getUserName();
               if (userName != null && !userName.equals("")) {
                  int index = userName.indexOf('@');
                  if (index > 1) {
                     userName = userName.substring(0, index);
                  }
                  schemasFromUserFiles = useUserNameAsSchema(userName.toUpperCase(), schemasFromUserFiles);
               }
            }
         } catch (SQLException ex) {
            throw new OseeWrappedException(ex);
         }
      }

      String tableDataSpace = OseeClientProperties.getOseeTableDataSpaceForDbInit();
      if (Strings.isValid(tableDataSpace)) {
         updateTableDataSpace(tableDataSpace, schemasFromUserFiles);
      }

      String indexDataSpace = OseeClientProperties.getOseeIndexDataSpaceForDbInit();
      if (Strings.isValid(indexDataSpace)) {
         updateIndexDataSpace(indexDataSpace, schemasFromUserFiles);
      }

      return schemasFromUserFiles;
   }

   private boolean useFileSpecifiedSchemas() {
      return OseeClientProperties.useSchemasSpecifiedInDbConfigFiles();
   }

   private void updateTableDataSpace(String tableDataSpace, Map<String, SchemaData> userSchemas) {
      for (String key : userSchemas.keySet()) {
         SchemaData schemaData = userSchemas.get(key);
         for (TableElement table : schemaData.getTableMap().values()) {
            table.setTablespace(tableDataSpace);
         }
      }
   }

   private void updateIndexDataSpace(String indexDataSpace, Map<String, SchemaData> userSchemas) {
      for (String key : userSchemas.keySet()) {
         SchemaData schemaData = userSchemas.get(key);
         for (TableElement table : schemaData.getTableMap().values()) {
            for (IndexElement indexElement : table.getIndexData()) {
               indexElement.setTablespace(indexDataSpace);
            }
         }
      }
   }

   private Map<String, SchemaData> useUserNameAsSchema(String userName, Map<String, SchemaData> userSchemas) {
      Map<String, SchemaData> newData = new HashMap<String, SchemaData>();
      SchemaData newSchemaData = new SchemaData();
      Set<String> keys = userSchemas.keySet();
      for (String key : keys) {
         SchemaData schemaData = userSchemas.get(key);
         List<TableElement> tables = schemaData.getTablesOrderedByDependency();
         for (TableElement table : tables) {
            TableElement newTable = useUserNameAsSchemaForTable(userName, table);
            newSchemaData.addTableDefinition(newTable);
         }
      }
      newData.put(userName, newSchemaData);
      return newData;
   }

   private TableElement useUserNameAsSchemaForTable(String userName, TableElement table) {
      TableElement newTable = new TableElement();

      Map<TableDescriptionFields, String> tableDescription = table.getDescription();
      Map<String, ColumnMetadata> columns = table.getColumns();
      List<ConstraintElement> constraints = table.getConstraints();
      List<ForeignKey> foreignKeys = table.getForeignKeyConstraints();
      List<IndexElement> indexElements = table.getIndexData();

      TableDescriptionFields[] descriptors = TableDescriptionFields.values();
      for (TableDescriptionFields field : descriptors) {
         String value = tableDescription.get(field);
         if (field.equals(TableDescriptionFields.schema)) {
            value = userName;
         }
         if (value != null && !value.equals("")) {
            newTable.addTableDescription(field, value);
         }
      }

      Set<String> columnKeys = columns.keySet();
      for (String key : columnKeys) {
         newTable.addColumn(columns.get(key));
      }
      for (ConstraintElement constraint : constraints) {
         constraint.setSchema(userName);
         newTable.addConstraint(constraint);
      }
      for (ForeignKey constraint : foreignKeys) {
         constraint.setSchema(userName);
         List<ReferenceClause> references = constraint.getReferences();
         for (ReferenceClause clause : references) {
            clause.setSchema(userName);
         }
         newTable.addConstraint(constraint);
      }
      for (IndexElement indexElement : indexElements) {
         newTable.addIndexData(indexElement);
      }
      return newTable;
   }
}
