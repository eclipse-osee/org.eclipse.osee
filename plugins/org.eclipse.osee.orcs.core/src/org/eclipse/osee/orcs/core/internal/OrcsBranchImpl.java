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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchReadable;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.branch.ArchiveUnarchiveBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.BranchDataFactory;
import org.eclipse.osee.orcs.core.internal.branch.CommitBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.CompareBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.CreateBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.DeleteBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.PurgeBranchCallable;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
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

   private final BranchCache branchCache;
   private final TransactionCache txCache;

   public OrcsBranchImpl(Log logger, OrcsSession session, BranchDataStore branchStore, QueryFactory queryFactory, LazyObject<ArtifactReadable> systemUser, OrcsTypes orcsTypes, BranchCache branchCache, TransactionCache txCache) {
      this.logger = logger;
      this.session = session;
      this.branchStore = branchStore;
      branchDataFactory = new BranchDataFactory(queryFactory);
      this.orcsTypes = orcsTypes;
      this.branchCache = branchCache;
      branchDataFactory = new BranchDataFactory(branchCache);
   }

   @Override
   public Callable<BranchReadable> createBranch(CreateBranchData branchData) {
      return new CreateBranchCallable(logger, session, branchStore, branchData);
   }

   @Override
   public Callable<BranchReadable> archiveUnarchiveBranch(IOseeBranch branch, ArchiveOperation archiveOp) {
      return new ArchiveUnarchiveBranchCallable(logger, session, branchStore, branchCache, branch, archiveOp);
   }

   @Override
   public Callable<BranchReadable> deleteBranch(IOseeBranch branch) {
      return new DeleteBranchCallable(logger, session, branchStore, branchCache, branch);
   }

   @Override
   public Callable<List<IOseeBranch>> purgeBranch(IOseeBranch branch, boolean recurse) {
      return new PurgeBranchCallable(logger, session, branchStore, branchCache, branch, recurse);
   }

   @Override
   public Callable<TransactionRecord> commitBranch(ArtifactReadable committer, IOseeBranch source, IOseeBranch destination) {
      return new CommitBranchCallable(logger, session, branchStore, branchCache, committer, source, destination);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(ITransaction sourceTx, ITransaction destinationTx) {
      return new CompareBranchCallable(logger, session, branchCache, branchStore, sourceTx, destinationTx);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(IOseeBranch branch) throws OseeCoreException {
      Branch fullBranch = branchCache.get(branch);
      TransactionRecord fromTx = fullBranch.getBaseTransaction();
      TransactionRecord toTx = branchCache.getHeadTransaction(fullBranch);
      return branchStore.compareBranch(session, fromTx, toTx);
   }

   @Override
   public Callable<Void> changeBranchState(IOseeBranch branch, BranchState newState) {
      return branchStore.changeBranchState(session, branch, newState);
   }

   @Override
   public Callable<Void> changeBranchType(IOseeBranch branch, BranchType branchType) {
      return branchStore.changeBranchType(session, branch, branchType);
   }

   @Override
   public Callable<URI> exportBranch(List<IOseeBranch> branches, PropertyStore options, String exportName) {
      return branchStore.exportBranch(session, orcsTypes, branches, options, exportName);
   }

   @Override
   public Callable<URI> importBranch(URI fileToImport, List<IOseeBranch> branches, PropertyStore options) {
      return branchStore.importBranch(session, orcsTypes, fileToImport, branches, options);
   }

   @Override
   public Callable<URI> checkBranchExchangeIntegrity(URI fileToCheck) {
      return branchStore.checkBranchExchangeIntegrity(session, fileToCheck);
   }

   @Override
   public Callable<BranchReadable> createTopLevelBranch(IOseeBranch branch, ArtifactReadable author) throws OseeCoreException {
      CreateBranchData branchData = branchDataFactory.createTopLevelBranchData(branch, author);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createBaselineBranch(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      CreateBranchData branchData =
         branchDataFactory.createBaselineBranchData(branch, author, parent, associatedArtifact);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createWorkingBranch(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      CreateBranchData branchData =
         branchDataFactory.createWorkingBranchData(branch, author, parent, associatedArtifact);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createCopyTxBranch(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException {
      CreateBranchData branchData =
         branchDataFactory.createCopyTxBranchData(branch, author, fromTransaction, associatedArtifact);
      return createBranch(branchData);
   }

   @Override
   public Callable<BranchReadable> createPortBranch(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException {
      CreateBranchData branchData =
         branchDataFactory.createPortBranchData(branch, author, fromTransaction, associatedArtifact);
      return createBranch(branchData);
   }
}
