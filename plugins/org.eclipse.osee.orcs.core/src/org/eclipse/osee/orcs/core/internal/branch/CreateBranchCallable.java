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

import org.eclipse.osee.framework.core.data.BranchReadable;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;

public class CreateBranchCallable extends AbstractBranchCallable<BranchReadable> {

   private final CreateBranchData branchData;
   private final QueryFactory queryFactory;

   public CreateBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, CreateBranchData branchData, QueryFactory queryFactory) {
      super(logger, session, branchStore);
      this.branchData = branchData;
      this.queryFactory = queryFactory;
   }

   @Override
   protected BranchReadable innerCall() throws Exception {
      Conditions.checkNotNull(branchData, "branchData");

      Conditions.checkNotNull(branchData.getBranch(), "branchUuid");
      Conditions.checkNotNull(branchData.getName(), "branchName");
      Conditions.checkNotNull(branchData.getBranchType(), "branchType");

      TransactionId txData = branchData.getFromTransaction();
      Conditions.checkNotNull(txData, "sourceTransaction");

      if (branchData.isTxCopyBranchType()) {
         TransactionQuery txQuery = queryFactory.transactionQuery();
         TransactionId givenTx = branchData.getFromTransaction();
         Conditions.checkNotNull(givenTx, "Transaction used for copy");
         branchData.setSavedTransaction(givenTx);
         TransactionId priorTx = txQuery.andIsPriorTx(givenTx).getResults().getExactlyOne();
         branchData.setFromTransaction(priorTx);
         getBranchStore().createBranchCopyTx(branchData);
      } else {
         getBranchStore().createBranch(branchData);
      }

      return queryFactory.branchQuery().andId(branchData.getBranch()).getResults().getExactlyOne();
   }
}
