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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.exchange.TranslationManager;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseDbSaxHandler extends BaseExportImportSaxHandler {

   private final List<Object[]> data;
   private final int cacheLimit;
   private final boolean isCacheAll;

   private OseeConnection connection;
   private MetaData metadata;
   private TranslationManager translator;
   private PropertyStore options;
   private final IOseeDatabaseService service;

   protected BaseDbSaxHandler(IOseeDatabaseService service, boolean isCacheAll, int cacheLimit) {
      super();
      if (cacheLimit < 0) {
         throw new IllegalArgumentException(String.format("Cache limit cannot be less than zero - cacheLimit=[%d]",
            cacheLimit));
      }
      this.service = service;
      this.options = new PropertyStore();
      this.translator = null;
      this.metadata = null;
      this.connection = null;
      this.isCacheAll = isCacheAll;
      this.cacheLimit = cacheLimit;
      this.data = new ArrayList<Object[]>();
   }

   public void setOptions(PropertyStore options) {
      if (options != null) {
         this.options = options;
      }
   }

   protected PropertyStore getOptions() {
      return this.options;
   }

   public void setMetaData(MetaData metadata) {
      this.metadata = metadata;
   }

   public void setConnection(OseeConnection connection) {
      this.connection = connection;
   }

   public void setTranslator(TranslationManager translator) {
      this.translator = translator;
   }

   protected OseeConnection getConnection() {
      return this.connection;
   }

   protected MetaData getMetaData() {
      return this.metadata;
   }

   protected TranslationManager getTranslator() {
      return this.translator;
   }

   public boolean isStorageNeeded() {
      return !isCacheAll && data.size() > cacheLimit;
   }

   protected void addData(Object[] objects) {
      this.data.add(objects);
   }

   protected void store(OseeConnection connection) throws OseeCoreException {
      if (!data.isEmpty()) {
         getDatabaseService().runBatchUpdate(connection, getMetaData().getQuery(), data);
         data.clear();
      }
   }

   private boolean isTruncateSupported() throws OseeCoreException {
      boolean isTruncateSupported = false;
      DatabaseMetaData metaData = connection.getMetaData();
      ResultSet resultSet = null;
      try {
         resultSet = metaData.getTablePrivileges(null, null, getMetaData().getTableName().toUpperCase());
         while (resultSet.next()) {
            String value = resultSet.getString("PRIVILEGE");
            if ("TRUNCATE".equalsIgnoreCase(value)) {
               isTruncateSupported = true;
               break;
            }
         }
      } catch (SQLException ex1) {
         OseeLog.log(Activator.class, Level.INFO, ex1);
      } finally {
         if (resultSet != null) {
            try {
               resultSet.close();
            } catch (SQLException ex) {
               // Do Nothing
            }
         }
      }
      return isTruncateSupported;
   }

   public void clearDataTable() throws OseeCoreException {
      String cmd = isTruncateSupported() ? "TRUNCATE TABLE" : "DELETE FROM";
      String deleteSql = String.format("%s %s", cmd, getMetaData().getTableName());
      try {
         getDatabaseService().runPreparedUpdate(connection, deleteSql);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.INFO, ex, "Error clearing: %s", deleteSql);
         throw ex;
      }
   }

   protected IOseeDatabaseService getDatabaseService() {
      return service;
   }

   public void reset() {
      this.connection = null;
      this.translator = null;
      this.options = null;
      this.metadata = null;
      this.data.clear();
   }
}
