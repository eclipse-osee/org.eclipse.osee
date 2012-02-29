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
package org.eclipse.osee.orcs.search;

/**
 * @author Roberto E. Escobar
 */
public class IndexerCollectorAdapter implements IndexerCollector {

   @Override
   public void onIndexTaskError(int indexerId, Throwable throwable) {
      // Empty
   }

   @Override
   public void onIndexTaskTotalToProcess(int totalQueries) {
      // Empty
   }

   @Override
   public void onIndexTaskSubmit(int indexerId) {
      // Empty
   }

   @Override
   public void onIndexTaskComplete(int indexerId, long waitTime, long processingTime) {
      // Empty
   }

   @Override
   public void onIndexItemAdded(int indexerId, long itemId, String word, long codedTag) {
      // Empty
   }

   @Override
   public void onIndexItemComplete(int indexerId, long itemId, int totalTags, long processingTime) {
      // Empty
   }

   @Override
   public void onIndexTotalTaskItems(long totalItems) {
      // Empty
   }

}
