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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.sql.Types;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeDb;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class RelationalExportItem extends AbstractDbExportItem {
   private final String query;
   private final StringBuffer binaryContentBuffer;
   private final StringBuffer stringContentBuffer;
   private final StringBuffer oseeCommentBuffer;
   private final StringBuffer branchNameBuffer;
   private final StringBuffer rationaleBuffer;

   public RelationalExportItem(ExportItem id, String query) {
      super(id);
      this.query = query;
      this.binaryContentBuffer = new StringBuffer();
      this.stringContentBuffer = new StringBuffer();
      this.oseeCommentBuffer = new StringBuffer();
      this.branchNameBuffer = new StringBuffer();
      this.rationaleBuffer = new StringBuffer();
   }

   protected String exportBinaryDataTo(File tempFolder, String uriTarget) throws Exception {
      tempFolder = new File(tempFolder + File.separator + ExportImportXml.RESOURCE_FOLDER_NAME);
      if (tempFolder.exists() != true) {
         tempFolder.mkdirs();
      }

      IResourceLocator locator =
            Activator.getInstance().getResourceLocatorManager().getResourceLocator(uriTarget);
      IResource resource = Activator.getInstance().getResourceManager().acquire(locator, new Options());

      File target = new File(tempFolder, locator.getRawPath());
      if (target.getParentFile() != null) {
         target.getParentFile().mkdirs();
      }

      InputStream sourceStream = null;
      OutputStream outputStream = null;
      try {
         sourceStream = resource.getContent();
         outputStream = new FileOutputStream(target);
         Lib.inputStreamToOutputStream(sourceStream, outputStream);
      } catch (Exception ex) {
         throw new Exception(String.format("Error processing: [%s]", locator.getRawPath()), ex);
      } finally {
         if (sourceStream != null) {
            try {
               sourceStream.close();
            } catch (Exception ex) {
            }
         }
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (Exception ex) {
            }
         }
      }
      return locator.getRawPath().replace('/', '\\');
   }

   @Override
   protected void doWork(Appendable appendable) throws Exception {
      IOseeStatement chStmt = ConnectionHandler.getStatement(getConnection());
      try {
         Pair<String, Object[]> sqlData = ExchangeDb.getQueryWithOptions(this.query, getJoinQueryId(), getOptions());
         chStmt.runPreparedQuery(sqlData.getFirst(), sqlData.getSecond());
         while (chStmt.next()) {
            processData(appendable, chStmt);
         }
      } finally {
         chStmt.close();
      }
   }

   protected void processData(Appendable appendable, IOseeStatement chStmt) throws Exception {
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.ENTRY);
      try {
         int numberOfColumns = chStmt.getColumnCount() + 1;
         for (int columnIndex = 1; columnIndex < numberOfColumns; columnIndex++) {
            String name = chStmt.getColumnName(columnIndex).toLowerCase();
            if (name.equals("uri")) {
               handleBinaryContent(binaryContentBuffer, getWriteLocation(), chStmt.getString(name));
            } else if (name.equals("value")) {
               handleStringContent(stringContentBuffer, getWriteLocation(), chStmt.getString(name),
                     ExportImportXml.STRING_CONTENT);
            } else if (name.equals(ExportImportXml.OSEE_COMMENT)) {
               handleStringContent(oseeCommentBuffer, getWriteLocation(), chStmt.getString(name),
                     ExportImportXml.OSEE_COMMENT);
            } else if (name.equals(ExportImportXml.BRANCH_NAME)) {
               handleStringContent(branchNameBuffer, getWriteLocation(), chStmt.getString(name),
                     ExportImportXml.BRANCH_NAME);
            } else if (name.equals(ExportImportXml.RATIONALE)) {
               handleStringContent(rationaleBuffer, getWriteLocation(), chStmt.getString(name),
                     ExportImportXml.RATIONALE);
            } else if (name.equals(ExportImportXml.ART_TYPE_ID) || name.equals(ExportImportXml.ATTR_TYPE_ID) || name.equals(ExportImportXml.REL_TYPE_ID)) {
               ExportImportXml.addXmlAttribute(appendable, ExportImportXml.TYPE_GUID, chStmt.getString(name));
            } else {
               switch (chStmt.getColumnType(columnIndex)) {
                  case Types.TIMESTAMP:
                     Timestamp timestamp = chStmt.getTimestamp(name);
                     ExportImportXml.addXmlAttribute(appendable, name, timestamp);
                     break;
                  default:
                     try {
                        String value = chStmt.getString(name);
                        ExportImportXml.addXmlAttribute(appendable, name, value);
                     } catch (Exception ex) {
                        throw new Exception(String.format("Unable to convert [%s] of raw type [%s] to string.", name,
                              chStmt.getColumnTypeName(columnIndex)), ex);
                     }
                     break;
               }
            }
         }
      } finally {
         if (binaryContentBuffer.length() > 0 || stringContentBuffer.length() > 0 || oseeCommentBuffer.length() > 0 || branchNameBuffer.length() > 0 || rationaleBuffer.length() > 0) {
            ExportImportXml.endOpenedPartialXmlNode(appendable);
            if (binaryContentBuffer.length() > 0) {
               appendable.append(binaryContentBuffer.toString());
               binaryContentBuffer.delete(0, binaryContentBuffer.length());
            }
            if (stringContentBuffer.length() > 0) {
               appendable.append(stringContentBuffer.toString());
               stringContentBuffer.delete(0, stringContentBuffer.length());
            }
            if (oseeCommentBuffer.length() > 0) {
               appendable.append(oseeCommentBuffer.toString());
               oseeCommentBuffer.delete(0, oseeCommentBuffer.length());
            }
            if (branchNameBuffer.length() > 0) {
               appendable.append(branchNameBuffer.toString());
               branchNameBuffer.delete(0, branchNameBuffer.length());
            }
            if (rationaleBuffer.length() > 0) {
               appendable.append(rationaleBuffer.toString());
               rationaleBuffer.delete(0, rationaleBuffer.length());
            }
            ExportImportXml.closeXmlNode(appendable, ExportImportXml.ENTRY);
         } else {
            ExportImportXml.closePartialXmlNode(appendable);
         }
      }
   }

   private void handleBinaryContent(Appendable appendable, File tempFolder, String uriData) throws Exception {
      if (Strings.isValid(uriData)) {
         uriData = exportBinaryDataTo(tempFolder, uriData);
         ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.BINARY_CONTENT);
         ExportImportXml.addXmlAttribute(appendable, "location", uriData);
         ExportImportXml.closePartialXmlNode(appendable);
      }
   }

   private void handleStringContent(Appendable appendable, File tempFolder, String stringValue, String tag) throws Exception {
      if (Strings.isValid(stringValue)) {
         ExportImportXml.openXmlNodeNoNewline(appendable, tag);
         Xml.writeAsCdata(appendable, stringValue);
         ExportImportXml.closeXmlNode(appendable, tag);
      }
   }

   @Override
   public void cleanUp() {
      super.cleanUp();
   }
}
