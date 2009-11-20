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
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 */
public interface IBranchCommitService {

   public void commitBranch(IProgressMonitor monitor, BranchCommitRequest branchCommitData, BranchCommitResponse response) throws OseeCoreException;
}
