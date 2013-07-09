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

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public interface QueryEngineIndexer {

   CancellableCallable<Integer> deleteIndexByQueryId(OrcsSession session, int queueId);

   CancellableCallable<Integer> purgeAllIndexes(OrcsSession session);

   CancellableCallable<?> indexBranches(OrcsSession session, AttributeTypes types, IndexerCollector collector, Collection<? extends IAttributeType> typeToTag, Set<ReadableBranch> branches, boolean indexOnlyMissing);

   CancellableCallable<Integer> indexAllFromQueue(OrcsSession session, AttributeTypes types, IndexerCollector collector);

   CancellableCallable<IndexerData> getIndexerData(OrcsSession session);

   CancellableCallable<List<Future<?>>> indexXmlStream(OrcsSession session, AttributeTypes types, IndexerCollector collector, InputStream inputStream);

}
