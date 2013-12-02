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
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.utility.MatchComparator;
import org.eclipse.osee.orcs.utility.SortOrder;

/**
 * @author Roberto E. Escobar
 */
public class FilteredResultSetCallable extends CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> implements PartitionFactory<Match<ArtifactReadable, AttributeReadable<?>>, Match<ArtifactReadable, AttributeReadable<?>>> {
   private static final String FILTER_WORKER_ID = "artifact.filter.workers";

   private final ExecutorAdmin executorAdmin;
   private final ArtifactFilter filter;
   private final CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> innerWorker;

   public FilteredResultSetCallable(ExecutorAdmin executorAdmin, ArtifactFilter filter, CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> innerWorker) {
      super();
      this.executorAdmin = executorAdmin;
      this.filter = filter;
      this.innerWorker = innerWorker;
   }

   @Override
   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> call() throws Exception {
      ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> results = innerWorker.call();

      List<Match<ArtifactReadable, AttributeReadable<?>>> artifacts = filter(results);

      Collections.sort(artifacts, new MatchComparator(SortOrder.ASCENDING));
      return ResultSets.newResultSet(artifacts);
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
   public Callable<Collection<Match<ArtifactReadable, AttributeReadable<?>>>> createWorker(Collection<Match<ArtifactReadable, AttributeReadable<?>>> toProcess) {
      return new FilteredMatchCallable(filter, toProcess);
   }

   private List<Match<ArtifactReadable, AttributeReadable<?>>> filter(Iterable<Match<ArtifactReadable, AttributeReadable<?>>> items) throws Exception {
      List<Future<Collection<Match<ArtifactReadable, AttributeReadable<?>>>>> futures =
         WorkUtility.partitionAndScheduleWork(executorAdmin, FILTER_WORKER_ID, this, items);
      final List<Match<ArtifactReadable, AttributeReadable<?>>> results =
         new LinkedList<Match<ArtifactReadable, AttributeReadable<?>>>();
      for (Future<Collection<Match<ArtifactReadable, AttributeReadable<?>>>> future : futures) {
         checkForCancelled();
         results.addAll(future.get());
      }
      return results;
   }
}
