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
