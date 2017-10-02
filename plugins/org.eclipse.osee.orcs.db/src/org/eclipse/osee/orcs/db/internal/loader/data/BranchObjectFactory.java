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
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.BranchData;

/**
 * @author Roberto E. Escobar
 */
public interface BranchObjectFactory extends OrcsDataFactory {

   BranchData createBranchData(BranchId branch, BranchType branchType, String name, BranchId parentBranch, TransactionId baseTransaction, TransactionId sourceTransaction, BranchArchivedState archiveState, BranchState branchState, ArtifactId associatedArtifact, boolean inheritAccessControl) ;

   BranchData createCopy(BranchData source) ;
}
