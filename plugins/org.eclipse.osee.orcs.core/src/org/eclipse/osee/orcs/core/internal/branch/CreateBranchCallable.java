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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.TransactionReadable;
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

      Conditions.checkNotNull(branchData.getUuid(), "branchUuid");
      Conditions.checkNotNull(branchData.getName(), "branchName");
      Conditions.checkNotNull(branchData.getBranchType(), "branchType");

      ITransaction txData = branchData.getFromTransaction();
      Conditions.checkNotNull(txData, "sourceTransaction");

      Callable<?> callable;
      if (branchData.isTxCopyBranchType()) {
         TransactionQuery txQuery = queryFactory.transactionQuery();
         ITransaction givenTx = branchData.getFromTransaction();
         Conditions.checkNotNull(givenTx, "Transaction used for copy");
         branchData.setSavedTransaction(givenTx);
         TransactionReadable priorTx = txQuery.andIsPriorTx(givenTx.getGuid()).getResults().getExactlyOne();
         branchData.setFromTransaction(priorTx);
         callable = getBranchStore().createBranchCopyTx(getSession(), branchData);
      } else {
         callable = getBranchStore().createBranch(getSession(), branchData);
      }
      callAndCheckForCancel(callable);
      return queryFactory.branchQuery().andUuids(branchData.getUuid()).getResults().getExactlyOne();
   }
}
