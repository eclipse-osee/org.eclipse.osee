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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.database.data.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.database.data.TableElement.TableTags;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.db.DbConfigFileInformation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseDataExtractor {

   private static final String SQL_WILD_QUERY = "SELECT * FROM ";
   private OseeConnection connection;
   private DatabaseSchemaExtractor databaseInfo;
   private Set<String> schemas;
   private File directory;
   private List<Thread> workerThreads;
   private Set<String> extractTables;
   private SupportedDatabase dbType;

   private class ColumnInfo {
      String name;
      SQL3DataType type;
   }

   public DatabaseDataExtractor(OseeConnection connection, Set<String> schemas, File directory) throws OseeDataStoreException {
      this.connection = connection;
      this.schemas = schemas;
      this.directory = directory;
      this.workerThreads = new ArrayList<Thread>();
      this.extractTables = new TreeSet<String>();
      this.dbType = SupportedDatabase.getDatabaseType(connection);
   }

   public void addTableNameToExtract(String fullyQualifiedTableName) {
      this.extractTables.add(fullyQualifiedTableName);
   }

   public void clearFilter() {
      this.extractTables.clear();
   }

   public void extract() throws OseeDataStoreException {
      FileUtility.setupDirectoryForWrite(directory);
      extractData();
   }

   private class DataExtractorThread extends Thread {
      private TableElement table;

      public DataExtractorThread(TableElement table) {
         this.table = table;
         setName(table.getName() + " Extractor");
      }

      public void run() {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
         try {
            try {
               chStmt.runPreparedQuery(SQL_WILD_QUERY + table.getFullyQualifiedTableName());
            } catch (OseeDataStoreException ex) {
               chStmt.runPreparedQuery(SQL_WILD_QUERY + table.getName());
            }

            Document document = buildXml(chStmt, table);
            if (document != null) {
               writeDocumentToFile(document, table.getFullyQualifiedTableName());
            }
         } catch (Exception ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE,
                  "Error Processing Table [ " + table.getSchema() + "." + table.getName() + " ] Data ", ex);
         } finally {
            chStmt.close();
         }
      }
   }

   public void waitForWorkerThreads() {
      for (Thread worker : workerThreads) {
         try {
            worker.join();
         } catch (InterruptedException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, "Thread [" + worker.getName() + "] was Interrupted. ",
                  ex);
         }
      }
   }

   private void extractData() throws OseeDataStoreException {
      databaseInfo = new DatabaseSchemaExtractor(connection, schemas);
      databaseInfo.extractSchemaData();
      Map<String, SchemaData> schemaDataMap = databaseInfo.getSchemas();
      Set<String> schemaKeys = schemaDataMap.keySet();
      for (String schema : schemaKeys) {
         SchemaData schemaData = schemaDataMap.get(schema);

         List<TableElement> tables = schemaData.getTablesOrderedByDependency();
         for (TableElement table : tables) {

            boolean extract = true;
            // only extract items in filter since filter was set with data
            if (this.extractTables != null && this.extractTables.size() > 0) {
               extract = extractTables.contains(table.getFullyQualifiedTableName());
            }

            if (extract) {
               DataExtractorThread workerThread = new DataExtractorThread(table);
               workerThreads.add(workerThread);
               workerThread.start();
            }
         }
      }
   }

   private Document buildXml(ConnectionHandlerStatement chStmt, TableElement table) throws OseeDataStoreException {
      ArrayList<ColumnInfo> columns = new ArrayList<ColumnInfo>();
      int numberOfColumns = chStmt.getColumnCount();
      for (int index = 1; index <= numberOfColumns; index++) {
         ColumnInfo columnInfo = new ColumnInfo();
         columnInfo.name = chStmt.getColumnName(index);
         columnInfo.name = columnInfo.name.toUpperCase();

         int dataType = chStmt.getColumnType(index);
         if (dbType.equals(SupportedDatabase.foxpro)) {
            if (dataType == Types.CHAR) {
               dataType = Types.VARCHAR;
            }
         }
         columnInfo.type = SQL3DataType.get(dataType);
         columns.add(columnInfo);
      }

      Document xmlDoc = new DocumentImpl();
      Element rootElement = xmlDoc.createElement(TableTags.Table.name());
      xmlDoc.appendChild(rootElement);
      rootElement.setAttribute(TableDescriptionFields.schema.name(), table.getSchema());
      rootElement.setAttribute(TableDescriptionFields.name.name(), table.getName());

      for (ColumnInfo info : columns) {
         Element columnInfo = xmlDoc.createElement(TableTags.ColumnInfo.name());
         rootElement.appendChild(columnInfo);
         columnInfo.setAttribute(ColumnFields.id.name(), info.name);
         columnInfo.setAttribute(ColumnFields.type.name(), info.type.name());
      }

      while (chStmt.next()) {
         Element columnElement = xmlDoc.createElement(TableTags.Row.name());
         for (ColumnInfo column : columns) {
            String columnValue;
            switch (column.type) {
               case BIGINT:
                  BigDecimal bigD = chStmt.getBigDecimal(column.name);
                  columnValue = (bigD != null ? bigD.toString() : "");
                  break;
               case DATE:
                  Date date = chStmt.getDate(column.name);
                  columnValue = (date != null ? date.toString() : "");
                  break;
               case TIME:
                  Time time = chStmt.getTime(column.name);
                  columnValue = (time != null ? time.toString() : "");
                  break;
               case TIMESTAMP:
                  Timestamp timestamp = chStmt.getTimestamp(column.name);
                  columnValue = (timestamp != null ? timestamp.toString() : "");
                  break;
               default:
                  columnValue = chStmt.getString(column.name);
                  columnValue = handleSpecialCharacters(columnValue);
                  break;
            }
            columnElement.setAttribute(column.name, (columnValue != null ? columnValue : ""));
         }
         rootElement.appendChild(columnElement);
      }
      return xmlDoc;
   }

   private String handleSpecialCharacters(String value) {
      // \0 An ASCII 0 (NUL) character.
      // '' A single quote (�'�) character.
      // \b A backspace character.
      // \n A newline (linefeed) character.
      // \r A carriage return character.
      // \t A tab character.
      // \Z ASCII 26 (Control-Z). See note following the table.

      if (value != null) {

         value = value.replaceAll("\0", "");
         value = value.replaceAll("'", "''");
         // value = value.replaceAll("\"", "\\\\\""); No need to do this.
         Pattern pattern =
               Pattern.compile("[^" + "a-zA-Z0-9" + "!@#$%\\^&*\\(\\)" + "+ _.-=" + "\'\"<>{}\\[\\]|:;,\n\r\t\b?/`~\\\\]+");
         Matcher matcher = pattern.matcher(value);

         while (matcher.find()) {
            // System.out.println("Matcher: [" + matcher.group() + "]");
            value = value.replace(matcher.group(), "");
         }
      }
      return value;
   }

   private void writeDocumentToFile(Document doc, String tableName) throws IOException {
      String fileString = directory + File.separator + tableName + DbConfigFileInformation.getDbDataFileExtension();
      OutputFormat outputFormat;
      OutputStreamWriter out = null;
      try {
         OutputStream bout = new BufferedOutputStream(new FileOutputStream(fileString));
         out = new OutputStreamWriter(bout);

         outputFormat = new OutputFormat("XML", "UTF-8", true);
         XMLSerializer xmlSerializer = new XMLSerializer(out, outputFormat);
         xmlSerializer.serialize(doc.getDocumentElement());
      } catch (FileNotFoundException ex) {
         OseeLog.log(DatabaseActivator.class, Level.SEVERE, "File error [" + fileString + "] ", ex);
      } catch (IOException ex) {
         OseeLog.log(DatabaseActivator.class, Level.SEVERE, "Error writing to File [" + fileString + "] ", ex);
      } finally {
         out.flush();
         out.close();
      }
   }
}