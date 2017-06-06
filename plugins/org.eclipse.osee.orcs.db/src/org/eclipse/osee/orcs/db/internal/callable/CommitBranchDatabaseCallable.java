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
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.change.ComputeNetChangeCallable;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenBranches;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author Roberto E. Escobar
 */
public class CommitBranchDatabaseCallable extends AbstractDatastoreCallable<TransactionId> {

   private final SqlJoinFactory joinFactory;
   private final IdentityManager idManager;
   private final ArtifactId committer;
   private final TransactionToken sourceHead;
   private final BranchReadable source;
   private final TransactionToken destinationHead;
   private final BranchReadable destination;
   private final MissingChangeItemFactory missingChangeItemFactory;
   private final ApplicabilityQuery applicQuery;

   private static final String SELECT_MERGE_BRANCH_UUID =
      "select merge_branch_id from osee_merge where source_branch_id = ? and dest_branch_id = ?";
   private static final String SELECT_MERGE_BRANCH_HEAD_TX =
      "select max(transaction_id) from osee_tx_details where branch_id = ?";

   public CommitBranchDatabaseCallable(Log logger, OrcsSession session, JdbcClient service, SqlJoinFactory joinFactory, IdentityManager idManager, ArtifactId committer, BranchReadable source, TransactionToken sourceHead, BranchReadable destination, TransactionToken destinationHead, MissingChangeItemFactory missingChangeItemFactory, ApplicabilityQuery applicQuery) {
      super(logger, session, service);
      this.joinFactory = joinFactory;
      this.idManager = idManager;
      this.committer = committer;
      this.source = source;
      this.sourceHead = sourceHead;
      this.destination = destination;
      this.destinationHead = destinationHead;
      this.missingChangeItemFactory = missingChangeItemFactory;
      this.applicQuery = applicQuery;
   }

   private List<ChangeItem> callComputeChanges(BranchId mergeBranch) throws Exception {
      TransactionId mergeTxId = getJdbcClient().fetch(TransactionId.SENTINEL, SELECT_MERGE_BRANCH_HEAD_TX, mergeBranch);

      Callable<List<ChangeItem>> loadChanges =
         new LoadDeltasBetweenBranches(getLogger(), getSession(), getJdbcClient(), joinFactory, sourceHead.getBranch(),
            destinationHead.getBranch(), destinationHead, mergeBranch, mergeTxId, applicQuery);
      List<ChangeItem> changes = callAndCheckForCancel(loadChanges);

      changes.addAll(missingChangeItemFactory.createMissingChanges(this, getSession(), changes, sourceHead,
         destinationHead, applicQuery));

      Callable<List<ChangeItem>> computeChanges = new ComputeNetChangeCallable(changes);

      List<ChangeItem> computedChanges = new ArrayList<>();
      for (ChangeItem item : callAndCheckForCancel(computeChanges)) {
         if (isAllowableChange(item.getIgnoreType())) {
            computedChanges.add(item);
         }
      }
      return computedChanges;
   }

   private boolean isAllowableChange(ChangeIgnoreType type) {
      return type.isNone() || type.isResurrected();
   }

   @Override
   public TransactionId call() throws Exception {
      BranchId mergeBranch = getJdbcClient().fetch(BranchId.SENTINEL, SELECT_MERGE_BRANCH_UUID, source, destination);
      List<ChangeItem> changes = callComputeChanges(mergeBranch);

      CancellableCallable<TransactionId> commitCallable =
         new CommitBranchDatabaseTxCallable(getLogger(), getSession(), getJdbcClient(), idManager,
            committer, source, destination, mergeBranch, changes, OseeCodeVersion.getVersionId());
      TransactionId newTx = callAndCheckForCancel(commitCallable);

      return newTx;
   }
}
