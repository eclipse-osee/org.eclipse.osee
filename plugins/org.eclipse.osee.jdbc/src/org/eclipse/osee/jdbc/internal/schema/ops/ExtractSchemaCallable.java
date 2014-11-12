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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.SQL3DataType;
import org.eclipse.osee.jdbc.internal.schema.data.AppliesToClause;
import org.eclipse.osee.jdbc.internal.schema.data.AppliesToClause.OrderType;
import org.eclipse.osee.jdbc.internal.schema.data.ColumnMetadata;
import org.eclipse.osee.jdbc.internal.schema.data.ConstraintElement;
import org.eclipse.osee.jdbc.internal.schema.data.ConstraintFactory;
import org.eclipse.osee.jdbc.internal.schema.data.ConstraintTypes;
import org.eclipse.osee.jdbc.internal.schema.data.ForeignKey;
import org.eclipse.osee.jdbc.internal.schema.data.IndexElement;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaData;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.ColumnFields;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.TableDescriptionFields;

/**
 * @author Roberto E. Escobar
 */
public class ExtractSchemaCallable implements Callable<Void> {
   private static final String DEFAULT_FILTER = "BIN.*";
   private static final Pattern sqlPattern = Pattern.compile("SQL\\d+");

   private DatabaseMetaData dbData;
   private String dbName;
   private String dbVersion;
   private final JdbcClient client;
   private final Map<String, SchemaData> database;
   private final List<String> filter = new ArrayList<String>();
   private final Set<String> tablesToExtract = new TreeSet<String>();
   private final Set<String> schemas;
   private final Matcher indexMatcher;

   public ExtractSchemaCallable(JdbcClient client, Set<String> schemas, Map<String, SchemaData> schemaData) {
      this.client = client;
      this.schemas = schemas;
      this.database = schemaData;
      filter.add(DEFAULT_FILTER);
      this.indexMatcher = sqlPattern.matcher("");
   }

   @Override
   public Void call() throws Exception {
      JdbcConnection connection = client.getConnection();
      try {
         this.dbData = connection.getMetaData();

         this.dbName = dbData.getDatabaseProductName();
         this.dbVersion = dbData.getDatabaseProductVersion();

         for (String schema : schemas) {
            SchemaData dbTables = getTableInformation(schema);
            database.put(schema, dbTables);
         }
      } finally {
         connection.close();
      }
      return null;
   }

   public void addToFilter(String value) {
      filter.add(value);
   }

   @Override
   public String toString() {
      StringBuilder buffer = new StringBuilder();
      Set<String> keys = database.keySet();
      for (String schema : keys) {
         SchemaData tableData = database.get(schema);
         buffer.append(" Schema: \n");
         buffer.append(schema);
         buffer.append("\n");
         buffer.append(tableData.toString());
      }
      return String.format("Name: [%s]\tVer: [%s]\n%s", dbName, dbVersion, buffer);
   }

   private boolean isFiltered(String value) {
      for (String filterExpression : filter) {
         Pattern searchPattern = Pattern.compile(filterExpression, Pattern.DOTALL);
         Matcher matcher = searchPattern.matcher(value);
         if (matcher.find()) {
            return true;
         }
      }
      return false;
   }

   public void addTableToExtract(String fullyqualifiedTableName) {
      this.tablesToExtract.add(fullyqualifiedTableName);
   }

   public void clearTableFilter() {
      tablesToExtract.clear();
   }

   private SchemaData getTableInformation(String schema) throws Exception {
      SchemaData dbTables = new SchemaData();
      ResultSet resultSet = null;
      try {
         resultSet = dbData.getTables(null, schema, null, new String[] {"TABLE"});
         if (resultSet != null) {
            while (resultSet.next()) {
               String tableName = resultSet.getString("TABLE_NAME").toUpperCase();
               String schemaName = resultSet.getString("TABLE_SCHEM");
               if (tableName != null && !isFiltered(tableName) && schemaName.equalsIgnoreCase(schema)) {
                  boolean extract = true;
                  if (!tablesToExtract.isEmpty()) {
                     extract = tablesToExtract.contains(schema + "." + tableName);
                  }

                  if (extract) {
                     TableElement tableEntry = new TableElement();
                     tableEntry.addTableDescription(TableDescriptionFields.name, tableName);
                     tableEntry.addTableDescription(TableDescriptionFields.schema, schemaName);
                     getColumnInformation(tableEntry);
                     getColumnPrimaryKey(tableEntry);

                     if (!(JdbcDbType.isDatabaseType(dbData, JdbcDbType.foxpro) || //
                     JdbcDbType.isDatabaseType(dbData, JdbcDbType.postgresql))) {
                        getColumnForeignKey(tableEntry);
                     }
                     getIndexInfo(tableEntry);
                     dbTables.addTableDefinition(tableEntry);
                  }
               }
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
      return dbTables;
   }

   private void getColumnInformation(TableElement aTable) throws Exception {
      ResultSet columns = null;
      try {
         try {
            columns = dbData.getColumns(null, aTable.getSchema(), aTable.getName(), null);
         } catch (SQLException ex) {
            columns = dbData.getColumns(null, null, aTable.getName(), null);
         }
         while (columns.next()) {
            String id = columns.getString("COLUMN_NAME");
            id = id.toUpperCase();
            ColumnMetadata column = new ColumnMetadata(id);

            int dataType = columns.getInt("DATA_TYPE");
            if (JdbcDbType.isDatabaseType(dbData, JdbcDbType.foxpro)) {
               if (dataType == Types.CHAR) {
                  dataType = Types.VARCHAR;
               }
            }
            String dataTypeName = SQL3DataType.get(dataType).name();
            column.addColumnField(ColumnFields.type, dataTypeName);

            String defaultValue = "";
            int defaultType = columns.getInt("NULLABLE");
            switch (defaultType) {
               case java.sql.DatabaseMetaData.columnNoNulls:
                  defaultValue = "not null";
                  break;
               case java.sql.DatabaseMetaData.columnNullable:
                  // Dont specify if Null - Let DB Decide.
                  defaultValue = "";
                  break;
               case java.sql.DatabaseMetaData.columnNullableUnknown:
               default:
                  // Since unknown then don't specify
                  defaultValue = "";
                  break;
            }
            if (!defaultValue.equals("")) {
               column.addColumnField(ColumnFields.defaultValue, defaultValue);
            }

            if (!JdbcDbType.isDatabaseType(dbData, JdbcDbType.foxpro)) {
               // int dataType = columns.getInt("DATA_TYPE");
               switch (dataType) {
                  case java.sql.Types.CHAR:
                  case java.sql.Types.VARCHAR:
                     String limits = columns.getString("COLUMN_SIZE");
                     if (Strings.isValid(limits)) {
                        column.addColumnField(ColumnFields.limits, limits);
                     }
                     break;
                  case java.sql.Types.DECIMAL:
                  case java.sql.Types.NUMERIC:
                     limits = columns.getString("COLUMN_SIZE");
                     String decimal = columns.getString("DECIMAL_DIGITS");
                     if (Strings.isValid(decimal)) {
                        if (Strings.isValid(limits)) {
                           limits += "," + decimal;
                        }
                     }
                     if (Strings.isValid(limits)) {
                        column.addColumnField(ColumnFields.limits, limits);
                     }
                  default:
                     break;
               }
            } else {
               switch (dataType) {
                  case java.sql.Types.CHAR:
                  case java.sql.Types.VARCHAR:
                     String limits = "255";
                     column.addColumnField(ColumnFields.limits, limits);
                     break;
                  default:
                     break;
               }
            }
            aTable.addColumn(column);
         }
      } finally {
         if (columns != null) {
            columns.close();
         }
      }
   }

   private void getColumnPrimaryKey(TableElement aTable) throws SQLException {
      ResultSet primaryKeys = null;
      try {
         try {
            primaryKeys = dbData.getPrimaryKeys(null, aTable.getSchema(), aTable.getName());
         } catch (SQLException ex) {
            primaryKeys = dbData.getPrimaryKeys(null, null, aTable.getName());
         }
         Map<String, Set<String>> constraintKeyMap = new HashMap<String, Set<String>>();

         while (primaryKeys.next()) {
            String column = primaryKeys.getString("COLUMN_NAME");
            String keyId = primaryKeys.getString("PK_NAME");

            if (!Strings.isValid(keyId)) {
               keyId = column + "_PK";
            }

            if (!constraintKeyMap.containsKey(keyId)) {
               Set<String> set = new TreeSet<String>();
               set.add(column);
               constraintKeyMap.put(keyId, set);
            } else {
               Set<String> set = constraintKeyMap.get(keyId);
               if (!set.contains(column)) {
                  set.add(column);
               }
            }
         }

         Set<String> keys = constraintKeyMap.keySet();
         for (String pk : keys) {
            ConstraintElement constraint =
               ConstraintFactory.getConstraint(ConstraintTypes.PRIMARY_KEY, aTable.getSchema(), pk, false);
            Set<String> columnSet = constraintKeyMap.get(pk);
            for (String column : columnSet) {
               constraint.addColumn(column);
            }
            aTable.addConstraint(constraint);
         }
      } finally {
         if (primaryKeys != null) {
            primaryKeys.close();
         }
      }
   }

   private void getColumnForeignKey(TableElement aTable) throws SQLException {
      ResultSet importedKeys = null;
      try {
         importedKeys = dbData.getImportedKeys(null, aTable.getSchema(), aTable.getName());

         while (importedKeys.next()) {

            String appliesToColumnId = importedKeys.getString("FKCOLUMN_NAME");
            String fkeyId = importedKeys.getString("FK_NAME");
            String fKeyAddress = importedKeys.getString("FKTABLE_SCHEM");

            String refersToTable = importedKeys.getString("PKTABLE_NAME");
            String refersToTableAddress = importedKeys.getString("PKTABLE_SCHEM");
            String referencesColumn = importedKeys.getString("PKCOLUMN_NAME");

            OnDeleteEnum onDeleteAction = OnDeleteEnum.UNSPECIFIED;
            String onDeleteRule = importedKeys.getString("DELETE_RULE");
            if (Strings.isValid(onDeleteRule)) {
               // System.out.println("onDelete: " + onDeleteRule);
               int type = Integer.parseInt(onDeleteRule);
               switch (type) {
                  case java.sql.DatabaseMetaData.importedKeyNoAction:
                     onDeleteAction = OnDeleteEnum.NO_ACTION;
                     break;
                  case java.sql.DatabaseMetaData.importedKeyRestrict:
                     onDeleteAction = OnDeleteEnum.RESTRICT;
                     break;
                  case java.sql.DatabaseMetaData.importedKeyCascade:
                     onDeleteAction = OnDeleteEnum.CASCADE;
                     break;
                  case java.sql.DatabaseMetaData.importedKeySetNull:
                     onDeleteAction = OnDeleteEnum.SET_NULL;
                     break;
                  case java.sql.DatabaseMetaData.importedKeySetDefault:
                  default:
                     onDeleteAction = OnDeleteEnum.UNSPECIFIED;
                     break;
               }
            }

            OnUpdateEnum onUpdateAction = OnUpdateEnum.UNSPECIFIED;
            String onUpdateRule = importedKeys.getString("UPDATE_RULE");
            if (Strings.isValid(onUpdateRule)) {
               // System.out.println("onUpdate: " + onUpdateRule);
               int type = Integer.parseInt(onUpdateRule);
               switch (type) {
                  case java.sql.DatabaseMetaData.importedKeyNoAction:
                     onUpdateAction = OnUpdateEnum.NO_ACTION;
                     break;
                  case java.sql.DatabaseMetaData.importedKeyRestrict:
                     onUpdateAction = OnUpdateEnum.RESTRICT;
                     break;
                  case java.sql.DatabaseMetaData.importedKeyCascade:
                  case java.sql.DatabaseMetaData.importedKeySetNull:
                  case java.sql.DatabaseMetaData.importedKeySetDefault:
                  default:
                     onUpdateAction = OnUpdateEnum.UNSPECIFIED;
                     break;
               }
            }

            boolean deferrable = false;
            String deferrabilityId = importedKeys.getString("DEFERRABILITY");
            if (Strings.isValid(deferrabilityId)) {
               int type = Integer.parseInt(deferrabilityId);
               switch (type) {
                  case java.sql.DatabaseMetaData.importedKeyInitiallyDeferred:
                  case java.sql.DatabaseMetaData.importedKeyInitiallyImmediate:
                     deferrable = true;
                     break;
                  case java.sql.DatabaseMetaData.importedKeyNotDeferrable:
                     deferrable = false;
                     break;
                  default:
                     deferrable = false;
                     break;
               }
            }

            if (!Strings.isValid(fKeyAddress)) {
               fKeyAddress = aTable.getSchema();
            }

            if (!Strings.isValid(fkeyId)) {
               fkeyId = appliesToColumnId + "_FK";
            }

            if (!Strings.isValid(refersToTableAddress)) {
               refersToTableAddress = aTable.getSchema();
            }

            ConstraintElement constraint =
               ConstraintFactory.getConstraint(ConstraintTypes.FOREIGN_KEY, fKeyAddress, fkeyId, deferrable);
            constraint.addColumn(appliesToColumnId);

            ReferenceClause ref = new ReferenceClause(refersToTableAddress, refersToTable);
            ref.addColumn(referencesColumn);

            ref.setOnDeleteAction(onDeleteAction);
            ref.setOnUpdateAction(onUpdateAction);

            ((ForeignKey) constraint).addReference(ref);

            aTable.addConstraint(constraint);
         }
      } finally {
         if (importedKeys != null) {
            importedKeys.close();
         }
      }
   }

   private void getIndexInfo(TableElement aTable) throws SQLException {
      ResultSet indexKeys = null;
      try {
         indexKeys = dbData.getIndexInfo(null, aTable.getSchema(), aTable.getName(), false, false);

         Map<String, Map<Integer, AppliesToClause>> indexMap = new HashMap<String, Map<Integer, AppliesToClause>>();

         while (indexKeys.next()) {
            String indexName = indexKeys.getString("INDEX_NAME");

            if (indexName != null && indexName.length() > 0) {
               Matcher matcher = indexMatcher.reset(indexName);
               if (!matcher.matches()) {
                  if (indexKeys.getShort("TYPE") == DatabaseMetaData.tableIndexOther) {

                     short ordinal = indexKeys.getShort("ORDINAL_POSITION");
                     String columnName = indexKeys.getString("COLUMN_NAME");

                     String orderTypeString = indexKeys.getString("ASC_OR_DESC");
                     OrderType orderType = OrderType.Undefined;
                     if (orderTypeString != null) {
                        if (orderTypeString.equalsIgnoreCase("A")) {
                           orderType = OrderType.Ascending;
                        } else if (orderTypeString.equalsIgnoreCase("D")) {
                           orderType = OrderType.Descending;
                        }
                     }

                     Map<Integer, AppliesToClause> appliesTo = null;
                     if (indexMap.containsKey(indexName)) {
                        appliesTo = indexMap.get(indexName);
                     } else {
                        appliesTo = new HashMap<Integer, AppliesToClause>();
                        indexMap.put(indexName, appliesTo);
                     }
                     appliesTo.put(new Integer(ordinal), new AppliesToClause(columnName, orderType));
                  }
               }
            }
         }
         for (String indexName : indexMap.keySet()) {
            Map<Integer, AppliesToClause> clauseMap = indexMap.get(indexName);
            IndexElement element = new IndexElement(indexName);

            Set<Integer> index = clauseMap.keySet();
            Set<Integer> sortedIndex = new TreeSet<Integer>();
            for (Integer val : index) {
               sortedIndex.add(val);
            }

            for (Integer val : sortedIndex) {
               AppliesToClause clause = clauseMap.get(val);
               element.addAppliesTo(clause.getColumnName(), clause.getOrderType());
            }
            aTable.addIndexData(element);
         }
      } finally {
         if (indexKeys != null) {
            indexKeys.close();
         }
      }
   }
}