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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;

/**
 * @author Roberto E. Escobar
 */
public class RelationalSaxHandler extends BaseExportImportSaxHandler {
   private List<Object[]> data = new ArrayList<Object[]>();
   private int cacheLimit;
   private Connection connection;
   private MetaData metadata;
   private boolean isCacheAll;

   public static RelationalSaxHandler newCacheAllDataRelationalSaxHandler() {
      return new RelationalSaxHandler(true, 0);
   }

   public static RelationalSaxHandler newLimitedCacheRelationalSaxHandler(int cacheLimit) {
      return new RelationalSaxHandler(false, cacheLimit);
   }

   protected RelationalSaxHandler(boolean isCacheAll, int cacheLimit) {
      super();
      if (cacheLimit < 0) {
         throw new IllegalArgumentException(String.format("Cache limit cannot be less than zero - cacheLimit=[%d]",
               cacheLimit));
      }
      this.metadata = null;
      this.connection = null;
      this.isCacheAll = isCacheAll;
      this.cacheLimit = cacheLimit;
      this.data = new ArrayList<Object[]>();
   }

   public void setMetaData(MetaData metadata) {
      this.metadata = metadata;
   }

   public void setConnection(Connection connection) {
      this.connection = connection;
   }

   protected Connection getConnection() {
      return this.connection;
   }

   protected MetaData getMetaData() {
      return this.metadata;
   }

   public void reset() {
      this.metadata = null;
      this.connection = null;
      this.data.clear();
   }

   @Override
   protected void processData(Map<String, String> fieldMap) {
      System.out.println(String.format("Table: [%s] Data: %s ", getMetaData(), fieldMap));
      Object[] objectData = getMetaData().toColumnsToObjectArray(fieldMap);
      if (objectData != null) {
         this.data.add(objectData);
         if (this.isCacheAll != true && this.data.size() > this.cacheLimit) {
            try {
               store();
            } catch (SQLException ex) {
               throw new IllegalStateException("Error inserting data.", ex);
            }
         }
      }
   }

   public void store() throws SQLException {
      if (this.data.isEmpty() != true) {
         ConnectionHandler.runPreparedUpdate(getConnection(), getMetaData().getQuery(), this.data);
         this.data.clear();
      }
   }
}
