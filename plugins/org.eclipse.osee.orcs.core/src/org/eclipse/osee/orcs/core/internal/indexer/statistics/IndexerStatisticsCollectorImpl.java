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

package org.eclipse.osee.orcs.core.internal.indexer.statistics;

import org.eclipse.osee.orcs.search.IndexerCollectorAdapter;

/**
 * @author Roberto E. Escobar
 */
public class IndexerStatisticsCollectorImpl extends IndexerCollectorAdapter {

   private final IndexerStatisticsImpl statistics;

   public IndexerStatisticsCollectorImpl(IndexerStatisticsImpl statistics) {
      this.statistics = statistics;
   }

   @Override
   public void onIndexTaskComplete(Long indexerId, long waitTime, long processingTime) {
      statistics.addIndexerTask(indexerId, waitTime, processingTime);
   }

   @Override
   public void onIndexItemComplete(Long indexerId, long itemId, int totalTags, long processingTime) {
      statistics.addIndexerItem(indexerId, itemId, totalTags, processingTime);
   }
}
