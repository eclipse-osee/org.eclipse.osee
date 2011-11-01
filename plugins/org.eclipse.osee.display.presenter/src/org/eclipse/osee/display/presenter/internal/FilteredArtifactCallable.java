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
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Roberto E. Escobar
 */
public class FilteredArtifactCallable extends CancellableCallable<List<ReadableArtifact>> implements PartitionFactory<ReadableArtifact> {

   private static final String FILTER_WORKER_ID = "artifact.filter.workers";

   private final ExecutorAdmin executorAdmin;
   private final ArtifactFilter filter;
   private final Collection<ReadableArtifact> artifacts;

   public FilteredArtifactCallable(ExecutorAdmin executorAdmin, ArtifactFilter filter, Collection<ReadableArtifact> artifacts) {
      super();
      this.executorAdmin = executorAdmin;
      this.filter = filter;
      this.artifacts = artifacts;
   }

   @Override
   public List<ReadableArtifact> call() throws Exception {
      List<Future<Collection<ReadableArtifact>>> futures =
         WorkUtility.partitionAndScheduleWork(executorAdmin, FILTER_WORKER_ID, this, artifacts);
      final List<ReadableArtifact> results = new LinkedList<ReadableArtifact>();
      for (Future<Collection<ReadableArtifact>> future : futures) {
         checkForCancelled();
         results.addAll(future.get());
      }
      return results;
   }

   @Override
   public Callable<Collection<ReadableArtifact>> createWorker(final Collection<ReadableArtifact> toProcess) {
      return new CancellableCallable<Collection<ReadableArtifact>>() {

         @Override
         public Collection<ReadableArtifact> call() throws Exception {
            checkForCancelled();
            Collections.filter(toProcess, filter);
            return toProcess;
         }
      };
   }

}
