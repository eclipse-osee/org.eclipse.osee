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
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.lifecycle.internal.OperationPointId;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public abstract class AbstractLifecyclePoint<H extends LifecycleOpHandler> extends AbstractLifecycleVisitor<H> {

   protected AbstractLifecyclePoint() {
      // Protect empty constructor
   }

   /**
    * Should only be called by {@link ILifecycleService}.
    */
   @Override
   final protected IStatus dispatch(IProgressMonitor monitor, H handler, String sourceId) {
      initializeHandlerData(handler);

      IStatus status = Status.OK_STATUS;
      OperationPointId pointId = OperationPointId.toEnum(sourceId);
      switch (pointId) {
         case CHECK_CONDITION_ID:
            status = handler.onCheck(monitor);
            break;
         case PRE_CONDITION_ID:
            status = handler.onPreCondition(monitor);
            break;
         case POST_CONDITION_ID:
            status = handler.onPostCondition(monitor);
            break;
         case NOOP_ID:
         default:
            break;
      }
      return status;
   }

   protected abstract void initializeHandlerData(H handler);

}
