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
package org.eclipse.osee.framework.branch.management.test.purge;

import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.branch.management.purge.DeletedBranchProvider;
import org.eclipse.osee.framework.branch.management.test.mocks.MockBranchProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.test.mocks.MockOseeDataAccessor;
import org.junit.Test;

/**
 * @author John Misinco
 */
public final class DeletedBranchProviderTest {

   @Test
   public void testGetBranches() throws OseeCoreException {
      Collection<Branch> branches;

      MockBranchProvider mbp = new MockBranchProvider();
      BranchCache mockCache = new BranchCache(new MockOseeDataAccessor<Branch>());

      branches = mbp.getBranches();
      mockCache.cache(branches.toArray(new Branch[branches.size()]));

      DeletedBranchProvider provider = new DeletedBranchProvider(mockCache);
      int numBranches = provider.getBranches().size();
      Assert.assertEquals(2, numBranches);
   }

   @Test(expected = OseeCoreException.class)
   public void testGetBranchesException() throws OseeCoreException {
      DeletedBranchProvider provider = new DeletedBranchProvider(null);
      provider.getBranches().size();
   }
}