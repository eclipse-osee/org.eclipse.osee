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
package org.eclipse.osee.framework.core.test.data;

import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCommitRequest}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BranchCommitRequestTest {

   private final BranchCommitRequest item;

   private final IBasicArtifact<?> expectedUser;
   private final Branch expectedSrcBranch;
   private final Branch expectedDestBranch;
   private final boolean expectedArchive;

   public BranchCommitRequestTest(IBasicArtifact<?> expectedUser, Branch expectedSrcBranch, Branch expectedDestBranch, boolean expectedArchive) {
      super();
      this.item = new BranchCommitRequest(expectedUser, expectedSrcBranch, expectedDestBranch, expectedArchive);
      this.expectedUser = expectedUser;
      this.expectedSrcBranch = expectedSrcBranch;
      this.expectedDestBranch = expectedDestBranch;
      this.expectedArchive = expectedArchive;
   }

   @Test
   public void testGetDestinationBranch() {
      Assert.assertEquals(expectedDestBranch, item.getDestinationBranch());
   }

   @Test
   public void testGetSourceBranch() {
      Assert.assertEquals(expectedSrcBranch, item.getSourceBranch());
   }

   @Test
   public void testGetUser() {
      Assert.assertEquals(expectedUser, item.getUser());
   }

   @Test
   public void testIsArchive() {
      Assert.assertEquals(expectedArchive, item.isArchiveAllowed());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();
      boolean archiveIt = false;
      for (int index = 1; index <= 2; index++) {
         Branch sourceBranch = MockDataFactory.createBranch(index);
         Branch destinationBranch = MockDataFactory.createBranch(index + 1);
         IBasicArtifact<?> userArt = MockDataFactory.createArtifact(index);
         archiveIt ^= archiveIt;
         data.add(new Object[] {userArt, sourceBranch, destinationBranch, archiveIt});
      }
      return data;
   }

}
