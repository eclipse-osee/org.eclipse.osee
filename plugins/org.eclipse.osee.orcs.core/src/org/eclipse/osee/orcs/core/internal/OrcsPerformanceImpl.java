/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.indexer.IndexerModule;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;
import org.eclipse.osee.orcs.statistics.QueryStatistics;

/**
 * @author Roberto E. Escobar
 */
public class OrcsPerformanceImpl implements OrcsPerformance {

   private final QueryModule queryModule;
   private final IndexerModule indexerModule;
   private final OrcsSession session;

   public OrcsPerformanceImpl(Log logger, OrcsSession session, QueryModule queryModule, IndexerModule indexerModule) {
      this.session = session;
      this.queryModule = queryModule;
      this.indexerModule = indexerModule;
   }

   public HasStatistics<?> getStatistics() {
      return queryModule;
   }

   @Override
   public QueryStatistics getQueryStatistics() {
      return queryModule.getStatistics(session);
   }

   @Override
   public IndexerStatistics getIndexerStatistics() {
      return indexerModule.getStatistics(session);
   }

   @Override
   public void clearQueryStatistics() {
      queryModule.clearStatistics(session);
   }

   @Override
   public void clearIndexerStatistics() {
      indexerModule.clearStatistics(session);
   }

}
