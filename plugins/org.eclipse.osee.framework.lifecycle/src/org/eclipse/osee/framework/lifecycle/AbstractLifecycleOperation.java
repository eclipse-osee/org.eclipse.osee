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

package org.eclipse.osee.framework.lifecycle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.lifecycle.internal.OperationPointId;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public abstract class AbstractLifecycleOperation extends AbstractOperation {

   private final ILifecycleService service;
   private final AbstractLifecyclePoint<?> lifecyclepoint;

   public AbstractLifecycleOperation(ILifecycleService service, AbstractLifecyclePoint<?> lifecyclePoint, String operationName, String pluginId) {
      super(operationName, pluginId);
      this.service = service;
      this.lifecyclepoint = lifecyclePoint;
   }

   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      doPointWork(monitor, OperationPointId.CHECK_CONDITION_ID);
      doPointWork(monitor, OperationPointId.PRE_CONDITION_ID);
      try {
         doCoreWork(monitor);
      } finally {
         doPointWork(monitor, OperationPointId.POST_CONDITION_ID);
      }
   }

   private void doPointWork(IProgressMonitor monitor, OperationPointId pointId) throws Exception {
      IStatus status = service.dispatch(monitor, lifecyclepoint, pointId.name());
      mergeStatus(status);
      checkForErrorsOrCanceled(monitor);
   }

   abstract protected void doCoreWork(IProgressMonitor monitor) throws Exception;

}
