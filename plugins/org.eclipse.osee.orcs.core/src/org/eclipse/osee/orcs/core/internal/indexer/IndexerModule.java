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
package org.eclipse.osee.orcs.core.internal.indexer;

import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.IndexerData;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.internal.HasStatistics;
import org.eclipse.osee.orcs.core.internal.indexer.statistics.IndexerStatisticsCollectorImpl;
import org.eclipse.osee.orcs.core.internal.indexer.statistics.IndexerStatisticsImpl;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.IndexerCollector;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;

/**
 * @author Roberto E. Escobar
 */
public class IndexerModule implements HasStatistics<IndexerStatistics> {

   private final IndexerStatisticsImpl statistics = new IndexerStatisticsImpl();

   private final Log logger;
   private final SystemPreferences preferences;
   private final ExecutorAdmin executorAdmin;
   private final QueryEngineIndexer queryIndexer;
   private final IndexerCollector systemCollector;
   private Future<Integer> task;

   public IndexerModule(Log logger, SystemPreferences preferences, ExecutorAdmin executorAdmin, QueryEngineIndexer queryIndexer) {
      this.logger = logger;
      this.preferences = preferences;
      this.executorAdmin = executorAdmin;
      this.queryIndexer = queryIndexer;
      this.systemCollector = new IndexerStatisticsCollectorImpl(statistics);
   }

   public void start(OrcsSession systemSession, AttributeTypes attributeTypes) {
      queryIndexer.addCollector(systemCollector);
      try {
         if (preferences.isBoolean(DataStoreConstants.DATASTORE_INDEX_ON_START_UP)) {
            task = executorAdmin.submit("Attribute Indexer",
               queryIndexer.indexAllFromQueue(systemSession, attributeTypes));
         } else {
            logger.info("Indexer was not executed on Server Startup.");
         }
      } catch (Exception ex) {
         logger.info("Indexer was not executed on Server Startup.");
      }
   }

   public void stop() {
      queryIndexer.removeCollector(systemCollector);
      if (task != null) {
         task.cancel(true);
         task = null;
      }
   }

   @Override
   public IndexerStatistics getStatistics(OrcsSession session) {
      try {
         IndexerData indexerData = queryIndexer.getIndexerData(session).call();
         statistics.setIndexerData(indexerData);
      } catch (Exception ex) {
         logger.warn(ex, "Error fetching indexer data - stats are unreliable");
      }
      return statistics;
   }

   @Override
   public void clearStatistics(OrcsSession session) {
      statistics.clear();
   }

   public QueryIndexer createQueryIndexer(OrcsSession session, AttributeTypes attributeTypes) {
      return new QueryIndexerImpl(session, queryIndexer, attributeTypes);
   }
}
