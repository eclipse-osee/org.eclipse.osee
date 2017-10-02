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
package org.eclipse.osee.framework.lifecycle.test.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleOperation;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.lifecycle.LifecycleServiceImpl;
import org.eclipse.osee.framework.lifecycle.test.mock.MockHandler;
import org.eclipse.osee.framework.lifecycle.test.mock.StrictMockLifecycePoint;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AbstractLifecycleOperation}
 * 
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class LifecycleOperationTest {

   @Test
   public void testOperation()  {
      ILifecycleService service = new LifecycleServiceImpl();

      service.addHandler(StrictMockLifecycePoint.TYPE, new MockHandler());

      IOperation operation = new MockLifecycleOperation(service, "a string", "b string");
      testOperation(operation, IStatus.OK);
   }

   private static class MockLifecycleOperation extends AbstractLifecycleOperation {

      public MockLifecycleOperation(ILifecycleService service, String a, String b) {
         super(service, new StrictMockLifecycePoint(a, b), "Mock Op", "TestBundle");
      }

      @Override
      protected void doCoreWork(IProgressMonitor monitor) throws Exception {
         // Empty
      }
   }

   private static IStatus testOperation(IOperation operation, int expectedSeverity) {
      IStatus status = Operations.executeWork(operation);
      Assert.assertEquals(status.toString(), expectedSeverity, status.getSeverity());
      return status;
   }
}
