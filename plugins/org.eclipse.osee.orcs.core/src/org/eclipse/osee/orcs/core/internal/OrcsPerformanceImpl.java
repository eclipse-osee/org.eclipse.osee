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
package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.core.internal.indexer.IndexerModule;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;
import org.eclipse.osee.orcs.statistics.QueryStatistics;

/**
 * @author Roberto E. Escobar
 */
public class OrcsPerformanceImpl implements OrcsPerformance {

   private final Log logger;
   private final QueryModule queryModule;
   private final IndexerModule indexerModule;
   private final SessionContext sessionContext;

   public OrcsPerformanceImpl(Log logger, SessionContext sessionContext, QueryModule queryModule, IndexerModule indexerModule) {
      this.logger = logger;
      this.sessionContext = sessionContext;
      this.queryModule = queryModule;
      this.indexerModule = indexerModule;
   }

   public HasStatistics<?> getStatistics() {
      return queryModule;
   }

   @Override
   public QueryStatistics getQueryStatistics() {
      return queryModule.getStatistics(sessionContext);
   }

   @Override
   public IndexerStatistics getIndexerStatistics() {
      return indexerModule.getStatistics(sessionContext);
   }

   @Override
   public void clearQueryStatistics() {
      queryModule.clearStatistics(sessionContext);
   }

   @Override
   public void clearIndexerStatistics() {
      indexerModule.clearStatistics(sessionContext);
   }

}
