/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.CountingLoadDataHandler;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.db.internal.search.QueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.QueryFilterFactory;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;

/**
 * @author Roberto E. Escobar
 */
public class ObjectQueryCallableFactory implements QueryCallableFactory {

   private final Log logger;
   private final DataLoaderFactory dataLoaderFactory;
   private final QuerySqlContextFactory queryContextFactory;
   private final QueryFilterFactory factory;

   public ObjectQueryCallableFactory(Log logger, DataLoaderFactory dataLoaderFactory, QuerySqlContextFactory queryEngine, QueryFilterFactoryImpl factory) {
      super();
      this.logger = logger;
      this.dataLoaderFactory = dataLoaderFactory;
      this.queryContextFactory = queryEngine;
      this.factory = factory;
   }

   private boolean isLoadLevelTooLow(LoadLevel level) {
      return LoadLevel.ARTIFACT_DATA == level;
   }

   @Override
   public int getArtifactCount(QueryData queryData) {
      QuerySqlContext queryContext = queryContextFactory.createQueryContext(null, queryData, QueryType.SELECT);

      DataLoader loader = dataLoaderFactory.newDataLoader(queryContext);
      loader.setOptions(queryData.getOptions());

      // Ensure we will receive attribute data for post-process
      if (isLoadLevelTooLow(OptionsUtil.getLoadLevel(queryData.getOptions()))) {
         OptionsUtil.setLoadLevel(queryData.getOptions(), LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA);
      }
      CountingLoadDataHandler countingHandler = factory.createHandler(queryData, queryContext, null);
      loader.load(null, countingHandler);
      return countingHandler.getCount();
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
         return getQueryData().hasCriteriaType(CriteriaAttributeKeywords.class);
      }

      protected int loadAndGetCount(LoadDataHandler handler, boolean enableFilter) throws Exception {
         QuerySqlContext queryContext =
            queryContextFactory.createQueryContext(getSession(), getQueryData(), QueryType.SELECT);
         checkForCancelled();

         DataLoader loader = dataLoaderFactory.newDataLoader(queryContext);
         loader.setOptions(getQueryData().getOptions());

         if (enableFilter) {
            // Ensure we will receive attribute data for post-process
            LoadLevel level = loader.getLoadLevel();
            if (isLoadLevelTooLow(level)) {
               loader.withLoadLevel(LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA);
            }
         }
         CountingLoadDataHandler countingHandler = factory.createHandler(getQueryData(), queryContext, handler);
         loader.load(this, countingHandler);
         return countingHandler.getCount();
      }
   }
}