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
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class BranchFactory implements IOseeTypeFactory {

   public Branch create(String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(name, "branch name");
      Conditions.checkNotNull(branchType, "branch type");
      Conditions.checkNotNull(branchState, "branch state");
      String checkedGuid = Conditions.checkGuidCreateIfNeeded(guid);

      Branch toReturn;
      if (branchType.isMergeBranch()) {
         toReturn = new MergeBranch(checkedGuid, name, branchType, branchState, isArchived);
      } else {
         toReturn = new Branch(checkedGuid, name, branchType, branchState, isArchived);
      }
      return toReturn;
   }

   public Branch createOrUpdate(AbstractOseeCache<String, Branch> cache, String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException {
      Conditions.checkNotNull(cache, "BranchCache");
      Branch branch = cache.getByGuid(guid);
      if (branch == null) {
         branch = create(guid, name, branchType, branchState, isArchived);
         cache.cache(branch);
      } else {
         branch.setName(name);
         branch.setArchived(isArchived);
         branch.setBranchState(branchState);
         branch.setBranchType(branchType);
      }
      return branch;
   }

   public Branch createOrUpdate(IOseeCache<String, Branch> cache, long uniqueId, StorageState storageState, String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException {
      Conditions.checkNotNull(cache, "BranchCache");
      Branch branch = cache.getById(uniqueId);
      if (branch == null) {
         branch = create(guid, name, branchType, branchState, isArchived);
         branch.setId(uniqueId);
         branch.setStorageState(storageState);
         cache.cache(branch);
      } else {
         branch.setName(name);
         branch.setArchived(isArchived);
         branch.setBranchState(branchState);
         branch.setBranchType(branchType);
         branch.setStorageState(storageState);
      }
      return branch;
   }
}
