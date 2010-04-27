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
package org.eclipse.osee.ats.test.cases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowManager;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.config.AtsConfigManager;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.XWorkingBranch;
import org.eclipse.osee.ats.util.widgets.commit.XCommitManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData.KindType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
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

   public static class AtsTestBranches extends NamedIdentity implements IOseeBranch {
      public static final AtsTestBranches BranchViaTeamDef =
            new AtsTestBranches("AyH_e6damwQgvDhKfAAA", "BranchViaTeamDef");
      public static final AtsTestBranches BranchViaVersions =
            new AtsTestBranches("AyH_e6damwQgvDhKfBBB", "BranchViaVersions");
      public static final AtsTestBranches BranchViaParallelVersions =
            new AtsTestBranches("AyH_e6damwQgvDhKfCCC", "BranchViaParallelVersions");

      private AtsTestBranches(String guid, String name) {
         super(guid, name);
      }

      public String getNamespace() {
         return String.format("org.branchTest.%s", this.getName().toLowerCase());
      }

      public Collection<String> appendToName(String... postFixes) {
         Collection<String> data = new ArrayList<String>();
         for (String postFix : postFixes) {
            data.add(String.format("%s - %s", this.getName(), postFix));
         }
         return data;
      }
   }

   @Before
   public void testSetup() throws Exception {
      if (AtsUtil.isProductionDb()) {
         throw new IllegalStateException("BranchConfigThroughTeamDefTest should not be run on production DB");
      }
      AtsBulkLoad.run(true);
   }

   @org.junit.Test
   public void testBranchViaVersions() throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Running testBranchViaVersions...");

      // Cleanup from previous run
      cleanupBranchTest(AtsTestBranches.BranchViaVersions);

      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring ATS for team org.branchTest.viaTeamDefs");

      // create team definition and actionable item
      String name = AtsTestBranches.BranchViaVersions.getName();
      String namespace = AtsTestBranches.BranchViaVersions.getNamespace();
      Collection<String> versions = AtsTestBranches.BranchViaVersions.appendToName("Ver1", "Ver2");
      Collection<String> actionableItems = AtsTestBranches.BranchViaVersions.appendToName("A1", "A2");
      configureAts(namespace, name, versions, actionableItems, namespace);

      // create main branch
      OseeLog.log(AtsPlugin.class, Level.INFO, "Creating root branch");
      // Create SAW_Bld_2 branch off SAW_Bld_1
      Branch viaTeamDefBranch = BranchManager.createTopLevelBranch(AtsTestBranches.BranchViaVersions);

      TestUtil.sleep(2000);

      // configure version to use branch and allow create/commit
      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring version to use branch and allow create/commit");
      TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME,
                  AtsTestBranches.BranchViaVersions.getName(), AtsUtil.getAtsBranch());
      VersionArtifact verArtToTarget = null;
      for (VersionArtifact vArt : teamDef.getVersionsArtifacts()) {
         if (vArt.getName().contains("Ver1")) {
            verArtToTarget = vArt;
         }
      }
      verArtToTarget.setSoleAttributeFromString(ATSAttributes.BASELINE_BRANCH_GUID_ATTRIBUTE.getStoreName(),
            viaTeamDefBranch.getGuid());
      // setup team def to allow create/commit of branch
      verArtToTarget.setSoleAttributeValue(ATSAttributes.ALLOW_COMMIT_BRANCH.getStoreName(), true);
      verArtToTarget.setSoleAttributeValue(ATSAttributes.ALLOW_CREATE_BRANCH.getStoreName(), true);
      verArtToTarget.persist();

      TestUtil.sleep(2000);

      // setup workflow page to have create/commit branch widgets
      setupWorkflowPageToHaveCreateCommitBranchWidgets(namespace);

      // create action and target for version
      OseeLog.log(AtsPlugin.class, Level.INFO, "Create new Action and target for version " + verArtToTarget);

      Collection<ActionableItemArtifact> selectedActionableItems =
            ActionableItemArtifact.getActionableItems(AtsTestBranches.BranchViaVersions.appendToName("A1"));
      Assert.assertFalse(selectedActionableItems.isEmpty());

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
      ActionArtifact actionArt =
            ActionManager.createAction(null, AtsTestBranches.BranchViaVersions.getName() + " Req Changes",
                  "description", ChangeType.Problem, PriorityType.Priority_1, false, null, selectedActionableItems,
                  transaction);
      actionArt.getTeamWorkFlowArtifacts().iterator().next().addRelation(
            AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, verArtToTarget);
      actionArt.getTeamWorkFlowArtifacts().iterator().next().persist(transaction);
      transaction.execute();

      final TeamWorkFlowArtifact teamWf = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);

      // Transition to desired state
      OseeLog.log(AtsPlugin.class, Level.INFO, "Transitioning to Implement state");

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
      dtwm.transitionTo(DefaultTeamState.Implement, null, false, transaction);
      teamWf.persist(transaction);
      transaction.execute();

      TestUtil.sleep(2000);
      SMAEditor.editArtifact(teamWf, true);
      // Verify XWorkingBranch and XCommitManger widgets exist in editor
      try {
         verifyXWidgetsExistInEditor(teamWf);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         fail(ex.getLocalizedMessage());
      }

      // create branch
      createBranch(namespace, teamWf);

      // make changes
      OseeLog.log(AtsPlugin.class, Level.INFO, "Make new requirement artifact");
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(teamWf.getWorkingBranch());
      Artifact blk3MainArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, teamWf.getWorkingBranch(),
                  AtsTestBranches.BranchViaVersions.getName() + " Requirement");
      rootArtifact.addChild(blk3MainArt);
      blk3MainArt.persist();

      // commit branch
      commitBranch(teamWf);

      TestUtil.sleep(2000);

      // test change report
      OseeLog.log(AtsPlugin.class, Level.INFO, "Test change report results");
      ChangeData changeData = teamWf.getBranchMgr().getChangeDataFromEarliestTransactionId();
      assertTrue("No changes detected", !changeData.isEmpty());

      Collection<Artifact> newArts = changeData.getArtifacts(KindType.Artifact, ModificationType.NEW);
      assertTrue("Should be 1 new artifact in change report, found " + newArts.size(), newArts.size() == 1);

   }

   @org.junit.Test
   public void testBranchViaTeamDefinition() throws Exception {

      OseeLog.log(AtsPlugin.class, Level.INFO, "Running testBranchViaTeamDefinition...");

      // Cleanup from previous run
      cleanupBranchTest(AtsTestBranches.BranchViaTeamDef);

      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring ATS for team org.branchTest.viaTeamDefs");
      // create team definition and actionable item

      String name = AtsTestBranches.BranchViaTeamDef.getName();
      String namespace = AtsTestBranches.BranchViaTeamDef.getNamespace();
      Collection<String> versions = null;
      Collection<String> actionableItems = AtsTestBranches.BranchViaTeamDef.appendToName("A1", "A2");
      configureAts(namespace, name, versions, actionableItems, namespace);

      // create main branch
      OseeLog.log(AtsPlugin.class, Level.INFO, "Creating root branch");
      // Create SAW_Bld_2 branch off SAW_Bld_1
      Branch viaTeamDefBranch = BranchManager.createTopLevelBranch(AtsTestBranches.BranchViaTeamDef);

      TestUtil.sleep(2000);

      // configure team def to use branch
      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring team def to use branch and allow create/commit");
      TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME,
                  AtsTestBranches.BranchViaTeamDef.getName(), AtsUtil.getAtsBranch());
      teamDef.setSoleAttributeFromString(ATSAttributes.BASELINE_BRANCH_GUID_ATTRIBUTE.getStoreName(),
            viaTeamDefBranch.getGuid());
      // setup team def to allow create/commit of branch
      teamDef.setSoleAttributeValue(ATSAttributes.ALLOW_COMMIT_BRANCH.getStoreName(), true);
      teamDef.setSoleAttributeValue(ATSAttributes.ALLOW_CREATE_BRANCH.getStoreName(), true);
      teamDef.setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), false);
      teamDef.persist();

      TestUtil.sleep(2000);

      // setup workflow page to have create/commit branch widgets
      setupWorkflowPageToHaveCreateCommitBranchWidgets(namespace);

      // create action,
      OseeLog.log(AtsPlugin.class, Level.INFO, "Create new Action");
      Collection<ActionableItemArtifact> selectedActionableItems =
            ActionableItemArtifact.getActionableItems(AtsTestBranches.BranchViaTeamDef.appendToName("A1"));
      Assert.assertFalse(selectedActionableItems.isEmpty());

      SkynetTransaction transaction =
            new SkynetTransaction(AtsUtil.getAtsBranch(), "Test branch via team definition: create action");
      String actionTitle = AtsTestBranches.BranchViaTeamDef.getName() + " Req Changes";
      ActionArtifact actionArt =
            ActionManager.createAction(null, actionTitle, "description", ChangeType.Problem, PriorityType.Priority_1,
                  false, null, selectedActionableItems, transaction);
      transaction.execute();

      final TeamWorkFlowArtifact teamWf = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);

      // Transition to desired state
      OseeLog.log(AtsPlugin.class, Level.INFO, "Transitioning to Implement state");
      transaction =
            new SkynetTransaction(AtsUtil.getAtsBranch(),
                  "Test branch via team definition: Transition to desired state");
      dtwm.transitionTo(DefaultTeamState.Implement, null, false, transaction);
      teamWf.persist(transaction);
      transaction.execute();

      TestUtil.sleep(4000);
      SMAEditor.editArtifact(teamWf, true);
      // Verify XWorkingBranch and XCommitManger widgets exist in editor
      try {
         verifyXWidgetsExistInEditor(teamWf);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         fail(ex.getLocalizedMessage());
      }

      // create branch
      createBranch(namespace, teamWf);

      // make changes
      OseeLog.log(AtsPlugin.class, Level.INFO, "Make new requirement artifact");
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(teamWf.getWorkingBranch());
      Artifact blk3MainArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, teamWf.getWorkingBranch(),
                  AtsTestBranches.BranchViaTeamDef.getName() + " Requirement");
      rootArtifact.addChild(blk3MainArt);
      blk3MainArt.persist();

      // commit branch
      commitBranch(teamWf);

      // test change report
      OseeLog.log(AtsPlugin.class, Level.INFO, "Test change report results");
      ChangeData changeData = teamWf.getBranchMgr().getChangeDataFromEarliestTransactionId();
      assertTrue("No changes detected", !changeData.isEmpty());

      Collection<Artifact> newArts = changeData.getArtifacts(KindType.Artifact, ModificationType.NEW);
      assertTrue("Should be 1 new artifact in change report, found " + newArts.size(), newArts.size() == 1);
   }

   private void cleanupBranchTest(AtsTestBranches testType) throws Exception {
      String namespace = "org.branchTest." + testType.getName().toLowerCase();
      OseeLog.log(AtsPlugin.class, Level.INFO, "Cleanup from previous run of ATS for team " + namespace);
      ActionArtifact aArt =
            (ActionArtifact) ArtifactQuery.checkArtifactFromTypeAndName(AtsArtifactTypes.Action,
                  testType.getName() + " Req Changes", AtsUtil.getAtsBranch());
      if (aArt != null) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
         for (TeamWorkFlowArtifact teamArt : aArt.getTeamWorkFlowArtifacts()) {
            SMAEditor.close(Collections.singleton(teamArt), false);
            teamArt.deleteAndPersist(transaction, true);
         }
         aArt.deleteAndPersist(transaction, true);
         transaction.execute();
      }

      // Delete VersionArtifacts
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.Version, AtsUtil.getAtsBranch());
      if (arts.size() > 0) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
         for (Artifact verArt : arts) {
            if (verArt.getName().contains(testType.getName())) {
               verArt.deleteAndPersist(transaction, true);
            }
         }
         transaction.execute();
      }

      // Delete Team Definitions
      Artifact art =
            ArtifactQuery.checkArtifactFromTypeAndName(AtsArtifactTypes.TeamDefinition, testType.getName(),
                  AtsUtil.getAtsBranch());
      if (art != null) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
         art.deleteAndPersist(transaction, true);
         transaction.execute();
      }

      // Delete AIs
      art =
            ArtifactQuery.checkArtifactFromTypeAndName(AtsArtifactTypes.ActionableItem, testType.getName(),
                  AtsUtil.getAtsBranch());
      if (art != null) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
         for (Artifact childArt : art.getChildren()) {
            childArt.deleteAndPersist(transaction, true);
         }
         art.deleteAndPersist(transaction, true);
         transaction.execute();
      }

      arts = ArtifactQuery.getArtifactListFromType(WorkPageDefinition.ARTIFACT_NAME, AtsUtil.getAtsBranch());
      if (arts.size() > 0) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
         for (Artifact workArt : arts) {
            if (workArt.getName().startsWith(namespace)) {
               workArt.deleteAndPersist(transaction, true);
            }
         }
         transaction.execute();
      }
      arts = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.WorkFlowDefinition, AtsUtil.getAtsBranch());
      if (arts.size() > 0) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Branch Configuration Test");
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
         for (Branch workingBranch : BranchManager.getBranches(BranchArchivedState.ALL, BranchType.WORKING)) {
            if (workingBranch.getName().contains(testType.getName())) {
               BranchManager.purgeBranch(workingBranch);
               TestUtil.sleep(2000);
            }
         }
         // delete baseline branch
         Branch branch = BranchManager.getBranchByGuid(testType.getGuid());
         if (branch != null) {
            BranchManager.purgeBranch(branch);
            TestUtil.sleep(2000);
         }

      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   private void commitBranch(TeamWorkFlowArtifact teamWf) throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Commit Branch");
      Job job =
            teamWf.getBranchMgr().commitWorkingBranch(false, true,
                  teamWf.getBranchMgr().getWorkingBranch().getParentBranch(), true);
      try {
         job.join();
      } catch (InterruptedException ex) {
         //
      }
   }

   private void createBranch(String namespace, TeamWorkFlowArtifact teamWf) throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Creating working branch");
      String implementPageId = namespace + ".Implement";
      Result result = teamWf.getBranchMgr().createWorkingBranch(implementPageId, false);
      if (result.isFalse()) {
         result.popup();
         return;
      }
      TestUtil.sleep(4000);
   }

   private void verifyXWidgetsExistInEditor(TeamWorkFlowArtifact teamWf) throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Verify XWorkingBranch and XCommitManger widgets exist in editor");
      SMAEditor smaEditor = SMAEditor.getSmaEditor(teamWf);
      TestUtil.sleep(4000);

      assertNotNull("Can't retrieve SMAEditor for workflow " + teamWf, smaEditor);

      Collection<XWidget> xWidgets =
            smaEditor.getXWidgetsFromState(smaEditor.getSma().getStateMgr().getCurrentStateName(), XWorkingBranch.class);
      assertTrue("Should be 1 XWorkingBranch widget in current state, found " + xWidgets.size(), xWidgets.size() == 1);
      xWidgets =
            smaEditor.getXWidgetsFromState(smaEditor.getSma().getStateMgr().getCurrentStateName(), XCommitManager.class);
      assertTrue("Should be 1 XCommitManager widget in current state, found " + xWidgets.size(), xWidgets.size() == 1);
   }

   private void setupWorkflowPageToHaveCreateCommitBranchWidgets(String namespace) throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Setup new workflow page to have create/commit branch widgets");
      String implementPageId = namespace + ".Implement";
      Artifact implementPageDef = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(implementPageId);
      implementPageDef.addRelation(CoreRelationTypes.WorkItem__Child,
            WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(ATSAttributes.WORKING_BRANCH_WIDGET.getStoreName()));
      implementPageDef.addRelation(CoreRelationTypes.WorkItem__Child,
            WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(ATSAttributes.COMMIT_MANAGER_WIDGET.getStoreName()));
      implementPageDef.persist();
      WorkItemDefinitionFactory.updateDefinitions(Collections.singleton(implementPageDef));
   }

   @After
   public void tearDown() throws Exception {
      cleanupBranchTest(AtsTestBranches.BranchViaVersions);
      cleanupBranchTest(AtsTestBranches.BranchViaTeamDef);
   }

   private void configureAts(String namespace, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems, String workflowId) throws Exception {
      AtsConfigManager.Display noDisplay = new MockAtsConfigDisplay();
      IOperation operation =
            new AtsConfigManager(noDisplay, namespace, teamDefName, versionNames, actionableItems, workflowId);
      Operations.executeWorkAndCheckStatus(operation, new NullProgressMonitor(), -1.0);
      TestUtil.sleep(2000);
   }

   private final class MockAtsConfigDisplay implements AtsConfigManager.Display {
      @Override
      public void openAtsConfigurationEditors(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> aias, WorkFlowDefinition workFlowDefinition) {
         // Nothing to do - we have no display during testing
      }
   }
}
