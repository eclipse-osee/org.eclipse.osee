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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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

   private Artifact sourceArtifact;

   private IOseeBranch sourceBranch;
   private IOseeBranch destinationBranch;
   private IOseeBranch updateTestParentSourceBranch;
   private IOseeBranch updateTestSourceBranch;

   @Before
   public void setUp() throws Exception {
      sourceBranch = createBranchToken("Source");
      destinationBranch = createBranchToken("Destination");
      updateTestParentSourceBranch = createBranchToken("updateTestParentSource");
      updateTestSourceBranch = createBranchToken("updateTestSource");

      BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, sourceBranch);
      BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, destinationBranch);

      // Add artifacts to source
      sourceArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact.persist(method.getQualifiedTestName());

      BranchManager.createWorkingBranch(sourceBranch, updateTestParentSourceBranch);
      BranchManager.createWorkingBranch(updateTestParentSourceBranch, updateTestSourceBranch);
   }

   @After
   public void tearDown() throws OseeCoreException {
      BranchManager.purgeBranch(destinationBranch);

      BranchManager.purgeBranch(updateTestSourceBranch);
      BranchManager.purgeBranch(updateTestParentSourceBranch);
      BranchManager.purgeBranch(sourceBranch);
   }

   @Test
   public void testIntroduceCrossBranch() throws Exception {
      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(destinationBranch), new Artifact[] {sourceArtifact},
            false, false, false);
      Operations.executeWork(dropHandler);
      sleep(5000);
      //Acquire the introduced artifact
      Artifact destArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact.getArtId(), destinationBranch);

      assertTrue(sourceArtifact.getName().equals(destArtifact.getName()));
   }

   @Test
   public void testUpdateBranch() throws Exception {
      Artifact updateTestArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact.getArtId(), updateTestSourceBranch);
      updateTestArtifact.setName("I am an update branch test");
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(updateTestSourceBranch);
      root.addChild(updateTestArtifact);
      updateTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact, new Artifact[] {updateTestArtifact}, false,
            false, false);
      Operations.executeWork(dropHandler);

      sleep(5000);
      //Acquire the updated artifact
      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(updateTestArtifact.getArtId(), sourceArtifact.getBranch());
      destArtifact.reloadAttributesAndRelations();
      assertTrue(updateTestArtifact.getName().equals(destArtifact.getName()));
   }

   private IOseeBranch createBranchToken(String name) {
      String branchName = String.format("%s__%s", method.getQualifiedTestName(), name);
      return TokenFactory.createBranch(GUID.create(), branchName);
   }
}
