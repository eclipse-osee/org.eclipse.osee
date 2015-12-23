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
