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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
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
   private final SqlArtifactLoader loader;
   private final BranchCache branchCache;

   public DataLoaderFactoryImpl(Log logger, IOseeDatabaseService dbService, SqlArtifactLoader loader, BranchCache branchCache) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.loader = loader;
      this.branchCache = branchCache;
   }

   @Override
   public DataLoader fromQueryContext(QueryContext queryContext) throws OseeCoreException {
      QuerySqlContext sqlQueryContext = toSqlContext(queryContext);
      AbstractLoadExecutor executor = new QueryContextLoadExecutor(loader, dbService, sqlQueryContext);
      return new DataLoaderImpl(logger, executor);
   }

   @Override
   public DataLoader fromBranchAndArtifactIds(String sessionId, IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException {
      int branchId = branchCache.getLocalId(branch);
      AbstractLoadExecutor executor = new LoadExecutor(loader, dbService, sessionId, branchId, artifactIds);
      return new DataLoaderImpl(logger, executor);
   }

   @Override
   public DataLoader fromBranchAndArtifactIds(String sessionId, IOseeBranch branch, int... artifactIds) throws OseeCoreException {
      return fromBranchAndArtifactIds(sessionId, branch, toCollection(artifactIds));
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
