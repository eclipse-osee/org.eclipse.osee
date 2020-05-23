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
