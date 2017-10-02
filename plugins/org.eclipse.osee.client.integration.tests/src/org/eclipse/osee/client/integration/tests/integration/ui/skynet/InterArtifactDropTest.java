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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandlerOperation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests cross branch drag and drop.
 *
 * @author Jeff C. Phillips
 * @author Megumi Telles
 */
public class InterArtifactDropTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static Artifact sourceArtifact1;
   private static Artifact sourceArtifact2;
   private static Artifact sourceArtifact3;
   private static Artifact sourceChildArtifact1;
   private static Artifact sourceDeleteArtifact1;

   private static IOseeBranch sourceBranch;
   private static IOseeBranch destinationBranch;
   private static IOseeBranch updateTestBranch1;
   private static IOseeBranch updateTestBranch2;
   private static IOseeBranch updateTestBranch3;

   private static boolean wasInitialized = false;

   @Before
   public void setUp() throws Exception {
      // Initialization must run after the integration rule
      // Can't use before class here
      if (!wasInitialized) {
         wasInitialized = true;
         initializeBranches();
         addArtifactsToBranches();
      }
   }

   @AfterClass
   public static void tearDown()  {
      if (wasInitialized) {
         purgeBranches();
      }
   }

   @Test
   public void testIntroduceCrossBranch() throws Exception {
      // source artifact newly introduced to destination branch
      InterArtifactExplorerDropHandlerOperation dropHandler = new InterArtifactExplorerDropHandlerOperation(
         OseeSystemArtifacts.getDefaultHierarchyRootArtifact(destinationBranch), new Artifact[] {sourceArtifact1},
         false);
      Operations.executeWork(dropHandler);
      // Acquire the introduced artifact
      Artifact destArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact1, destinationBranch);

      assertTrue(sourceArtifact1.getName().equals(destArtifact.getName()));
      assertTrue(sourceArtifact1.getGammaId() == destArtifact.getGammaId());
      assertTrue(sourceArtifact1.getModType() == destArtifact.getModType());
   }

   @Test
   public void testUpdateBranch() throws Exception {
      // source artifact updated and introduced to existing destination
      // artifact
      Artifact updateTestArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact1, updateTestBranch2);
      Attribute<Object> testAttr = updateTestArtifact.getSoleAttribute(CoreAttributeTypes.Name);
      testAttr.setFromString("I am an update branch test");
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(updateTestBranch2);
      root.addChild(updateTestArtifact);
      updateTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact1, new Artifact[] {updateTestArtifact}, false);
      Operations.executeWork(dropHandler);

      // Acquire the updated artifact
      Artifact destArtifact = ArtifactQuery.getArtifactFromId(updateTestArtifact, sourceArtifact1.getBranch());
      destArtifact.reloadAttributesAndRelations();
      assertTrue(updateTestArtifact.getName().equals(destArtifact.getName()));
      assertTrue(updateTestArtifact.getGammaId() == destArtifact.getGammaId());
      assertTrue(updateTestArtifact.getModType() == destArtifact.getModType());
   }

   @Test
   public void testDeleteAttributeFromArtifactBranch() throws Exception {
      // attribute deleted and introduced to existing attribute on
      // sourceBranch
      Artifact deleteTestArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact2, updateTestBranch1);
      Attribute<Object> destAttribute = deleteTestArtifact.getSoleAttribute(CoreAttributeTypes.StaticId);
      deleteTestArtifact.deleteAttribute(destAttribute);
      deleteTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact2, new Artifact[] {deleteTestArtifact}, false);
      Operations.executeWork(dropHandler);

      Artifact destArtifact = ArtifactQuery.getArtifactFromId(deleteTestArtifact, sourceArtifact2.getBranch());
      Attribute<?> destAttr = destArtifact.getAttributeById(destAttribute.getId(), true);
      assertTrue(destAttr.getModificationType().equals(ModificationType.DELETED));
   }

   @Test
   public void testDeleteRelationFromArtifactBranch() {
      // relation deleted and introduced to existing relation on sourceBranch
      Artifact deleteTestArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact2, updateTestBranch1);
      deleteTestArtifact.deleteRelation(CoreRelationTypes.Default_Hierarchical__Child, sourceChildArtifact1);
      deleteTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact2, new Artifact[] {deleteTestArtifact}, false);
      Operations.executeWork(dropHandler);

      Artifact destArtifact = ArtifactQuery.getArtifactFromId(deleteTestArtifact, sourceArtifact2.getBranch());
      for (RelationLink relLink : destArtifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
         if (relLink.getArtifactIdA().equals(sourceArtifact2.getId()) && relLink.getArtifactIdB().equals(
            sourceChildArtifact1.getId())) {
            assertTrue(relLink.getModificationType().equals(ModificationType.DELETED));
         }
      }
   }

   @Test
   public void testDeleteArtifactBranch() throws Exception {
      // artifact deleted and introduced to existing destination sourceBranch
      Artifact deleteTestArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact2, updateTestBranch1);
      Attribute<Object> destAttribute = deleteTestArtifact.getSoleAttribute(CoreAttributeTypes.Name);
      deleteTestArtifact.delete();
      deleteTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact2, new Artifact[] {deleteTestArtifact}, false);
      Operations.executeWork(dropHandler);

      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(deleteTestArtifact, sourceArtifact2.getBranch(), DeletionFlag.INCLUDE_DELETED);
      assertTrue(destArtifact.getModType().equals(ModificationType.DELETED));
      Attribute<?> destAttr = destArtifact.getAttributeById(destAttribute.getId(), true);
      assertTrue(destAttr.getModificationType().equals(ModificationType.ARTIFACT_DELETED));
      for (RelationLink relLink : destArtifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
         assertTrue(relLink.getModificationType().equals(ModificationType.DELETED));
      }
   }

   @Test
   public void testDestinationArtifactDoesNotExistAsDeletedBranch() throws Exception {
      // artifact deleted on source branch, does not exist on destination
      // branch
      // the deleted artifact will be introduced as deleted
      InterArtifactExplorerDropHandlerOperation dropHandler = new InterArtifactExplorerDropHandlerOperation(
         OseeSystemArtifacts.getDefaultHierarchyRootArtifact(destinationBranch), new Artifact[] {sourceDeleteArtifact1},
         false);
      Operations.executeWork(dropHandler);

      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(sourceDeleteArtifact1, destinationBranch, DeletionFlag.INCLUDE_DELETED);
      // artifact should be introduced
      assertNotNull(destArtifact);
      assertTrue(sourceDeleteArtifact1.getName().equals(destArtifact.getName()));
      assertTrue(sourceDeleteArtifact1.getGammaId() == destArtifact.getGammaId());
      assertTrue(sourceDeleteArtifact1.getModType() == destArtifact.getModType());
   }

   @Test
   public void testUndeleteArtifactOnBranch() throws Exception {
      // artifact deleted on updateBranch2 and introduced as non-deleted
      // artifact on updateTestBranch2
      Artifact testArtifact = ArtifactQuery.getArtifactFromId(sourceDeleteArtifact1, updateTestBranch3);

      InterArtifactExplorerDropHandlerOperation dropHandler = new InterArtifactExplorerDropHandlerOperation(
         OseeSystemArtifacts.getDefaultHierarchyRootArtifact(updateTestBranch2), new Artifact[] {testArtifact}, false);
      Operations.executeWork(dropHandler);

      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(sourceDeleteArtifact1, updateTestBranch2, DeletionFlag.EXCLUDE_DELETED);
      // artifact should be introduced
      assertNotNull(destArtifact);
      assertTrue(testArtifact.getName().equals(destArtifact.getName()));
      assertTrue(testArtifact.getGammaId() == destArtifact.getGammaId());
      assertTrue(testArtifact.getModType() == destArtifact.getModType());
   }

   private void addArtifactsToBranches()  {
      BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, sourceBranch);
      BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, destinationBranch);

      // Add artifacts to source
      sourceArtifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact1.persist(method.getQualifiedTestName());
      sourceChildArtifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceChildArtifact1.persist(method.getQualifiedTestName());
      // sourceArtifact2 has an additional attribute and a child artifact
      // associated to it
      sourceArtifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact2.addAttribute(CoreAttributeTypes.StaticId);
      sourceArtifact2.addChild(sourceChildArtifact1);
      sourceArtifact2.persist(method.getQualifiedTestName());

      // updateTestBranch has sourceArtifact1/2, sourceChildArtifact1 and
      // sourceDeleteArtifact
      BranchManager.createWorkingBranch(sourceBranch, updateTestBranch1);

      sourceArtifact3 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact3.persist(method.getQualifiedTestName());
      sourceDeleteArtifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceDeleteArtifact1.persist(method.getQualifiedTestName());

      // updateTestBranch has sourceArtifact1/2/3, sourceChildArtifact1,
      // sourceDeleteArtifact1
      BranchManager.createWorkingBranch(sourceBranch, updateTestBranch3);

      sourceDeleteArtifact1.delete();
      sourceDeleteArtifact1.persist(method.getQualifiedTestName());

      // updateTestBranch has sourceArtifact1/2/3, sourceChildArtifact1,
      // sourceDeleteArtifact1 (deleted)
      BranchManager.createWorkingBranch(sourceBranch, updateTestBranch2);
   }

   private void initializeBranches() {
      sourceBranch = createBranchToken("Source");
      destinationBranch = createBranchToken("Destination");
      updateTestBranch1 = createBranchToken("updateTestBranch");
      updateTestBranch2 = createBranchToken("updateTestBranch");
      updateTestBranch3 = createBranchToken("updateTestBranch");
   }

   private static void purgeBranches()  {
      BranchManager.purgeBranch(destinationBranch);
      BranchManager.purgeBranch(updateTestBranch1);
      BranchManager.purgeBranch(updateTestBranch2);
      BranchManager.purgeBranch(updateTestBranch3);
      BranchManager.purgeBranch(sourceBranch);
   }

   private IOseeBranch createBranchToken(String name) {
      String branchName = String.format("%s__%s", method.getQualifiedTestName(), name);
      return IOseeBranch.create(branchName);
   }
}
