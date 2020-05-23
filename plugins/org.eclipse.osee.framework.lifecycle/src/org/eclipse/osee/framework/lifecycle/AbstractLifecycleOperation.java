/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.lifecycle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.lifecycle.internal.OperationPointId;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public abstract class AbstractLifecycleOperation extends AbstractOperation {

   private final ILifecycleService service;
   private final AbstractLifecyclePoint<?> lifecyclepoint;

   public AbstractLifecycleOperation(ILifecycleService service, AbstractLifecyclePoint<?> lifecyclePoint, String operationName, String pluginId, OperationLogger logger) {
      super(operationName, pluginId, logger);
      this.service = service;
      this.lifecyclepoint = lifecyclePoint;
   }

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
      Operations.checkForErrorStatus(status);
      checkForCancelledStatus(monitor);
   }

   abstract protected void doCoreWork(IProgressMonitor monitor) throws Exception;

}
