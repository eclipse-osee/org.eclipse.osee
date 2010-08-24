/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.test.mocks;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.operation.IOperation;

/**
 * @author Ryan D. Brooks
 */
public class MockOperation implements IOperation {
   private final IStatus status;

   public MockOperation(IStatus status) {
      this.status = status;
   }

   @Override
   public String getName() {
      return "MockOperation";
   }

   @Override
   public IStatus run(SubMonitor subMonitor) {
      return status;
   }
}