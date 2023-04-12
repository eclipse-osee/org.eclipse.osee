/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportSql;
import org.eclipse.osee.orcs.db.internal.exchange.ExportTableConstants;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public class DbTableSqlExportItem extends AbstractSqlExportItem {
   private final String query;
   private final Object[] bindData;

   private final IResourceManager resourceManager;

   public DbTableSqlExportItem(Log logger, JdbcClient jdbcClient, IResourceManager resourceManager, ExportItem id, String query, Object[] bindData) {
      super(logger, id, jdbcClient);
      this.resourceManager = resourceManager;
      this.query = query;
      this.bindData = bindData;
   }

   protected String exportBinaryDataTo(File tempFolder, String uriTarget) throws IOException {
      tempFolder = new File(tempFolder + File.separator + ExportTableConstants.RESOURCE_FOLDER_NAME);
      if (tempFolder.exists() != true) {
         tempFolder.mkdirs();
      }

      IResourceLocator locator = resourceManager.getResourceLocator(uriTarget);
      IResource resource = resourceManager.acquire(locator, new PropertyStore());

      File target = new File(tempFolder, locator.getRawPath());
      if (target.getParentFile() != null) {
         target.getParentFile().mkdirs();
      }

      InputStream sourceStream = null;
      OutputStream outputStream = null;
      try {
         if (resource != null) {
            sourceStream = resource.getContent();
            outputStream = new FileOutputStream(target);
            Lib.inputStreamToOutputStream(sourceStream, outputStream);
         }
      } finally {
         Lib.close(sourceStream);
         Lib.close(outputStream);
      }
      return uriTarget;
   }

   @Override
   protected void doWork(Appendable appendable) {
      this.jdbcClient.runQueryWithMaxFetchSize(stmt -> processData(appendable, stmt), query, bindData);
   }

   private void processData(Appendable appendable, JdbcStatement chStmt) {
      try {

         try {
            boolean firstAttribute = false;
            ExportImportSql.openSqlValue(appendable);
            int numberOfColumns = chStmt.getColumnCount();
            for (int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex++) {
               firstAttribute = (columnIndex == 1) ? true : false;
               String columnName = chStmt.getColumnName(columnIndex).toLowerCase();
               Object value = chStmt.getObject(columnIndex);

               if (columnName.equals("uri")) {
                  handleBinaryContent(appendable, value, firstAttribute);
               } else if (columnName.equals("value")) {
                  handleStringContent(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.OSEE_COMMENT)) {
                  handleStringContent(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.BRANCH_NAME)) {
                  handleStringContent(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.RATIONALE)) {
                  handleStringContent(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.GUID)) {
                  handleStringContent(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.ART_TYPE_ID)) {
                  handleTypeId(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.ATTR_TYPE_ID)) {
                  handleTypeId(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.REL_TYPE_ID)) {
                  handleTypeId(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.BUILD_ID)) {
                  handleTypeId(appendable, value, firstAttribute);
               } else if (columnName.equals(ExportTableConstants.TX_TYPE)) {
                  handleTypeId(appendable, value, firstAttribute);
               } else {
                  Timestamp timestamp = asTimestamp(value);
                  if (timestamp != null && firstAttribute) {
                     ExportImportSql.addFirstSqlStringAttribute(appendable, timestamp);
                  } else if (timestamp != null && !firstAttribute) {
                     ExportImportSql.addSqlStringAttribute(appendable, timestamp);
                  } else {
                     try {
                        if (firstAttribute == true) {
                           ExportImportSql.addFirstSqlAttribute(appendable, value);
                        } else {
                           ExportImportSql.addSqlAttribute(appendable, value);
                        }
                     } catch (Exception ex) {
                        throw new OseeCoreException(ex, "Unable to convert [%s] of raw type [%s] to string.",
                           columnName, chStmt.getColumnTypeName(columnIndex));
                     }
                  }
               }
            }
         } finally {
            ExportImportSql.closeSqlValue(appendable);
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Failure during %s processData", getClass().getSimpleName());
      }
   }

   private Timestamp asTimestamp(Object value) {
      Timestamp toReturn = null;
      if (value instanceof Timestamp) {
         toReturn = (Timestamp) value;
      } else if (value != null) {
         try {
            // Account for oracle driver issues
            Class<?> clazz = value.getClass();
            Method method = clazz.getMethod("timestampValue");
            Object object = method.invoke(value);
            if (object instanceof Timestamp) {
               toReturn = (Timestamp) object;
            }
         } catch (NoSuchMethodException ex) {
            // Do Nothing
         } catch (Exception ex) {
            getLogger().warn(ex, "Error converting [%s] to timestamp", value);
         }
      }
      return toReturn;
   }

   private void handleBinaryContent(Appendable appendable, Object value, boolean firstAttribute) throws IOException {
      String uriData = (String) value;
      if (Strings.isValid(uriData)) {
         uriData = exportBinaryDataTo(getWriteLocation(), uriData);
         if (firstAttribute == true) {
            ExportImportSql.addFirstSqlStringAttribute(appendable, uriData);
         } else {
            ExportImportSql.addSqlStringAttribute(appendable, uriData);
         }
      } else {
         if (firstAttribute == true) {
            appendable.append("''");
         } else {
            appendable.append(",''");
         }
      }
   }

   private void handleTypeId(Appendable appendable, Object value, boolean firstAttribute) throws IOException {
      long typeId = -1;
      if (value instanceof Short) {
         Short xShort = (Short) value;
         typeId = xShort.longValue();
      } else if (value instanceof Integer) {
         typeId = ((Integer) value).longValue();
      } else if (value instanceof Long) {
         typeId = (Long) value;
      } else if (value instanceof BigInteger) {
         typeId = ((BigInteger) value).longValue();
      } else if (value instanceof BigDecimal) {
         typeId = ((BigDecimal) value).longValue();
      } else {
         throw new OseeCoreException("Undefined Type [%s]", value != null ? value.getClass().getSimpleName() : value);
      }
      String uuidString = String.valueOf(typeId);
      if (firstAttribute == true) {
         ExportImportSql.addFirstSqlAttribute(appendable, uuidString);
      } else {
         ExportImportSql.addSqlAttribute(appendable, uuidString);
      }
   }

   private void handleStringContent(Appendable appendable, Object value, boolean firstAttribute) throws IOException {
      String stringValue = (String) value;
      if (Strings.isValid(stringValue)) {
         if (firstAttribute == true) {
            ExportImportSql.addFirstSqlStringAttribute(appendable, stringValue);
         } else {
            ExportImportSql.addSqlStringAttribute(appendable, stringValue);
         }
      } else {
         if (firstAttribute == true) {
            appendable.append("''");
         } else {
            appendable.append(",''");
         }
      }
   }

   public static String escapeSql(String value) {
      if (!Strings.isValid(value)) {
         throw new OseeArgumentException("Invalid String given to escapeSql");
      }
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         switch (c) {
            case '\'':
               result.append("''");
               break;
            case '\\':
               result.append("\\\\");
               break;
            case '\r':
               result.append("\\r");
               break;
            case '\n':
               result.append("\\n");
               break;
            case '\t':
               result.append("\\t");
               break;
            case '\b':
               result.append("\\b");
               break;
            case '\f':
               result.append("\\f");
               break;
            default:
               result.append(c);
         }
      }
      return result.toString();
   }
}
