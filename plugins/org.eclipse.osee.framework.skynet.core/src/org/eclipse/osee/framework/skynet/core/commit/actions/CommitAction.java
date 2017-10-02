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
package org.eclipse.osee.framework.skynet.core.commit.actions;

import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Theron Virgin
 */
public interface CommitAction {
   public void runCommitAction(BranchId sourceBranch, BranchId destinationBranch) ;
}