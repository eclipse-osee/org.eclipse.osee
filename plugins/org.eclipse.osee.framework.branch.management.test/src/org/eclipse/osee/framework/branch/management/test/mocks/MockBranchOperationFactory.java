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
package org.eclipse.osee.framework.branch.management.test.mocks;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.purge.IBranchOperationFactory;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.test.mocks.MockOperation;

/**
 * @author John Misinco
 */
public final class MockBranchOperationFactory implements IBranchOperationFactory {

   private final MockOperation mockOp = new MockOperation();
   private final List<Branch> calledBranches = new ArrayList<Branch>();

   public boolean getCalled() {
      return mockOp.getCalled();
   }

   public List<Branch> getCallOrder() {
      return calledBranches;
   }

   @Override
   public IOperation createOperation(Branch branch) {
      calledBranches.add(branch);
      return mockOp;
   }
}