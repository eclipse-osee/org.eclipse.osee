/*******************************************************************************
 * Copyright (c) 2018 Boeing.
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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

/**
 * @author Donald G. Dunne
 */
public class EmptyOperation implements IOperation {
   private final IStatus status;
   private boolean called;

   public EmptyOperation() {
      this(Status.OK_STATUS);
   }

   public EmptyOperation(IStatus status) {
      setCalled(false);
      this.status = status;
   }

   @Override
   public String getName() {
      return "EmptyOperation";
   }

   @Override
   public IStatus run(SubMonitor subMonitor) {
      setCalled(true);
      return status;
   }

   public boolean getCalled() {
      return called;
   }

   public void setCalled(boolean called) {
      this.called = called;
   }

   @Override
   public OperationLogger getLogger() {
      return null;
   }
}