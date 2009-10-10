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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowManager;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.AtsBulkLoadCache;
import org.eclipse.osee.ats.config.AtsConfigManager;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.XWorkingBranch;
import org.eclipse.osee.ats.util.widgets.commit.XCommitManager;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchControlled;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData.KindType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.support.test.util.ITestBranch;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * Run from the ATS Navigator after the DB is configured for either ATS - Dev or Demo
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchConfigurationTest {

   public enum TestType implements ITestBranch {
      BranchViaTeamDef("AyH_e6damwQgvDhKfAAA"),
      BranchViaVersions("AyH_e6damwQgvDhKfBBB"),
      BranchViaParallelVersions("AyH_e6damwQgvDhKfCCC");

      private final String guid;

      private TestType(String guid) {
         this.guid = guid;
      }

      public String getGuid() {
         return guid;
      }
   }

   @Before
   public void testSetup() throws Exception {
      if (AtsUtil.isProductionDb()) {
         throw new IllegalStateException("BranchConfigThroughTeamDefTest should not be run on production DB");
      }
      AtsBulkLoadCache.run(true);
   }

   @org.junit.Test
   public void testBranchViaVersions() throws Exception {

      OseeLog.log(AtsPlugin.class, Level.INFO, "Running testBranchViaVersions...");

      String namespace = "org.branchTest." + TestType.BranchViaVersions.name().toLowerCase();
      // Cleanup from previous run
      cleanupBranchTest(TestType.BranchViaVersions);

      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring ATS for team org.branchTest.viaTeamDefs");
      // create team definition and actionable item
      AtsConfigManager.configureAtsForDefaultTeam(namespace, TestType.BranchViaVersions.name(), Arrays.asList(
            TestType.BranchViaVersions.name() + "- Ver1", TestType.BranchViaVersions.name() + "- Ver2"), Arrays.asList(
            TestType.BranchViaVersions.name() + "- A1", TestType.BranchViaVersions.name() + "- A2"), namespace);

      TestUtil.sleep(2000);

      // create main branch
      OseeLog.log(AtsPlugin.class, Level.INFO, "Creating root branch");
      // Create SAW_Bld_2 branch off SAW_Bld_1
      Branch viaTeamDefBranch = createRootBranch(TestType.BranchViaVersions);

      TestUtil.sleep(2000);

      // configure version to use branch and allow create/commit
      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring version to use branch and allow create/commit");
      TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME,
                  TestType.BranchViaVersions.name(), AtsUtil.getAtsBranch());
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
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      ActionArtifact actionArt =
            ActionManager.createAction(null, TestType.BranchViaVersions.name() + " Req Changes", "description",
                  ChangeType.Problem, PriorityType.Priority_1, Arrays.asList("Other"), false, null,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(TestType.BranchViaVersions.name() + "- A1")),
                  transaction);
      actionArt.getTeamWorkFlowArtifacts().iterator().next().addRelation(
            AtsRelation.TeamWorkflowTargetedForVersion_Version, verArtToTarget);
      actionArt.getTeamWorkFlowArtifacts().iterator().next().persist(transaction);
      transaction.execute();

      final TeamWorkFlowArtifact teamWf = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);

      // Transition to desired state
      OseeLog.log(AtsPlugin.class, Level.INFO, "Transitioning to Implement state");

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      dtwm.transitionTo(DefaultTeamState.Implement, null, false, transaction);
      teamWf.persist(transaction);
      transaction.execute();

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
      Artifact rootArtifact =
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(teamWf.getSmaMgr().getBranchMgr().getWorkingBranch());
      Artifact blk3MainArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT,
                  teamWf.getSmaMgr().getBranchMgr().getWorkingBranch(),
                  TestType.BranchViaVersions.name() + " Requirement");
      rootArtifact.addChild(blk3MainArt);
      blk3MainArt.persist();

      // commit branch
      commitBranch(teamWf);

      // test change report
      OseeLog.log(AtsPlugin.class, Level.INFO, "Test change report results");
      Collection<Artifact> newArts =
            teamWf.getSmaMgr().getBranchMgr().getChangeDataFromEarliestTransactionId().getArtifacts(KindType.Artifact,
                  ModificationType.NEW);
      assertTrue("Should be 1 new artifact in change report, found " + newArts.size(), newArts.size() == 1);

   }

   @org.junit.Test
   public void testBranchViaTeamDefinition() throws Exception {

      OseeLog.log(AtsPlugin.class, Level.INFO, "Running testBranchViaTeamDefinition...");

      String namespace = "org.branchTest." + TestType.BranchViaTeamDef.name().toLowerCase();
      // Cleanup from previous run
      cleanupBranchTest(TestType.BranchViaTeamDef);

      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring ATS for team org.branchTest.viaTeamDefs");
      // create team definition and actionable item
      AtsConfigManager.configureAtsForDefaultTeam(namespace, TestType.BranchViaTeamDef.name(), null, Arrays.asList(
            TestType.BranchViaTeamDef.name() + "- A1", TestType.BranchViaTeamDef.name() + "- A2"), namespace);

      TestUtil.sleep(2000);

      // create main branch
      OseeLog.log(AtsPlugin.class, Level.INFO, "Creating root branch");
      // Create SAW_Bld_2 branch off SAW_Bld_1
      Branch viaTeamDefBranch = createRootBranch(TestType.BranchViaTeamDef);

      TestUtil.sleep(2000);

      // configure team def to use branch
      OseeLog.log(AtsPlugin.class, Level.INFO, "Configuring team def to use branch and allow create/commit");
      TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME,
                  TestType.BranchViaTeamDef.name(), AtsUtil.getAtsBranch());
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
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      ActionArtifact actionArt =
            ActionManager.createAction(null, TestType.BranchViaTeamDef.name() + " Req Changes", "description",
                  ChangeType.Problem, PriorityType.Priority_1, Arrays.asList("Other"), false, null,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(TestType.BranchViaTeamDef.name() + "- A1")),
                  transaction);
      transaction.execute();

      final TeamWorkFlowArtifact teamWf = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);

      // Transition to desired state
      OseeLog.log(AtsPlugin.class, Level.INFO, "Transitioning to Implement state");
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      dtwm.transitionTo(DefaultTeamState.Implement, null, false, transaction);
      teamWf.persist(transaction);
      transaction.execute();

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
      Artifact rootArtifact =
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(teamWf.getSmaMgr().getBranchMgr().getWorkingBranch());
      Artifact blk3MainArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT,
                  teamWf.getSmaMgr().getBranchMgr().getWorkingBranch(),
                  TestType.BranchViaTeamDef.name() + " Requirement");
      rootArtifact.addChild(blk3MainArt);
      blk3MainArt.persist();

      // commit branch
      commitBranch(teamWf);

      // test change report
      OseeLog.log(AtsPlugin.class, Level.INFO, "Test change report results");
      Collection<Artifact> newArts =
            teamWf.getSmaMgr().getBranchMgr().getChangeDataFromEarliestTransactionId().getArtifacts(KindType.Artifact,
                  ModificationType.NEW);
      assertTrue("Should be 1 new artifact in change report, found " + newArts.size(), newArts.size() == 1);
   }

   private Branch createRootBranch(ITestBranch branch) throws Exception {
      return BranchManager.createTopLevelBranch(branch.name(), branch.name(), branch.getGuid());
   }

   private void cleanupBranchTest(TestType testType) throws Exception {
      String namespace = "org.branchTest." + testType.name().toLowerCase();
      OseeLog.log(AtsPlugin.class, Level.INFO, "Cleanup from previous run of ATS for team " + namespace);
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      ActionArtifact aArt =
            (ActionArtifact) ArtifactQuery.checkArtifactFromTypeAndName(ActionArtifact.ARTIFACT_NAME,
                  testType.name() + " Req Changes", AtsUtil.getAtsBranch());
      if (aArt != null) {
         for (TeamWorkFlowArtifact teamArt : aArt.getTeamWorkFlowArtifacts()) {
            SMAEditor.close(Collections.singleton(teamArt), false);
            teamArt.deleteAndPersist(transaction);
         }
         aArt.deleteAndPersist(transaction);
      }
      transaction.execute();

      // Delete VersionArtifacts
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      for (Artifact verArt : ArtifactQuery.getArtifactListFromType(VersionArtifact.ARTIFACT_NAME,
            AtsUtil.getAtsBranch())) {
         if (verArt.getName().contains(testType.name())) {
            verArt.deleteAndPersist(transaction);
         }
      }
      transaction.execute();

      // Delete Team Definitions
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      Artifact art =
            ArtifactQuery.checkArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME, testType.name(),
                  AtsUtil.getAtsBranch());
      if (art != null) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();

      // Delete AIs
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      art =
            ArtifactQuery.checkArtifactFromTypeAndName(ActionableItemArtifact.ARTIFACT_NAME, testType.name(),
                  AtsUtil.getAtsBranch());
      if (art != null) {
         for (Artifact childArt : art.getChildren()) {
            childArt.deleteAndPersist(transaction);
         }
         art.deleteAndPersist(transaction);
      }
      transaction.execute();

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      for (Artifact workArt : ArtifactQuery.getArtifactListFromType(WorkPageDefinition.ARTIFACT_NAME,
            AtsUtil.getAtsBranch())) {
         if (workArt.getName().startsWith(namespace)) {
            workArt.deleteAndPersist(transaction);
         }
      }
      for (Artifact workArt : ArtifactQuery.getArtifactListFromType(WorkFlowDefinition.ARTIFACT_NAME,
            AtsUtil.getAtsBranch())) {
         if (workArt.getName().startsWith(namespace)) {
            workArt.deleteAndPersist(transaction);
         }
      }
      transaction.execute();

      try {
         // delete working branches
         for (Branch workingBranch : BranchManager.getBranches(BranchArchivedState.ALL, BranchControlled.ALL,
               BranchType.WORKING)) {
            if (workingBranch.getName().contains(testType.name())) {
               BranchManager.purgeBranchInJob(workingBranch);
               TestUtil.sleep(2000);
            }
         }
         // delete baseline branch
         Branch branch = BranchManager.getBranchByGuid(testType.getGuid());
         if (branch != null) {
            BranchManager.purgeBranchInJob(branch);
            TestUtil.sleep(2000);
         }

      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   private void commitBranch(TeamWorkFlowArtifact teamWf) throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Commit Branch");
      teamWf.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true,
            teamWf.getSmaMgr().getBranchMgr().getWorkingBranch().getParentBranch(), true);
      TestUtil.sleep(2000);
   }

   private void createBranch(String namespace, TeamWorkFlowArtifact teamWf) throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Creating working branch");
      String implementPageId = namespace + ".Implement";
      Result result = teamWf.getSmaMgr().getBranchMgr().createWorkingBranch(implementPageId, false);
      if (result.isFalse()) {
         result.popup();
         return;
      }
      TestUtil.sleep(2000);
   }

   private void verifyXWidgetsExistInEditor(TeamWorkFlowArtifact teamWf) throws OseeCoreException {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Verify XWorkingBranch and XCommitManger widgets exist in editor");
      SMAEditor smaEditor = SMAEditor.getSmaEditor(teamWf);
      assertNotNull("Can't retrieve SMAEditor for workflow " + teamWf, smaEditor);

      Collection<XWidget> xWidgets =
            smaEditor.getXWidgetsFromState(smaEditor.getSmaMgr().getStateMgr().getCurrentStateName(),
                  XWorkingBranch.class);
      assertTrue("Should be 1 XWorkingBranch widget in current state, found " + xWidgets.size(), xWidgets.size() == 1);
      xWidgets =
            smaEditor.getXWidgetsFromState(smaEditor.getSmaMgr().getStateMgr().getCurrentStateName(),
                  XCommitManager.class);
      assertTrue("Should be 1 XCommitManager widget in current state, found " + xWidgets.size(), xWidgets.size() == 1);
   }

   private void setupWorkflowPageToHaveCreateCommitBranchWidgets(String namespace) throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO, "Setup new workflow page to have create/commit branch widgets");
      String implementPageId = namespace + ".Implement";
      Artifact implementPageDef = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(implementPageId);
      implementPageDef.addRelation(AtsRelation.WorkItem__Child,
            WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(ATSAttributes.WORKING_BRANCH_WIDGET.getStoreName()));
      implementPageDef.addRelation(AtsRelation.WorkItem__Child,
            WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(ATSAttributes.COMMIT_MANAGER_WIDGET.getStoreName()));
      implementPageDef.persist();
   }

   @After
   public void tearDown() throws Exception {
      cleanupBranchTest(TestType.BranchViaVersions);
      cleanupBranchTest(TestType.BranchViaTeamDef);
   }

}
