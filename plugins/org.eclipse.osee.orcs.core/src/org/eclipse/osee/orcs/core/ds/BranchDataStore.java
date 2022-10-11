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

package org.eclipse.osee.orcs.core.ds;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author Roberto E. Escobar
 */
public interface BranchDataStore {

   void addMissingApplicabilityFromParentBranch(BranchId branch);

   void createBranch(CreateBranchData branchData, UserService userService);

   void createBranchCopyTx(CreateBranchData branchData, UserService userService);

   Callable<Void> purgeBranch(OrcsSession session, Branch branch);

   TransactionId commitBranch(OrcsSession session, ArtifactId committer, OrcsTokenService tokenService, Branch source, TransactionToken sourceHead, Branch destination, TransactionToken destinationHead, OrcsApi orcsApi);

   List<ChangeItem> compareBranch(OrcsSession session, OrcsTokenService tokenService, TransactionToken sourceTx, TransactionToken destinationTx, OrcsApi orcsApi);

   Callable<URI> exportBranch(OrcsSession session, List<? extends BranchId> branches, PropertyStore options, String exportName);

   Callable<URI> importBranch(OrcsSession session, URI fileToImport, List<? extends BranchId> branches, PropertyStore options);

   Callable<Void> changeBranchState(OrcsSession session, BranchId branch, BranchState branchState);

   Callable<Void> changeBranchType(OrcsSession session, BranchId branch, BranchType branchType);

   Callable<Void> changeBranchName(OrcsSession session, BranchId branch, String branchName);

   Callable<Void> changeBranchAssociatedArt(OrcsSession session, BranchId branch, ArtifactId assocArt);

   Callable<Void> archiveUnArchiveBranch(OrcsSession session, BranchId branch, ArchiveOperation op);

   Callable<Void> deleteBranch(OrcsSession session, BranchId branch);

   void setBranchPermission(ArtifactId subject, BranchId branch, PermissionEnum permission);

   XResultData createBranchValidation(CreateBranchData branchData, UserService userService);
}