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
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.lifecycle.internal.OperationPointId;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public abstract class AbstractLifecycleOperation extends AbstractOperation {

   private final LifecycleService service;
   private final AbstractLifecyclePoint<?> lifecyclepoint;

   public AbstractLifecycleOperation(LifecycleService service, AbstractLifecyclePoint<?> lifecyclePoint, String operationName, String pluginId) {
      super(operationName, pluginId);
      this.service = service;
      this.lifecyclepoint = lifecyclePoint;
   }

   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      service.dispatch(monitor, lifecyclepoint, OperationPointId.CHECK_CONDITION_ID.name());
      service.dispatch(monitor, lifecyclepoint, OperationPointId.PRE_CONDITION_ID.name());
      try {
         doCoreWork(monitor);
      } finally {
         service.dispatch(monitor, lifecyclepoint, OperationPointId.POST_CONDITION_ID.name());
      }
   }

   abstract protected void doCoreWork(IProgressMonitor monitor) throws Exception;

}
