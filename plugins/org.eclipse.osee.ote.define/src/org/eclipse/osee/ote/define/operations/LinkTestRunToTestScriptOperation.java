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
package org.eclipse.osee.ote.define.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerExecutor;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;

/**
 * @author Roberto E. Escobar
 */
public class LinkTestRunToTestScriptOperation {
   private static final String OPERATION_NAME = "Link Test Run to Test Script";
   private final List<Artifact> artifacts;
   private final List<Artifact> unlinked;
   private final List<Artifact> linked;

   public LinkTestRunToTestScriptOperation(Artifact... artifacts) {
      this.artifacts = Arrays.asList(artifacts);
      this.unlinked = new ArrayList<>();
      this.linked = new ArrayList<>();
   }

   public void execute(final IProgressMonitor monitor)  {
      monitor.setTaskName(OPERATION_NAME);
      final AtomicInteger count = new AtomicInteger(0);

      ThreadedWorkerFactory<Object> outfileToArtifactFactory = new ThreadedWorkerFactory<Object>() {

         @Override
         public int getWorkSize() {
            return artifacts.size();
         }

         @Override
         public Callable<Object> createWorker(int startIndex, int endIndex) {
            return new LinkTestRunToTestScriptCallable(monitor, artifacts.subList(startIndex, endIndex), count,
               artifacts.size());
         }

      };

      ThreadedWorkerExecutor<Object> executor = new ThreadedWorkerExecutor<>(outfileToArtifactFactory, true);
      executor.executeWorkersBlocking();
   }

   public Artifact[] getLinkedArtifacts() {
      return linked.toArray(new Artifact[linked.size()]);
   }

   public Artifact[] getUnlinkedArtifacts() {
      return unlinked.toArray(new Artifact[unlinked.size()]);
   }

   private class LinkTestRunToTestScriptCallable implements Callable<Object> {

      private final IProgressMonitor monitor;
      private final List<Artifact> artifacts;
      private final AtomicInteger count;
      private final int totalSize;

      public LinkTestRunToTestScriptCallable(IProgressMonitor monitor, List<Artifact> artifacts, AtomicInteger count, int totalSize) {
         this.monitor = monitor;
         this.artifacts = artifacts;
         this.count = count;
         this.totalSize = totalSize;
      }

      @Override
      public Object call() throws Exception {
         for (Artifact testRun : artifacts) {

            monitor.subTask(String.format("Linking [%s] [%s of %s] ", testRun.getName(), count.incrementAndGet(),
               totalSize));
            ArtifactTestRunOperator operator = new ArtifactTestRunOperator(testRun);
            try {
               operator.createTestScriptSoftLink();
               synchronized (linked) {
                  linked.add(testRun);
               }
            } catch (Exception ex) {
               synchronized (unlinked) {
                  unlinked.add(testRun);
               }
            }
            if (monitor.isCanceled() == true) {
               break;
            }
            monitor.worked(1);
         }
         return null;
      }

   }
}
