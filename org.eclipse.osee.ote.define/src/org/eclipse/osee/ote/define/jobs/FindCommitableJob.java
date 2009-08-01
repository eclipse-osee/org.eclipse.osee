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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.OteDefinePlugin;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;

/**
 * @author Roberto E. Escobar
 */
public class FindCommitableJob extends Job {

   private static final String JOB_NAME = "Check commit allowed";
   private Artifact[] artifactsToSort;
   private Artifact[] itemsToCommit;
   private Artifact[] nonCommitableItems;

   public FindCommitableJob(Artifact... artifactsToSort) {
      super(JOB_NAME);
      setUser(true);
      setPriority(Job.LONG);
      this.artifactsToSort = artifactsToSort;
      this.itemsToCommit = this.nonCommitableItems = new Artifact[0];
   }

   public Artifact[] getAll() {
      return this.artifactsToSort;
   }

   public Artifact[] getCommitAllowed() {
      return itemsToCommit;
   }

   public Artifact[] getCommitNotAllowed() {
      return nonCommitableItems;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      monitor.beginTask(getName(), artifactsToSort.length);
      List<Artifact> commitable = new ArrayList<Artifact>();
      List<Artifact> nonCommitable = new ArrayList<Artifact>();
      for (Artifact artifact : artifactsToSort) {
         try {
            TestRunOperator operator = new TestRunOperator(artifact);
            if (operator.isCommitAllowed() == true) {
               commitable.add(artifact);
            } else {
               nonCommitable.add(artifact);
            }
         } catch (OseeArgumentException ex) {
            OseeLog.log(OteDefinePlugin.class, Level.SEVERE, ex);
         }

         if (monitor.isCanceled() == true) {
            break;
         }
         monitor.worked(1);
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
}
