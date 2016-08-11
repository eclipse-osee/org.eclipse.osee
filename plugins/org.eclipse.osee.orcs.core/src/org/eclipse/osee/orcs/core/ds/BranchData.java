/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public interface BranchData extends IOseeBranch {

   ArtifactId getAssociatedArtifact();

   void setAssociatedArtifact(ArtifactId artId);

   TransactionId getBaseTransaction();

   void setBaseTransaction(TransactionId baseTx);

   TransactionId getSourceTransaction();

   void setSourceTransaction(TransactionId sourceTx);

   BranchId getParentBranch();

   void setParentBranch(BranchId parent);

   boolean hasParentBranch();

   BranchArchivedState getArchiveState();

   void setArchiveState(BranchArchivedState state);

   BranchState getBranchState();

   void setBranchState(BranchState state);

   BranchType getBranchType();

   void setBranchType(BranchType type);

   boolean isInheritAccessControl();

   void setInheritAccessControl(boolean inheritAccessControl);

}
