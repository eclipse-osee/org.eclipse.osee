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
import org.eclipse.osee.framework.core.data.LazyObject;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.branch.ArchiveUnarchiveBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.ChangeBranchStateCallable;
import org.eclipse.osee.orcs.core.internal.branch.ChangeBranchTypeCallable;
import org.eclipse.osee.orcs.core.internal.branch.CommitBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.CompareBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.CreateBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.DeleteBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.PurgeBranchCallable;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author Roberto E. Escobar
 */
public class OrcsBranchImpl implements OrcsBranch {

   private final Log logger;

   private final SessionContext sessionContext;
   private final BranchDataStore branchStore;
   private final BranchCache branchCache;
   private final TransactionCache txCache;
   private final LazyObject<ArtifactReadable> systemUser;

   public OrcsBranchImpl(Log logger, SessionContext sessionContext, BranchDataStore branchStore, BranchCache branchCache, TransactionCache txCache, LazyObject<ArtifactReadable> systemUser) {
      this.logger = logger;
      this.sessionContext = sessionContext;
      this.branchStore = branchStore;
      this.branchCache = branchCache;
      this.txCache = txCache;
      this.systemUser = systemUser;
   }

   @Override
   public Callable<ReadableBranch> createBranch(CreateBranchData newBranchData) {
      return new CreateBranchCallable(logger, sessionContext, branchStore, newBranchData);
   }

   @Override
   public Callable<ReadableBranch> archiveUnarchiveBranch(IOseeBranch branch, ArchiveOperation archiveOp) {
      return new ArchiveUnarchiveBranchCallable(logger, sessionContext, branchStore, branchCache, branch, archiveOp);
   }

   @Override
   public Callable<ReadableBranch> deleteBranch(IOseeBranch branch) {
      return new DeleteBranchCallable(logger, sessionContext, branchStore, branchCache, branch);
   }

   @Override
   public Callable<List<ReadableBranch>> purgeBranch(IOseeBranch branch, boolean recurse) {
      return new PurgeBranchCallable(logger, sessionContext, branchStore, branchCache, branch, recurse);
   }

   @Override
   public Callable<TransactionRecord> commitBranch(ArtifactReadable committer, IOseeBranch source, IOseeBranch destination) {
      return new CommitBranchCallable(logger, sessionContext, branchStore, branchCache, committer, source, destination);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(ITransaction sourceTx, ITransaction destinationTx) {
      return new CompareBranchCallable(logger, sessionContext, branchStore, txCache, sourceTx, destinationTx);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(IOseeBranch branch) throws OseeCoreException {
      return branchStore.compareBranch(sessionContext.toString(), branchCache.get(branch));
   }

   @Override
   public Callable<ReadableBranch> changeBranchState(IOseeBranch branch, BranchState newState) {
      return new ChangeBranchStateCallable(logger, sessionContext, branchStore, branchCache, branch, newState);
   }

   @Override
   public Callable<ReadableBranch> changeBranchType(IOseeBranch branch, BranchType branchType) {
      return new ChangeBranchTypeCallable(logger, sessionContext, branchStore, branchCache, branch, branchType);
   }

   @Override
   public ReadableBranch getBranchFromId(int id) throws OseeCoreException {
      return branchCache.getById(id);
   }

   @Override
   public Callable<URI> exportBranch(List<IOseeBranch> branches, PropertyStore options, String exportName) {
      return branchStore.exportBranch(sessionContext.toString(), branches, options, exportName);
   }

   @Override
   public Callable<URI> importBranch(URI fileToImport, List<IOseeBranch> branches, PropertyStore options) {
      return branchStore.importBranch(sessionContext.toString(), fileToImport, branches, options);
   }

   @Override
   public Callable<URI> checkBranchExchangeIntegrity(URI fileToCheck) {
      return branchStore.checkBranchExchangeIntegrity(sessionContext.toString(), fileToCheck);
   }

   @Override
   public ReadableBranch createTopLevelBranch(ArtifactReadable author, String branchName) throws OseeCoreException {
      return createBranch(author, branchCache.getSystemRootBranch(), branchName, BranchType.BASELINE);
   }

   @Override
   public ReadableBranch createChildBranch(ArtifactReadable author, IOseeBranch parent, String childBranchName) throws OseeCoreException {
      return createBranch(author, branchCache.get(parent), childBranchName, BranchType.WORKING);
   }

   private ReadableBranch createBranch(ArtifactReadable author, Branch parent, String branchName, BranchType branchType) throws OseeCoreException {
      CreateBranchData createData = new CreateBranchData();
      String creationComment = String.format("Branch Creation for %s", branchName);
      TransactionRecord headTransaction = txCache.getHeadTransaction(parent);
      createData.setGuid(GUID.create());
      createData.setName(branchName);
      createData.setBranchType(branchType);
      createData.setCreationComment(creationComment);
      createData.setFromTransaction(TokenFactory.createTransaction(headTransaction.getId()));
      createData.setUserArtifact(author);
      createData.setAssociatedArtifact(systemUser.get());
      createData.setMergeDestinationBranchId(-1);
      createData.setMergeAddressingQueryId(-1);
      createData.setTxCopyBranchType(false);
      Callable<ReadableBranch> callable = createBranch(createData);
      ReadableBranch branch = null;
      try {
         branch = callable.call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return branch;
   }

}
