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

import junit.framework.Assert;
import org.eclipse.osee.framework.branch.management.purge.RecursiveBranchProvider;
import org.eclipse.osee.framework.branch.management.test.mocks.MockBranchProvider;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class RecursiveBranchProviderTest {

   @Test
   public void testGetBranches() throws OseeCoreException {
      MockBranchProvider mbp = new MockBranchProvider();
      BranchFilter filter = new BranchFilter();
      filter.setNegatedBranchTypes(BranchType.BASELINE);

      RecursiveBranchProvider provider = new RecursiveBranchProvider(mbp.getRootBranch(), filter);
      int numBranches = provider.getBranches().size();
      Assert.assertEquals(numBranches, 8);
   }

   @Test(expected = OseeCoreException.class)
   public void testGetBranchesException() throws OseeCoreException {
      RecursiveBranchProvider provider = new RecursiveBranchProvider(null, null);
      provider.getBranches().size();
   }
}