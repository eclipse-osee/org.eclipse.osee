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

package org.eclipse.osee.orcs.search;

/**
 * @author Roberto E. Escobar
 */
public interface IndexerCollector {

   void onIndexTaskError(int indexerId, Throwable throwable);

   void onIndexTaskTotalToProcess(int totalIndexTasks);

   void onIndexTaskSubmit(int indexerId);

   void onIndexTaskComplete(int indexerId, long waitTime, long processingTime);

   void onIndexItemAdded(int indexerId, long itemId, String word, long codedTag);

   void onIndexItemComplete(int indexerId, long itemId, int totalTags, long processingTime);

   void onIndexTotalTaskItems(long totalItems);
}
