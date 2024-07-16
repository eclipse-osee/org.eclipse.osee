/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer;

import java.util.Collection;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.TagQueueJoinQuery;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public final class IndexAllInQueueCallable extends AbstractDatastoreCallable<Integer> {

   private final SqlJoinFactory joinFactory;
   private final IndexingTaskConsumer consumer;
   private final IndexerCollector collector;
   private final OrcsTokenService tokenService;
   private Collection<Long> queryIds;

   public IndexAllInQueueCallable(Log logger, OrcsSession session, JdbcClient service, SqlJoinFactory joinFactory, OrcsTokenService tokenService, IndexingTaskConsumer consumer, IndexerCollector collector) {
      super(logger, session, service);
      this.consumer = consumer;
      this.collector = collector;
      this.joinFactory = joinFactory;
      this.tokenService = tokenService;
   }

   @Override
   public Integer call() throws Exception {
      TagQueueJoinQuery joinQuery = joinFactory.createTagQueueJoinQuery();
      queryIds = joinQuery.getAllQueryIds();

      getLogger().info("Submitting - [%d] index tasks from queue", queryIds.size());
      for (Long queryId : queryIds) {
         consumer.submitTaskId(getSession(), tokenService, collector, queryId);
      }
      return queryIds.size();
   }

   @Override
   public void setCancel(boolean isCancelled) {
      super.setCancel(isCancelled);
      if (isCancelled()) {
         if (queryIds != null && !queryIds.isEmpty()) {
            consumer.cancelTaskId(queryIds);
            queryIds = null;
         }
      }
   }

}
