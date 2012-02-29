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
package org.eclipse.osee.orcs.core.internal.indexer.collector;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.orcs.search.IndexerCollectorAdapter;

/**
 * @author Roberto E. Escobar
 */
public class TagCollectorImpl extends IndexerCollectorAdapter {

   private volatile Map<Integer, Throwable> tagErrors;
   private volatile Set<Integer> queryIds;
   private volatile boolean wasProcessed;
   private volatile int expectedTotal;
   private volatile int queryCount;
   private volatile int attributeCount;

   public TagCollectorImpl() {
      this.queryIds = Collections.synchronizedSet(new HashSet<Integer>());
      this.wasProcessed = false;
      this.queryCount = 0;
      this.attributeCount = 0;
      this.tagErrors = Collections.synchronizedMap(new HashMap<Integer, Throwable>());
   }

   public boolean wasProcessed() {
      return wasProcessed;
   }

   public boolean hasErrors() {
      return tagErrors.size() > 0;
   }

   public int getAttributeCount() {
      return attributeCount;
   }

   public int getQueryCount() {
      return queryCount;
   }

   public Map<Integer, Throwable> getTagErrors() {
      return tagErrors;
   }

   @Override
   public void onIndexTaskTotalToProcess(int totalQueries) {
      this.expectedTotal = totalQueries;
   }

   @Override
   synchronized public void onIndexTaskError(int queryId, Throwable throwable) {
      tagErrors.put(queryId, throwable);
      this.wasProcessed = true;
      this.notify();
   }

   @Override
   synchronized public void onIndexTaskSubmit(int queryId) {
      queryCount++;
      queryIds.add(queryId);
   }

   @Override
   public void onIndexItemComplete(int queryId, long gammaId, int totalTags, long processingTime) {
      if (this.queryIds.contains(queryId)) {
         attributeCount++;
      }
   }

   @Override
   synchronized public void onIndexTaskComplete(int queryId, long waitTime, long processingTime) {
      if (this.queryIds.contains(queryId)) {
         this.queryIds.remove(queryId);
         if (this.queryIds.isEmpty() && queryCount == expectedTotal) {
            this.wasProcessed = true;
            this.notify();
         }
      }
   }
}
