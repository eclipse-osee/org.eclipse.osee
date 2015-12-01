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
import java.util.function.Predicate;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface BranchReadable extends IOseeBranch {

   BranchType getBranchType();

   BranchState getBranchState();

   boolean isArchived();

   Integer getAssociatedArtifactId() throws OseeCoreException;

   TransactionRecord getBaseTransaction() throws OseeCoreException;

   TransactionRecord getSourceTransaction() throws OseeCoreException;

   BranchId getParentBranch() throws OseeCoreException;

   Collection<? extends BranchReadable> getAllChildBranches(boolean recurse) throws OseeCoreException;

   void getChildBranches(Collection<BranchReadable> children, boolean recurse, Predicate<BranchReadable> filter);

   Collection<? extends BranchId> getAncestors() throws OseeCoreException;

   boolean isAncestorOf(BranchId branch) throws OseeCoreException;
}