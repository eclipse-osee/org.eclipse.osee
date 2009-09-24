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
package org.eclipse.osee.framework.branch.management.internal;

import org.eclipse.osee.framework.branch.management.Branch;
import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;

/**
 * @author Andrew M. Finkbeiner
 */
public class BranchCreation implements IBranchCreation {

   public int createBranch(Branch branch, int authorId, String creationComment, int populateBaseTxFromAddressingQueryId, int destinationBranchId) throws Exception {
      IOperation operation =
            new CreateBranchOperation(branch, authorId, creationComment, populateBaseTxFromAddressingQueryId,
                  destinationBranchId);
      Operations.executeWork(operation, new LogProgressMonitor(), -1);
      Operations.checkForErrorStatus(operation.getStatus());
      return branch.getBranchId();
   }
}
