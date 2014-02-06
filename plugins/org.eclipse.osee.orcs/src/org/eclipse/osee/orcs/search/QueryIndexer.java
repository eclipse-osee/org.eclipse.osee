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

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.orcs.data.BranchReadable;

/**
 * @author Roberto E. Escobar
 */
public interface QueryIndexer {

   CancellableCallable<Integer> indexAllFromQueue(IndexerCollector... collector);

   CancellableCallable<Integer> indexBranches(Set<BranchReadable> branches, boolean indexOnlyMissing, IndexerCollector... collector);

   /**
    * Create tags for attributes specified in xml stream. Notifies listener of tagging events. <b>
    * 
    * <pre>
    * The XML data is formatted as follows:
    *    &lt;AttributeTag&gt;
    *       &lt;entry gammaId=&quot;90&quot;/&gt;
    *       &lt;entry gammaId=&quot;91&quot;/&gt;
    *                .
    *                .
    *                .
    *    &lt;AttributeTag&gt;
    * </pre>
    * 
    * </b>
    * 
    * @param listener object listening for tag events
    * @param inputStream xml inputStream
    */
   CancellableCallable<List<Future<?>>> indexXmlStream(InputStream inputStream, IndexerCollector... collector);

   void submitXmlStream(InputStream inputStream) throws Exception;

   CancellableCallable<Integer> deleteIndexByQueryId(int queueId);

   CancellableCallable<Integer> purgeAllIndexes();

}
