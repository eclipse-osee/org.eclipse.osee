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

package org.eclipse.osee.orcs.db.internal.search.indexer.callable;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.IndexerData;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;

/**
 * @author Roberto E. Escobar
 */
public class IndexerDatabaseStatisticsCallable extends AbstractDatastoreCallable<IndexerData> {

   private static final String SELECT_TOTAL_TAGS = "select count(1) from osee_search_tags";

   private static final String SELECT_TOTAL_QUERY_IDS_IN_QUEUE =
      "select count(DISTINCT query_id) from osee_tag_gamma_queue";

   public IndexerDatabaseStatisticsCallable(Log logger, OrcsSession session, JdbcClient jdbcClient) {
      super(logger, session, jdbcClient);
   }

   @Override
   public IndexerData call() throws Exception {
      IndexerData indexerData = new IndexerData();

      indexerData.setTotalItemsInQueue(getJdbcClient().fetch(-1L, SELECT_TOTAL_QUERY_IDS_IN_QUEUE));
      indexerData.setTotalTags(getJdbcClient().fetch(-1L, SELECT_TOTAL_TAGS));

      return indexerData;
   }

}
