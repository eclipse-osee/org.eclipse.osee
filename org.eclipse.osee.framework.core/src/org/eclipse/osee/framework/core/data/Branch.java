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

package org.eclipse.osee.framework.core.data;

import java.util.Collection;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public abstract class Branch extends AbstractOseeType implements Comparable<Branch>, IAccessControllable, IAdaptable {

   protected Branch(String guid, String name) {
      super(guid, name);
   }

   public abstract Branch getParentBranch() throws OseeCoreException;

   public abstract boolean hasParentBranch() throws OseeCoreException;

   public abstract String getShortName();

   public abstract BranchType getBranchType();

   public abstract BranchState getBranchState();

   public abstract BranchArchivedState getArchiveState();

   public abstract IBasicArtifact<?> getAssociatedArtifact() throws OseeCoreException;

   public abstract void setAssociatedArtifact(IBasicArtifact<?> artifact) throws OseeCoreException;

   public abstract TransactionRecord getBaseTransaction() throws OseeCoreException;

   public abstract TransactionRecord getSourceTransaction() throws OseeCoreException;

   public abstract Collection<String> getAliases() throws OseeCoreException;

   public abstract void setAliases(String... alias) throws OseeCoreException;

   public abstract void setArchived(boolean isArchived);

   public abstract void setBranchState(BranchState branchState);

   public abstract void setBranchType(BranchType branchType);

   public abstract boolean isEditable();

   public abstract int compareTo(Branch other);

   public abstract Collection<Branch> getChildren() throws OseeCoreException;

   public abstract Branch getAccessControlBranch();

   public abstract Collection<Branch> getChildBranches() throws OseeCoreException;

   public abstract Collection<Branch> getChildBranches(boolean recurse) throws OseeCoreException;

   public abstract Collection<Branch> getAncestors() throws OseeCoreException;

   public abstract Collection<Branch> getWorkingBranches() throws OseeCoreException;

   @SuppressWarnings("unchecked")
   public abstract Object getAdapter(Class adapter);

}