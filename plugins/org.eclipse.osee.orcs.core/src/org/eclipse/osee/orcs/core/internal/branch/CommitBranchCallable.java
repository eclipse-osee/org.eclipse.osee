/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.branch;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.search.QueryFactory;

public class CommitBranchCallable extends AbstractBranchCallable<TransactionToken> {

   private final ArtifactId committer;
   private final BranchId source;
   private final BranchId destination;
   private final QueryFactory queryFactory;
   private final OrcsTokenService tokenService;
   private final OrcsApi orcsApi;

   public CommitBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, OrcsApi orcsApi, ArtifactId committer, BranchId source, BranchId destination, OrcsTokenService tokenService) {
      super(logger, session, branchStore);
      this.committer = committer;
      this.source = source;
      this.destination = destination;
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.tokenService = tokenService;
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

      TransactionId newTx = getBranchStore().commitBranch(getSession(), committer, tokenService, sourceBranch,
         sourceHead, destinationBranch, destinationHead, orcsApi);
      return queryFactory.transactionQuery().andTxId(newTx).getResults().getExactlyOne();
   }
}