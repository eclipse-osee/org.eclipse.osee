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

package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.OseeField;

/**
 * @author Roberto E. Escobar
 */
public final class MergeBranch extends Branch {

   public MergeBranch(String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) {
      super(guid, name, branchType, branchState, isArchived);
   }

   @Override
   protected void initializeFields() {
      super.initializeFields();
      addField(BranchField.MERGE_BRANCH_SOURCE, new OseeField<Branch>());
      addField(BranchField.MERGE_BRANCH_DESTINATION, new OseeField<Branch>());
   }

   public Branch getSourceBranch() throws OseeCoreException {
      return getFieldValue(BranchField.MERGE_BRANCH_SOURCE);
   }

   public Branch getDestinationBranch() throws OseeCoreException {
      return getFieldValue(BranchField.MERGE_BRANCH_DESTINATION);
   }

   public void setSourceBranch(Branch branch) throws OseeCoreException {
      setField(BranchField.MERGE_BRANCH_SOURCE, branch);
   }

   public void setDestinationBranch(Branch branch) throws OseeCoreException {
      setField(BranchField.MERGE_BRANCH_DESTINATION, branch);
   }
}