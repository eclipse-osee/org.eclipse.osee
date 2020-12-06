/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.util.Collection;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandlerOperation;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * @author Megumi Telles
 */
public class ConflictIntroduceTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   private static IOseeBranch sourceBranch;
   private static IOseeBranch destinationBranch;
   private static IOseeBranch updateBranch;

   private static Artifact artifactToDelete;

   private static String TESTNAME = "ConflictIntroduceTest";

   @BeforeClass
   public static void setUp() throws Exception {
      sourceBranch = createBranchToken("Source");
      destinationBranch = createBranchToken("Destination");
      updateBranch = createBranchToken("Update");

      // set up destination branch
      BranchManager.createWorkingBranch(SAW_Bld_1, destinationBranch);
      artifactToDelete = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirementMsWord,
         "Read-only Robots", SAW_Bld_1);

      // Delete artifact and commit to destination branch
      BranchManager.createWorkingBranch(destinationBranch, updateBranch);
      Artifact art = ArtifactQuery.getArtifactFromId(artifactToDelete, updateBranch);
      art.deleteAndPersist(ConflictIntroduceTest.class.getSimpleName());

      ConflictManagerExternal conflictManager = new ConflictManagerExternal(destinationBranch, updateBranch);
      TransactionResult transactionResult = BranchManager.commitBranch(null, conflictManager, false, false);
      if (transactionResult.isFailed()) {
         throw new OseeCoreException(transactionResult.toString());
      }

      // create source branch
      BranchManager.createWorkingBranch(destinationBranch, sourceBranch);
   }

   @Test
   public void testIntroduceNoConflict() {
      // Introduce the artifact
      InterArtifactExplorerDropHandlerOperation dropHandler = new InterArtifactExplorerDropHandlerOperation(
         OseeSystemArtifacts.getDefaultHierarchyRootArtifact(sourceBranch), new Artifact[] {artifactToDelete}, false);
      Operations.executeWork(dropHandler);
      // Acquire the introduced artifact
      Artifact destArtifact = ArtifactQuery.getArtifactFromId(artifactToDelete, sourceBranch);
      Assert.assertNotNull(destArtifact);

      // check for conflicts....there should be no conflict in this case.
      Collection<Conflict> conflicts = null;
      try {
         conflicts = ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destinationBranch,
            BranchManager.getBaseTransaction(sourceBranch), new NullProgressMonitor());
         assertEquals(0, conflicts.size());
      } catch (Exception ex) {
         fail(Lib.exceptionToString(ex));
      }
      Assert.assertNotNull(conflicts);
   }

   @Test
   public void testModifiedButDeletedConflict() {

      Artifact artifactToModify = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
         "Cognitive_Decision_Aiding", destinationBranch);
      assertNotNull(artifactToModify);
      artifactToModify.setSoleAttributeFromString(CoreAttributeTypes.Name, "Cognitive_Decision_Aiding2");
      artifactToModify.persist(TESTNAME);

      Artifact dArtifact = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
         "Cognitive_Decision_Aiding", sourceBranch);
      assertNotNull(dArtifact);
      dArtifact.deleteAndPersist(getClass().getSimpleName());

      // check for conflict....this should be a conflict
      Collection<Conflict> conflicts = null;
      try {
         conflicts = ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destinationBranch,
            BranchManager.getBaseTransaction(sourceBranch), new NullProgressMonitor());
         assertEquals(2, conflicts.size());
      } catch (Exception ex) {
         fail(Lib.exceptionToString(ex));
      }
      Assert.assertNotNull(conflicts);
   }

   @Test
   public void testDeletedButModifiedConflict() {
      Artifact sourceArt =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component, "Chassis", sourceBranch);
      assertNotNull(sourceArt);
      sourceArt.deleteAndPersist(getClass().getSimpleName());

      Artifact artifactToModify =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component, "Chassis", destinationBranch);
      assertNotNull(artifactToModify);
      artifactToModify.setSoleAttributeFromString(CoreAttributeTypes.Name, "Chassis2");
      artifactToModify.persist(TESTNAME);

      // check for conflict....this should be a conflict
      Collection<Conflict> conflicts = null;
      try {
         conflicts = ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destinationBranch,
            BranchManager.getBaseTransaction(sourceBranch), new NullProgressMonitor());
         assertEquals(1, conflicts.size());
      } catch (Exception ex) {
         fail(Lib.exceptionToString(ex));
      }
      Assert.assertNotNull(conflicts);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      BranchManager.refreshBranches();
      IOseeBranch mBranch = null;
      if (sourceBranch != null && destinationBranch != null) {
         mBranch = BranchManager.getMergeBranch(sourceBranch, destinationBranch);
      }

      BranchManager.purgeBranch(mBranch);
      BranchManager.purgeBranch(updateBranch);
      BranchManager.purgeBranch(sourceBranch);
      BranchManager.purgeBranch(destinationBranch);
   }

   private static IOseeBranch createBranchToken(String name) {
      String branchName = String.format("%s__%s", TESTNAME, name);
      return IOseeBranch.create(branchName);
   }

}
