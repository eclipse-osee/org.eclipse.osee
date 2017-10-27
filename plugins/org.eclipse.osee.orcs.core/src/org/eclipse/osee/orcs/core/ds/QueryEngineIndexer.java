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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public interface QueryEngineIndexer {

   void addCollector(IndexerCollector collector);

   void removeCollector(IndexerCollector collector);

   CancellableCallable<IndexerData> getIndexerData(OrcsSession session);

   CancellableCallable<Integer> deleteIndexByQueryId(OrcsSession session, int queueId);

   CancellableCallable<Integer> purgeAllIndexes(OrcsSession session);

   CancellableCallable<Integer> indexBranches(OrcsSession session, AttributeTypes types, Collection<? extends AttributeTypeId> typeToTag, Set<BranchReadable> branches, boolean indexOnlyMissing, IndexerCollector... collector);

   CancellableCallable<Integer> indexAllFromQueue(OrcsSession session, AttributeTypes types, IndexerCollector... collector);

   CancellableCallable<List<Future<?>>> indexResources(OrcsSession session, AttributeTypes types, Iterable<Long> datas, IndexerCollector... collector);

   void indexAttrTypeIds(OrcsSession session, AttributeTypes attributeTypes, Iterable<Long> attrTypeIds);
}