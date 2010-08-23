/*
 * Created on Aug 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.test.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.test.mocks.Asserts;
import org.eclipse.osee.framework.core.test.mocks.MockOperation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AbstractOperation}
 * 
 * @author Ryan D. Brooks
 */
public class AbstractOperationTest {

   @Test
   public void testGetStatusOK() {
      IOperation operation = new MockOperation();
      Asserts.testOperation(operation, IStatus.OK);
      Assert.assertEquals(Status.OK_STATUS, operation.getStatus());
   }

   @Test
   public void testGetStatusWithOneError() {
      Exception expection = new Exception("What did you do?!");
      IOperation operation = new MockOperation("Test Status", expection);
      Asserts.testOperation(operation, IStatus.ERROR);

      IStatus status = operation.getStatus();
      Assert.assertEquals(expection.toString(), status.getMessage());
   }

   @Test
   public void testGetStatusMultiOK() {
      CompositeOperation compositeOperation =
         new CompositeOperation("two oks", "test", new MockOperation(), new MockOperation());
      Asserts.testOperation(compositeOperation, IStatus.OK);
      Assert.assertEquals(0, compositeOperation.getStatus().getChildren().length);
   }

   @Test
   public void testGetStatusMultiStatus() {
      IOperation operation1 = new MockOperation();
      IOperation operation2 = new MockOperation(new Exception("ex1"));
      CompositeOperation compositeOperation = new CompositeOperation("two exceptions", "test", operation1, operation2);

      Asserts.testOperation(compositeOperation, IStatus.ERROR);

      IStatus status = compositeOperation.getStatus();
      Assert.assertEquals(status.getClass(), MultiStatus.class);

      String expectedMessage = status.getMessage();
      Assert.assertEquals("status message did not match message", expectedMessage, status.getMessage());
   }
}
