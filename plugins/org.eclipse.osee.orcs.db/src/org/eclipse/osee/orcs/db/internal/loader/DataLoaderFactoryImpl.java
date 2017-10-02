/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.db.internal.loader.executors.AbstractLoadExecutor;
import org.eclipse.osee.orcs.db.internal.loader.executors.ArtifactQueryContextLoadExecutor;
import org.eclipse.osee.orcs.db.internal.loader.executors.QueryContextLoadExecutor;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.engines.ArtifactQuerySqlContext;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class DataLoaderFactoryImpl implements DataLoaderFactory {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlObjectLoader loader;
   private final SqlJoinFactory joinFactory;

   public DataLoaderFactoryImpl(Log logger, JdbcClient jdbcClient, SqlObjectLoader loader, SqlJoinFactory joinFactory) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.loader = loader;
      this.joinFactory = joinFactory;
   }

   @Override
   public int getCount(HasCancellation cancellation, QueryContext queryContext)  {
      QuerySqlContext context = adapt(QuerySqlContext.class, queryContext);

      int count = -1;
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
         logger.trace("%s Count - queryContext[%s]", getClass().getSimpleName(), queryContext);
      }

      try {
         for (AbstractJoinQuery join : context.getJoins()) {
            join.store();
         }
         if (cancellation != null) {
            cancellation.checkForCancelled();
         }
         count = jdbcClient.fetch(-1, context.getSql(), context.getParameters().toArray());
      } finally {
         for (AbstractJoinQuery join : context.getJoins()) {
            try {
               join.close();
            } catch (Exception ex) {
               // Do nothing
            }
         }
      }

      if (logger.isTraceEnabled()) {
         logger.trace("%s Count [%s] - count[%s] queryContext[%s]", getClass().getSimpleName(),
            Lib.getElapseString(startTime), count, queryContext);
      }
      return count;
   }

   @Override
   public DataLoader newDataLoader(QueryContext queryContext)  {
      AbstractLoadExecutor executor;
      if (queryContext instanceof ArtifactQuerySqlContext) {
         ArtifactQuerySqlContext sqlQueryContext = adapt(ArtifactQuerySqlContext.class, queryContext);
         executor = new ArtifactQueryContextLoadExecutor(loader, jdbcClient, joinFactory, sqlQueryContext);
      } else {
         QuerySqlContext sqlQueryContext = adapt(QuerySqlContext.class, queryContext);
         executor = new QueryContextLoadExecutor(loader, jdbcClient, sqlQueryContext);
      }
      Options options = OptionsUtil.createOptions();
      return new DataLoaderImpl(logger, executor, options, null, null, loader, joinFactory);
   }

   @Override
   public DataLoader newDataLoader(OrcsSession session, BranchId branch, Collection<ArtifactId> ids) {
      ArrayList<Integer> intIds = new ArrayList<>();
      for (ArtifactId id : ids) {
         intIds.add(id.getId().intValue());
      }
      return newDataLoaderFromIds(session, branch, intIds);
   }

   @Override
   public DataLoader newDataLoaderFromIds(OrcsSession session, BranchId branch, Collection<Integer> ids)  {
      Conditions.checkNotNull(branch, "branch");
      Options options = OptionsUtil.createOptions();
      return new DataLoaderImpl(logger, ids, options, session, branch, loader, joinFactory);
   }

   @Override
   public DataLoader newDataLoaderFromGuids(OrcsSession session, BranchId branch, String... guids)  {
      return newDataLoaderFromGuids(session, branch, Arrays.asList(guids));
   }

   @Override
   public DataLoader newDataLoaderFromGuids(OrcsSession session, BranchId branch, Collection<String> guids)  {
      Conditions.checkNotNull(branch, "branch");
      Options options = OptionsUtil.createOptions();
      return new DataLoaderImpl(logger, options, session, branch, loader, guids, joinFactory);
   }

   @SuppressWarnings("unchecked")
   private <T> T adapt(Class<T> clazz, QueryContext queryContext)  {
      T toReturn = null;
      if (clazz.isAssignableFrom(queryContext.getClass())) {
         toReturn = (T) queryContext;
      } else {
         throw new OseeCoreException("Invalid query context type [%s] - expected [%s]",
            queryContext.getClass().getName(), clazz.getName());
      }
      return toReturn;
   }

}
