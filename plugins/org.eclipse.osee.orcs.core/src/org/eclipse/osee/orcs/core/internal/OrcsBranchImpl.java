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
package org.eclipse.osee.orcs.core.internal;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.branch.BranchDataFactory;
import org.eclipse.osee.orcs.core.internal.branch.CommitBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.CompareBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.CreateBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.PurgeBranchCallable;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsBranchImpl implements OrcsBranch {

   private final Log logger;

   private final OrcsSession session;
   private final BranchDataStore branchStore;
   private final BranchDataFactory branchDataFactory;
   private final OrcsTypes orcsTypes;
   private final QueryFactory queryFactory;

   public OrcsBranchImpl(Log logger, OrcsSession session, BranchDataStore branchStore, QueryFactory queryFactory, OrcsTypes orcsTypes) {
      this.logger = logger;
      this.session = session;
      this.branchStore = branchStore;
      branchDataFactory = new BranchDataFactory(queryFactory);
      this.orcsTypes = orcsTypes;
      this.queryFactory = queryFactory;
   }

   @Override
   public Callable<BranchReadable> createBranch(CreateBranchData branchData) {
      return new CreateBranchCallable(logger, session, branchStore, branchData, queryFactory);
   }

   @Override
   public Callable<Void> archiveUnarchiveBranch(BranchId branch, ArchiveOperation archiveOp) {
      return branchStore.archiveUnArchiveBranch(session, branch, archiveOp);
   }

   @Override
   public Callable<Void> deleteBranch(BranchId branch) {
      return branchStore.deleteBranch(session, branch);
   }

   @Override
   public Callable<List<BranchId>> purgeBranch(BranchId branch, boolean recurse) {
      return new PurgeBranchCallable(logger, session, branchStore, branch, recurse, queryFactory);
   }

   @Override
   public Callable<TransactionToken> commitBranch(ArtifactId committer, BranchId source, BranchId destination) {
      return new CommitBranchCallable(logger, session, branchStore, queryFactory, committer, source, destination);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(TransactionToken sourceTx, TransactionToken destinationTx) {
      return new CompareBranchCallable(logger, session, branchStore, sourceTx, destinationTx,
         queryFactory.applicabilityQuery());
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(BranchId branch)  {
      TransactionId baseTransaction =
         queryFactory.branchQuery().andId(branch).getResults().getExactlyOne().getBaseTransaction();
      TransactionToken fromTx = queryFactory.transactionQuery().andTxId(baseTransaction).getResults().getExactlyOne();
      TransactionToken toTx = queryFactory.transactionQuery().andIsHead(branch).getResults().getExactlyOne();
      return branchStore.compareBranch(session, fromTx, toTx, queryFactory.applicabilityQuery());
   }

   @Override
   public Callable<Void> changeBranchState(BranchId branch, BranchState branchState) {
      return branchStore.changeBranchState(session, branch, branchState);
   }

   @Override
   public Callable<Void> changeBranchType(BranchId branch, BranchType branchType) {
      return branchStore.changeBranchType(session, branch, branchType);
   }

   @Override
   public Callable<Void> changeBranchName(BranchId branch, String branchName) {
      return branchStore.changeBranchName(session, branch, branchName);
   }

   @Override
   public Callable<Void> associateBranchToArtifact(BranchId branch, ArtifactId associatedArtifact) {
      Conditions.checkNotNull(associatedArtifact, "associatedArtifact");
      return branchStore.changeBranchAssociatedArt(session, branch, associatedArtifact);
   }

   @Override
   public Callable<Void> unassociateBranch(BranchId branch) {
      return branchStore.changeBranchAssociatedArt(session, branch, ArtifactId.SENTINEL);
   }

   @Override
   public Callable<URI> exportBranch(List<? extends BranchId> branches, PropertyStore options, String exportName) {
      return branchStore.exportBranch(session, orcsTypes, branches, options, exportName);
   }

   @Override
   public Callable<URI> importBranch(URI fileToImport, List<? extends BranchId> branches, PropertyStore options) {
      return branchStore.importBranch(session, orcsTypes, fileToImport, branches, options);
   }

   @Override
   public Callable<URI> checkBranchExchangeIntegrity(URI fileToCheck) {
      return branchStore.checkBranchExchangeIntegrity(session, fileToCheck);
   }

   @Override
   public Callable<BranchReadable> createTopLevelBranch(IOseeBranch branch, ArtifactId author)  {
      CreateBranchData branchData = branchDataFactory.createTopLevelBranchData(branch, author);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createBaselineBranch(IOseeBranch branch, ArtifactId author, IOseeBranch parent, ArtifactId associatedArtifact)  {
      CreateBranchData branchData =
         branchDataFactory.createBaselineBranchData(branch, author, parent, associatedArtifact);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createWorkingBranch(IOseeBranch branch, ArtifactId author, IOseeBranch parent, ArtifactId associatedArtifact)  {
      CreateBranchData branchData =
         branchDataFactory.createWorkingBranchData(branch, author, parent, associatedArtifact);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createCopyTxBranch(IOseeBranch branch, ArtifactId author, TransactionId fromTransaction, ArtifactId associatedArtifact)  {
      CreateBranchData branchData =
         branchDataFactory.createCopyTxBranchData(branch, author, fromTransaction, associatedArtifact);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createPortBranch(IOseeBranch branch, ArtifactId author, TransactionId fromTransaction, ArtifactId associatedArtifact)  {
      CreateBranchData branchData =
         branchDataFactory.createPortBranchData(branch, author, fromTransaction, associatedArtifact);
      return createBranch(branchData);
   }

   @Override
   public void addMissingApplicabilityFromParentBranch(BranchId branch) {
      branchStore.addMissingApplicabilityFromParentBranch(branch);
   }
}
