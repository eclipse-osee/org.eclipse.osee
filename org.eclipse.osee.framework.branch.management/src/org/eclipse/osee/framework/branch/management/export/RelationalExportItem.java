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
package org.eclipse.osee.framework.branch.management.export;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.branch.management.Activator;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class RelationalExportItem extends AbstractExportItem {
   private static final String BINARY_DATA_FOLDER = "resources";

   private String query;
   private StringBuffer binaryContentBuffer;
   private StringBuffer stringContentBuffer;
   private int joinQueryId;
   private Set<IExportColumnListener> exportColumnListeners;

   public RelationalExportItem(String name, int priority, String query) {
      super(name, priority);
      this.query = query;
      this.binaryContentBuffer = new StringBuffer();
      this.stringContentBuffer = new StringBuffer();
      this.joinQueryId = -1;
      this.exportColumnListeners = java.util.Collections.synchronizedSet(new HashSet<IExportColumnListener>());
   }

   public String getQuery() {
      StringBuffer toReturn = new StringBuffer(query);
      if (getOptions().getBoolean(ExportOptions.include_baseline_txs.name()) != true && query.contains("txd1")) {
         toReturn.append(" AND txd1.TX_TYPE = 0");
      }
      return query;
   }

   protected String exportBinaryDataTo(File tempFolder, String uriTarget) throws Exception {
      tempFolder = new File(tempFolder + File.separator + BINARY_DATA_FOLDER);
      if (tempFolder.exists() != true) {
         tempFolder.mkdirs();
      }
      int index = uriTarget.lastIndexOf("/");
      String fileName = uriTarget.substring(index + 1, uriTarget.length());

      Options options = new Options();

      IResourceLocator locator = Activator.getInstance().getResourceLocatorManager().getResourceLocator(uriTarget);
      IResource resource = Activator.getInstance().getResourceManager().acquire(locator, options);

      File target = new File(tempFolder, fileName);
      Lib.copyFile(new File(resource.getLocation()), target);
      return target.getName();
   }

   public void setJoinQueryId(int joinQueryId) {
      this.joinQueryId = joinQueryId;
   }

   public int getJoinQueryId() {
      return this.joinQueryId;
   }

   protected void doWork(Writer writer) throws Exception {
      Connection connection = null;
      ConnectionHandlerStatement stmt = null;
      try {
         connection = OseeDbConnection.getConnection();
         stmt = ConnectionHandler.runPreparedQuery(connection, getQuery(), SQL3DataType.INTEGER, getJoinQueryId());
         while (stmt.next()) {
            processData(writer, stmt.getRset());
         }
      } finally {
         DbUtil.close(stmt);
         if (connection != null) {
            connection.close();
         }
      }
   }

   protected void processData(Writer writer, ResultSet resultSet) throws Exception {
      writer.write("<entry ");
      try {
         ResultSetMetaData meta = resultSet.getMetaData();
         int numberOfColumns = meta.getColumnCount() + 1;
         for (int columnIndex = 1; columnIndex < numberOfColumns; columnIndex++) {
            String name = meta.getColumnName(columnIndex).toLowerCase();
            notifyOnColumnExport(name, resultSet);
            if (name.equals("uri")) {
               handleBinaryContent(binaryContentBuffer, getWriteLocation(), name, resultSet);
            } else if (name.equals("value")) {
               handleStringContent(stringContentBuffer, getWriteLocation(), name, resultSet);
            } else {
               switch (meta.getColumnType(columnIndex)) {
                  case Types.TIMESTAMP:
                     Timestamp timestamp = resultSet.getTimestamp(columnIndex);
                     writeXmlAttribute(writer, name, timestamp);
                     break;
                  default:
                     try {
                        String value = resultSet.getString(columnIndex);
                        writeXmlAttribute(writer, name, value);
                     } catch (Exception ex) {
                        throw new Exception(String.format("Unable to convert [%s] of raw type [%s] to string.", name,
                              meta.getColumnTypeName(columnIndex)), ex);
                     }
                     break;
               }
            }
         }
      } finally {
         if (binaryContentBuffer.length() > 0 || stringContentBuffer.length() > 0) {
            writer.write(">\n");
            if (binaryContentBuffer.length() > 0) {
               writer.write(binaryContentBuffer.toString());
               binaryContentBuffer.delete(0, binaryContentBuffer.length());
            }
            if (stringContentBuffer.length() > 0) {
               writer.write(stringContentBuffer.toString());
               stringContentBuffer.delete(0, stringContentBuffer.length());
            }
            writer.write("</entry>\n");
         } else {
            writer.write(" />\n");
         }
      }
   }

   private void writeXmlAttribute(Appendable appendable, String name, Object value) throws IOException {
      appendable.append(String.format("%s=\"%s\" ", name, value));
   }

   private void handleBinaryContent(Appendable appendable, File tempFolder, String name, ResultSet resultSet) throws Exception {
      String uriData = resultSet.getString("uri");
      if (Strings.isValid(uriData)) {
         String relativePath = exportBinaryDataTo(tempFolder, uriData);
         appendable.append(String.format("<BinaryData location=\"%s\" />\n", relativePath));
      }
   }

   private void handleStringContent(Appendable appendable, File tempFolder, String name, ResultSet resultSet) throws Exception {
      String stringValue = resultSet.getString("value");
      if (Strings.isValid(stringValue)) {
         appendable.append("<StringValue>");
         Xml.writeAsCdata(appendable, stringValue);
         appendable.append("</StringValue>\n");
      }
   }

   public void addExportColumnListener(IExportColumnListener exportColumnListener) {
      if (exportColumnListener != null) {
         this.exportColumnListeners.add(exportColumnListener);
      }
   }

   public void removeExportColumnListener(IExportColumnListener exportColumnListener) {
      if (exportColumnListener != null) {
         this.exportColumnListeners.remove(exportColumnListener);
      }
   }

   public void cleanUp() {
      this.exportColumnListeners.clear();
      setJoinQueryId(-1);
      super.cleanUp();
   }

   private void notifyOnColumnExport(String columnName, ResultSet resultSet) throws Exception {
      for (IExportColumnListener listener : this.exportColumnListeners) {
         listener.onColumnExport(columnName, resultSet);
      }
   }

   public interface IExportColumnListener {
      public abstract void onColumnExport(String columnName, ResultSet resultSet) throws Exception;
   }
}
