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
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author Ryan D. Brooks
 */
public class JobChangeLogger extends JobChangeAdapter {
   private final OperationLogger logger;

   public JobChangeLogger(OperationLogger logger) {
      super();
      this.logger = logger;
   }

   @Override
   public void done(IJobChangeEvent event) {
      IStatus status = event.getResult();
      if (!status.isOK()) {
         logger.log(status);
      }
   }
}
