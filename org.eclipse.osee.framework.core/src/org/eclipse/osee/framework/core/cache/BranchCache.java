/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.cache;

import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class BranchCache extends AbstractOseeCache<Branch> {
   public BranchCache(IOseeDataAccessor<Branch> dataAccessor) {
      super(OseeCacheEnum.BRANCH_CACHE, dataAccessor, false);
   }

   public Branch getSystemRootBranch() throws OseeCoreException {
      ensurePopulated();
      return get(CoreBranches.SYSTEM_ROOT);
   }

   public Branch getCommonBranch() throws OseeCoreException {
      ensurePopulated();
      return get(CoreBranches.COMMON);
   }

   public MergeBranch findMergeBranch(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      Conditions.checkNotNull(destinationBranch, "destination branch");
      MergeBranch toReturn = null;
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch()) && destinationBranch.equals(mergeBranch.getDestinationBranch())) {
               toReturn = mergeBranch;
               break;
            }
         }
      }
      return toReturn;
   }
}
