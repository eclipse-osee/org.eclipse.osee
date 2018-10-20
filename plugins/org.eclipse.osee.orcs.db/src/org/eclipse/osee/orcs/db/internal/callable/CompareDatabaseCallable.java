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
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.TransactionTokenDelta;
import org.eclipse.osee.orcs.db.internal.change.AddSyntheticArtifactChangeData;
import org.eclipse.osee.orcs.db.internal.change.ComputeNetChangeCallable;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenBranches;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenTxsOnTheSameBranch;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.search.QueryFactory;

public class CompareDatabaseCallable extends AbstractDatastoreCallable<List<ChangeItem>> {

   private final SqlJoinFactory joinFactory;
   private final TransactionToken sourceTx;
   private final TransactionToken destinationTx;
   private final MissingChangeItemFactory missingChangeItemFactory;
   private final ApplicabilityQuery applicQuery;
   private final BranchId mergeBranch;
   private final QueryFactory queryFactory;

   public CompareDatabaseCallable(Log logger, OrcsSession session, JdbcClient service, SqlJoinFactory joinFactory, TransactionToken sourceTx, TransactionToken destinationTx, BranchId mergeBranch, MissingChangeItemFactory missingChangeItemFactory, QueryFactory queryFactory) {
      super(logger, session, service);
      this.joinFactory = joinFactory;
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
      this.mergeBranch = mergeBranch;
      this.missingChangeItemFactory = missingChangeItemFactory;
      this.queryFactory = queryFactory;
      this.applicQuery = queryFactory.applicabilityQuery();
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      TransactionTokenDelta txDelta = new TransactionTokenDelta(sourceTx, destinationTx);

      Callable<List<ChangeItem>> callable;
      BranchId branch = BranchId.SENTINEL;
      if (txDelta.areOnTheSameBranch()) {
         callable = new LoadDeltasBetweenTxsOnTheSameBranch(getLogger(), getSession(), getJdbcClient(), joinFactory,
            txDelta, applicQuery);
         branch = txDelta.getStartTx().getBranch();
      } else {
         branch = sourceTx.getBranch();

         callable = new LoadDeltasBetweenBranches(getLogger(), getSession(), getJdbcClient(), joinFactory,
            sourceTx.getBranch(), destinationTx.getBranch(), destinationTx, mergeBranch, queryFactory);
      }
      List<ChangeItem> changes = callAndCheckForCancel(callable);

      changes.addAll(missingChangeItemFactory.createMissingChanges(changes, sourceTx, destinationTx, applicQuery));
      Callable<List<ChangeItem>> computeChanges = new ComputeNetChangeCallable(changes);
      changes = callAndCheckForCancel(computeChanges);

      AddSyntheticArtifactChangeData addArtifactData =
         new AddSyntheticArtifactChangeData(changes, getJdbcClient(), branch);
      return addArtifactData.doWork();
   }
}