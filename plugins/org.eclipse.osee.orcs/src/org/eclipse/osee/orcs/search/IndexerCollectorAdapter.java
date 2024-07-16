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

package org.eclipse.osee.orcs.search;

/**
 * @author Roberto E. Escobar
 */
public class IndexerCollectorAdapter implements IndexerCollector {

   @Override
   public void onIndexTaskError(Long indexerId, Throwable throwable) {
      // Empty
   }

   @Override
   public void onIndexTaskTotalToProcess(int totalQueries) {
      // Empty
   }

   @Override
   public void onIndexTaskSubmit(Long indexerId) {
      // Empty
   }

   @Override
   public void onIndexTaskComplete(Long indexerId, long waitTime, long processingTime) {
      // Empty
   }

   @Override
   public void onIndexItemAdded(Long indexerId, long itemId, String word, long codedTag) {
      // Empty
   }

   @Override
   public void onIndexItemComplete(Long indexerId, long itemId, int totalTags, long processingTime) {
      // Empty
   }

   @Override
   public void onIndexTotalTaskItems(long totalItems) {
      // Empty
   }

}
