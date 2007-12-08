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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.db.DbConfigFileInformation;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.sql.manager.SqlManager;
import org.eclipse.osee.framework.ui.plugin.util.db.data.ColumnDbData;
import org.eclipse.osee.framework.ui.plugin.util.db.data.ColumnMetadata;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement.TableTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseDataImporter {
   private Connection connection;
   private File directory;
   private Logger logger;
   private SqlManager sqlManager;
   private List<String> tableOrder;
   private String schemaToImportTo;
   private Set<String> tableFilter;

   private class TableData extends TableElement {
      private List<List<ColumnDbData>> rowData;

      public TableData() {
         rowData = new ArrayList<List<ColumnDbData>>();
      }

      public void addRow(List<ColumnDbData> row) {
         rowData.add(row);
      }

      public List<List<ColumnDbData>> getRows() {
         return rowData;
      }
   }

   public DatabaseDataImporter(Connection connection, File directory, SqlManager sqlManager) {
      this.connection = connection;
      this.directory = directory;
      this.logger = ConfigUtil.getConfigFactory().getLogger(DatabaseDataImporter.class);
      this.sqlManager = sqlManager;
      this.tableFilter = new TreeSet<String>();
   }

   public void setImportOrder(List<String> tableOrder) {
      this.tableOrder = tableOrder;
   }

   public void setSchemaToImportTo(String schema) {
      this.schemaToImportTo = schema;
   }

   public void addToTableFilter(String fullyQualifiedTableName) {
      this.tableFilter.add(fullyQualifiedTableName);
   }

   public void clearTableFilter() {
      this.tableFilter.clear();
   }

   public List<File> orderFilesByImportOrder(Map<String, File> toOrder) {
      List<File> orderedSet = new ArrayList<File>();
      if (tableOrder != null && tableOrder.size() != 0) {
         for (String tableName : tableOrder) {
            if (toOrder.containsKey(tableName + DbConfigFileInformation.getDbDataFileExtension())) {
               orderedSet.add(toOrder.get(tableName + DbConfigFileInformation.getDbDataFileExtension()));
            }
         }
      } else {
         Set<String> keys = toOrder.keySet();
         for (String key : keys) {
            orderedSet.add(toOrder.get(key));
         }
      }
      return orderedSet;
   }

   public Map<String, File> filterDataToImport(Map<String, File> toProcess) {
      Map<String, File> filteredList = new HashMap<String, File>();
      if (tableFilter != null && tableFilter.size() != 0) {
         for (String tableName : tableFilter) {
            if (toProcess.containsKey(tableName + DbConfigFileInformation.getDbDataFileExtension())) {
               filteredList.put(tableName + DbConfigFileInformation.getDbDataFileExtension(),
                     toProcess.get(tableName + DbConfigFileInformation.getDbDataFileExtension()));
            }
         }
      } else {
         return toProcess;
      }
      return filteredList;
   }

   public void importDataIntoDatabase() {
      if (FileUtility.isValidDirectory(directory)) {
         Map<String, File> filesToProcess = getFilesToProcess();
         Map<String, File> filteredFiles = filterDataToImport(filesToProcess);
         List<File> files = orderFilesByImportOrder(filteredFiles);
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder;
         Document document;
         for (File file : files) {
            try {
               builder = factory.newDocumentBuilder();
               document = builder.parse(file);
               processData(parseXMLDbDataFile(document));
            } catch (ParserConfigurationException ex) {
               logger.log(Level.SEVERE, "Unable to Parse File. ", ex);
            } catch (Exception ex) {
               logger.log(Level.SEVERE, "Exception: \n", ex);
            }
         }
      }
   }

   private Map<String, File> getFilesToProcess() {
      Map<String, File> toReturn = new HashMap<String, File>();
      List<File> files = FileUtility.getDBDataFileList(directory);
      for (File fileName : files) {
         toReturn.put(fileName.getName(), new File(directory + File.separator + fileName.getName()));
      }
      return toReturn;
   }

   private void processData(List<TableData> tables) throws SQLException, Exception {
      if (tables.size() != 0) {
         for (TableData tableData : tables) {
            logger.log(Level.INFO, "Populating: [ " + tableData.getFullyQualifiedTableName() + "]\n");
            List<List<ColumnDbData>> rows = tableData.getRows();
            if (!rows.isEmpty()) {
               for (List<ColumnDbData> rowData : rows) {
                  sqlManager.insertData(connection, rowData, tableData);
               }
            }
         }
      }
   }

   private void parseColumnMetadata(Element tableElement, TableData tableData) {
      NodeList columnElements = tableElement.getElementsByTagName(TableTags.ColumnInfo.name());
      if (columnElements != null) {
         for (int index = 0; index < columnElements.getLength(); index++) {
            Element columnElement = (Element) columnElements.item(index);
            if (columnElement != null) {

               NamedNodeMap attributeMap = columnElement.getAttributes();
               if (attributeMap != null && attributeMap.getLength() != 0) {
                  ColumnMetadata columnMetaData = new ColumnMetadata("");
                  for (int attributeIndex = 0; attributeIndex < attributeMap.getLength(); attributeIndex++) {
                     Node node = attributeMap.item(attributeIndex);
                     String nodeName = node.getNodeName();
                     String nodeValue = node.getTextContent();
                     if (nodeName != null && !nodeName.equals("")) {
                        columnMetaData.addColumnField(ColumnFields.valueOf(nodeName),
                              (nodeValue != null ? nodeValue : ""));
                     }
                  }
                  tableData.addColumn(columnMetaData);
               }
            }
         }
      }
   }

   private void parseRowInformation(Element tableElement, TableData tableData) {
      NodeList rowElements = tableElement.getElementsByTagName(TableTags.Row.name());
      if (rowElements != null) {
         for (int rowIndex = 0; rowIndex < rowElements.getLength(); rowIndex++) {
            Element row = (Element) rowElements.item(rowIndex);
            if (row != null) {
               NamedNodeMap attributeMap = row.getAttributes();
               if (attributeMap != null && attributeMap.getLength() != 0) {
                  List<ColumnDbData> rowData = new ArrayList<ColumnDbData>();
                  tableData.addRow(rowData);
                  for (int attributeIndex = 0; attributeIndex < attributeMap.getLength(); attributeIndex++) {
                     Node node = attributeMap.item(attributeIndex);
                     String nodeName = node.getNodeName();
                     String nodeValue = node.getTextContent();
                     if (nodeName != null && !nodeName.equals("")) {
                        rowData.add(new ColumnDbData(nodeName, (nodeValue != null ? nodeValue : "")));
                     }
                  }
               }
            }
         }
      }
   }

   private List<TableData> parseXMLDbDataFile(Document document) {
      NodeList tableElements = document.getElementsByTagName(TableTags.Table.name());
      List<TableData> tables = new ArrayList<TableData>();
      for (int index = 0; index < tableElements.getLength(); index++) {
         Element tableXmlElement = (Element) tableElements.item(index);
         if (tableXmlElement != null) {
            NamedNodeMap map = tableXmlElement.getAttributes();
            if (map != null && map.getLength() != 0) {
               Node tableName = map.getNamedItem(TableDescriptionFields.name.name());
               Node tableAddress = map.getNamedItem(TableDescriptionFields.schema.name());
               String tableNameString = "";
               String tableAddressString = "";
               if (tableName != null) {
                  tableNameString = tableName.getTextContent();
               }
               if (tableAddress != null) {
                  tableAddressString = tableAddress.getTextContent();
               }

               if (tableNameString != null && !tableNameString.equals("") && tableAddressString != null && !tableAddressString.equals("")) {
                  TableData tableData = new TableData();
                  tableData.addTableDescription(TableDescriptionFields.name, tableNameString);
                  if (this.schemaToImportTo != null && !this.schemaToImportTo.equals("")) {
                     tableData.addTableDescription(TableDescriptionFields.schema, schemaToImportTo);
                  } else {
                     tableData.addTableDescription(TableDescriptionFields.schema, tableAddressString);
                  }
                  parseRowInformation(tableXmlElement, tableData);
                  parseColumnMetadata(tableXmlElement, tableData);
                  tables.add(tableData);
               }
            }
         }
      }
      return tables;
   }
}
