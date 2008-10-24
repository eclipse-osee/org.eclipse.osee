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
package org.eclipse.osee.framework.branch.management.exchange.export;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class MetadataExportItem extends AbstractDbExportItem {
   private final List<AbstractExportItem> exportItems;

   public MetadataExportItem(int priority, String name, List<AbstractExportItem> exportItems) {
      super(priority, name, ExportImportXml.DB_SCHEMA);
      this.exportItems = exportItems;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.AbstractExportItem#doWork(java.lang.Appendable)
    */
   @Override
   protected void doWork(Appendable appendable) throws Exception {
      ExportImportXml.openXmlNode(appendable, ExportImportXml.METADATA);
      try {
         DatabaseMetaData metaData = getConnection().getMetaData();
         String[] tableTypes = getTypes(metaData);
         for (AbstractExportItem item : exportItems) {
            if (!item.equals(this) && Strings.isValid(item.getSource())) {
               processMetaData(appendable, metaData, tableTypes, item.getSource());
               if (item instanceof RelationalExportItemWithType) {
                  AbstractExportItem typeItem = ((RelationalExportItemWithType) item).getTypeItem();
                  processMetaData(appendable, metaData, tableTypes, typeItem.getSource());
               }
            }
         }
      } finally {
         ExportImportXml.closeXmlNode(appendable, ExportImportXml.METADATA);
      }
   }

   private String[] getTypes(DatabaseMetaData metaData) throws SQLException {
      List<String> toReturn = new ArrayList<String>();
      ResultSet resultSet = null;
      try {
         resultSet = metaData.getTableTypes();
         if (resultSet != null) {
            while (resultSet.next()) {
               toReturn.add(resultSet.getString("TABLE_TYPE"));
            }
         }
      } finally {
         if (resultSet != null) {
            resultSet.close();
         }
      }
      return toReturn.toArray(new String[toReturn.size()]);
   }

   private void processMetaData(Appendable appendable, DatabaseMetaData metaData, String[] tableTypes, String targetTable) throws Exception {
      ResultSet resultSet = null;
      try {
         resultSet = metaData.getTables(null, null, "OSEE%", tableTypes);
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

   private void processColumnMetaData(Appendable appendable, DatabaseMetaData metaData, String schema, String tableName) throws SQLException, OseeDataStoreException, IOException {
      ResultSet resultSet = null;
      try {
         try {
            resultSet = metaData.getColumns(null, schema, tableName, null);
         } catch (SQLException ex) {
            resultSet = metaData.getColumns(null, null, tableName, null);
         }
         if (resultSet != null) {
            SupportedDatabase dbType = SupportedDatabase.getDatabaseType(metaData.getConnection());
            while (resultSet.next()) {
               ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.COLUMN);
               try {
                  String columnId = resultSet.getString("COLUMN_NAME").toLowerCase();
                  ExportImportXml.addXmlAttribute(appendable, ExportImportXml.ID, columnId);

                  int dataType = resultSet.getInt("DATA_TYPE");
                  if (dbType.equals(SupportedDatabase.foxpro)) {
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
