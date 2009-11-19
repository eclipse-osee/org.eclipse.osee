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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.test.util.BranchTestUtil;
import org.eclipse.osee.framework.core.test.util.UserArtifactTestUtil;
import org.junit.Test;

/**
 * Test Case for {@link BranchCommitData}
 * 
 * @author Megumi Telles
 */

public class BranchCommitDataTest {
   private final String GUID = "gjdkfghfr183848754";
   private final String BRANCH_NAME = "test branch";
   private final BranchState BRANCH_STATE = BranchState.CREATED;
   private final BranchType BRANCH_TYPE = BranchType.BASELINE;
   private final boolean isArchived = false;
   private final String user = "Tom Jones";

   @Test
   public void testBranchCommitDataConstruction() {
      BranchTestUtil sourceBranch =
            new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BranchType.WORKING, BranchArchivedState.UNARCHIVED,
                  isArchived);
      BranchTestUtil destinationBranch =
            new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BRANCH_TYPE, BranchArchivedState.UNARCHIVED, isArchived);
      UserArtifactTestUtil userArt = new UserArtifactTestUtil(user, "EJDFKGJDFKGJ19394FDJDLF", 999999);
      BranchCommitData data =
            new BranchCommitData(getUserArtifact(), getSourceBranch(), getDestinationBranch(), isArchived);
      assertEquals(getUserArtifact(), data.getUser());
      assertEquals(getSourceBranch(), data.getSourceBranch());
      assertEquals(getDestinationBranch(), data.getDestinationBranch());
      assertEquals(isArchived, data.isArchiveAllowed());

      sourceBranch = null;
      assertNotSame(getSourceBranch(), data.getSourceBranch());

      destinationBranch = null;
      assertNotSame(getDestinationBranch(), data.getDestinationBranch());

      assertNotSame(true, data.isArchiveAllowed());

   }

   private BranchTestUtil getSourceBranch() {
      return new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BranchType.WORKING, BranchArchivedState.UNARCHIVED,
            isArchived);
   }

   private BranchTestUtil getDestinationBranch() {
      return new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BRANCH_TYPE, BranchArchivedState.UNARCHIVED,
            isArchived);
   }

   private UserArtifactTestUtil getUserArtifact() {
      return new UserArtifactTestUtil(user, "EJDFKGJDFKGJ19394FDJDLF", 999999);
   }

}
