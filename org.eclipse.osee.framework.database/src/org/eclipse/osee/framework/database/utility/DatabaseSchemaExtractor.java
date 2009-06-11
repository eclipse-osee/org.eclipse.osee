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
package org.eclipse.osee.framework.database.utility;

import java.io.File;
import java.io.IOException;
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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.database.data.AppliesToClause;
import org.eclipse.osee.framework.database.data.ColumnMetadata;
import org.eclipse.osee.framework.database.data.ConstraintElement;
import org.eclipse.osee.framework.database.data.ConstraintFactory;
import org.eclipse.osee.framework.database.data.ConstraintTypes;
import org.eclipse.osee.framework.database.data.ForeignKey;
import org.eclipse.osee.framework.database.data.IndexElement;
import org.eclipse.osee.framework.database.data.ReferenceClause;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.data.AppliesToClause.OrderType;
import org.eclipse.osee.framework.database.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.framework.database.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.framework.database.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.database.data.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.db.DbConfigFileInformation;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseSchemaExtractor {

   private DatabaseMetaData dbData;
   private String dbName;
   private String dbVersion;
   private Map<String, SchemaData> database;
   private List<String> filter;
   private Set<String> tablesToExtract;
   private Set<String> schemas;

   private static final String DEFAULT_FILTER = "BIN.*";

   public DatabaseSchemaExtractor(Set<String> schemas) throws OseeDataStoreException {
      try {
         this.dbData = ConnectionHandler.getMetaData();
         this.dbName = dbData.getDatabaseProductName();
         this.dbVersion = dbData.getDatabaseProductVersion();
         this.filter = new ArrayList<String>();
         filter.add(DEFAULT_FILTER);
         this.tablesToExtract = new TreeSet<String>();
         this.schemas = schemas;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public void addToFilter(String value) {
      filter.add(value);
   }

   public String getDBName() {
      return dbName;
   }

   public String getDBVersion() {
      return dbVersion;
   }

   public Map<String, SchemaData> getSchemas() {
      return database;
   }

   public void extractSchemaData() throws OseeDataStoreException {
      database = new HashMap<String, SchemaData>();
      for (String schema : schemas) {
         SchemaData dbTables = getTableInformation(schema);
         database.put(schema, dbTables);
      }
   }

   public Set<String> getAllSchemas() throws SQLException {
      ResultSet schemaResults = dbData.getSchemas();
      Set<String> schemaSet = new TreeSet<String>();

      while (schemaResults.next()) {
         String schema = schemaResults.getString("TABLE_SCHEM");
         if (schema != null && !schema.equals("")) {
            schemaSet.add(schema);
         }
      }
      schemaResults.close();
      return schemaSet;
   }

   /**
    * Writes the XML files in the directory specified.
    * 
    * @param directory The directory tow write the XML files.
    * @throws IOException
    */
   public void writeToFile(File directory) throws IOException {
      FileUtility.setupDirectoryForWrite(directory);
      Set<String> keys = database.keySet();
      for (String schema : keys) {
         SchemaData tableData = database.get(schema);
         File xmlFile =
               new File(
                     directory.getAbsolutePath() + File.separator + schema + DbConfigFileInformation.getSchemaFileExtension());
         try {
            Jaxp.writeXmlDocument(tableData.getXmlDocument(), xmlFile);
         } catch (TransformerException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex.toString(), ex);
         } catch (ParserConfigurationException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   public String toString() {
      String toReturn = "Name: " + dbName + "\tVer: " + dbVersion + "\n";
      Set<String> keys = database.keySet();
      for (String schema : keys) {
         SchemaData tableData = database.get(schema);
         toReturn += " Schema: \n" + schema + "\n" + tableData.toString();
      }
      return toReturn;
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

   private SchemaData getTableInformation(String schemaPattern) throws OseeDataStoreException {
      try {
         SchemaData dbTables = new SchemaData();
         ResultSet tables = null;
         tables = dbData.getTables(null, null, null, new String[] {"TABLE"});

         while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME").toUpperCase();
            String schemaName = tables.getString("TABLE_SCHEM");
            if (tableName != null && !isFiltered(tableName) && schemaName.equalsIgnoreCase(schemaPattern)) {
               boolean extract = true;
               if (this.tablesToExtract != null && this.tablesToExtract.size() > 0) {
                  extract = tablesToExtract.contains(schemaPattern + "." + tableName);
               }

               if (extract) {
                  TableElement tableEntry = new TableElement();
                  tableEntry.addTableDescription(TableDescriptionFields.name, tableName);
                  tableEntry.addTableDescription(TableDescriptionFields.schema, schemaName);
                  getColumnInformation(tableEntry);
                  getColumnPrimaryKey(tableEntry);

                  if (!(SupportedDatabase.isDatabaseType(SupportedDatabase.foxpro) || SupportedDatabase.isDatabaseType(SupportedDatabase.postgresql))) {
                     getColumnForeignKey(tableEntry);
                  }
                  getIndexInfo(tableEntry);
                  dbTables.addTableDefinition(tableEntry);
               }
            }
         }
         tables.close();
         return dbTables;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   private void getColumnInformation(TableElement aTable) throws SQLException, OseeDataStoreException {
      ResultSet columns = null;
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
         if (SupportedDatabase.isDatabaseType(SupportedDatabase.foxpro)) {
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

         if (!SupportedDatabase.isDatabaseType(SupportedDatabase.foxpro)) {
            // int dataType = columns.getInt("DATA_TYPE");
            switch (dataType) {
               case java.sql.Types.CHAR:
               case java.sql.Types.VARCHAR:
                  String limits = columns.getString("COLUMN_SIZE");
                  if (limits != null && !limits.equals("")) {
                     column.addColumnField(ColumnFields.limits, limits);
                  }
                  break;
               case java.sql.Types.DECIMAL:
               case java.sql.Types.NUMERIC:
                  limits = columns.getString("COLUMN_SIZE");
                  String decimal = columns.getString("DECIMAL_DIGITS");
                  if (decimal != null && !decimal.equals("")) {
                     if (limits != null && !limits.equals("")) {
                        limits += "," + decimal;
                     }
                  }
                  if (limits != null && !limits.equals("")) {
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
                  if (limits != null && !limits.equals("")) {
                     column.addColumnField(ColumnFields.limits, limits);
                  }
                  break;
               default:
                  break;
            }
         }
         aTable.addColumn(column);
      }
      columns.close();
   }

   private void getColumnPrimaryKey(TableElement aTable) throws SQLException {
      ResultSet primaryKeys = null;
      try {
         primaryKeys = dbData.getPrimaryKeys(null, aTable.getSchema(), aTable.getName());
      } catch (SQLException ex) {
         primaryKeys = dbData.getPrimaryKeys(null, null, aTable.getName());
      }
      Map<String, Set<String>> constraintKeyMap = new HashMap<String, Set<String>>();

      while (primaryKeys.next()) {
         String column = primaryKeys.getString("COLUMN_NAME");
         String keyId = primaryKeys.getString("PK_NAME");

         if (keyId == null || keyId.equals("")) {
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
      primaryKeys.close();
   }

   private void getColumnForeignKey(TableElement aTable) throws SQLException {
      ResultSet importedKeys = dbData.getImportedKeys(null, aTable.getSchema(), aTable.getName());

      while (importedKeys.next()) {

         String appliesToColumnId = importedKeys.getString("FKCOLUMN_NAME");
         String fkeyId = importedKeys.getString("FK_NAME");
         String fKeyAddress = importedKeys.getString("FKTABLE_SCHEM");

         String refersToTable = importedKeys.getString("PKTABLE_NAME");
         String refersToTableAddress = importedKeys.getString("PKTABLE_SCHEM");
         String referencesColumn = importedKeys.getString("PKCOLUMN_NAME");

         OnDeleteEnum onDeleteAction = OnDeleteEnum.UNSPECIFIED;
         String onDeleteRule = importedKeys.getString("DELETE_RULE");
         if (onDeleteRule != null && !onDeleteRule.equals("")) {
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
         if (onUpdateRule != null && !onUpdateRule.equals("")) {
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
         if (deferrabilityId != null && !deferrabilityId.equals("")) {
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

         if (fKeyAddress == null || fKeyAddress.equals("")) {
            fKeyAddress = aTable.getSchema();
         }

         if (fkeyId == null || fkeyId.equals("")) {
            fkeyId = appliesToColumnId + "_FK";
         }

         if (refersToTableAddress == null || refersToTableAddress.equals("")) {
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
      importedKeys.close();
   }

   private void getIndexInfo(TableElement aTable) throws SQLException {
      ResultSet indexKeys = dbData.getIndexInfo(null, aTable.getSchema(), aTable.getName(), false, false);
      Pattern pattern = Pattern.compile("SQL\\d+");

      Map<String, Map<Integer, AppliesToClause>> indexMap = new HashMap<String, Map<Integer, AppliesToClause>>();

      while (indexKeys.next()) {
         String indexName = indexKeys.getString("INDEX_NAME");

         if (indexName != null && indexName.length() > 0) {
            Matcher matcher = pattern.matcher(indexName);
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
      indexKeys.close();
   }
}