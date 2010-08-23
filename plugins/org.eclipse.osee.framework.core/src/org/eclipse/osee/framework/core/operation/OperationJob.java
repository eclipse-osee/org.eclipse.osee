/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ryan D. Brooks
 */
public final class OperationJob extends Job {
   private final IOperation operation;
   private final OperationReporter reporter;

   /**
    * @param operation the operation that will be executed by this Job's run method
    */
   public OperationJob(IOperation operation) {
      this(operation, null);
   }

   /**
    * @param operation the operation that will be executed by this Job's run method
    * @param reporter reporter.report(IStatus status) is passed the IStatus returned by the operation's run method
    */
   public OperationJob(IOperation operation, OperationReporter reporter) {
      super(operation.getName());
      this.operation = operation;
      this.reporter = reporter;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus status = operation.run(monitor).getStatus();
      if (reporter != null) {
         reporter.report(status);
      }
      return status;
   }

   @Override
   public boolean belongsTo(Object family) {
      return OperationJob.class.equals(family);
   }
}