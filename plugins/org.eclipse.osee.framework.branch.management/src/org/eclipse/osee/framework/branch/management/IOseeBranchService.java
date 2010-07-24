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

package org.eclipse.osee.framework.branch.management;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.BranchCommitRequest;
import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.message.BranchCreationRequest;
import org.eclipse.osee.framework.core.message.BranchCreationResponse;
import org.eclipse.osee.framework.core.message.ChangeBranchArchiveStateRequest;
import org.eclipse.osee.framework.core.message.ChangeBranchStateRequest;
import org.eclipse.osee.framework.core.message.ChangeBranchTypeRequest;
import org.eclipse.osee.framework.core.message.ChangeReportRequest;
import org.eclipse.osee.framework.core.message.ChangeReportResponse;
import org.eclipse.osee.framework.core.message.PurgeBranchRequest;
import org.eclipse.osee.framework.core.operation.IOperation;

/**
 * @author Jeff C. Phillips
 */
public interface IOseeBranchService {
   void commitBranch(IProgressMonitor monitor, BranchCommitRequest branchCommitData, BranchCommitResponse response) throws OseeCoreException;

   IOperation getChanges(IProgressMonitor monitor, ChangeReportRequest request, ChangeReportResponse response) throws OseeCoreException;

   IOperation createBranch(IProgressMonitor monitor, BranchCreationRequest request, BranchCreationResponse response) throws OseeCoreException;

   IOperation purge(IProgressMonitor monitor, PurgeBranchRequest request) throws OseeCoreException;

   IOperation updateBranchType(IProgressMonitor monitor, ChangeBranchTypeRequest request) throws OseeCoreException;

   IOperation updateBranchState(IProgressMonitor monitor, ChangeBranchStateRequest request) throws OseeCoreException;

   IOperation updateBranchArchiveState(IProgressMonitor monitor, ChangeBranchArchiveStateRequest request) throws OseeCoreException;

   IOperation createSystemRootBranch(IProgressMonitor monitor) throws OseeCoreException;
}
