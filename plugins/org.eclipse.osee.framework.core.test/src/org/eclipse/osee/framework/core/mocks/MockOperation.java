/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.core.mocks;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;

/**
 * @author Ryan D. Brooks
 */
public class MockOperation implements IOperation {
   private final IStatus status;
   private boolean called;

   public MockOperation() {
      this(Status.OK_STATUS);
   }

   public MockOperation(IStatus status) {
      setCalled(false);
      this.status = status;
   }

   @Override
   public String getName() {
      return "MockOperation";
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