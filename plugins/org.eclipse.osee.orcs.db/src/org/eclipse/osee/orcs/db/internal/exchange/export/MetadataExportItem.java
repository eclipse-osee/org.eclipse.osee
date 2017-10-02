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
package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.SQL3DataType;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportXml;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public class MetadataExportItem extends AbstractXmlExportItem {
   private final List<AbstractExportItem> exportItems;
   private final JdbcClient jdbcClient;

   public MetadataExportItem(Log logger, List<AbstractExportItem> exportItems, JdbcClient jdbcClient) {
      super(logger, ExportItem.EXPORT_DB_SCHEMA);
      this.exportItems = exportItems;
      this.jdbcClient = jdbcClient;
   }

   @Override
   protected void doWork(Appendable appendable) throws Exception {
      ExportImportXml.openXmlNode(appendable, ExportImportXml.METADATA);
      try (JdbcConnection connection = jdbcClient.getConnection()) {
         DatabaseMetaData metaData = connection.getMetaData();
         String[] tableTypes = getTypes(metaData);
         String schema = getSchema(metaData);
         for (AbstractExportItem item : exportItems) {
            if (!item.equals(this) && Strings.isValid(item.getSource())) {
               processMetaData(appendable, metaData, schema, tableTypes, item.getSource());
            }
         }
      } finally {
         ExportImportXml.closeXmlNode(appendable, ExportImportXml.METADATA);
      }
   }

   private boolean isTypeAllowed(String type) {
      boolean toReturn = false;
      if (!type.contains("system")) {
         if (type.contains("table") || type.contains("synonym") || type.contains("view")) {
            toReturn = true;
         }
      }
      return toReturn;
   }

   private String[] getTypes(DatabaseMetaData metaData) throws SQLException {
      List<String> toReturn = new ArrayList<>();
      ResultSet resultSet = null;
      try {
         resultSet = metaData.getTableTypes();
         if (resultSet != null) {
            while (resultSet.next()) {
               String type = resultSet.getString("TABLE_TYPE");
               if (isTypeAllowed(type.toLowerCase())) {
                  toReturn.add(type);
               }
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
      return toReturn.toArray(new String[toReturn.size()]);
   }

   private String getSchema(DatabaseMetaData metaData) throws SQLException {
      String toReturn = "%";
      ResultSet resultSet = null;
      try {
         resultSet = metaData.getSchemas();
         if (resultSet != null) {
            while (resultSet.next()) {
               String rawSchema = resultSet.getString("TABLE_SCHEM");
               if (rawSchema.equalsIgnoreCase("osee")) {
                  toReturn = rawSchema + toReturn;
                  break;
               }
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
      return toReturn;
   }

   private void processMetaData(Appendable appendable, DatabaseMetaData metaData, String schema, String[] tableTypes, String targetTable) throws Exception {
      ResultSet resultSet = null;
      try {

         resultSet = metaData.getTables(null, null, schema, tableTypes);
         if (resultSet != null) {
            while (resultSet.next()) {
               String tableName = resultSet.getString("TABLE_NAME");
               String schemaName = resultSet.getString("TABLE_SCHEM");
               if (targetTable.equalsIgnoreCase(tableName)) {
                  ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.TABLE);
                  ExportImportXml.addXmlAttribute(appendable, ExportImportXml.TABLE_NAME, tableName.toLowerCase());
                  ExportImportXml.endOpenedPartialXmlNode(appendable);

                  processColumnMetaData(appendable, metaData, schemaName, tableName);

                  ExportImportXml.closeXmlNode(appendable, ExportImportXml.TABLE);
                  break;
               }
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
   }

   private void processColumnMetaData(Appendable appendable, DatabaseMetaData metaData, String schema, String tableName) throws SQLException, IOException {
      ResultSet resultSet = null;
      try {
         try {
            resultSet = metaData.getColumns(null, schema, tableName, null);
         } catch (SQLException ex) {
            resultSet = metaData.getColumns(null, null, tableName, null);
         }
         if (resultSet != null) {
            while (resultSet.next()) {
               ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.COLUMN);
               try {
                  String columnId = resultSet.getString("COLUMN_NAME").toLowerCase();
                  ExportImportXml.addXmlAttribute(appendable, ExportImportXml.ID, columnId);

                  int dataType = resultSet.getInt("DATA_TYPE");
                  if (JdbcDbType.getDbType(metaData).equals(JdbcDbType.foxpro)) {
                     if (dataType == Types.CHAR) {
                        dataType = Types.VARCHAR;
                     }
                  }
                  String dataTypeName = SQL3DataType.get(dataType).name();
                  ExportImportXml.addXmlAttribute(appendable, ExportImportXml.TYPE, dataTypeName);
               } finally {
                  ExportImportXml.closePartialXmlNode(appendable);
               }
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
   }
}
