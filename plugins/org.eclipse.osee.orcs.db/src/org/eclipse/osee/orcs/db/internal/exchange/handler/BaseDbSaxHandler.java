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
package org.eclipse.osee.orcs.db.internal.exchange.handler;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.TranslationManager;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseDbSaxHandler extends BaseExportImportSaxHandler {

   private final List<Object[]> data;
   private final int cacheLimit;
   private final boolean isCacheAll;

   private MetaData metadata;
   private TranslationManager translator;
   private PropertyStore options;
   private final JdbcClient service;
   private final Log logger;

   protected BaseDbSaxHandler(Log logger, JdbcClient service, boolean isCacheAll, int cacheLimit) {
      super();
      if (cacheLimit < 0) {
         throw new IllegalArgumentException(
            String.format("Cache limit cannot be less than zero - cacheLimit=[%d]", cacheLimit));
      }
      this.logger = logger;
      this.service = service;
      this.options = new PropertyStore();
      this.translator = null;
      this.metadata = null;
      this.isCacheAll = isCacheAll;
      this.cacheLimit = cacheLimit;
      this.data = new ArrayList<>();
   }

   protected Log getLogger() {
      return logger;
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

   public void setTranslator(TranslationManager translator) {
      this.translator = translator;
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

   public void store() throws OseeCoreException {
      store(null);
   }

   public void store(JdbcConnection connection) throws OseeCoreException {
      if (!data.isEmpty()) {
         getDatabaseService().runBatchUpdate(connection, getMetaData().getQuery(), data);
         data.clear();
      }
   }

   public void clearDataTable() throws OseeCoreException {
      service.clearTable(getMetaData().getTableName());
   }

   protected JdbcClient getDatabaseService() {
      return service;
   }

   public void reset() {
      this.translator = null;
      this.options = null;
      this.metadata = null;
      this.data.clear();
   }
}