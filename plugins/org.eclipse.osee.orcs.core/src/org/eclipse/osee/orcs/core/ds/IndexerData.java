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
package org.eclipse.osee.orcs.core.ds;

/**
 * @author Roberto E. Escobar
 */
public class IndexerData {

   private long totalTags;
   private long totalItemsInQueue;
   private int workersInQueue;

   public IndexerData() {
      super();
   }

   public int getWorkersInQueue() {
      return workersInQueue;
   }

   public void setWorkersInQueue(int workersInQueue) {
      this.workersInQueue = workersInQueue;
   }

   public long getTotalTags() {
      return totalTags;
   }

   public void setTotalTags(long totalTags) {
      this.totalTags = totalTags;
   }

   public long getTotalItemsInQueue() {
      return totalItemsInQueue;
   }

   public void setTotalItemsInQueue(long totalItemsInQueue) {
      this.totalItemsInQueue = totalItemsInQueue;
   }

   @Override
   public String toString() {
      return "IndexerData [totalTags=" + totalTags + ", totalItemsInQueue=" + totalItemsInQueue + ", workersInQueue=" + workersInQueue + "]";
   }
}
