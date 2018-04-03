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
package org.eclipse.osee.ote.define.jobs;

import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerExecutor;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;

/**
 * @author Roberto E. Escobar
 */
public class FindCommitableJob extends Job {

   private static final String JOB_NAME = "Check commit allowed";
   private static final String PLUGIN_ID = "org.eclipse.osee.ote.define";
   private final List<Artifact> artifactsToSort;
   private Artifact[] itemsToCommit;
   private Artifact[] nonCommitableItems;
   List<Artifact> commitable;
   List<Artifact> nonCommitable;

   public FindCommitableJob(Artifact... artifactsToSort) {
      super(JOB_NAME);
      setUser(true);
      setPriority(Job.LONG);
      this.artifactsToSort = Arrays.asList(artifactsToSort);
      this.itemsToCommit = this.nonCommitableItems = new Artifact[0];
   }

   public Artifact[] getAll() {
      return artifactsToSort.toArray(new Artifact[artifactsToSort.size()]);
   }

   public Artifact[] getCommitAllowed() {
      return itemsToCommit;
   }

   public Artifact[] getCommitNotAllowed() {
      return nonCommitableItems;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      monitor.beginTask(getName(), artifactsToSort.size());
      commitable = new ArrayList<>();
      nonCommitable = new ArrayList<>();

      ThreadedWorkerFactory<Object> outfileToArtifactFactory = new ThreadedWorkerFactory<Object>() {

         @Override
         public int getWorkSize() {
            return artifactsToSort.size();
         }

         @Override
         public Callable<Object> createWorker(int startIndex, int endIndex) {
            return new FindCommitableCallable(artifactsToSort.subList(startIndex, endIndex), monitor);
         }

      };

      ThreadedWorkerExecutor<Object> executor = new ThreadedWorkerExecutor<>(outfileToArtifactFactory, false);
      try {
         executor.executeWorkersBlocking();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      }

      itemsToCommit = commitable.toArray(new Artifact[commitable.size()]);
      nonCommitableItems = nonCommitable.toArray(new Artifact[nonCommitable.size()]);
      if (monitor.isCanceled() != true) {
         toReturn = Status.OK_STATUS;
      }
      monitor.subTask("Done");
      monitor.done();
      return toReturn;
   }

   private class FindCommitableCallable implements Callable<Object> {

      private final List<Artifact> artifactsToSort;
      private final IProgressMonitor monitor;

      public FindCommitableCallable(List<Artifact> artifactsToSort, IProgressMonitor monitor) {
         this.artifactsToSort = artifactsToSort;
         this.monitor = monitor;
      }

      @Override
      public Object call() throws Exception {
         for (Artifact artifact : artifactsToSort) {
            try {
               ArtifactTestRunOperator operator = new ArtifactTestRunOperator(artifact);
               if (operator.isCommitAllowed() == true) {
                  synchronized (commitable) {
                     commitable.add(artifact);
                  }
               } else {
                  synchronized (nonCommitable) {
                     nonCommitable.add(artifact);
                  }
               }
            } catch (OseeArgumentException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
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
