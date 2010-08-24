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
package org.eclipse.osee.framework.core.test.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.enums.OperationBehavior;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.test.mocks.Asserts;
import org.eclipse.osee.framework.core.test.mocks.MockAbstractOperation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AbstractOperation}
 * 
 * @author Ryan D. Brooks
 */
public class AbstractOperationTest {

   @Test
   public void testAbstractOperationOk() {
      IOperation operation = new MockAbstractOperation();
      IStatus status = Asserts.testOperation(operation, IStatus.OK);
      Assert.assertEquals(Status.OK_STATUS, status);
   }

   @Test
   public void testAbstractOperationWithOneError() {
      Exception expection = new Exception("What did you do?!");
      IOperation operation = new MockAbstractOperation("Test Status", expection);
      IStatus status = Asserts.testOperation(operation, IStatus.ERROR);
      Assert.assertEquals(expection.toString(), status.getMessage());
   }

   @Test
   public void testCompositeOperationOk() {
      CompositeOperation compositeOperation =
         new CompositeOperation("two oks", "test", new MockAbstractOperation(), new MockAbstractOperation());
      IStatus status = Asserts.testOperation(compositeOperation, IStatus.OK);
      Assert.assertEquals(0, status.getChildren().length);
   }

   @Test
   public void testCompositeOperationContinueOnError() {
      Exception exception1 = new Exception("ex1");
      Exception exception2 = new Exception("ex2");
      IOperation operation1 = new MockAbstractOperation(exception1);
      IOperation operation2 = new MockAbstractOperation(exception2);
      CompositeOperation compositeOperation =
         new CompositeOperation("two exceptions", "test", OperationBehavior.ContinueOnError, operation1, operation2);

      IStatus status = Asserts.testOperation(compositeOperation, IStatus.ERROR);

      Assert.assertEquals(MultiStatus.class, status.getClass());
      Assert.assertEquals(exception1.toString(), status.getChildren()[0].getMessage());
      Assert.assertEquals(exception2.toString(), status.getChildren()[1].getMessage());
   }
}
