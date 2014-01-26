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
package org.eclipse.osee.framework.core.model;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface BranchReadable extends IOseeBranch {

   long getId();

   boolean isEditable();

   BranchType getBranchType();

   BranchState getBranchState();

   BranchArchivedState getArchiveState();

   Integer getAssociatedArtifactId() throws OseeCoreException;

   TransactionRecord getBaseTransaction() throws OseeCoreException;

   TransactionRecord getSourceTransaction() throws OseeCoreException;

   BranchReadable getParentBranch() throws OseeCoreException;

   boolean hasParentBranch() throws OseeCoreException;

   Collection<? extends BranchReadable> getChildBranches() throws OseeCoreException;

   Collection<? extends BranchReadable> getChildBranches(boolean recurse) throws OseeCoreException;

   Collection<? extends BranchReadable> getAllChildBranches(boolean recurse) throws OseeCoreException;

   void getChildBranches(Collection<? extends BranchReadable> children, boolean recurse, BranchFilter filter) throws OseeCoreException;

   Collection<? extends BranchReadable> getAncestors() throws OseeCoreException;

   boolean isAncestorOf(IOseeBranch branch) throws OseeCoreException;

   String getShortName();

}
