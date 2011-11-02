/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import org.eclipse.osee.display.presenter.ArtifactFilter;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.WorkUtility;
import org.eclipse.osee.executor.admin.WorkUtility.PartitionFactory;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.data.ResultSetList;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.ResultSet;
import org.eclipse.osee.orcs.utility.MatchComparator;
import org.eclipse.osee.orcs.utility.SortOrder;

/**
 * @author Roberto E. Escobar
 */
public class FilteredResultSetCallable extends CancellableCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> implements PartitionFactory<Match<ReadableArtifact, ReadableAttribute<?>>, Match<ReadableArtifact, ReadableAttribute<?>>> {
   private static final String FILTER_WORKER_ID = "artifact.filter.workers";

   private final ExecutorAdmin executorAdmin;
   private final ArtifactFilter filter;
   private final CancellableCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> innerWorker;

   public FilteredResultSetCallable(ExecutorAdmin executorAdmin, ArtifactFilter filter, CancellableCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> innerWorker) {
      super();
      this.executorAdmin = executorAdmin;
      this.filter = filter;
      this.innerWorker = innerWorker;
   }

   @Override
   public ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> call() throws Exception {
      ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> results = innerWorker.call();

      List<Match<ReadableArtifact, ReadableAttribute<?>>> artifacts = filter(results.getList());

      Collections.sort(artifacts, new MatchComparator(SortOrder.ASCENDING));
      return new ResultSetList<Match<ReadableArtifact, ReadableAttribute<?>>>(artifacts);
   }

   @Override
   public boolean isCancelled() {
      return super.isCancelled() && innerWorker.isCancelled();
   }

   @Override
   public void setCancel(boolean isCancelled) {
      super.setCancel(isCancelled);
      innerWorker.setCancel(isCancelled);
   }

   @Override
   public void checkForCancelled() throws CancellationException {
      super.checkForCancelled();
      innerWorker.checkForCancelled();
   }

   @Override
   public Callable<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>> createWorker(Collection<Match<ReadableArtifact, ReadableAttribute<?>>> toProcess) {
      return new FilteredMatchCallable(filter, toProcess);
   }

   private List<Match<ReadableArtifact, ReadableAttribute<?>>> filter(Collection<Match<ReadableArtifact, ReadableAttribute<?>>> items) throws Exception {
      List<Future<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>>> futures =
         WorkUtility.partitionAndScheduleWork(executorAdmin, FILTER_WORKER_ID, this, items);
      final List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new LinkedList<Match<ReadableArtifact, ReadableAttribute<?>>>();
      for (Future<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>> future : futures) {
         checkForCancelled();
         results.addAll(future.get());
      }
      return results;
   }
}
