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

package org.eclipse.osee.orcs.db.internal.search.indexer;

public final class IndexerConstants {

   private IndexerConstants() {
      // Constants Class
   }

   public static final String INDEXING_CONSUMER_EXECUTOR_ID = "indexing.consumer.executor.id";
   public static final int INDEXER_CACHE_LIMIT = 1000;
   public static final boolean INDEXER_CACHE_ALL_ITEMS = false;
   public static final int INDEX_QUERY_ID_LOADER_TOTAL_RETRIES = 10;

}
