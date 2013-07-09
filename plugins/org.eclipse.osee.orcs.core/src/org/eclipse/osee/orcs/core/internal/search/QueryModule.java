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
package org.eclipse.osee.orcs.core.internal.search;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.core.internal.HasStatistics;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.statistics.QueryStatistics;

/**
 * @author Roberto E. Escobar
 */
public class QueryModule implements HasStatistics<QueryStatistics> {

   private final QueryStatisticsImpl statistics = new QueryStatisticsImpl();

   private final CriteriaFactory criteriaFctry;
   private final CallableQueryFactory callableQueryFactory;

   public QueryModule(Log logger, QueryEngine queryEngine, ArtifactLoaderFactory objectLoader, DataLoaderFactory dataLoader, ArtifactTypes artifactTypeCache, AttributeTypes attributeTypeCache) {
      QueryStatsCollectorImpl queryStatsCollector = new QueryStatsCollectorImpl(statistics);
      this.criteriaFctry = new CriteriaFactory(artifactTypeCache, attributeTypeCache);
      this.callableQueryFactory =
         new CallableQueryFactory(logger, queryEngine, queryStatsCollector, objectLoader, dataLoader,
            attributeTypeCache);
   }

   public QueryFactory createQueryFactory(OrcsSession session) {
      return new QueryFactoryImpl(session, criteriaFctry, callableQueryFactory);
   }

   @Override
   public QueryStatistics getStatistics(OrcsSession session) {
      return statistics.clone();
   }

   @Override
   public void clearStatistics(OrcsSession session) {
      statistics.clear();
   }

}
