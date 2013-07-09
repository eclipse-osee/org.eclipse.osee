/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.branch;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.CreateBranchData;

public class CreateBranchCallable extends AbstractBranchCallable<ReadableBranch> {

   private final CreateBranchData branchData;

   public CreateBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, CreateBranchData branchData) {
      super(logger, session, branchStore);
      this.branchData = branchData;
   }

   @Override
   protected ReadableBranch innerCall() throws Exception {
      Conditions.checkNotNull(branchData, "branchData");

      Conditions.checkNotNull(branchData.getGuid(), "branchGuid");
      Conditions.checkNotNull(branchData.getName(), "branchName");
      Conditions.checkNotNull(branchData.getBranchType(), "branchType");

      ITransaction txData = branchData.getFromTransaction();
      Conditions.checkNotNull(txData, "sourceTransaction");

      Callable<Branch> callable;
      if (branchData.isTxCopyBranchType()) {
         callable = getBranchStore().createBranchCopyTx(getSession(), branchData);
      } else {
         callable = getBranchStore().createBranch(getSession(), branchData);
      }

      return callAndCheckForCancel(callable);
   }
}
