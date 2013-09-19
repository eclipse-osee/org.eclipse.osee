/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer;

import java.util.Collection;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.TagQueueJoinQuery;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public final class IndexAllInQueueCallable extends AbstractDatastoreCallable<Integer> {

   private final IndexingTaskConsumer consumer;
   private final IndexerCollector collector;
   private final AttributeTypes types;
   private Collection<Integer> queryIds;

   public IndexAllInQueueCallable(Log logger, OrcsSession session, IOseeDatabaseService service, AttributeTypes types, IndexingTaskConsumer consumer, IndexerCollector collector) {
      super(logger, session, service);
      this.types = types;
      this.consumer = consumer;
      this.collector = collector;
   }

   @Override
   public Integer call() throws Exception {
      TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery(getDatabaseService());
      queryIds = joinQuery.getAllQueryIds();

      getLogger().info("Submitting - [%d] index tasks from queue", queryIds.size());
      for (Integer queryId : queryIds) {
         consumer.submitTaskId(getSession(), types, collector, queryId);
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
