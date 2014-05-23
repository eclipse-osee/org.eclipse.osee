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
package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public interface BranchReadable extends HasLocalId<Long>, IOseeBranch {

   BranchArchivedState getArchiveState();

   BranchState getBranchState();

   BranchType getBranchType();

   boolean hasParentBranch();

   // These get Id method might change
   int getAssociatedArtifactId();

   int getBaseTransaction();

   int getSourceTransaction();

   long getParentBranch();

   boolean isInheritAccessControl();

}