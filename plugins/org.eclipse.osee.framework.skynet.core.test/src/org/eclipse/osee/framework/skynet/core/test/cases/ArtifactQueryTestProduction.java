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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryTestProduction {

   @org.junit.Test
   public void testGetArtifactFromHRID() throws OseeCoreException {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());
      Artifact artifact = ArtifactQuery.getArtifactFromId(root.getHumanReadableId(), BranchManager.getCommonBranch());
      assertEquals(root.getHumanReadableId(), artifact.getHumanReadableId());
   }

   @org.junit.Test
   public void testGetArtifactFromGUID() throws OseeCoreException {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());
      Artifact artifact = ArtifactQuery.getArtifactFromId(root.getGuid(), BranchManager.getCommonBranch());
      assertEquals(root.getGuid(), artifact.getGuid());
   }

}