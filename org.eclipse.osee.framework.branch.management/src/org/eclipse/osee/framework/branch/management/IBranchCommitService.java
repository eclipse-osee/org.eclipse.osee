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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 */
public interface IBranchCommitService {

   public IStatus commitBranch(IProgressMonitor monitor, IBasicArtifact<?> user, Branch sourceBranch, Branch destinationBranch, boolean archiveSourceBranch) throws OseeCoreException;

   public IStatus commitBranch(IProgressMonitor monitor, BranchCommitData branchCommitData) throws OseeCoreException;
}
