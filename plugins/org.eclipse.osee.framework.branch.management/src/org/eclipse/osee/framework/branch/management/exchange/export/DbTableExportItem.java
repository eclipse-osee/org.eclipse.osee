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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeDb;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.branch.management.exchange.OseeServices;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public class DbTableExportItem extends AbstractDbExportItem {
   private final String query;
   private final StringBuilder binaryContentBuffer = new StringBuilder();
   private final StringBuilder stringContentBuffer = new StringBuilder();
   private final StringBuilder oseeCommentBuffer = new StringBuilder();
   private final StringBuilder branchNameBuffer = new StringBuilder();
   private final StringBuilder rationaleBuffer = new StringBuilder();
   private final OseeServices services;

   public DbTableExportItem(OseeServices services, ExportItem id, String query) {
      super(id);
      this.services = services;
      this.query = query;
   }

   protected String exportBinaryDataTo(File tempFolder, String uriTarget) throws OseeCoreException, IOException {
      tempFolder = new File(tempFolder + File.separator + ExportImportXml.RESOURCE_FOLDER_NAME);
      if (tempFolder.exists() != true) {
         tempFolder.mkdirs();
      }

      IResourceLocator locator = services.getResourceLocatorManager().getResourceLocator(uriTarget);
      IResource resource = services.getResourceManager().acquire(locator, new PropertyStore());

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
      } finally {
         Lib.close(sourceStream);
         Lib.close(outputStream);
      }
      return locator.getRawPath().replace('/', '\\');
   }

   @Override
   protected void doWork(Appendable appendable) throws Exception {
      IOseeStatement chStmt = services.getDatabaseService().getStatement();
      try {
         Pair<String, Object[]> sqlData = ExchangeDb.getQueryWithOptions(query, getJoinQueryId(), getOptions());
         chStmt.runPreparedQuery(10000, sqlData.getFirst(), sqlData.getSecond());
         while (chStmt.next()) {
            processData(appendable, chStmt);
         }
      } finally {
         chStmt.close();
      }
   }

   private void processData(Appendable appendable, IOseeStatement chStmt) throws Exception {
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.ENTRY);

      try {
         int numberOfColumns = chStmt.getColumnCount();
         for (int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex++) {
            String columnName = chStmt.getColumnName(columnIndex).toLowerCase();
            Object value = chStmt.getObject(columnIndex);
            if (columnName.equals("uri")) {
               handleBinaryContent(binaryContentBuffer, value);
            } else if (columnName.equals("value")) {
               handleStringContent(stringContentBuffer, value, ExportImportXml.STRING_CONTENT);
            } else if (columnName.equals(ExportImportXml.OSEE_COMMENT)) {
               handleStringContent(oseeCommentBuffer, value, columnName);
            } else if (columnName.equals(ExportImportXml.BRANCH_NAME)) {
               handleStringContent(branchNameBuffer, value, columnName);
            } else if (columnName.equals(ExportImportXml.RATIONALE)) {
               handleStringContent(rationaleBuffer, value, columnName);
            } else if (columnName.equals(ExportImportXml.ART_TYPE_ID)) {
               handleTypeId(appendable, value, services.getCachingService().getArtifactTypeCache());
            } else if (columnName.equals(ExportImportXml.ATTR_TYPE_ID)) {
               handleTypeId(appendable, value, services.getCachingService().getAttributeTypeCache());
            } else if (columnName.equals(ExportImportXml.REL_TYPE_ID)) {
               handleTypeId(appendable, value, services.getCachingService().getRelationTypeCache());
            } else {
               if (value instanceof Timestamp) {
                  Timestamp timestamp = (Timestamp) value;
                  ExportImportXml.addXmlAttribute(appendable, columnName, timestamp);
               } else {
                  try {
                     ExportImportXml.addXmlAttribute(appendable, columnName, value);
                  } catch (Exception ex) {
                     throw new Exception(String.format("Unable to convert [%s] of raw type [%s] to string.",
                        columnName, chStmt.getColumnTypeName(columnIndex)), ex);
                  }
               }
            }
         }
      } finally {
         if (binaryContentBuffer.length() > 0 || stringContentBuffer.length() > 0 || oseeCommentBuffer.length() > 0 || branchNameBuffer.length() > 0 || rationaleBuffer.length() > 0) {
            ExportImportXml.endOpenedPartialXmlNode(appendable);
            if (binaryContentBuffer.length() > 0) {
               appendable.append(binaryContentBuffer);
               binaryContentBuffer.delete(0, binaryContentBuffer.length());
            }
            if (stringContentBuffer.length() > 0) {
               appendable.append(stringContentBuffer);
               stringContentBuffer.delete(0, stringContentBuffer.length());
            }
            if (oseeCommentBuffer.length() > 0) {
               appendable.append(oseeCommentBuffer);
               oseeCommentBuffer.delete(0, oseeCommentBuffer.length());
            }
            if (branchNameBuffer.length() > 0) {
               appendable.append(branchNameBuffer);
               branchNameBuffer.delete(0, branchNameBuffer.length());
            }
            if (rationaleBuffer.length() > 0) {
               appendable.append(rationaleBuffer);
               rationaleBuffer.delete(0, rationaleBuffer.length());
            }
            ExportImportXml.closeXmlNode(appendable, ExportImportXml.ENTRY);
         } else {
            ExportImportXml.closePartialXmlNode(appendable);
         }
      }
   }

   private void handleBinaryContent(Appendable appendable, Object value) throws OseeCoreException, IOException {
      String uriData = (String) value;
      if (Strings.isValid(uriData)) {
         uriData = exportBinaryDataTo(getWriteLocation(), uriData);
         ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.BINARY_CONTENT);
         ExportImportXml.addXmlAttribute(appendable, "location", uriData);
         ExportImportXml.closePartialXmlNode(appendable);
      }
   }

   private void handleTypeId(Appendable appendable, Object value, AbstractOseeCache<?> cache) throws IOException, OseeCoreException {
      int typeId = ((BigDecimal) value).intValueExact();
      String guid = cache.getById(typeId).getGuid();
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.TYPE_GUID, guid);
   }

   private void handleStringContent(Appendable appendable, Object value, String tag) throws IOException {
      String stringValue = (String) value;
      if (Strings.isValid(stringValue)) {
         ExportImportXml.openXmlNodeNoNewline(appendable, tag);
         Xml.writeAsCdata(appendable, stringValue);
         ExportImportXml.closeXmlNode(appendable, tag);
      }
   }
}
