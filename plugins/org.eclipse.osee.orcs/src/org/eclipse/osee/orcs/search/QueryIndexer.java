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

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.executor.CancellableCallable;

/**
 * @author Roberto E. Escobar
 */
public interface QueryIndexer {

   CancellableCallable<Integer> indexAllFromQueue(IndexerCollector... collector);

   CancellableCallable<Integer> indexBranches(Set<Branch> branches, boolean indexOnlyMissing,
      IndexerCollector... collector);

   Callable<List<Future<?>>> indexResources(Iterable<Long> gammaIds, IndexerCollector... collector);

   void indexAttrTypeIds(Iterable<Long> gammaIds);

   void indexMissingByAttrTypeIds(Iterable<Long> attrTypeIds);

   void indexMissing(Iterable<AttributeTypeGeneric<?>> attrTypes);

   CancellableCallable<Integer> deleteIndexByQueryId(int queueId);

   CancellableCallable<Integer> purgeAllIndexes();

}
