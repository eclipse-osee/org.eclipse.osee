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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.change.ComputeNetChangeCallable;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenBranches;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class CommitBranchDatabaseCallable extends AbstractDatastoreCallable<TransactionId> {

   private final SqlJoinFactory joinFactory;
   private final IdentityManager idManager;
   private final ArtifactId committer;
   private final TransactionToken sourceHead;
   private final Branch source;
   private final TransactionToken destinationHead;
   private final Branch destination;
   private final MissingChangeItemFactory missingChangeItemFactory;
   private final QueryFactory queryFactory;
   private final BranchId mergeBranch;

   public CommitBranchDatabaseCallable(Log logger, OrcsSession session, JdbcClient service, SqlJoinFactory joinFactory, IdentityManager idManager, ArtifactId committer, Branch source, TransactionToken sourceHead, Branch destination, TransactionToken destinationHead, BranchId mergeBranch, MissingChangeItemFactory missingChangeItemFactory, QueryFactory queryFactory) {
      super(logger, session, service);
      this.joinFactory = joinFactory;
      this.idManager = idManager;
      this.committer = committer;
      this.source = source;
      this.sourceHead = sourceHead;
      this.destination = destination;
      this.destinationHead = destinationHead;
      this.mergeBranch = mergeBranch;
      this.missingChangeItemFactory = missingChangeItemFactory;
      this.queryFactory = queryFactory;
   }

   private List<ChangeItem> callComputeChanges() throws Exception {

      Callable<List<ChangeItem>> loadChanges = new LoadDeltasBetweenBranches(getLogger(), getSession(), getJdbcClient(),
         joinFactory, sourceHead.getBranch(), destinationHead.getBranch(), destinationHead, mergeBranch, queryFactory);
      List<ChangeItem> changes = callAndCheckForCancel(loadChanges);

      changes.addAll(missingChangeItemFactory.createMissingChanges(changes, sourceHead, destinationHead,
         queryFactory.applicabilityQuery()));

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
      List<ChangeItem> changes = callComputeChanges();

      CancellableCallable<TransactionId> commitCallable =
         new CommitBranchDatabaseTxCallable(getLogger(), getSession(), getJdbcClient(), idManager, committer, source,
            destination, mergeBranch, changes, OseeCodeVersion.getVersionId());
      TransactionId newTx = callAndCheckForCancel(commitCallable);

      return newTx;
   }
}