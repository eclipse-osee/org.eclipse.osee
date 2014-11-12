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
package org.eclipse.osee.jdbc.internal.schema.sql;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.SQL3DataType;
import org.eclipse.osee.jdbc.internal.schema.JdbcWriter;
import org.eclipse.osee.jdbc.internal.schema.data.AppliesToClause;
import org.eclipse.osee.jdbc.internal.schema.data.ColumnDbData;
import org.eclipse.osee.jdbc.internal.schema.data.ColumnMetadata;
import org.eclipse.osee.jdbc.internal.schema.data.ConstraintElement;
import org.eclipse.osee.jdbc.internal.schema.data.ForeignKey;
import org.eclipse.osee.jdbc.internal.schema.data.IndexElement;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaDataLookup;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.ColumnFields;

/**
 * @author Roberto E. Escobar
 */
public abstract class SqlManager {

   protected SqlDataType sqlDataType;
   public static final String CREATE_STRING = "CREATE";
   public static final String DROP_STRING = "DROP";

   protected final JdbcWriter client;

   protected SqlManager(JdbcWriter client, SqlDataType sqlDataType) {
      this.client = client;
      this.sqlDataType = sqlDataType;
   }

   protected void warn(String msg, Object... data) {
      //
   }

   protected void debug(String msg, Object... data) {
      //
   }

   protected String join(String[] data, String separator) {
      return Collections.toString(separator, (Object[]) data);
   }

   protected String join(List<String> data, String separator) {
      return Collections.toString(separator, data);
   }

   public abstract void createTable(TableElement tableDef) throws OseeCoreException;

   public abstract void dropTable(TableElement tableDef) throws OseeCoreException;

   public void insertData(List<ColumnDbData> rowData, TableElement tableMetadata) throws OseeCoreException {
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
      toExecute += join(columnNames, ",");
      toExecute += "\n) VALUES (\n";
      toExecute += join(placeHolders, ",");
      toExecute += ")\n";

      Object[] data = new Object[columnNames.size()];
      for (int index = 0; index < columnNames.size(); index++) {
         data[index] = preparedStatementHelper(columnTypes.get(index), columnValues.get(index));
      }
      client.runPreparedUpdate(toExecute, data);
   }

   public Object preparedStatementHelper(SQL3DataType columnType, String value) {
      switch (columnType) {
         case BINARY:
         case BIT:
            return Strings.isValid(value) ? Byte.parseByte(value) : 0;
         case TINYINT:
         case SMALLINT:
            return Strings.isValid(value) ? Short.valueOf(value) : 0;
         case INTEGER:
            return Strings.isValid(value) ? Integer.valueOf(value) : 0;
         case BIGINT:
            return Strings.isValid(value) ? BigDecimal.valueOf(Double.valueOf(value)) : new BigDecimal(0);
         case FLOAT:
            return Strings.isValid(value) ? Float.valueOf(value) : 0.0f;
         case NUMERIC:
         case DECIMAL:
         case REAL:
         case DOUBLE:
            return Strings.isValid(value) ? Double.valueOf(value) : 0.0;
         case CHAR:
         case VARCHAR:
         case LONGVARCHAR:
            return value;
         case DATE:
            return !Strings.isValid(value) ? SQL3DataType.DATE : Date.valueOf(value);
         case TIMESTAMP:
            return Strings.isValid(value) ? Timestamp.valueOf(value) : GlobalTime.GreenwichMeanTimestamp();
         case TIME:
            return !Strings.isValid(value) ? SQL3DataType.TIME : Time.valueOf(value);
         case VARBINARY:
         case LONGVARBINARY:
            return value.getBytes();
         case BLOB:
            return new BufferedInputStream(new ByteArrayInputStream(value.getBytes()));
         case CLOB:
            return new BufferedInputStream(new ByteArrayInputStream(value.getBytes()));
         case BOOLEAN:
            return !Strings.isValid(value) ? false : Boolean.parseBoolean(value);
         default:
            throw JdbcException.newJdbcException("unexpected column type [%s]", columnType);
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

      if (Strings.isValid(columnLimits)) {
         toReturn.append(" (" + columnLimits + ")");
      }
      if (Strings.isValid(defaultValue)) {
         toReturn.append(" " + defaultValue);
      }
      return toReturn.toString();
   }

   protected String handleConstraintCreationSection(List<? extends ConstraintElement> constraints, String tableId) {
      List<String> constraintStatements = new ArrayList<String>();
      for (ConstraintElement constraint : constraints) {
         constraintStatements.add(constraintDataToSQL(constraint, tableId));
      }
      StringBuilder toExecute = new StringBuilder();
      toExecute.append((constraintStatements.size() != 0 ? ",\n" : ""));
      toExecute.append(join(constraintStatements, ",\n"));
      return toExecute.toString();
   }

   protected String formatQuotedString(String value, String splitAt) {
      String[] array = value.split(splitAt);
      for (int index = 0; index < array.length; index++) {
         array[index] = "\"" + array[index] + "\"";
      }
      String separator = splitAt.replaceAll("\\\\", "");
      return join(array, separator);
   }

   public String constraintDataToSQL(ConstraintElement constraint, String tableID) {
      StringBuilder toReturn = new StringBuilder();
      String id = formatQuotedString(constraint.getId(), "\\.");
      String type = constraint.getConstraintType().toString();
      String appliesTo = formatQuotedString(constraint.getCommaSeparatedColumnsList(), ",");

      if (Strings.isValid(id) && Strings.isValid(appliesTo)) {
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
                  warn("Skipping CONSTRAINT at Table: %s\n\t %s", tableID, fk);
               }
            }
         }
      } else {
         warn("Skipping CONSTRAINT at Table: %s\n\t %s", tableID, constraint);
      }
      return toReturn.toString();
   }

   public void createSchema(String schema) throws OseeCoreException {
      client.runPreparedUpdate(CREATE_STRING + " SCHEMA \"" + schema + "\"");
   }

   public void dropSchema(String schema) throws OseeCoreException {
      client.runPreparedUpdate(DROP_STRING + " SCHEMA \"" + schema + "\" CASCADE");
   }

   protected String insertDataToSQL(String fullyQualifiedTableName, List<String> columns, List<String> columnData) {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append("INSERT INTO " + formatQuotedString(fullyQualifiedTableName, "\\.") + " (\n");
      toExecute.append(join(columns, ","));
      toExecute.append("\n) VALUES (\n");
      toExecute.append(join(columnData, ","));
      toExecute.append(")\n");
      return toExecute.toString();
   }

   public void createIndex(TableElement tableDef) throws OseeCoreException {
      List<IndexElement> tableIndices = tableDef.getIndexData();
      String indexId = null;
      StringBuilder appliesTo = new StringBuilder();
      String tableName = formatQuotedString(tableDef.getFullyQualifiedTableName(), "\\.");
      for (IndexElement iData : tableIndices) {
         if (iData.ignoreMySql()) {
            continue;
         }
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
         debug(toExecute);
         client.runPreparedUpdate(toExecute);
      }
   }

   protected String createIndexPostProcess(IndexElement indexElement, String original) {
      return original;
   }

   public void dropIndex(TableElement tableDef) throws OseeCoreException {
      List<IndexElement> tableIndices = tableDef.getIndexData();
      String tableName = tableDef.getFullyQualifiedTableName();
      for (IndexElement iData : tableIndices) {
         debug("Dropping Index: [%s] FROM [%s]\n", iData.getId(), tableName);
         client.runPreparedUpdate(DROP_STRING + " INDEX " + iData.getId());
      }
   }
}
