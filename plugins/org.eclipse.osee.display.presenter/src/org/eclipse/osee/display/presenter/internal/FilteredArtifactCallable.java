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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.display.presenter.ArtifactFilter;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.WorkUtility;
import org.eclipse.osee.executor.admin.WorkUtility.PartitionFactory;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class FilteredArtifactCallable extends CancellableCallable<List<ArtifactReadable>> implements PartitionFactory<ArtifactReadable, ArtifactReadable> {

   private static final String FILTER_WORKER_ID = "artifact.filter.workers";

   private final ExecutorAdmin executorAdmin;
   private final ArtifactFilter filter;
   private final Iterable<ArtifactReadable> artifacts;

   public FilteredArtifactCallable(ExecutorAdmin executorAdmin, ArtifactFilter filter, Iterable<ArtifactReadable> artifacts) {
      super();
      this.executorAdmin = executorAdmin;
      this.filter = filter;
      this.artifacts = artifacts;
   }

   @Override
   public List<ArtifactReadable> call() throws Exception {
      List<Future<Collection<ArtifactReadable>>> futures =
         WorkUtility.partitionAndScheduleWork(executorAdmin, FILTER_WORKER_ID, this, artifacts);
      final List<ArtifactReadable> results = new LinkedList<ArtifactReadable>();
      for (Future<Collection<ArtifactReadable>> future : futures) {
         checkForCancelled();
         results.addAll(future.get());
      }
      return results;
   }

   @Override
   public Callable<Collection<ArtifactReadable>> createWorker(final Collection<ArtifactReadable> toProcess) {
      return new CancellableCallable<Collection<ArtifactReadable>>() {

         @Override
         public Collection<ArtifactReadable> call() throws Exception {
            checkForCancelled();
            Collections.filter(toProcess, filter);
            return toProcess;
         }
      };
   }

}
