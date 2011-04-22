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
package org.eclipse.osee.framework.branch.management.purge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author John Misinco
 */
public final class BranchOperation extends AbstractOperation {

   private final IBranchOperationFactory operationFactory;
   private final IBranchesProvider branchProvider;

   public BranchOperation(OperationLogger logger, IBranchOperationFactory operationFactory, IBranchesProvider branchProvider) {
      super("Branches", Activator.PLUGIN_ID, logger);
      this.operationFactory = operationFactory;
      this.branchProvider = branchProvider;
   }

   public BranchOperation(OperationLogger logger, IBranchOperationFactory operationFactory, Branch branch) {
      this(logger, operationFactory, new SingleBranchProvider(branch));
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      Conditions.checkNotNull(operationFactory, "operationFactory");
      Conditions.checkNotNull(branchProvider, "branchProvider");
      Collection<Branch> branches = branchProvider.getBranches();
      Conditions.checkNotNull(branches, "branches");

      logf("Branch Operation Starting for %d branch(es).", branches.size());
      for (Branch branch : order(branches)) {
         AbstractOperation.checkForCancelledStatus(monitor);
         IOperation subOp = operationFactory.createOperation(branch);
         log(subOp.getName());
         doSubWork(subOp, monitor, 0);
      }
      log("Branch Operation Completed.");
   }

   private List<Branch> order(Collection<Branch> branches) throws OseeCoreException {
      List<Branch> list = new ArrayList<Branch>(branches);
      for (int i = 0; i < list.size(); i++) {
         Branch cur = list.get(i);
         Branch parent = cur.getParentBranch();

         //this is the last element in the list
         if (parent == null || !list.contains(parent)) {
            Branch last = list.get(list.size() - 1);
            list.set(i, last);
            list.set(list.size() - 1, cur);
         } else {
            int parentIdx = list.indexOf(parent);
            //need to swap
            if (parentIdx < i) {
               list.set(i, parent);
               list.set(parentIdx, cur);
               //reset i
               i--;
            }
         }
      }
      return list;
   }
}
