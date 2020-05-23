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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class MockAbstractOperation extends AbstractOperation {

   private final Exception exceptionToThrow;

   public MockAbstractOperation() {
      this(null);
   }

   public MockAbstractOperation(Exception exceptionToThrow) {
      this("Mock Operation", exceptionToThrow);
   }

   public MockAbstractOperation(String operationName, Exception exceptionToThrow) {
      super(operationName, "Test Plugin-id");
      this.exceptionToThrow = exceptionToThrow;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Assert.assertNotNull(monitor);
      if (exceptionToThrow != null) {
         throw exceptionToThrow;
      }
   }

}