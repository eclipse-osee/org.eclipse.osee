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

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Roberto E. Escobar
 */
public interface BranchDataStore {

   Callable<Branch> createBranch(String sessionId, CreateBranchData branchData);

   Callable<Branch> purgeBranch(String sessionId, Branch branch);

   Callable<TransactionRecord> commitBranch(String sessionId, ReadableArtifact committer, Branch source, Branch destination);

   Callable<List<ChangeItem>> compareBranch(String sessionId, TransactionRecord sourceTx, TransactionRecord destinationTx);


   Callable<Branch> deleteRelationTypeFromBranch(IOseeBranch branch, IRelationTypeSide relationType, int aArtId, int bArtId, int artUserId, String comment);
}
