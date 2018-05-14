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

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.executor.CancellableCallable;

/**
 * @author Roberto E. Escobar
 */
public interface QueryIndexer {

   CancellableCallable<Integer> indexAllFromQueue(IndexerCollector... collector);

   CancellableCallable<Integer> indexBranches(Set<Branch> branches, boolean indexOnlyMissing, IndexerCollector... collector);

   Callable<List<Future<?>>> indexResources(Iterable<Long> gammaIds, IndexerCollector... collector);

   void indexAttrTypeIds(Iterable<Long> gammaIds);

   CancellableCallable<Integer> deleteIndexByQueryId(int queueId);

   CancellableCallable<Integer> purgeAllIndexes();

}
