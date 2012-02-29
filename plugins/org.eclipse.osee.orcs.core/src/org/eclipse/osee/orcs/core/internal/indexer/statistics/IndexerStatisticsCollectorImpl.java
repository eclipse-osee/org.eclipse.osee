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
   public void onIndexTaskComplete(int indexerId, long waitTime, long processingTime) {
      statistics.addIndexerTask(indexerId, waitTime, processingTime);
   }

   @Override
   public void onIndexItemComplete(int indexerId, long itemId, int totalTags, long processingTime) {
      statistics.addIndexerItem(indexerId, itemId, totalTags, processingTime);
   }
}
