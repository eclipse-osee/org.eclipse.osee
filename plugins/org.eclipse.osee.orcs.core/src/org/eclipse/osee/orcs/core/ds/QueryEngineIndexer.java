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

package org.eclipse.osee.orcs.core.ds;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public interface QueryEngineIndexer {

   void addCollector(IndexerCollector collector);

   void removeCollector(IndexerCollector collector);

   CancellableCallable<IndexerData> getIndexerData(OrcsSession session);

   CancellableCallable<Integer> deleteIndexByQueryId(OrcsSession session, int queueId);

   CancellableCallable<Integer> purgeAllIndexes(OrcsSession session);

   CancellableCallable<Integer> indexBranches(OrcsSession session, OrcsTokenService tokenService, Set<Branch> branches,
      boolean indexOnlyMissing, IndexerCollector... collector);

   CancellableCallable<Integer> indexAllFromQueue(OrcsSession session, OrcsTokenService tokenService,
      IndexerCollector... collector);

   CancellableCallable<List<Future<?>>> indexResources(OrcsSession session, OrcsTokenService tokenService,
      Iterable<Long> datas, IndexerCollector... collector);

   void indexAttrTypeIds(OrcsSession session, OrcsTokenService tokenService, Iterable<Long> attrTypeIds);

   void indexAttrTypeMissingOnly(OrcsTokenService tokenService, Iterable<Long> attrTypeIds);

   /**
    * Direct batch re-index that bypasses the async pipeline. Streams attribute data, tokenizes in Java, and batch
    * inserts tags directly. Much faster for bulk migration after truncating osee_search_tags.
    */
   void indexDirectByAttrType(OrcsTokenService tokenService, Long attrTypeId);

   /**
    * Index only attributes modified within the specified time window that are missing from osee_search_tags_hash.
    * Scopes the query to recent transactions via osee_tx_details.time.
    *
    * @return number of gammas indexed
    */
   long indexRecentlyModified(OrcsTokenService tokenService, int hours);
}