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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.exchange.TranslationManager;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseDbSaxHandler extends BaseExportImportSaxHandler {

   private final List<Object[]> data;
   private final int cacheLimit;
   private final boolean isCacheAll;

   private Connection connection;
   private MetaData metadata;
   private TranslationManager translator;
   private Options options;

   protected BaseDbSaxHandler(boolean isCacheAll, int cacheLimit) {
      super();
      if (cacheLimit < 0) {
         throw new IllegalArgumentException(String.format("Cache limit cannot be less than zero - cacheLimit=[%d]",
               cacheLimit));
      }
      this.options = new Options();
      this.translator = null;
      this.metadata = null;
      this.connection = null;
      this.isCacheAll = isCacheAll;
      this.cacheLimit = cacheLimit;
      this.data = new ArrayList<Object[]>();
   }

   public void setOptions(Options options) {
      if (options != null) {
         this.options = options;
      }
   }

   protected Options getOptions() {
      return this.options;
   }

   public void setMetaData(MetaData metadata) {
      this.metadata = metadata;
   }

   public void setConnection(Connection connection) {
      this.connection = connection;
   }

   public void setTranslator(TranslationManager translator) {
      this.translator = translator;
   }

   protected Connection getConnection() {
      return this.connection;
   }

   protected MetaData getMetaData() {
      return this.metadata;
   }

   protected TranslationManager getTranslator() {
      return this.translator;
   }

   public boolean isStorageNeeded() {
      return this.isCacheAll != true && this.data.size() > this.cacheLimit;
   }

   protected void addData(Object[] objects) {
      this.data.add(objects);
   }

   protected void store(Connection connection) throws OseeDataStoreException {
      if (this.data.isEmpty() != true) {
         ConnectionHandler.runBatchUpdate(connection, getMetaData().getQuery(), this.data);
         this.data.clear();
      }
   }

   public void reset() {
      this.connection = null;
      this.translator = null;
      this.options = null;
      this.metadata = null;
      this.data.clear();
   }
}
