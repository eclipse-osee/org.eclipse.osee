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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.search.QueryFactory;

public class CommitBranchCallable extends AbstractBranchCallable<TransactionToken> {

   private final ArtifactId committer;
   private final BranchId source;
   private final BranchId destination;
   private final QueryFactory queryFactory;
   private final OrcsTypes orcsTypes;

   public CommitBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, QueryFactory queryFactory, ArtifactId committer, BranchId source, BranchId destination, OrcsTypes orcsTypes) {
      super(logger, session, branchStore);
      this.committer = committer;
      this.source = source;
      this.destination = destination;
      this.queryFactory = queryFactory;
      this.orcsTypes = orcsTypes;
   }

   @Override
   protected TransactionToken innerCall() throws Exception {
      Conditions.checkNotNull(source, "sourceBranch");
      Conditions.checkNotNull(destination, "destinationBranch");

      Branch sourceBranch = queryFactory.branchQuery().andId(source).getResults().getExactlyOne();
      TransactionToken sourceHead = queryFactory.transactionQuery().andIsHead(source).getResults().getExactlyOne();
      Branch destinationBranch = queryFactory.branchQuery().andId(destination).getResults().getExactlyOne();
      TransactionToken destinationHead =
         queryFactory.transactionQuery().andIsHead(destination).getResults().getExactlyOne();

      Conditions.checkNotNull(sourceBranch, "sourceBranch");
      Conditions.checkNotNull(destinationBranch, "destinationBranch");

      TransactionId newTx = getBranchStore().commitBranch(getSession(), committer, orcsTypes, sourceBranch, sourceHead,
         destinationBranch, destinationHead, queryFactory);
      return queryFactory.transactionQuery().andTxId(newTx).getResults().getExactlyOne();
   }
}