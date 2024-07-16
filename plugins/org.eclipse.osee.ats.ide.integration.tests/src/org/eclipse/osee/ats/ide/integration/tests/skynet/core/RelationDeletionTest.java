/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationDeletionTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   private List<Artifact> artifacts;

   @Before
   public void setup() {
      artifacts = new ArrayList<>();
   }

   @After
   public void cleanUp() {
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
   }

   @Test
   public void testDeleteRelationPersistsBothSides() throws Exception {
      Artifact parent = createArtifact(CoreArtifactTypes.Folder, COMMON);
      Artifact child1 = createArtifact(CoreArtifactTypes.Folder, COMMON);
      Artifact child2 = createArtifact(CoreArtifactTypes.Folder, COMMON);
      Artifact child3 = createArtifact(CoreArtifactTypes.Folder, COMMON);
      parent.addRelation(CoreRelationTypes.DefaultHierarchical_Child, child1);
      parent.addRelation(CoreRelationTypes.DefaultHierarchical_Child, child2);
      parent.addRelation(CoreRelationTypes.DefaultHierarchical_Child, child3);
      parent.persist(getClass().getSimpleName());

      assertTrue("Failed to add all three children",
         parent.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Child).size() == 3);

      child1.deleteRelation(CoreRelationTypes.DefaultHierarchical_Parent, parent);

      assertTrue("We removed a relation so it should still be dirty.", child1.isDirty());
      assertTrue("Parent artifact should be marked as dirty since it's relation has changed.", parent.isDirty());

      child1.persist(getClass().getSimpleName());

      assertFalse("Parent artifact should be clean now.", parent.isDirty());
      assertFalse("Child artifact should also be clean.", child1.isDirty());

      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Child);

      assertTrue("The deleted child was not successfully removed.", children.size() == 2);

      assertTrue("Child2 is not the first in the list and it should be.", children.get(0) == child2);
   }

   @Test
   public void testDeleteThenUnDeleteRelation() {
      Artifact parent = createArtifact(CoreArtifactTypes.Folder, COMMON);
      Artifact child1 = createArtifact(CoreArtifactTypes.Folder, COMMON);

      parent.addRelation(CoreRelationTypes.DefaultHierarchical_Child, child1);
      parent.persist(getClass().getSimpleName());

      assertTrue("Failed to add child",
         parent.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Child).size() == 1);

      child1.deleteRelation(CoreRelationTypes.DefaultHierarchical_Parent, parent);

      assertTrue("We removed a relation so it should still be dirty.", child1.isDirty());
      assertTrue("Parent artifact should be marked as dirty since it's relation has changed.", parent.isDirty());

      child1.persist(getClass().getSimpleName());

      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Child);

      assertTrue("The deleted child was not successfully removed.", children.size() == 0);

      parent.addRelation(CoreRelationTypes.DefaultHierarchical_Child, child1);

      assertFalse("This previously deleted child still has modification type deleted", child1.isDeleted());
      parent.persist(getClass().getSimpleName());

      assertTrue("Failed to add child previously deleted child relation",
         parent.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Child).size() == 1);

   }

   private Artifact createArtifact(ArtifactTypeToken artifactType, BranchToken branch) {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(artifactType, branch);
      artifacts.add(newArtifact);
      return newArtifact;
   }
}
