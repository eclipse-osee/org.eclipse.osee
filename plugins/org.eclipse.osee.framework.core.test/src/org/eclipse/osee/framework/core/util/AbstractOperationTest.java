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

package org.eclipse.osee.framework.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.enums.OperationBehavior;
import org.eclipse.osee.framework.core.mocks.MockAbstractOperation;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.Operations;
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
      IStatus status = testOperation(operation, IStatus.OK);
      Assert.assertEquals(Status.OK_STATUS, status);
   }

   @Test
   public void testAbstractOperationWithOneError() {
      Exception expection = new Exception("What did you do?!");
      IOperation operation = new MockAbstractOperation("Test Status", expection);
      IStatus status = testOperation(operation, IStatus.ERROR);
      Assert.assertEquals(expection.toString(), status.getMessage());
   }

   @Test
   public void testCompositeOperationOk() {
      OperationBuilder builder =
         Operations.createBuilder("two oks", new MockAbstractOperation(), new MockAbstractOperation());
      IStatus status = testOperation(builder.build(), IStatus.OK);
      Assert.assertEquals(0, status.getChildren().length);
   }

   @Test
   public void testCompositeOperationContinueOnError() {
      Exception exception1 = new Exception("ex1");
      Exception exception2 = new Exception("ex2");
      IOperation operation1 = new MockAbstractOperation(exception1);
      IOperation operation2 = new MockAbstractOperation(exception2);
      OperationBuilder builder = Operations.createBuilder("two exceptions", operation1, operation2);
      builder.executionBehavior(OperationBehavior.ContinueOnError);
      IStatus status = testOperation(builder.build(), IStatus.ERROR);

      Assert.assertEquals(MultiStatus.class, status.getClass());
      Assert.assertEquals(exception1.toString(), status.getChildren()[0].getMessage());
      Assert.assertEquals(exception2.toString(), status.getChildren()[1].getMessage());
   }

   private static IStatus testOperation(IOperation operation, int expectedSeverity) {
      IStatus status = Operations.executeWork(operation);
      Assert.assertEquals(status.toString(), expectedSeverity, status.getSeverity());
      return status;
   }
}
