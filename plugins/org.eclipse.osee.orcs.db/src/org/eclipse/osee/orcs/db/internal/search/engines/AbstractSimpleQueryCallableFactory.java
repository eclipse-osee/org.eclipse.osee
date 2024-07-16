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

import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.QueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSimpleQueryCallableFactory implements QueryCallableFactory {

   private final Log logger;
   private final DataLoaderFactory objectLoader;
   private final QuerySqlContextFactory queryContextFactory;

   public AbstractSimpleQueryCallableFactory(Log logger, DataLoaderFactory objectLoader, QuerySqlContextFactory queryEngine) {
      this.logger = logger;
      this.objectLoader = objectLoader;
      this.queryContextFactory = queryEngine;
   }

   @Override
   public CancellableCallable<Integer> createQuery(OrcsSession session, final QueryData queryData, final LoadDataHandler handler) {
      return new AbstractSearchCallable(logger, session, queryData) {

         @Override
         protected Integer innerCall() throws Exception {
            QuerySqlContext queryContext = queryContextFactory.createQueryContext(session, queryData, QueryType.SELECT);
            checkForCancelled();

            DataLoader loader = objectLoader.newDataLoader(queryContext);
            loader.setOptions(queryData.getOptions());

            final AtomicInteger counter = new AtomicInteger();
            LoadDataHandler countingHandler = createCountingHandler(counter, handler);
            loader.load(this, countingHandler);
            return counter.get();
         }
      };
   }

   protected abstract LoadDataHandler createCountingHandler(AtomicInteger counter, LoadDataHandler handler);
}