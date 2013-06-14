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
package org.eclipse.osee.orcs.db.internal.search.indexer;

import java.util.Collection;
import java.util.concurrent.Future;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public interface IndexingTaskConsumer {

   int cancelIndexer() throws Exception;

   int cancelTaskId(Collection<Integer> taskIds);

   int getWorkersInQueue();

   Future<?> submitTaskId(AttributeTypes types, IndexerCollector collector, final int queryId) throws Exception;

}