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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author Roberto E. Escobar
 */
public interface BranchDataStore {

   Callable<Branch> createBranch(OrcsSession session, CreateBranchData branchData);

   Callable<Branch> createBranchCopyTx(OrcsSession session, CreateBranchData branchData);

   Callable<Branch> purgeBranch(OrcsSession session, Branch branch);

   Callable<TransactionRecord> commitBranch(OrcsSession session, ArtifactReadable committer, Branch source, Branch destination);

   Callable<List<ChangeItem>> compareBranch(OrcsSession session, TransactionRecord sourceTx, TransactionRecord destinationTx);

   Callable<URI> exportBranch(OrcsSession session, OrcsTypes orcsTypes, List<IOseeBranch> branches, PropertyStore options, String exportName);

   Callable<URI> importBranch(OrcsSession session, OrcsTypes orcsTypes, URI fileToImport, List<IOseeBranch> branches, PropertyStore options);

   Callable<URI> checkBranchExchangeIntegrity(OrcsSession session, URI fileToCheck);

   Callable<Void> changeBranchState(OrcsSession session, IOseeBranch branch, BranchState newState);

   Callable<Void> changeBranchType(OrcsSession session, IOseeBranch branch, BranchType newType);

   Callable<Void> archiveUnArchiveBranch(OrcsSession session, IOseeBranch branch, ArchiveOperation op);

}
