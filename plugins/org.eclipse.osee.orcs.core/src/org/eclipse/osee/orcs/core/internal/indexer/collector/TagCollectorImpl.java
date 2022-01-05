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

package org.eclipse.osee.orcs.core.internal.indexer.collector;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.osee.orcs.search.IndexerCollectorAdapter;

/**
 * @author Roberto E. Escobar
 */
public class TagCollectorImpl extends IndexerCollectorAdapter {

   private volatile Map<Long, Throwable> tagErrors;
   private volatile Set<Long> queryIds;
   private volatile boolean wasProcessed;
   private volatile int expectedTotal;
   private final AtomicInteger queryCount = new AtomicInteger();
   private final AtomicInteger attributeCount = new AtomicInteger();

   public TagCollectorImpl() {
      this.queryIds = Collections.synchronizedSet(new HashSet<Long>());
      this.wasProcessed = false;
      this.queryCount.set(0);
      this.attributeCount.set(0);
      this.tagErrors = Collections.synchronizedMap(new HashMap<Long, Throwable>());
   }

   public boolean wasProcessed() {
      return wasProcessed;
   }

   public boolean hasErrors() {
      return tagErrors.size() > 0;
   }

   public int getAttributeCount() {
      return attributeCount.get();
   }

   public int getQueryCount() {
      return queryCount.get();
   }

   public Map<Long, Throwable> getTagErrors() {
      return tagErrors;
   }

   @Override
   public void onIndexTaskTotalToProcess(int totalQueries) {
      this.expectedTotal = totalQueries;
   }

   @Override
   synchronized public void onIndexTaskError(Long queryId, Throwable throwable) {
      tagErrors.put(queryId, throwable);
      this.wasProcessed = true;
      this.notify();
   }

   @Override
   synchronized public void onIndexTaskSubmit(Long queryId) {
      queryCount.incrementAndGet();
      queryIds.add(queryId);
   }

   @Override
   public void onIndexItemComplete(Long queryId, long gammaId, int totalTags, long processingTime) {
      if (this.queryIds.contains(queryId)) {
         attributeCount.incrementAndGet();
      }
   }

   @Override
   synchronized public void onIndexTaskComplete(Long queryId, long waitTime, long processingTime) {
      if (this.queryIds.contains(queryId)) {
         this.queryIds.remove(queryId);
         if (this.queryIds.isEmpty() && queryCount.get() == expectedTotal) {
            this.wasProcessed = true;
            this.notify();
         }
      }
   }
}
