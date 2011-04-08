/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.test.purge;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.branch.management.purge.BranchOperation;
import org.eclipse.osee.framework.branch.management.test.mocks.MockBranchOperationFactory;
import org.eclipse.osee.framework.branch.management.test.mocks.MockBranchProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John Misinco
 */
public final class BranchOperationTest {

   private void runTest(boolean nullFactory, boolean nullProvider, boolean nullLogger, boolean expectedResult) throws OseeCoreException {
      OperationLogger logger = NullOperationLogger.getSingleton();

      MockBranchOperationFactory mbo = new MockBranchOperationFactory();
      MockBranchProvider mbp = new MockBranchProvider();

      if (nullFactory) {
         mbo = null;
      }
      if (nullProvider) {
         mbp = null;
      }
      if (nullLogger) {
         logger = null;
      }

      BranchOperation bo = new BranchOperation(logger, mbo, mbp);
      IStatus status = Operations.executeWork(bo);

      if (expectedResult) {
         Assert.assertEquals(Status.OK_STATUS, status);
         Assert.assertTrue(mbo.getCalled() == expectedResult);
         Assert.assertTrue(verifyCallOrder(mbo.getCallOrder()));
      } else {
         Assert.assertFalse(Status.OK_STATUS == status);
      }

   }

   private boolean verifyCallOrder(List<Branch> callOrder) throws OseeCoreException {
      boolean result = true;
      for (Branch cur : callOrder) {
         int idxCur = callOrder.indexOf(cur);
         Branch parent = cur.getParentBranch();
         if (parent != null) {
            int idxParent = callOrder.indexOf(parent);
            if (idxCur > idxParent) {
               result = false;
               break;
            }
         }
      }
      return result;
   }

   @Test
   public void testBranchOperation() throws OseeCoreException {
      runTest(false, false, false, true);
   }

   @Test
   public void testBranchOperationException__nullFactory() throws OseeCoreException {
      runTest(true, false, false, false);
   }

   @Test
   public void testBranchOperationException__nullProvider() throws OseeCoreException {
      runTest(false, true, false, false);
   }

   @Test
   public void testBranchOperationException__nullLogger() throws OseeCoreException {
      runTest(false, false, true, false);
   }

   @Test
   public void testBranchOperationException__allNull() throws OseeCoreException {
      runTest(true, true, true, false);
   }
}
