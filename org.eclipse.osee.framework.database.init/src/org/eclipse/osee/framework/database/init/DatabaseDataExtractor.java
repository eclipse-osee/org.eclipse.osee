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

package org.eclipse.osee.framework.database.init;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.database.init.TableElement.ColumnFields;
import org.eclipse.osee.framework.database.init.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.database.init.TableElement.TableTags;
import org.eclipse.osee.framework.database.init.internal.DatabaseInitActivator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseDataExtractor {

   private static final String SQL_WILD_QUERY = "SELECT * FROM ";
   private DatabaseSchemaExtractor databaseInfo;
   private final Set<String> schemas;
   private final File directory;
   private final List<Thread> workerThreads;
   private final Set<String> extractTables;

   private class ColumnInfo {
      String name;
      SQL3DataType type;
   }

   public DatabaseDataExtractor(Set<String> schemas, File directory) throws OseeDataStoreException {
      this.schemas = schemas;
      this.directory = directory;
      this.workerThreads = new ArrayList<Thread>();
      this.extractTables = new TreeSet<String>();
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
      private final TableElement table;

      public DataExtractorThread(TableElement table) {
         this.table = table;
         setName(table.getName() + " Extractor");
      }

      @Override
      public void run() {
         IOseeStatement chStmt = null;
         OutputStream outputStream = null;
         try {
            chStmt = ConnectionHandler.getStatement();
            String fileName = table.getFullyQualifiedTableName() + DbConfigFileInformation.getDbDataFileExtension();
            outputStream = new BufferedOutputStream(new FileOutputStream(new File(directory, fileName)));

            try {
               chStmt.runPreparedQuery(SQL_WILD_QUERY + table.getFullyQualifiedTableName());
            } catch (OseeDataStoreException ex) {
               chStmt.runPreparedQuery(SQL_WILD_QUERY + table.getName());
            }

            buildXml(chStmt, table, outputStream);
         } catch (Exception ex) {
            OseeLog.log(DatabaseInitActivator.class, Level.SEVERE,
                  "Error Processing Table [ " + table.getSchema() + "." + table.getName() + " ] Data ", ex);
         } finally {
            if (chStmt != null) {
               chStmt.close();
            }
            if (outputStream != null) {
               try {
                  outputStream.close();
               } catch (IOException ex) {
                  // Do Nothing
               }
            }
         }
      }
   }

   public void waitForWorkerThreads() {
      for (Thread worker : workerThreads) {
         try {
            worker.join();
         } catch (InterruptedException ex) {
            OseeLog.log(DatabaseInitActivator.class, Level.SEVERE,
                  "Thread [" + worker.getName() + "] was Interrupted. ", ex);
         }
      }
   }

   private void extractData() throws OseeDataStoreException {
      databaseInfo = new DatabaseSchemaExtractor(schemas);
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

   private void buildXml(IOseeStatement chStmt, TableElement table, OutputStream outputStream) throws Exception {
      ArrayList<ColumnInfo> columns = new ArrayList<ColumnInfo>();
      int numberOfColumns = chStmt.getColumnCount();
      for (int index = 1; index <= numberOfColumns; index++) {
         ColumnInfo columnInfo = new ColumnInfo();
         columnInfo.name = chStmt.getColumnName(index);
         columnInfo.name = columnInfo.name.toUpperCase();

         int dataType = chStmt.getColumnType(index);
         if (chStmt.isDatabaseType(SupportedDatabase.foxpro)) {
            if (dataType == Types.CHAR) {
               dataType = Types.VARCHAR;
            }
         }
         columnInfo.type = SQL3DataType.get(dataType);
         columns.add(columnInfo);
      }

      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream);
      writer.writeStartDocument("UTF-8", "1.0");
      writer.writeStartElement(TableTags.Table.name());
      writer.writeAttribute(TableDescriptionFields.schema.name(), table.getSchema());
      writer.writeAttribute(TableDescriptionFields.name.name(), table.getName());

      for (ColumnInfo info : columns) {
         writer.writeStartElement(TableTags.ColumnInfo.name());
         writer.writeAttribute(ColumnFields.id.name(), info.name);
         writer.writeAttribute(ColumnFields.type.name(), info.type.name());
         writer.writeEndElement();
      }

      while (chStmt.next()) {
         writer.writeStartElement(TableTags.Row.name());
         for (ColumnInfo column : columns) {
            String columnValue;
            switch (column.type) {
               case BIGINT:
                  BigDecimal bigD = chStmt.getBigDecimal(column.name);
                  columnValue = bigD != null ? bigD.toString() : "";
                  break;
               case DATE:
                  Date date = chStmt.getDate(column.name);
                  columnValue = date != null ? date.toString() : "";
                  break;
               case TIME:
                  Time time = chStmt.getTime(column.name);
                  columnValue = time != null ? time.toString() : "";
                  break;
               case TIMESTAMP:
                  Timestamp timestamp = chStmt.getTimestamp(column.name);
                  columnValue = timestamp != null ? timestamp.toString() : "";
                  break;
               default:
                  columnValue = chStmt.getString(column.name);
                  columnValue = handleSpecialCharacters(columnValue);
                  break;
            }
            writer.writeAttribute(column.name, (columnValue != null ? columnValue : ""));
         }
         writer.writeEndElement();
      }
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
   }

   private String handleSpecialCharacters(String value) {
      // \0 An ASCII 0 (NUL) character.
      // '' A single quote (ï¿½'ï¿½) character.
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
}