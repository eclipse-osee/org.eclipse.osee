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

package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.List;
import org.junit.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactQueryTestDemo {

   @Test
   public void testGetArtifactFromGUIDDeleted() throws OseeCoreException {
      Artifact newArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArtifact.persist();

      // Should exist
      Artifact searchedArtifact =
         ArtifactQuery.getArtifactFromId(newArtifact.getGuid(), BranchManager.getCommonBranch());
      Assert.assertNotNull(searchedArtifact);

      // Should exist with allowDeleted
      searchedArtifact =
         ArtifactQuery.getArtifactFromId(newArtifact.getGuid(), BranchManager.getCommonBranch(), INCLUDE_DELETED);
      Assert.assertNotNull(searchedArtifact);

      newArtifact.deleteAndPersist();

      try {
         Artifact ret = ArtifactQuery.checkArtifactFromId(newArtifact.getGuid(), BranchManager.getCommonBranch());
         Assert.assertNull(ret);
      } catch (ArtifactDoesNotExist ex) {
         Assert.fail("ArtifactQuery should never throw ArtifactDoesNotExist with QueryType.CHECK");
      }

      // Should NOT exist, cause deleted
      try {
         ArtifactQuery.getArtifactFromId(newArtifact.getGuid(), BranchManager.getCommonBranch());
         Assert.fail("artifact query should have thrown does not exist exception");
      } catch (ArtifactDoesNotExist ex) {
         // do nothing, this is the expected case
      }

      // Should still exist with allowDeleted
      searchedArtifact =
         ArtifactQuery.getArtifactFromId(newArtifact.getGuid(), BranchManager.getCommonBranch(), INCLUDE_DELETED);
      Assert.assertNotNull(searchedArtifact);

   }

   @Test
   public void testGetArtifactListFromType() throws OseeCoreException {
      // Should exist
      List<Artifact> searchedArtifacts =
         ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SoftwareRequirement, DeletionFlag.INCLUDE_DELETED);
      // make sure at least one artifact exists
      Assert.assertTrue("No artifacts found", searchedArtifacts.size() > 0);

      //check to see if there are multiple branches found
      String firstGuid = "";
      Boolean pass = false;
      for (Artifact a : searchedArtifacts) {
         if ("" == firstGuid) {
            firstGuid = a.getBranchGuid();
         } else {
            if (firstGuid != a.getBranchGuid()) {
               pass = true;
               break;
            }
         }
      }
      Assert.assertTrue("No artifacts on multiple branches found", pass);
   }

   @Test
   public void testGetOrCreate() throws OseeCoreException {
      String guid = GUID.create();
      Branch branch = BranchManager.createTopLevelBranch("test branch");
      Artifact artifact1 = ArtifactQuery.getOrCreate(guid, null, CoreArtifactTypes.GeneralData, branch);
      Assert.assertNotNull(artifact1);
      Artifact artifact2 = ArtifactQuery.getOrCreate(guid, null, CoreArtifactTypes.GeneralData, branch);
      Assert.assertEquals(artifact1, artifact2);
      BranchManager.deleteBranch(branch);
   }
}