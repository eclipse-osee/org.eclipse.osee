/*
 * Created on Aug 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.test.mocks;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.junit.Assert;

public class MockOperation extends AbstractOperation {

   private final Exception exceptionToThrow;

   public MockOperation() {
      this(null);
   }

   public MockOperation(Exception exceptionToThrow) {
      this("Mock Operation", exceptionToThrow);
   }

   public MockOperation(String operationName, Exception exceptionToThrow) {
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