/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.branch.provider;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public final class DeletedBranchProviderTest {

   private int expectedResult(Collection<Branch> branches) {
      int result = 0;
      for (Branch branch : branches) {
         if (branch.getBranchState() == BranchState.DELETED && branch.isArchived() && !(branch.getBranchType().isBaselineBranch())) {
            result++;
         }
      }
      return result;
   }

   @Test
   public void testGetBranches() {
      BranchCache mockCache = new BranchCache(new MockOseeDataAccessor<Branch>());

      Collection<Branch> branches = MockBranchProvider.createTestBranches();
      mockCache.cache(branches.toArray(new Branch[branches.size()]));

      DeletedBranchProvider provider = new DeletedBranchProvider(mockCache);
      int numBranches = provider.getBranches().size();
      Assert.assertEquals(expectedResult(branches), numBranches);
   }

   @Test(expected = OseeCoreException.class)
   public void testGetBranchesException() {
      DeletedBranchProvider provider = new DeletedBranchProvider(null);
      provider.getBranches().size();
   }
}