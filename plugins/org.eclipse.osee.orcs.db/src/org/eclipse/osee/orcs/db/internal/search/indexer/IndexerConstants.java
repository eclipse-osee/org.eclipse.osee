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
