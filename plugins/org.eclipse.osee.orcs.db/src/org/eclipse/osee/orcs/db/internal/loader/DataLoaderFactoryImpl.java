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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.db.internal.loader.executors.AbstractLoadExecutor;
import org.eclipse.osee.orcs.db.internal.loader.executors.LoadExecutor;
import org.eclipse.osee.orcs.db.internal.loader.executors.QueryContextLoadExecutor;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;

/**
 * @author Roberto E. Escobar
 */
public class DataLoaderFactoryImpl implements DataLoaderFactory {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final SqlObjectLoader loader;
   private final BranchCache branchCache;

   public DataLoaderFactoryImpl(Log logger, IOseeDatabaseService dbService, SqlObjectLoader loader, BranchCache branchCache) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.loader = loader;
      this.branchCache = branchCache;
   }

   @Override
   public int getCount(HasCancellation cancellation, QueryContext queryContext) throws OseeCoreException {
      QuerySqlContext context = toSqlContext(queryContext);

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
         count = dbService.runPreparedQueryFetchObject(-1, context.getSql(), context.getParameters().toArray());
      } finally {
         for (AbstractJoinQuery join : context.getJoins()) {
            try {
               join.delete();
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
   public DataLoader fromQueryContext(QueryContext queryContext) throws OseeCoreException {
      QuerySqlContext sqlQueryContext = toSqlContext(queryContext);
      AbstractLoadExecutor executor = new QueryContextLoadExecutor(loader, dbService, sqlQueryContext);
      Options options = OptionsUtil.createOptions();
      return new DataLoaderImpl(logger, executor, options);
   }

   @Override
   public DataLoader fromBranchAndArtifactIds(OrcsSession session, IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(artifactIds, "artifactIds");

      int branchId = branchCache.getLocalId(branch);
      AbstractLoadExecutor executor = new LoadExecutor(loader, dbService, session, branchId, artifactIds);
      Options options = OptionsUtil.createOptions();
      return new DataLoaderImpl(logger, executor, options);
   }

   @Override
   public DataLoader fromBranchAndArtifactIds(OrcsSession session, IOseeBranch branch, int... artifactIds) throws OseeCoreException {
      return fromBranchAndArtifactIds(session, branch, toCollection(artifactIds));
   }

   private QuerySqlContext toSqlContext(QueryContext queryContext) throws OseeCoreException {
      QuerySqlContext sqlContext = null;
      if (queryContext instanceof QuerySqlContext) {
         sqlContext = (QuerySqlContext) queryContext;
      } else {
         throw new OseeCoreException("Invalid query context type [%s] - expected SqlContext",
            queryContext.getClass().getName());
      }
      return sqlContext;
   }

   private Collection<Integer> toCollection(int... ids) {
      Set<Integer> toReturn = new HashSet<Integer>();
      for (Integer id : ids) {
         toReturn.add(id);
      }
      return toReturn;
   }

}
