/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.CountingLoadDataHandler;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.QueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.QueryFilterFactory;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;

/**
 * @author Roberto E. Escobar
 */
public class ObjectQueryCallableFactory implements QueryCallableFactory {

   private final Log logger;
   private final DataLoaderFactory objectLoader;
   private final QuerySqlContextFactory queryContextFactory;
   private final QueryFilterFactory factory;

   public ObjectQueryCallableFactory(Log logger, DataLoaderFactory objectLoader, QuerySqlContextFactory queryEngine, QueryFilterFactoryImpl factory) {
      super();
      this.logger = logger;
      this.objectLoader = objectLoader;
      this.queryContextFactory = queryEngine;
      this.factory = factory;
   }

   @Override
   public CancellableCallable<Integer> createCount(OrcsSession session, QueryData queryData) {
      return new AbstractObjectSearchCallable(logger, session, queryData) {

         @Override
         protected Integer innerCall() throws Exception {
            int count = -1;
            if (isPostProcessRequired()) {
               count = loadAndGetCount(null, true);
            } else {
               count = getCount();
            }
            checkForCancelled();
            return count;
         }

         protected int getCount()  {
            QuerySqlContext queryContext =
               queryContextFactory.createQueryContext(getSession(), getQueryData(), QueryType.COUNT);
            checkForCancelled();
            return objectLoader.getCount(this, queryContext);
         }

      };
   }

   @Override
   public CancellableCallable<Integer> createQuery(OrcsSession session, final QueryData queryData, final LoadDataHandler handler) {
      return new AbstractObjectSearchCallable(logger, session, queryData) {

         @Override
         protected Integer innerCall() throws Exception {
            boolean enableFilter = isPostProcessRequired();
            return loadAndGetCount(handler, enableFilter);
         }
      };
   }

   private abstract class AbstractObjectSearchCallable extends AbstractSearchCallable {

      public AbstractObjectSearchCallable(Log logger, OrcsSession session, QueryData queryData) {
         super(logger, session, queryData);
      }

      private boolean isLoadLevelTooLow(LoadLevel level) {
         return LoadLevel.ARTIFACT_DATA == level;
      }

      protected boolean isPostProcessRequired() {
         return factory.isFilterRequired(getQueryData());
      }

      protected int loadAndGetCount(LoadDataHandler handler, boolean enableFilter) throws Exception {
         QuerySqlContext queryContext =
            queryContextFactory.createQueryContext(getSession(), getQueryData(), QueryType.SELECT);
         checkForCancelled();

         DataLoader loader = objectLoader.newDataLoader(queryContext);
         loader.setOptions(getQueryData().getOptions());

         if (enableFilter) {
            // Ensure we will receive attribute data for post-process
            LoadLevel level = loader.getLoadLevel();
            if (isLoadLevelTooLow(level)) {
               loader.withLoadLevel(LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA);
            }
         }
         CountingLoadDataHandler countingHandler = factory.createHandler(this, getQueryData(), queryContext, handler);
         loader.load(this, countingHandler);
         return countingHandler.getCount();
      }
   }

}
