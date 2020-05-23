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

package org.eclipse.osee.orcs.statistics;

/**
 * @author Roberto E. Escobar
 */
public interface IndexerStatistics {

   public long getLongestQueryIdWaitTime();

   public long getLongestQueryIdProcessingTime();

   public long getAverageQueryIdWaitTime();

   public int getTotalQueryIdsProcessed();

   public long getAverageQueryIdProcessingTime();

   public long getAverageAttributeProcessingTime();

   public long getTotalTags();

   public int getTotalAttributesProcessed();

   public long getLongestAttributeProcessingTime();

   public IndexerItemStatistics getLongestTask();

   public IndexerItemStatistics getMostTagsTask();

   public long getTagsInSystem();

   public long getTotalQueryIdsInQueue();

   public int getWorkersInQueue();
}
