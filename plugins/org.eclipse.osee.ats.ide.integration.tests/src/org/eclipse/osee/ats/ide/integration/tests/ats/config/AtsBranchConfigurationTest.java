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
package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.branch.AtsBranchManager;
import org.eclipse.osee.ats.ide.branch.AtsBranchUtil;
import org.eclipse.osee.ats.ide.config.AtsConfigOperation;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * Run from the ATS Navigator after the DB is configured for either ATS - Dev or Demo
 *
 * @author Donald G. Dunne
 */
public class AtsBranchConfigurationTest {

   public static final IOseeBranch BRANCH_VIA_TEAM_DEFINITION = IOseeBranch.create("BranchViaTeamDef");
   public static final IOseeBranch BRANCH_VIA_VERSIONS = IOseeBranch.create("BranchViaVersions");
   private final boolean DEBUG = false;

   private static Collection<String> appendToName(IOseeBranch branch, String... postFixes) {
      Collection<String> data = new ArrayList<>();
      for (String postFix : postFixes) {
         data.add(String.format("%s - %s", branch.getName(), postFix));
      }
      return data;
   }

   private static String asNamespace(IOseeBranch branch) {
      return String.format("org.branchTest.%s", branch.getName().toLowerCase());
   }

   @Before
   public void testSetup() throws Exception {
      if (AtsClientService.get().getStoreService().isProductionDb()) {
         throw new IllegalStateException("BranchConfigThroughTeamDefTest should not be run on production DB");
      }
   }

   @org.junit.Test
   public void testBranchViaVersions() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Running testBranchViaVersions...");
      }

      // Cleanup from previous run
      cleanupBranchTest(BRANCH_VIA_VERSIONS);

      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO,
            "Configuring ATS for team org.branchTest.viaTeamDefs");
      }

      // create team definition and actionable item
      String name = BRANCH_VIA_VERSIONS.getName();
      String namespace = asNamespace(BRANCH_VIA_VERSIONS);
      Collection<String> versions = appendToName(BRANCH_VIA_VERSIONS, "Ver1", "Ver2");
      Collection<String> actionableItems = appendToName(BRANCH_VIA_VERSIONS, "A1", "A2");
      AtsConfigOperation operation = configureAts(namespace, name, versions, actionableItems);

      AtsClientService.get().getServerEndpoints().getConfigEndpoint().getWithPend();
      AtsClientService.get().getConfigService().getConfigurationsWithPend();

      // create main branch
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Creating root branch");
      }
      // Create SAW_Bld_2 branch off SAW_Bld_1
      BranchId viaTeamDefBranch = BranchManager.createTopLevelBranch(BRANCH_VIA_VERSIONS);

      // configure version to use branch and allow create/commit
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO,
            "Configuring version to use branch and allow create/commit");
      }
      IAtsTeamDefinition teamDef = operation.getTeamDefinition();
      IAtsVersion versionToTarget = null;
      long version1Id = 0L, version2Id = 0L;
      Collection<IAtsVersion> versions2 = AtsClientService.get().getVersionService().getVersions(teamDef);
      for (IAtsVersion vArt : versions2) {
         if (vArt.getName().contains("Ver1")) {
            versionToTarget = vArt;
            version1Id = vArt.getId();
         } else {
            version2Id = vArt.getId();
         }
      }

      Assert.assertNotNull(versionToTarget);
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(versionToTarget, AtsAttributeTypes.BaselineBranchId,
         viaTeamDefBranch.getIdString());
      changes.setSoleAttributeValue(versionToTarget, AtsAttributeTypes.AllowCommitBranch, true);
      changes.setSoleAttributeValue(versionToTarget, AtsAttributeTypes.AllowCreateBranch, true);
      changes.execute();

      AtsClientService.get().getServerEndpoints().getConfigEndpoint().getWithPend();
      AtsClientService.get().getConfigService().getConfigurationsWithPend();

      // create action and target for version
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO,
            "Create new Action and target for version " + versionToTarget);
      }

      Collection<IAtsActionableItem> selectedActionableItems =
         AtsClientService.get().getActionableItemService().getActionableItems(appendToName(BRANCH_VIA_VERSIONS, "A1"));
      assertFalse(selectedActionableItems.isEmpty());

      ActionResult result = AtsClientService.get().getActionFactory().createAction(null,
         BRANCH_VIA_VERSIONS.getName() + " Req Changes", "description", ChangeType.Problem, "1", false, null,
         selectedActionableItems, new Date(), AtsClientService.get().getUserService().getCurrentUser(), null, changes);
      IAtsTeamWorkflow teamWf = AtsClientService.get().getWorkItemService().getTeams(result).iterator().next();
      AtsClientService.get().getVersionService().setTargetedVersion(teamWf, versionToTarget, changes);
      changes.execute();

      AtsClientService.get().getServerEndpoints().getConfigEndpoint().getWithPend();
      AtsClientService.get().getConfigService().getConfigurationsWithPend();

      TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices());

      // Transition to desired state
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Transitioning to Implement state");
      }

      dtwm.transitionTo(TeamState.Implement, AtsClientService.get().getUserService().getCurrentUser(), false, changes);
      ((TeamWorkFlowArtifact) teamWf.getStoreObject()).persist("Branch Configuration Test");

      WorkflowEditor.edit(teamWf);

      // create branch
      BranchId workingBranch = createBranch(namespace, teamWf);

      // make changes
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Make new requirement artifact");
      }
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(workingBranch);
      Artifact blk3MainArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, workingBranch,
         BRANCH_VIA_VERSIONS.getName() + " Requirement");
      rootArtifact.addChild(blk3MainArt);
      blk3MainArt.persist(getClass().getSimpleName());

      // commit branch
      commitBranch((TeamWorkFlowArtifact) teamWf.getStoreObject());

      // test change report
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Test change report results");
      }
      ChangeData changeData =
         AtsBranchManager.getChangeDataFromEarliestTransactionId((TeamWorkFlowArtifact) teamWf.getStoreObject());
      assertFalse("No changes detected", changeData.isEmpty());

      Collection<Artifact> newArts = changeData.getArtifacts(KindType.Artifact, ModificationType.NEW);
      assertTrue("Should be 1 new artifact in change report, found " + newArts.size(), newArts.size() == 1);

      TestUtil.severeLoggingEnd(monitor,
         Arrays.asList("Version [[" + version1Id + "][BranchViaVersions - Ver1]] has no related team defininition",
            "Version [[" + version2Id + "][BranchViaVersions - Ver2]] has no related team defininition"));
   }

   @org.junit.Test
   public void testBranchViaTeamDefinition() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Running testBranchViaTeamDefinition...");
      }

      // Cleanup from previous run
      cleanupBranchTest(BRANCH_VIA_TEAM_DEFINITION);

      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO,
            "Configuring ATS for team org.branchTest.viaTeamDefs");
         // create team definition and actionable item
      }

      String name = BRANCH_VIA_TEAM_DEFINITION.getName();
      String namespace = asNamespace(BRANCH_VIA_TEAM_DEFINITION);
      Collection<String> versions = null;
      Collection<String> actionableItems = appendToName(BRANCH_VIA_TEAM_DEFINITION, "A1", "A2");
      AtsConfigOperation operation = configureAts(namespace, name, versions, actionableItems);

      // create main branch
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Creating root branch");
      }
      // Create SAW_Bld_2 branch off SAW_Bld_1
      BranchId viaTeamDefBranch = BranchManager.createTopLevelBranch(BRANCH_VIA_TEAM_DEFINITION);

      // configure team def to use branch
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO,
            "Configuring team def to use branch and allow create/commit");
      }
      IAtsTeamDefinition teamDef = operation.getTeamDefinition();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.BaselineBranchId, viaTeamDefBranch.getIdString());
      // setup team def to allow create/commit of branch
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.AllowCommitBranch, true);
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.AllowCreateBranch, true);
      changes.execute();

      // create action,
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Create new Action");
      }
      Collection<IAtsActionableItem> selectedActionableItems =
         AtsClientService.get().getActionableItemService().getActionableItems(
            appendToName(BRANCH_VIA_TEAM_DEFINITION, "A1"));
      assertFalse(selectedActionableItems.isEmpty());

      changes.reset("Test branch via team definition: create action");
      String actionTitle = BRANCH_VIA_TEAM_DEFINITION.getName() + " Req Changes";
      changes.clear();
      ActionResult result = AtsClientService.get().getActionFactory().createAction(null, actionTitle, "description",
         ChangeType.Problem, "1", false, null, selectedActionableItems, new Date(),
         AtsClientService.get().getUserService().getCurrentUser(), null, changes);
      changes.execute();

      final IAtsTeamWorkflow teamWf = AtsClientService.get().getWorkItemService().getTeams(result).iterator().next();
      TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices());

      // Transition to desired state
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Transitioning to Implement state");
      }
      changes.reset("Test branch via team definition: create action");
      dtwm.transitionTo(TeamState.Implement, AtsClientService.get().getUserService().getCurrentUser(), false, changes);
      changes.execute();

      // create branch
      BranchId workingBranch = createBranch(namespace, teamWf);

      // make changes
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Make new requirement artifact");
      }
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(workingBranch);
      Artifact blk3MainArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, workingBranch,
         BRANCH_VIA_TEAM_DEFINITION.getName() + " Requirement");
      rootArtifact.addChild(blk3MainArt);
      blk3MainArt.persist(getClass().getSimpleName());

      // commit branch
      commitBranch((TeamWorkFlowArtifact) teamWf.getStoreObject());

      // test change report
      if (DEBUG) {
         OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO, "Test change report results");
      }
      ChangeData changeData =
         AtsBranchManager.getChangeDataFromEarliestTransactionId((TeamWorkFlowArtifact) teamWf.getStoreObject());
      assertTrue("No changes detected", !changeData.isEmpty());

      Collection<Artifact> newArts = changeData.getArtifacts(KindType.Artifact, ModificationType.NEW);
      assertTrue("Should be 1 new artifact in change report, found " + newArts.size(), newArts.size() == 1);

      TestUtil.severeLoggingEnd(monitor);
   }

   public static void cleanupBranchTest(IOseeBranch branch) throws Exception {
      String namespace = "org.branchTest." + branch.getName().toLowerCase();
      Artifact aArt = ArtifactQuery.checkArtifactFromTypeAndName(AtsArtifactTypes.Action,
         branch.getName() + " Req Changes", AtsClientService.get().getAtsBranch());
      if (aArt != null) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Branch Configuration Test");
         for (IAtsTeamWorkflow teamWf : AtsClientService.get().getWorkItemService().getTeams(aArt)) {
            WorkflowEditor.close(Collections.singleton(teamWf), false);
            ((TeamWorkFlowArtifact) teamWf.getStoreObject()).deleteAndPersist(transaction, true);
         }
         aArt.deleteAndPersist(transaction, true);
         transaction.execute();
      }

      // Delete VersionArtifacts
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Branch Configuration Test");
      for (IAtsVersion version : AtsClientService.get().getQueryService().createQuery(
         AtsArtifactTypes.Version).getItems(IAtsVersion.class)) {
         if (version.getName().contains(branch.getName())) {
            Artifact artifact = AtsClientService.get().getQueryServiceClient().getArtifact(version);
            if (artifact != null) {
               artifact.deleteAndPersist(transaction);
            }
         }
      }
      transaction.execute();

      // Delete Team Definitions
      transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Branch Configuration Test");
      for (Artifact teamDefArt : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.TeamDefinition,
         branch.getName(), AtsClientService.get().getAtsBranch())) {
         teamDefArt.deleteAndPersist(transaction, false);
      }
      transaction.execute();

      // Delete AIs
      transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Branch Configuration Test");
      for (Artifact aiaArt : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.ActionableItem,
         branch.getName(), AtsClientService.get().getAtsBranch())) {
         for (Artifact childArt : aiaArt.getChildren()) {
            childArt.deleteAndPersist(transaction, false);
         }

         aiaArt.deleteAndPersist(transaction, false);
      }
      transaction.execute();

      // Work Definition
      Collection<Artifact> arts =
         ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.WorkDefinition, AtsClientService.get().getAtsBranch());
      if (arts.size() > 0) {
         transaction =
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Branch Configuration Test");
         for (Artifact workArt : arts) {
            if (workArt.getName().startsWith(namespace)) {
               workArt.deleteAndPersist(transaction, true);
            }
         }
         transaction.execute();
      }

      try {
         BranchManager.refreshBranches();
         // delete working branches
         for (IOseeBranch workingBranch : BranchManager.getBranches(BranchArchivedState.ALL, BranchType.WORKING)) {
            if (workingBranch.getName().contains(branch.getName())) {
               BranchManager.purgeBranch(workingBranch);
            }
         }
         if (BranchManager.branchExists(branch)) {
            BranchManager.purgeBranch(branch);
         }

      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   public static void commitBranch(TeamWorkFlowArtifact teamWf) throws Exception {
      IOperation op = AtsBranchManager.commitWorkingBranch(teamWf, false, true,
         BranchManager.getParentBranch(AtsClientService.get().getBranchService().getWorkingBranch(teamWf)), true);
      Operations.executeWorkAndCheckStatus(op);
   }

   public static BranchId createBranch(String namespace, IAtsTeamWorkflow teamWf) throws Exception {
      Result result = AtsBranchUtil.createWorkingBranch_Validate((TeamWorkFlowArtifact) teamWf.getStoreObject());
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return BranchId.SENTINEL;
      }
      AtsBranchUtil.createWorkingBranch_Create(teamWf, true);

      BranchId workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamWf, true);
      Assert.assertTrue("No working branch created", workingBranch.isValid());
      return workingBranch;
   }

   @After
   public void tearDown() throws Exception {
      cleanupBranchTest(BRANCH_VIA_VERSIONS);
      cleanupBranchTest(BRANCH_VIA_TEAM_DEFINITION);
   }

   public static AtsConfigOperation configureAts(String workDefinitionName, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems) throws Exception {
      AtsConfigOperation atsConfigManagerOperation =
         new AtsConfigOperation(workDefinitionName, teamDefName, versionNames, actionableItems);
      Operations.executeWorkAndCheckStatus(atsConfigManagerOperation);
      return atsConfigManagerOperation;
   }

}
