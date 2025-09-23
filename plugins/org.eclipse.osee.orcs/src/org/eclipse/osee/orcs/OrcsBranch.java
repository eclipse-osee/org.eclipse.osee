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

package org.eclipse.osee.orcs;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
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

   BranchToken createTopLevelBranch(BranchToken branch);

   Branch createBaselineBranch(BranchToken branch, BranchToken parent, ArtifactId associatedArtifact);

   Branch createWorkingBranch(BranchToken branch, BranchToken parent, ArtifactId associatedArtifact);

   Branch createCopyTxBranch(BranchToken branch, TransactionId fromTransaction, ArtifactId associatedArtifact);

   Branch createPortBranch(BranchToken branch, TransactionId fromTransaction, ArtifactId associatedArtifact);

   Branch createBranch(CreateBranchData branchData);

   /////////////////////////////////////////////////////////////////////////

   PermissionEnum getBranchPermission(ArtifactId subject, BranchId branch);

   void setBranchPermission(ArtifactId subject, BranchId branch, PermissionEnum permission);

   XResultData changeBranchState(BranchId branch, BranchState branchState);

   XResultData changeBranchType(BranchId branch, BranchType branchType);

   XResultData changeBranchName(BranchId branch, String name);

   XResultData associateBranchToArtifact(BranchId branch, ArtifactId associatedArtifact);

   XResultData unassociateBranch(BranchId branch);

   XResultData deleteBranch(BranchId branch);

   /////////////////////////////////////////////////////////////////////////

   Callable<List<BranchId>> purgeBranch(BranchId branch, boolean recurse);

   Callable<TransactionToken> commitBranch(ArtifactId committer, BranchId source, BranchId destination);

   List<ChangeItem> compareBranch(TransactionToken sourceTx, TransactionToken destinationTx);

   List<ChangeItem> compareBranch(BranchId branch);

   Callable<URI> exportBranch(List<? extends BranchId> branches, PropertyStore options, String exportName);

   Callable<URI> importBranch(URI fileToImport, List<? extends BranchId> branches, PropertyStore options);

   void addMissingApplicabilityFromParentBranch(BranchId branch);

   BranchToken createProgramBranch(BranchToken branch);

   XResultData createBranchValidation(CreateBranchData branchData);

   XResultData setBranchCategory(BranchId branch, BranchCategoryToken category);

   XResultData setBranchCategory(BranchId branch, UserId asUser, BranchCategoryToken category);

   XResultData deleteBranchCategory(BranchId branch, BranchCategoryToken category);

   XResultData deleteBranchCategory(BranchId branch, UserId asUser, BranchCategoryToken category);

   BranchToken createProgramBranch(BranchToken branch, BranchToken parent);

   boolean setBranchState(BranchId branchId, BranchState newState);

   XResultData unarchiveBranch(BranchId branch);

   XResultData archiveBranch(BranchId branch);

}