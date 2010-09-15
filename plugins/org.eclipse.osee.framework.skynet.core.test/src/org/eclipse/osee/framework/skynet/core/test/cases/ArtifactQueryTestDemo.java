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
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class ArtifactQueryTestDemo {

   @org.junit.Test
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
}