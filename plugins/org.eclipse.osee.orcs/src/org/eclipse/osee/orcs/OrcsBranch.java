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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.TransactionReadable;

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

   Callable<BranchReadable> createTopLevelBranch(IOseeBranch branch, ArtifactReadable author) throws OseeCoreException;

   Callable<BranchReadable> createBaselineBranch(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException;

   Callable<BranchReadable> createWorkingBranch(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException;

   Callable<BranchReadable> createCopyTxBranch(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException;

   Callable<BranchReadable> createPortBranch(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException;

   /////////////////////////////////////////////////////////////////////////

   Callable<Void> changeBranchState(IOseeBranch branch, BranchState branchState);

   Callable<Void> changeBranchType(IOseeBranch branch, BranchType branchType);

   Callable<Void> changeBranchName(IOseeBranch branch, String name);

   Callable<Void> associateBranchToArtifact(IOseeBranch branch, ArtifactReadable associatedArtifact);

   Callable<Void> unassociateBranch(IOseeBranch branch);

   Callable<Void> deleteBranch(IOseeBranch branch);

   /////////////////////////////////////////////////////////////////////////

   Callable<BranchReadable> createBranch(CreateBranchData branchData);

   Callable<Void> archiveUnarchiveBranch(IOseeBranch branch, ArchiveOperation archiveOp);

   Callable<List<IOseeBranch>> purgeBranch(IOseeBranch branch, boolean recurse);

   Callable<TransactionReadable> commitBranch(ArtifactReadable committer, IOseeBranch source, IOseeBranch destination);

   Callable<List<ChangeItem>> compareBranch(TransactionReadable sourceTx, TransactionReadable destinationTx);

   Callable<List<ChangeItem>> compareBranch(IOseeBranch branch) throws OseeCoreException;

   Callable<URI> exportBranch(List<IOseeBranch> branches, PropertyStore options, String exportName);

   Callable<URI> importBranch(URI fileToImport, List<IOseeBranch> branches, PropertyStore options);

   Callable<URI> checkBranchExchangeIntegrity(URI fileToCheck);

}
