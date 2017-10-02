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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class MultiBranchProviderTest {

   @Test
   public void testGetBranchesRecursive() {
      BranchFilter filter = new BranchFilter();
      filter.setNegatedBranchTypes(BranchType.BASELINE);
      Set<Branch> branchRoots = new HashSet<>();

      Collection<Branch> testBranchesA = MockBranchProvider.createTestBranches();
      Collection<Branch> testBranchesB = MockBranchProvider.createTestBranches();

      branchRoots.add(MockBranchProvider.getRootBranch(testBranchesA));
      branchRoots.add(MockBranchProvider.getRootBranch(testBranchesB));

      MultiBranchProvider provider = new MultiBranchProvider(true, branchRoots, filter);
      int numBranches = provider.getBranches().size();
      int expectedSize = testBranchesA.size() + testBranchesB.size();
      Assert.assertEquals(expectedSize, numBranches);
   }

   @Test
   public void testGetBranchesNonRecursive() {
      BranchFilter filter = new BranchFilter();
      filter.setNegatedBranchTypes(BranchType.BASELINE);

      Collection<Branch> testBranches = MockBranchProvider.createTestBranches();

      MultiBranchProvider provider1 =
         new MultiBranchProvider(false, Collections.singleton(MockBranchProvider.getRootBranch(testBranches)), filter);
      int numBranches = provider1.getBranches().size();
      Assert.assertEquals(1, numBranches);

      Set<Branch> branches = new HashSet<>(MockBranchProvider.createTestBranches());
      MultiBranchProvider provider2 = new MultiBranchProvider(false, branches, filter);
      numBranches = provider2.getBranches().size();
      Assert.assertEquals(branches.size(), numBranches);
   }

   @Test(expected = OseeCoreException.class)
   public void testGetBranchesException() {
      MultiBranchProvider provider = new MultiBranchProvider(true, null, null);
      provider.getBranches().size();
   }
}