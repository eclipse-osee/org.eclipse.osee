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

import static java.lang.Thread.sleep;
import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandlerOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests cross branch drag and drop.
 * 
 * @author Jeff C. Phillips
 */
public class InterArtifactDropTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private Artifact sourceArtifact1;
   private Artifact sourceArtifact2;
   private Artifact sourceArtifact3;
   private Artifact sourceArtifact4;
   private Artifact sourceChildArtifact1;
   private Artifact sourceChildArtifact2;
   private Artifact sourceDeleteArtifact;

   private IOseeBranch sourceBranch;
   private IOseeBranch destinationBranch;
   private IOseeBranch updateTestDestinationBranch;
   private IOseeBranch updateTestParentSourceBranch;
   private IOseeBranch updateTestSourceBranch;

   @Before
   public void setUp() throws Exception {
      sourceBranch = createBranchToken("Source");
      destinationBranch = createBranchToken("Destination");
      destinationBranch = createBranchToken("UpdateDestinationBranch");
      updateTestParentSourceBranch = createBranchToken("updateTestParentSource");
      updateTestSourceBranch = createBranchToken("updateTestSource");

      BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, sourceBranch);
      BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, destinationBranch);

      // Add artifacts to source
      sourceArtifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact1.persist(method.getQualifiedTestName());
      sourceChildArtifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceChildArtifact1.persist(method.getQualifiedTestName());
      // sourceArtifact2 has an additional attribute and a child artifact associated to it
      sourceArtifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact2.addAttribute(CoreAttributeTypes.StaticId);
      sourceArtifact2.addChild(sourceChildArtifact1);
      sourceArtifact2.persist(method.getQualifiedTestName());

      sourceDeleteArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceDeleteArtifact.persist(method.getQualifiedTestName());

      BranchManager.createWorkingBranch(sourceBranch, updateTestDestinationBranch);

      sourceArtifact3 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact3.persist(method.getQualifiedTestName());
      sourceChildArtifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceChildArtifact2.persist(method.getQualifiedTestName());
      sourceArtifact4 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact4.addChild(sourceChildArtifact2);
      sourceArtifact4.persist(method.getQualifiedTestName());

      BranchManager.createWorkingBranch(sourceBranch, updateTestParentSourceBranch);
      BranchManager.createWorkingBranch(updateTestParentSourceBranch, updateTestSourceBranch);
   }

   @After
   public void tearDown() throws OseeCoreException {
      BranchManager.purgeBranch(destinationBranch);
      BranchManager.purgeBranch(updateTestDestinationBranch);
      BranchManager.purgeBranch(updateTestSourceBranch);
      BranchManager.purgeBranch(updateTestParentSourceBranch);
      BranchManager.purgeBranch(sourceBranch);
   }

   @Test
   public void testIntroduceCrossBranch() throws Exception {
      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(destinationBranch), new Artifact[] {sourceArtifact1},
            false);
      Operations.executeWork(dropHandler);
      sleep(5000);
      //Acquire the introduced artifact
      Artifact destArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact1.getArtId(), destinationBranch);

      assertTrue(sourceArtifact1.getName().equals(destArtifact.getName()));
   }

   @Test
   public void testUpdateBranch() throws Exception {
      Artifact updateTestArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact1.getArtId(), updateTestSourceBranch);
      updateTestArtifact.setName("I am an update branch test");
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(updateTestSourceBranch);
      root.addChild(updateTestArtifact);
      updateTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact1, new Artifact[] {updateTestArtifact}, false);
      Operations.executeWork(dropHandler);

      sleep(5000);
      //Acquire the updated artifact
      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(updateTestArtifact.getArtId(), sourceArtifact1.getBranch());
      destArtifact.reloadAttributesAndRelations();
      assertTrue(updateTestArtifact.getName().equals(destArtifact.getName()));
   }

   @Test
   public void testDeleteArtifactBranch() throws Exception {
      Artifact deleteTestArtifact =
         ArtifactQuery.getArtifactFromId(sourceArtifact2.getArtId(), updateTestDestinationBranch);
      Attribute<Object> destAttribute = deleteTestArtifact.getSoleAttribute(CoreAttributeTypes.StaticId);
      deleteTestArtifact.delete();
      deleteTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact2, new Artifact[] {deleteTestArtifact}, false);
      Operations.executeWork(dropHandler);
      sleep(5000);

      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(deleteTestArtifact.getArtId(), sourceArtifact2.getBranch());
      assertTrue(destArtifact.getModType().equals(ModificationType.DELETED));
      Attribute<?> destAttr = destArtifact.getAttributeById(destAttribute.getId(), true);
      assertTrue(destAttr.getModificationType().equals(ModificationType.ARTIFACT_DELETED));
      for (RelationLink relLink : destArtifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
         assertTrue(relLink.getModificationType().equals(ModificationType.ARTIFACT_DELETED));
      }
   }

   @Test
   public void testDeleteAttributeFromArtifactBranch() throws Exception {
      Artifact deleteTestArtifact =
         ArtifactQuery.getArtifactFromId(sourceArtifact2.getArtId(), updateTestDestinationBranch);
      Attribute<Object> destAttribute = deleteTestArtifact.getSoleAttribute(CoreAttributeTypes.StaticId);
      deleteTestArtifact.deleteAttribute(destAttribute);
      deleteTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact2, new Artifact[] {deleteTestArtifact}, false);
      Operations.executeWork(dropHandler);
      sleep(5000);

      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(deleteTestArtifact.getArtId(), sourceArtifact2.getBranch());
      Attribute<?> destAttr = destArtifact.getAttributeById(destAttribute.getId(), true);
      assertTrue(destAttr.getModificationType().equals(ModificationType.DELETED));
   }

   @Test
   public void testDeleteRelationFromArtifactBranch() throws Exception {
      Artifact deleteTestArtifact =
         ArtifactQuery.getArtifactFromId(sourceArtifact2.getArtId(), updateTestDestinationBranch);
      deleteTestArtifact.deleteRelation(CoreRelationTypes.Default_Hierarchical__Child, sourceChildArtifact1);
      deleteTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact2, new Artifact[] {deleteTestArtifact}, false);
      Operations.executeWork(dropHandler);
      sleep(5000);

      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(deleteTestArtifact.getArtId(), sourceArtifact2.getBranch());
      for (RelationLink relLink : destArtifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
         if (relLink.getAArtifactId() == sourceArtifact2.getArtId() && relLink.getBArtifactId() == sourceChildArtifact1.getArtId()) {
            assertTrue(relLink.getModificationType().equals(ModificationType.ARTIFACT_DELETED));
         }
      }
   }

   @Test
   public void testDestinationArtifactDeletedBranch() throws Exception {
      Artifact deleteTestArtifact =
         ArtifactQuery.getArtifactFromId(sourceDeleteArtifact.getArtId(), updateTestDestinationBranch);

   }

   private IOseeBranch createBranchToken(String name) {
      String branchName = String.format("%s__%s", method.getQualifiedTestName(), name);
      return TokenFactory.createBranch(GUID.create(), branchName);
   }
}
