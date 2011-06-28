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
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.rule.OseeHousekeepingRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationDeletionTest {

   private final List<Artifact> artifacts = new ArrayList<Artifact>();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(ClientSessionManager.isProductionDataStore());
   }

   @After
   public void cleanUp() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
   }

   @Test
   public void testDeleteRelationPersistsBothSides() throws Exception {
      SevereLoggingMonitor monitor = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitor);

      Branch branch = BranchManager.getCommonBranch();
      Artifact parent = createArtifact(CoreArtifactTypes.Folder, branch);
      Artifact child1 = createArtifact(CoreArtifactTypes.Folder, branch);
      Artifact child2 = createArtifact(CoreArtifactTypes.Folder, branch);
      Artifact child3 = createArtifact(CoreArtifactTypes.Folder, branch);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child1);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child2);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child3);
      parent.persist(getClass().getSimpleName());

      assertTrue("Failed to add all three children",
         parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child).size() == 3);

      child1.deleteRelation(CoreRelationTypes.Default_Hierarchical__Parent, parent);

      assertTrue("We removed a relation so it should still be dirty.", child1.isDirty());
      assertTrue("Parent artifact should be marked as dirty since it's relation has changed.", parent.isDirty());

      child1.persist(getClass().getSimpleName());

      assertFalse("Parent artifact should be clean now.", parent.isDirty());
      assertFalse("Child artifact should also be clean.", child1.isDirty());

      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);

      assertTrue("The deleted child was not successfully removed.", children.size() == 2);

      assertTrue("Child2 is not the first in the list and it should be.", children.get(0) == child2);

      assertTrue(monitor.toString(), monitor.getSevereLogs().isEmpty());
   }

   private Artifact createArtifact(IArtifactType artifactType, Branch branch) throws OseeCoreException {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(artifactType, branch);
      artifacts.add(newArtifact);
      return newArtifact;
   }
}
