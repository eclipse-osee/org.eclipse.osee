/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;

/**
 * @author Roberto E. Escobar
 */
public class OperationCallableAdapter extends CancellableCallable<IStatus> {

   private final IOperation operation;

   public OperationCallableAdapter(IOperation operation) {
      this.operation = operation;
   }

   @Override
   public IStatus call() throws Exception {
      IStatus status = Operations.executeWork(operation);
      Operations.checkForErrorStatus(status);
      return status;
   }
}