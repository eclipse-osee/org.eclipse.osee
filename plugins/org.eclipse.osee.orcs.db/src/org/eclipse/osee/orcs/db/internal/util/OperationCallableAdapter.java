/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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