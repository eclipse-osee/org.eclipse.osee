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

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsBranch {

   Callable<ReadableBranch> createBranch(CreateBranchData branchData);

   Callable<ReadableBranch> archiveUnarchiveBranch(IOseeBranch branch, ArchiveOperation archiveOp);

   Callable<ReadableBranch> deleteBranch(IOseeBranch branch);

   Callable<List<ReadableBranch>> purgeBranch(IOseeBranch branch, boolean recurse);

   Callable<TransactionRecord> commitBranch(ReadableArtifact committer, IOseeBranch source, IOseeBranch destination);

   Callable<List<ChangeItem>> compareBranch(ITransaction sourceTx, ITransaction destinationTx);

   Callable<ReadableBranch> changeBranchState(IOseeBranch branch, BranchState newState);

   Callable<ReadableBranch> changeBranchType(IOseeBranch branch, BranchType branchType);

   // For backwards compatibility - should be removed in the future or added to branchQuery
   ReadableBranch getBranchFromId(int id) throws OseeCoreException;

}
