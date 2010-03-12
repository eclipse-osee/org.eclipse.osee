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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.Before;

/**
 * @author Andrew M Finkbeiner
 */
public class RelationDeletionTest {

   private static final String ARTIFACT_TYPE = "Folder";

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(ClientSessionManager.isProductionDataStore());
   }

   @org.junit.Test
   public void testDeleteRelationPersistsBothSides() throws Exception {
      SevereLoggingMonitor monitor = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitor);

      Branch branch = BranchManager.getCommonBranch();
      Artifact parent = createArtifact(ARTIFACT_TYPE, branch);
      Artifact child1 = createArtifact(ARTIFACT_TYPE, branch);
      Artifact child2 = createArtifact(ARTIFACT_TYPE, branch);
      Artifact child3 = createArtifact(ARTIFACT_TYPE, branch);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child1);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child2);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child3);
      parent.persist();

      assertTrue("Failed to add all three children", parent.getRelatedArtifacts(
            CoreRelationTypes.Default_Hierarchical__Child).size() == 3);

      child1.deleteRelation(CoreRelationTypes.Default_Hierarchical__Parent, parent);

      assertTrue("We removed a relation so it should still be dirty.", child1.isDirty());
      assertTrue("Parent artifact should be marked as dirty since it's relation has changed.", parent.isDirty());

      child1.persist();

      assertFalse("Parent artifact should be clean now.", parent.isDirty());
      assertFalse("Child artifact should also be clean.", child1.isDirty());

      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);

      assertTrue("The deleted child was not successfully removed.", children.size() == 2);

      assertTrue("Child2 is not the first in the list and it should be.", children.get(0) == child2);

      assertTrue(monitor.toString(), monitor.getSevereLogs().size() == 0);
   }

   private Artifact createArtifact(String type, Branch branch) throws OseeCoreException {
      return ArtifactTypeManager.makeNewArtifact(ArtifactTypeManager.getType(ARTIFACT_TYPE), branch);
   }
}
