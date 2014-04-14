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
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class BranchFactory implements IOseeTypeFactory {

   public Branch create(long uuid, String name, BranchType branchType, BranchState branchState, boolean isArchived, boolean inheritAccessControl) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(name, "branch name");
      Conditions.checkNotNull(branchType, "branch type");
      Conditions.checkNotNull(branchState, "branch state");

      Branch toReturn;
      if (branchType.isMergeBranch()) {
         toReturn = new MergeBranch(uuid, name, branchType, branchState, isArchived, inheritAccessControl);
      } else {
         toReturn = new Branch(uuid, name, branchType, branchState, isArchived, inheritAccessControl);
      }
      return toReturn;
   }

   public Branch createOrUpdate(AbstractOseeCache<Long, Branch> cache, long uuid, String name, BranchType branchType, BranchState branchState, boolean isArchived, boolean inheritAccessControl) throws OseeCoreException {
      Conditions.checkNotNull(cache, "BranchCache");
      Branch branch = cache.getByGuid(uuid);
      if (branch == null) {
         branch = create(uuid, name, branchType, branchState, isArchived, inheritAccessControl);
         cache.cache(branch);
      } else {
         branch.setName(name);
         branch.setArchived(isArchived);
         branch.setBranchState(branchState);
         branch.setBranchType(branchType);
         branch.setInheritAccessControl(inheritAccessControl);
      }
      return branch;
   }

   public Branch createOrUpdate(IOseeCache<Long, Branch> cache, long uuid, String name, BranchType branchType, BranchState branchState, boolean isArchived, StorageState storageState, boolean inheritAccessControl) throws OseeCoreException {
      Conditions.checkNotNull(cache, "BranchCache");
      Branch branch = cache.getById(uuid);
      if (branch == null) {
         branch = create(uuid, name, branchType, branchState, isArchived, inheritAccessControl);
         branch.setStorageState(storageState);
         cache.cache(branch);
      } else {
         branch.setName(name);
         branch.setArchived(isArchived);
         branch.setBranchState(branchState);
         branch.setBranchType(branchType);
         branch.setStorageState(storageState);
         branch.setInheritAccessControl(inheritAccessControl);
      }
      return branch;
   }
}
