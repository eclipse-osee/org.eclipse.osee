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
package org.eclipse.osee.framework.core.datastore.schema.operations;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.datastore.IOseeSchemaProvider;
import org.eclipse.osee.framework.core.datastore.SchemaCreationOptions;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.data.ColumnMetadata;
import org.eclipse.osee.framework.core.datastore.schema.data.ConstraintElement;
import org.eclipse.osee.framework.core.datastore.schema.data.ForeignKey;
import org.eclipse.osee.framework.core.datastore.schema.data.IndexElement;
import org.eclipse.osee.framework.core.datastore.schema.data.ReferenceClause;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaData;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaXmlParser;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class LoadUserSchemasOperation extends AbstractOperation {

   private final Map<String, SchemaData> schemas;
   private final IOseeSchemaProvider schemaProvider;
   private final SchemaCreationOptions options;

   public LoadUserSchemasOperation(Map<String, SchemaData> schemas, IOseeSchemaProvider schemaProvider, SchemaCreationOptions options) {
      super("Load Schema Data from URIs", Activator.PLUGIN_ID);
      this.schemas = schemas;
      this.schemaProvider = schemaProvider;
      this.options = options;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      SchemaXmlParser parser = new SchemaXmlParser();
      parser.parseFromSchemaProvider(schemaProvider, schemas);

      if (!options.isUseFileSpecifiedSchemas()) {
         try {
            DatabaseMetaData meta = ConnectionHandler.getMetaData();
            if (meta != null) {
               String userName = meta.getUserName();
               if (Strings.isValid(userName)) {
                  int index = userName.indexOf('@');
                  if (index > 1) {
                     userName = userName.substring(0, index);
                  }
                  Map<String, SchemaData> newData = useUserNameAsSchema(userName.toUpperCase(), schemas);
                  schemas.clear();
                  schemas.putAll(newData);
               }
            }
         } catch (SQLException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }

      if (options.isTableDataSpaceValid()) {
         for (SchemaData schemaData : schemas.values()) {
            schemaData.setTableDataSpaceName(options.getTableDataSpace());
         }
      }

      if (options.isIndexDataSpaceValid()) {
         for (SchemaData schemaData : schemas.values()) {
            schemaData.setIndexDataSpaceName(options.getIndexDataSpace());
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
         if (Strings.isValid(value)) {
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
