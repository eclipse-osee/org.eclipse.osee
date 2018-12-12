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
package org.eclipse.osee.orcs;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsBranch {

   // In Table
   /// branch Uuid
   /// branch guid
   /// parent txId
   /// parent branch Uuid
   /// isArchived
   /// baseline TxId

   // Branch Metadata
   // branch guid -- artifact guid
   // branch name
   // branch type
   // branch state
   // assoc art id

   IOseeBranch createTopLevelBranch(IOseeBranch branch, ArtifactId account);

   Branch createBaselineBranch(IOseeBranch branch, ArtifactId author, IOseeBranch parent, ArtifactId associatedArtifact);

   Branch createWorkingBranch(IOseeBranch branch, ArtifactId author, IOseeBranch parent, ArtifactId associatedArtifact);

   Branch createCopyTxBranch(IOseeBranch branch, ArtifactId author, TransactionId fromTransaction, ArtifactId associatedArtifact);

   Branch createPortBranch(IOseeBranch branch, ArtifactId author, TransactionId fromTransaction, ArtifactId associatedArtifact);

   Branch createBranch(CreateBranchData branchData);

   /////////////////////////////////////////////////////////////////////////

   void setBranchPermission(ArtifactId subject, BranchId branch, PermissionEnum permission);

   Callable<Void> changeBranchState(BranchId branch, BranchState branchState);

   Callable<Void> changeBranchType(BranchId branch, BranchType branchType);

   Callable<Void> changeBranchName(BranchId branch, String name);

   Callable<Void> associateBranchToArtifact(BranchId branch, ArtifactId associatedArtifact);

   Callable<Void> unassociateBranch(BranchId branch);

   Callable<Void> deleteBranch(BranchId branch);

   /////////////////////////////////////////////////////////////////////////

   Callable<Void> archiveUnarchiveBranch(BranchId branch, ArchiveOperation archiveOp);

   Callable<List<BranchId>> purgeBranch(BranchId branch, boolean recurse);

   Callable<TransactionToken> commitBranch(ArtifactId committer, BranchId source, BranchId destination);

   List<ChangeItem> compareBranch(TransactionToken sourceTx, TransactionToken destinationTx);

   List<ChangeItem> compareBranch(BranchId branch);

   Callable<URI> exportBranch(List<? extends BranchId> branches, PropertyStore options, String exportName);

   Callable<URI> importBranch(URI fileToImport, List<? extends BranchId> branches, PropertyStore options);

   Callable<URI> checkBranchExchangeIntegrity(URI fileToCheck);

   void addMissingApplicabilityFromParentBranch(BranchId branch);

}