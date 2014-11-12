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
package org.eclipse.osee.jdbc.internal.schema.ops;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcSchemaOptions;
import org.eclipse.osee.jdbc.JdbcSchemaResource;
import org.eclipse.osee.jdbc.internal.schema.data.ColumnMetadata;
import org.eclipse.osee.jdbc.internal.schema.data.ConstraintElement;
import org.eclipse.osee.jdbc.internal.schema.data.ForeignKey;
import org.eclipse.osee.jdbc.internal.schema.data.IndexElement;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaData;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaXmlParser;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.TableDescriptionFields;

/**
 * @author Roberto E. Escobar
 */
public class LoadUserSchemasCallable implements Callable<Void> {

   private final JdbcClient client;
   private final Map<String, SchemaData> schemas;
   private final Iterable<JdbcSchemaResource> schemaResources;
   private final JdbcSchemaOptions options;

   public LoadUserSchemasCallable(JdbcClient client, Map<String, SchemaData> schemas, Iterable<JdbcSchemaResource> schemaResources, JdbcSchemaOptions options) {
      this.client = client;
      this.schemas = schemas;
      this.schemaResources = schemaResources;
      this.options = options;
   }

   private DatabaseMetaData getMetaData() throws OseeCoreException {
      JdbcConnection connection = client.getConnection();
      try {
         return connection.getMetaData();
      } finally {
         connection.close();
      }
   }

   private Set<JdbcSchemaResource> getValidSchemas() {
      Set<JdbcSchemaResource> schemasToParse = new LinkedHashSet<JdbcSchemaResource>();
      for (JdbcSchemaResource resource : schemaResources) {
         if (resource.isApplicable(client.getConfig())) {
            schemasToParse.add(resource);
         }
      }
      return schemasToParse;
   }

   @Override
   public Void call() throws Exception {

      SchemaXmlParser parser = new SchemaXmlParser();
      parser.parse(getValidSchemas(), schemas);

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
            throw newJdbcException(ex);
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
