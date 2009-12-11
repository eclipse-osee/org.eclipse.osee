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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryTest {

   @org.junit.Test
   public void testGetArtifactFromHRID() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(common);
      Artifact artifact = ArtifactQuery.getArtifactFromId(root.getHumanReadableId(), common);
      assertEquals(root.getHumanReadableId(), artifact.getHumanReadableId());
   }

   @org.junit.Test
   public void testGetArtifactFromGUID() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(common);
      Artifact artifact = ArtifactQuery.getArtifactFromId(root.getGuid(), common);
      assertEquals(root.getGuid(), artifact.getGuid());
   }

   @org.junit.Test
   public void testGetArtifactsFromBranch() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromBranch(common, true);

      assertTrue(artifacts.size() > 0);
      for (Artifact artifact : artifacts) {
         assertTrue(artifact.getName().length() > 0);
         artifact.isOrphan(); // this is good exercise - like doing push-ups
      }
   }
}