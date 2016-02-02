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

import static org.eclipse.osee.framework.core.data.RelationalConstants.BRANCH_SENTINEL;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.data.TransactionReadableDelta;
import org.eclipse.osee.orcs.db.internal.change.AddArtifactChangeDataCallable;
import org.eclipse.osee.orcs.db.internal.change.ComputeNetChangeCallable;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenBranches;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenTxsOnTheSameBranch;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

public class CompareDatabaseCallable extends AbstractDatastoreCallable<List<ChangeItem>> {

   private final SqlJoinFactory joinFactory;
   private final TransactionReadable sourceTx;
   private final TransactionReadable destinationTx;
   private final MissingChangeItemFactory missingChangeItemFactory;

   private static final String SELECT_MERGE_BRANCH_UUID =
      "select merge_branch_id from osee_merge where source_branch_id = ? and dest_branch_id = ?";
   private static final String SELECT_MERGE_BRANCH_HEAD_TX =
      "select max(transaction_id) from osee_tx_details where branch_id = ?";

   public CompareDatabaseCallable(Log logger, OrcsSession session, JdbcClient service, SqlJoinFactory joinFactory, TransactionReadable sourceTx, TransactionReadable destinationTx, MissingChangeItemFactory missingChangeItemFactory) {
      super(logger, session, service);
      this.joinFactory = joinFactory;
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
      this.missingChangeItemFactory = missingChangeItemFactory;
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      TransactionReadableDelta txDelta = new TransactionReadableDelta(sourceTx, destinationTx);

      Callable<List<ChangeItem>> callable;
      if (txDelta.areOnTheSameBranch()) {
         callable =
            new LoadDeltasBetweenTxsOnTheSameBranch(getLogger(), getSession(), getJdbcClient(), joinFactory, txDelta);
      } else {
         Long mergeBranchId = getJdbcClient().runPreparedQueryFetchObject(BRANCH_SENTINEL.getId(),
            SELECT_MERGE_BRANCH_UUID, sourceTx.getBranch(), destinationTx.getBranch());
         BranchId mergeBranch = TokenFactory.createBranch(mergeBranchId);

         Integer mergeTxId = null;
         if (mergeBranch.isValid()) {
            mergeTxId = getJdbcClient().runPreparedQueryFetchObject(-1, SELECT_MERGE_BRANCH_HEAD_TX, mergeBranch);
         }
         callable = new LoadDeltasBetweenBranches(getLogger(), getSession(), getJdbcClient(), joinFactory,
            sourceTx.getBranch(), destinationTx.getBranch(), destinationTx.getGuid(), mergeBranch, mergeTxId);
      }
      List<ChangeItem> changes = callAndCheckForCancel(callable);

      changes.addAll(
         missingChangeItemFactory.createMissingChanges(this, getSession(), changes, sourceTx, destinationTx));
      Callable<List<ChangeItem>> computeChanges = new ComputeNetChangeCallable(changes);
      changes = callAndCheckForCancel(computeChanges);

      Callable<List<ChangeItem>> addArtifactData = new AddArtifactChangeDataCallable(changes);
      return callAndCheckForCancel(addArtifactData);
   }

}
