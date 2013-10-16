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
package org.eclipse.osee.database.schema.internal.callable;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.database.schema.DatabaseCallable;
import org.eclipse.osee.database.schema.SchemaOptions;
import org.eclipse.osee.database.schema.SchemaResourceProvider;
import org.eclipse.osee.database.schema.internal.data.ColumnMetadata;
import org.eclipse.osee.database.schema.internal.data.ConstraintElement;
import org.eclipse.osee.database.schema.internal.data.ForeignKey;
import org.eclipse.osee.database.schema.internal.data.IndexElement;
import org.eclipse.osee.database.schema.internal.data.ReferenceClause;
import org.eclipse.osee.database.schema.internal.data.SchemaData;
import org.eclipse.osee.database.schema.internal.data.SchemaXmlParser;
import org.eclipse.osee.database.schema.internal.data.TableElement;
import org.eclipse.osee.database.schema.internal.data.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class LoadUserSchemasCallable extends DatabaseCallable<Object> {

   private final Map<String, SchemaData> schemas;
   private final SchemaResourceProvider provider;
   private final SchemaOptions options;

   public LoadUserSchemasCallable(Log logger, IOseeDatabaseService dbService, Map<String, SchemaData> schemas, SchemaResourceProvider provider, SchemaOptions options) {
      super(logger, dbService);
      this.schemas = schemas;
      this.provider = provider;
      this.options = options;
   }

   private DatabaseMetaData getMetaData() throws OseeCoreException {
      OseeConnection connection = getDatabaseService().getConnection();
      try {
         return connection.getMetaData();
      } finally {
         connection.close();
      }
   }

   @Override
   public Object call() throws Exception {
      SchemaXmlParser parser = new SchemaXmlParser(getLogger());
      parser.parse(provider.getSchemaResources(), schemas);

      if (!options.isUseFileSpecifiedSchemas()) {
         try {
            DatabaseMetaData meta = getMetaData();
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
      return null;
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
