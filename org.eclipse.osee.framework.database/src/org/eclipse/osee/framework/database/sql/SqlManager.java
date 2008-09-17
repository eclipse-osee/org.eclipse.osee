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
package org.eclipse.osee.framework.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.database.data.AppliesToClause;
import org.eclipse.osee.framework.database.data.ColumnDbData;
import org.eclipse.osee.framework.database.data.ColumnMetadata;
import org.eclipse.osee.framework.database.data.ConstraintElement;
import org.eclipse.osee.framework.database.data.ForeignKey;
import org.eclipse.osee.framework.database.data.IndexElement;
import org.eclipse.osee.framework.database.data.ReferenceClause;
import org.eclipse.osee.framework.database.data.SchemaDataLookup;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.framework.database.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.framework.database.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.database.sql.datatype.SqlDataType;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;

/**
 * @author Roberto E. Escobar
 */
public abstract class SqlManager {

   protected Logger logger;
   protected SqlDataType sqlDataType;
   public static final String CREATE_STRING = "CREATE";
   public static final String DROP_STRING = "DROP";

   public SqlManager(Logger logger, SqlDataType sqlDataType) {
      super();
      this.logger = logger;
      this.sqlDataType = sqlDataType;
   }

   public abstract void createTable(Connection connection, TableElement tableDef) throws SQLException, Exception;

   public abstract void dropTable(Connection connection, TableElement tableDef) throws SQLException, Exception;

   public void insertData(Connection connection, List<ColumnDbData> rowData, TableElement tableMetadata) throws SQLException, Exception {
      List<String> columnNames = new ArrayList<String>();
      List<String> placeHolders = new ArrayList<String>();
      List<String> columnValues = new ArrayList<String>();
      List<SQL3DataType> columnTypes = new ArrayList<SQL3DataType>();

      for (ColumnDbData dbData : rowData) {
         String columnId = dbData.getColumnName();
         String columnValue = dbData.getColumnValue();

         ColumnMetadata columnMetadata = SchemaDataLookup.getColumnDefinition(tableMetadata.getColumns(), columnId);
         SQL3DataType type = SQL3DataType.valueOf(columnMetadata.getColumnField(ColumnFields.type));

         columnNames.add("\"" + columnId + "\"");
         placeHolders.add("?");
         columnValues.add(columnValue);
         columnTypes.add(type);
      }

      String toExecute =
            "INSERT INTO " + formatQuotedString(tableMetadata.getFullyQualifiedTableName(), "\\.") + " (\n";
      toExecute += StringFormat.listToCommaSeparatedString(columnNames);
      toExecute += "\n) VALUES (\n";
      toExecute += StringFormat.listToCommaSeparatedString(placeHolders);
      toExecute += ")\n";

      PreparedStatement statement = null;
      try {
         statement = connection.prepareStatement(toExecute);
         statement.setFetchSize(1000);
         for (int index = 0; index < columnNames.size(); index++) {
            sqlDataType.preparedStatementHelper(statement, index + 1, columnTypes.get(index), columnValues.get(index));
         }
         statement.executeUpdate();
      } catch (SQLException e) {
         throw new Exception(statement + "\n", e);
      } finally {
         if (statement != null) {
            statement.close();
         }
      }
   }

   public String getType(SQL3DataType dataType) {
      return sqlDataType.getType(dataType);
   }

   public String columnDataToSQL(Map<ColumnFields, String> column) {
      StringBuilder toReturn = new StringBuilder();

      String columnLimits = column.get(ColumnFields.limits);
      String defaultValue = column.get(ColumnFields.defaultValue);

      SQL3DataType dataType = SQL3DataType.valueOf(column.get(ColumnFields.type));
      columnLimits = sqlDataType.getLimit(dataType, columnLimits);
      toReturn.append(column.get(ColumnFields.id));
      toReturn.append(" ");
      toReturn.append(sqlDataType.getType(dataType));

      if (columnLimits != null && !columnLimits.equals("")) {
         toReturn.append(" (" + columnLimits + ")");
      }
      if (defaultValue != null && !defaultValue.equals("")) {
         toReturn.append(" " + defaultValue);
      }
      return toReturn.toString();
   }

   @SuppressWarnings("unchecked")
   protected String handleConstraintCreationSection(List constraints, String tableId) {
      List<String> constraintStatements = new ArrayList<String>();
      for (Object object : constraints) {
         ConstraintElement constraint = (ConstraintElement) object;
         constraintStatements.add(constraintDataToSQL(constraint, tableId));
      }
      StringBuilder toExecute = new StringBuilder();
      toExecute.append((constraintStatements.size() != 0 ? ",\n" : ""));
      toExecute.append(StringFormat.listToValueSeparatedString(constraintStatements, ",\n"));
      return toExecute.toString();
   }

   protected String formatQuotedString(String value, String splitAt) {
      String[] array = value.split(splitAt);
      for (int index = 0; index < array.length; index++) {
         array[index] = "\"" + array[index] + "\"";
      }
      // return value;
      return StringFormat.separateWith(array, splitAt.replaceAll("\\\\", ""));
   }

   public String constraintDataToSQL(ConstraintElement constraint, String tableID) {
      StringBuilder toReturn = new StringBuilder();
      String id = formatQuotedString(constraint.getId(), "\\.");
      String type = constraint.getConstraintType().toString();
      String appliesTo = formatQuotedString(constraint.getCommaSeparatedColumnsList(), ",");

      if (id != null && !id.equals("") && appliesTo != null && !appliesTo.equals("")) {
         toReturn.append("CONSTRAINT " + id + " " + type + " (" + appliesTo + ")");

         if (constraint instanceof ForeignKey) {
            ForeignKey fk = (ForeignKey) constraint;
            List<ReferenceClause> refs = fk.getReferences();

            for (ReferenceClause ref : refs) {
               String refTable = formatQuotedString(ref.getFullyQualifiedTableName(), "\\.");
               String refColumns = formatQuotedString(ref.getCommaSeparatedColumnsList(), ",");

               String onUpdate = "";
               if (!ref.getOnUpdateAction().equals(OnUpdateEnum.UNSPECIFIED)) {
                  onUpdate = "ON UPDATE " + ref.getOnUpdateAction().toString();
               }

               String onDelete = "";
               if (!ref.getOnDeleteAction().equals(OnDeleteEnum.UNSPECIFIED)) {
                  onDelete = "ON DELETE " + ref.getOnDeleteAction().toString();
               }

               if (refTable != null && refColumns != null && !refTable.equals("") && !refColumns.equals("")) {
                  toReturn.append(" REFERENCES " + refTable + " (" + refColumns + ")");
                  if (!onUpdate.equals("")) {
                     toReturn.append(" " + onUpdate);
                  }

                  if (!onDelete.equals("")) {
                     toReturn.append(" " + onDelete);
                  }

                  if (constraint.isDeferrable()) {
                     toReturn.append(" DEFERRABLE");
                  }
               }

               else {
                  logger.log(Level.WARNING, "Skipping CONSTRAINT at Table: " + tableID + "\n\t " + fk.toString());
               }

            }
         }
      } else {
         logger.log(Level.WARNING, "Skipping CONSTRAINT at Table: " + tableID + "\n\t " + constraint.toString());
      }
      return toReturn.toString();
   }

   protected void executeStatement(Connection connection, String sqlStatement) throws SQLException, Exception {
      Statement statement = null;
      try {
         statement = connection.createStatement();
         statement.execute(sqlStatement);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, sqlStatement);
         throw ex;
      } finally {
         if (statement != null) {
            statement.close();
         }
      }
   }

   public void createSchema(Connection connection, String schema) throws SQLException, Exception {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append(CREATE_STRING + " SCHEMA \"" + schema + "\"");
      executeStatement(connection, toExecute.toString());
   }

   public void dropSchema(Connection connection, String schema) throws SQLException, Exception {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append(DROP_STRING + " SCHEMA \"" + schema + "\" RESTRICT");
      executeStatement(connection, toExecute.toString());
   }

   protected String insertDataToSQL(String fullyQualifiedTableName, List<String> columns, List<String> columnData) {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append("INSERT INTO " + formatQuotedString(fullyQualifiedTableName, "\\.") + " (\n");
      toExecute.append(StringFormat.listToCommaSeparatedString(columns));
      toExecute.append("\n) VALUES (\n");
      toExecute.append(StringFormat.listToCommaSeparatedString(columnData));
      toExecute.append(")\n");
      return toExecute.toString();
   }

   public void createIndex(Connection connection, TableElement tableDef) throws SQLException, Exception {
      List<IndexElement> tableIndeces = tableDef.getIndexData();
      String indexId = null;
      StringBuilder appliesTo = new StringBuilder();
      String tableName = formatQuotedString(tableDef.getFullyQualifiedTableName(), "\\.");
      for (IndexElement iData : tableIndeces) {
         if (iData.ignoreMySql()) continue;
         indexId = iData.getId();
         appliesTo.delete(0, appliesTo.length());

         List<AppliesToClause> appliesToList = iData.getAppliesToList();
         for (int index = 0; index < appliesToList.size(); index++) {
            AppliesToClause record = appliesToList.get(index);
            appliesTo.append(record.getColumnName());

            switch (record.getOrderType()) {
               case Ascending:
                  appliesTo.append(" ASC");
                  break;
               case Descending:
                  appliesTo.append(" DESC");
                  break;
               default:
                  break;
            }
            if (index + 1 < appliesToList.size()) {
               appliesTo.append(", ");
            }
         }
         String toExecute =
               String.format("%s %s INDEX %s ON %s (%s)", CREATE_STRING, iData.getIndexType(), indexId, tableName,
                     appliesTo);
         toExecute = createIndexPostProcess(iData, toExecute);
         logger.log(Level.INFO, toExecute + "\n");
         executeStatement(connection, toExecute);
      }
   }

   protected String createIndexPostProcess(IndexElement indexElement, String original) {
      return original;
   }

   public void dropIndex(Connection connection, TableElement tableDef) throws SQLException, Exception {
      List<IndexElement> tableIndeces = tableDef.getIndexData();
      String tableName = tableDef.getFullyQualifiedTableName();
      for (IndexElement iData : tableIndeces) {
         logger.log(Level.INFO, String.format("Dropping Index: [%s] FROM [%s]\n", iData.getId(), tableName));
         executeStatement(connection, DROP_STRING + " INDEX " + iData.getId());
      }
   }
}
