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

package org.eclipse.osee.orcs.core.internal.indexer.statistics;

import org.eclipse.osee.orcs.statistics.IndexerItemStatistics;

/**
 * @author Roberto E. Escobar
 */
public class IndexerItemStatisticsImpl implements Cloneable, IndexerItemStatistics {
   private final long uniqueId;
   private final int totalTags;
   private final long processingTime;

   public IndexerItemStatisticsImpl(long uniqueId, int totalTags, long processingTime) {
      super();
      this.uniqueId = uniqueId;
      this.totalTags = totalTags;
      this.processingTime = processingTime;
   }

   @Override
   public long getId() {
      return uniqueId;
   }

   @Override
   public int getTotalTags() {
      return totalTags;
   }

   @Override
   public long getProcessingTime() {
      return processingTime;
   }

   @Override
   protected IndexerItemStatisticsImpl clone() {
      return new IndexerItemStatisticsImpl(uniqueId, totalTags, processingTime);
   }
}
