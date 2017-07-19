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
package org.eclipse.osee.orcs.core.ds;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author Roberto E. Escobar
 */
public interface BranchDataStore {

   void addMissingApplicabilityFromParentBranch(BranchId branch);

   void createBranch(CreateBranchData branchData);

   void createBranchCopyTx(CreateBranchData branchData);

   Callable<Void> purgeBranch(OrcsSession session, BranchReadable branch);

   Callable<TransactionId> commitBranch(OrcsSession session, ArtifactId committer, BranchReadable source, TransactionToken sourceHead, BranchReadable destination, TransactionToken destinationHead, ApplicabilityQuery applicQuery);

   Callable<List<ChangeItem>> compareBranch(OrcsSession session, TransactionToken sourceTx, TransactionToken destinationTx, ApplicabilityQuery applicQuery);

   Callable<URI> exportBranch(OrcsSession session, OrcsTypes orcsTypes, List<? extends BranchId> branches, PropertyStore options, String exportName);

   Callable<URI> importBranch(OrcsSession session, OrcsTypes orcsTypes, URI fileToImport, List<? extends BranchId> branches, PropertyStore options);

   Callable<URI> checkBranchExchangeIntegrity(OrcsSession session, URI fileToCheck);

   Callable<Void> changeBranchState(OrcsSession session, BranchId branch, BranchState branchState);

   Callable<Void> changeBranchType(OrcsSession session, BranchId branch, BranchType branchType);

   Callable<Void> changeBranchName(OrcsSession session, BranchId branch, String branchName);

   Callable<Void> changeBranchAssociatedArt(OrcsSession session, BranchId branch, ArtifactId assocArt);

   Callable<Void> archiveUnArchiveBranch(OrcsSession session, BranchId branch, ArchiveOperation op);

   Callable<Void> deleteBranch(OrcsSession session, BranchId branch);

}
