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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
public class MetaDataSaxHandler extends AbstractSaxHandler {

   private final Map<String, MetaData> importMetadataMap;
   private final Map<String, MetaData> targetMetadataMap;
   private MetaData currentMetadata;

   public MetaDataSaxHandler() {
      this.importMetadataMap = new HashMap<String, MetaData>();
      this.targetMetadataMap = new HashMap<String, MetaData>();
   }

   public MetaData getMetadata(String source) {
      return targetMetadataMap.get(source);
   }

   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase(ExportImportXml.METADATA)) {
         this.importMetadataMap.clear();
      } else if (localName.equalsIgnoreCase(ExportImportXml.TABLE)) {
         String tableName = attributes.getValue(ExportImportXml.TABLE_NAME);
         if (Strings.isValid(tableName)) {
            this.currentMetadata = new MetaData(tableName);
            this.importMetadataMap.put(tableName, currentMetadata);
         } else {
            this.currentMetadata = null;
         }
      } else if (localName.equalsIgnoreCase(ExportImportXml.COLUMN)) {
         String columnName = attributes.getValue(ExportImportXml.ID);
         String typeName = attributes.getValue(ExportImportXml.TYPE);
         SQL3DataType sql3DataType = SQL3DataType.valueOf(typeName);
         this.currentMetadata.addColumn(columnName, sql3DataType);
      }
   }

   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      try {
         if (localName.equalsIgnoreCase(ExportImportXml.TABLE)) {
            this.currentMetadata = null;
         }
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
   }

   public void checkAndLoadTargetDbMetadata() throws OseeCoreException, SQLException {
      Map<String, MetaData> targetTables = getTargetDbMetadata();

      StringBuffer errorMessage = new StringBuffer();
      for (String tableName : targetTables.keySet()) {
         MetaData sourceMeta = this.importMetadataMap.get(tableName);
         MetaData destinationMeta = targetTables.get(tableName);
         Collection<String> sourceColumns = sourceMeta.getColumnNames();
         for (String destinationColumn : destinationMeta.getColumnNames()) {
            if (!sourceColumns.contains(destinationColumn)) {
               errorMessage.append(String.format(
                     "Target column not found in source database.\nTable:[%s] - [%s not in (%s)]\n", tableName,
                     destinationColumn, sourceColumns));
            }
         }
      }
      if (errorMessage.length() > 0) {
         throw new OseeCoreException(errorMessage.toString());
      }
      this.targetMetadataMap.putAll(targetTables);
   }

   private Map<String, MetaData> getTargetDbMetadata() throws SQLException, OseeDataStoreException {
      Map<String, MetaData> targetDbMetadata = new HashMap<String, MetaData>();
      DatabaseMetaData dbMetaData = ConnectionHandler.getMetaData();
      for (String sourceTables : importMetadataMap.keySet()) {
         processMetaData(targetDbMetadata, dbMetaData, sourceTables);
      }
      return targetDbMetadata;
   }

   private void processMetaData(Map<String, MetaData> targetDbMetadata, DatabaseMetaData dbMetaData, String targetTable) throws SQLException, OseeDataStoreException {
      ResultSet resultSet = null;
      try {
         resultSet = dbMetaData.getTables(null, null, null, new String[] {"TABLE"});
         if (resultSet != null) {
            while (resultSet.next()) {
               String tableName = resultSet.getString("TABLE_NAME");
               String schemaName = resultSet.getString("TABLE_SCHEM");
               if (targetTable.equalsIgnoreCase(tableName)) {
                  String name = tableName.toLowerCase();
                  MetaData currentMetadata = new MetaData(name);
                  targetDbMetadata.put(name, currentMetadata);
                  processColumnMetaData(currentMetadata, dbMetaData, schemaName, tableName);
               }
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
   }

   private void processColumnMetaData(MetaData currentMetadata, DatabaseMetaData dbMetaData, String schema, String tableName) throws SQLException, OseeDataStoreException {
      ResultSet resultSet = null;

      try {
         try {
            resultSet = dbMetaData.getColumns(null, schema, tableName, null);
         } catch (SQLException ex) {
            resultSet = dbMetaData.getColumns(null, null, tableName, null);
         }
         if (resultSet != null) {
            while (resultSet.next()) {
               String columnId = resultSet.getString("COLUMN_NAME").toLowerCase();
               int dataType = resultSet.getInt("DATA_TYPE");
               if (SupportedDatabase.isDatabaseType(dbMetaData, SupportedDatabase.foxpro)) {
                  if (dataType == Types.CHAR) {
                     dataType = Types.VARCHAR;
                  }
               }
               currentMetadata.addColumn(columnId, SQL3DataType.get(dataType));
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
   }
}
