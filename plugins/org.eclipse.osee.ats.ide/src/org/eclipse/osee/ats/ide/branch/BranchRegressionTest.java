/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.branch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.branch.BranchStatus;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.widgets.XWorkingBranchEnablement;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public abstract class BranchRegressionTest {

   private static final String PRE_BRANCH_CHANGES = "testMakePreBranchChanges_";
   public static String PRE_BRANCH_ARTIFACT_NAME = "Pre-Branch Artifact to Delete";
   protected static Artifact actionArt;
   protected static TeamWorkFlowArtifact reqTeam;
   protected static TeamWorkFlowArtifact codeTeam;
   protected static TeamWorkFlowArtifact testTeam;

   public static String SOFTWARE_REQUIREMENTS = "Software Requirements";
   public static String FIRST_ARTIFACT = "First Artifact";
   public static String SECOND_ARTIFACT = "Second Artifact";
   public static String THIRD_ARTIFACT = "Third Artifact";
   public static String FOURTH_ARTIFACT = "Fourth Artifact - No CSCI";
   public static String FIFTH_ARTIFACT = "Fifth Artifact - Unspecified CSCI";
   public static String SUBSYSTEM_ARTIFACT = "Subsystem Artifact (no partition)";
   protected final List<String> ArtifactModifiedNames = new ArrayList<>();
   protected final List<String> NonRelArtifactModifedNames = new ArrayList<>();

   protected static SevereLoggingMonitor monitorLog;
   protected Artifact softReqArt;
   protected Artifact secondArt;
   protected Artifact thirdArt;
   protected Artifact fourthArt;
   protected Artifact fifthArt;
   protected Artifact createAndDeleteArt;
   protected Artifact subsystemArt;
   protected Artifact preBranchArt;
   protected BranchToken workingBranch;

   /**
    * Test creation of Action (rpcrNum), creation of workingBranch, modification of some artifacts, commit of
    * workingBranch, and verification that artifacts are now on main workingBranch, and creation of code/test tasks. DB
    * must be wiped.
    */
   public BranchRegressionTest() {
      ArtifactModifiedNames.addAll(Arrays.asList(FIRST_ARTIFACT, SECOND_ARTIFACT, THIRD_ARTIFACT, FOURTH_ARTIFACT,
         FIFTH_ARTIFACT, SUBSYSTEM_ARTIFACT, DemoArtifactToken.SystemReqArtifact.getName(), SOFTWARE_REQUIREMENTS));
      NonRelArtifactModifedNames.addAll(Arrays.asList(FIRST_ARTIFACT, SECOND_ARTIFACT, THIRD_ARTIFACT, FOURTH_ARTIFACT,
         FIFTH_ARTIFACT, SUBSYSTEM_ARTIFACT, DemoArtifactToken.SystemReqArtifact.getName()));
   }

   @BeforeClass
   public static void setUp() {
      RenderingUtil.setPopupsAllowed(false);
      OseeProperties.setIsInTest(true);
   }

   @AfterClass
   public static void tearDown() {
      OseeProperties.setIsInTest(false);
   }

   // Extend with additional checks
   protected void testAfterCreateBranchSectionTime() {
      // do nothing
   }

   // Extend with additional checks
   public void testWorkingBranchCommitCheck() {
      // do nothing
   }

   @org.junit.Test
   public void test() {
      testSetup();
      testSetupInitialConditions();
      testMakePreBranchChanges();
      testCreateAction();
      testXCommitManagerAfterActionCreate();
      testCreateBranchFirstTime();
      testDeleteBranch();
      testXCommitManagerAfterDeleteBranch();
      testCreateBranchSecondTime();
      testXWorkingBranchAfterSecondCreateBranch();
      testBranchesListedInXCommitManager();

      // Make and test branch changes - Begin
      testAfterCreateBranchSectionTime(); // Extend with additional checks

      createFirstAndSecondReqArt();
      testCodeTaskCreationAfterFirstAndSecond(); // Extend with additional checks

      createThirdFourthFifthReqArt();
      testCodeTaskCreationAfterThirdFourthFifth(); // Extend with additional checks

      createReqArtToDelete();
      testCodeTaskCreationAfterCreateReqArtToDelete(); // Extend with additional checks

      makeNameChangeToReqArtToDelete();
      testCodeTestCreationAfterChangeToReqArtifactToDelete(); // Extend with additional checks

      deleteReqArtToDelete();
      testCodeTestCreationAfterDeleteReqArtifactToDelete(); // Extend with additional checks

      createSubsystemArt();
      deletePreBranchArt();
      // Make and test branch changes - End

      testWorkingBranchCommitCheck();

      // Add trace so commit will go through
      createParentArtsOnWorkingBranch();
      testAfterCreateParentArtsOnWorkingBranch(); // Extend with additional checks

      testWorkingBranchCommit();

      testXWorkingBranchAfterBranchCommit();

      testChangesMadeWereCommitted();

      testRequirementsWorkflowCompletion();
      testCodeTaskCreationAfterReqCompletion();

      testShowRelatedTasksAction(); // Extend with additional checks
      testShowRelatedRequirementAction(); // Extend with additional checks
      testShowRequirementDiffsAction(); // Extend with additional checks

      testSevereLoggingMonitorResults();

      testCleanup();
   }

   public void testSetup() {
      // Clear all listeners so events only processed by this test
      OseeEventManager.removeAllListeners();

      // Purge Action if already exists
      Collection<IAtsAction> actionArts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         AtsApiService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).andAttr(
            AtsAttributeTypes.LegacyPcrId, getLegacyPcrId()).createFilter().getActions());

      Set<Artifact> artsToDel = new HashSet<>();
      for (IAtsAction actionArt : actionArts) {
         artsToDel.add((Artifact) actionArt.getStoreObject());
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(actionArt)) {
            artsToDel.add((Artifact) team.getStoreObject());
            artsToDel.addAll(
               Collections.castAll(AtsObjects.getArtifacts(AtsApiService.get().getTaskService().getTasks(team))));
         }
      }

      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artsToDel));

      testCleanup();
   }

   public void testCleanup() {

      Set<Artifact> artsToDel = new HashSet<>();
      for (String artName : Arrays.asList(PRE_BRANCH_ARTIFACT_NAME)) {
         for (Artifact art : ArtifactQuery.getArtifactListFromName(artName, DemoBranches.SAW_Bld_2,
            DeletionFlag.INCLUDE_DELETED, QueryOption.EXACT_MATCH_OPTIONS)) {
            artsToDel.add(art);
         }
      }
      if (!artsToDel.isEmpty()) {
         Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artsToDel));
      }

      // Purge working branches
      List<String> branchNameStrs = new ArrayList<String>();
      branchNameStrs.add(getLegacyPcrId());
      branchNameStrs.add(PRE_BRANCH_CHANGES);
      if (Strings.isValid(getBranchNameContains())) {
         branchNameStrs.add(getBranchNameContains());
      }
      purgeWorkingBranches(branchNameStrs);

      AtsUtilClient.setEmailEnabled(true);
      OseeEventManager.removeAllListeners();
   }

   protected static void purgeWorkingBranches(Collection<String> branchNamesContain) {
      try {
         // Delete working branches
         for (BranchToken workingBranch : BranchManager.getBranches(BranchArchivedState.ALL, BranchType.WORKING)) {
            for (String branchName : branchNamesContain) {
               if (workingBranch.getName().contains(branchName)) {
                  ArtifactCache.deCache(workingBranch);
                  BranchManager.purgeBranch(workingBranch);
               }
            }
         }
      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   public void testSetupInitialConditions() {
      AtsUtilClient.setEmailEnabled(false);
      AtsUtil.setIsInTest(true);
      monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
   }

   public void testMakePreBranchChanges() {
      Assert.assertNotNull("Can't get program workingBranch", getProgramBranch());

      Artifact softReqArt =
         ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name, SOFTWARE_REQUIREMENTS, getProgramBranch());
      Assert.assertNotNull("Can't get softReqArt", softReqArt);

      AtsApiService.get().getAccessControlService().setPermission(UserManager.getUser(), getProgramBranch(),
         PermissionEnum.FULLACCESS);

      createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, softReqArt, PRE_BRANCH_ARTIFACT_NAME,
         getPreBranchCscis(), getProgramBranch());
      Assert.assertNotNull("Can't get new software requirement artifact", softReqArt);

   }

   public abstract void testCreateAction();

   /**
    * @return true if code workflow should be created for this test
    */
   public boolean hasCodeWorkflow() {
      return true;
   }

   protected void testTeamWorkflows(Collection<IAtsTeamWorkflow> teamWfs) {
      for (IAtsTeamWorkflow teamWf : teamWfs) {
         if (teamWf.getTeamDefinition().getName().contains("Req")) {
            reqTeam = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
         if (hasCodeWorkflow() && teamWf.getTeamDefinition().getName().contains("Code")) {
            codeTeam = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
         if (teamWf.getTeamDefinition().getName().contains("Test")) {
            testTeam = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
      }

      actionArt = (Artifact) reqTeam.getParentAction().getStoreObject();

      validateNoBoostrapUser();
      Assert.assertNotNull("Req workflow not created", reqTeam);
      if (hasCodeWorkflow()) {
         Assert.assertNotNull("Code workflow not created", codeTeam);
      }
      Assert.assertNotNull("Test workflow not created", testTeam);
      Assert.assertNotNull("Action not created", actionArt);

      WorkflowEditor.editArtifact(reqTeam);

      // Verify the enablement of XWorkingBranch
      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Not_Started, enablement.getStatus());
      Assert.assertTrue("Create Branch Button should be enabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertFalse("Show Artifact Explorer Button should be disabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertFalse("Show Change Report Button should be disabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertFalse("Delete Button should be disabled", enablement.isDeleteBranchButtonEnabled());
   }

   protected void validateNoBoostrapUser() {
      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(actionArt)) {
         team.getStateMgr().validateNoBootstrapUser();
      }
   }

   public void testXCommitManagerAfterActionCreate() {
      Assert.assertEquals("Should be no committed branches", 0,
         AtsApiService.get().getBranchService().getBranchesCommittedTo(reqTeam).size());
   }

   public void testCreateBranchFirstTime() {
      AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(reqTeam, true);
      workingBranch = reqTeam.getWorkingBranchForceCacheUpdate();
      Assert.assertNotNull("workingBranch returned null", workingBranch);

      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Changes_InProgress, enablement.getStatus());
      Assert.assertFalse("Create Branch Button should be disabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertTrue("Show Artifact Explorer Button should be enabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertTrue("Show Change Report Button should be enabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertTrue("Delete Button should be enabled", enablement.isDeleteBranchButtonEnabled());
   }

   public void testDeleteBranch() {
      // verify deletion of the workingBranch
      AtsApiService.get().getBranchServiceIde().deleteWorkingBranch(reqTeam, false, true);
      Assert.assertTrue(reqTeam.getWorkingBranch().isInvalid());

      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Not_Started, enablement.getStatus());
      Assert.assertTrue("Create Branch Button should be enabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertFalse("Show Artifact Explorer Button should be disabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertFalse("Show Change Report Button should be disabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertFalse("Delete Button should be disabled", enablement.isDeleteBranchButtonEnabled());
   }

   public void testXCommitManagerAfterDeleteBranch() {
      IAtsBranchService branchService = AtsApiService.get().getBranchService();
      Collection<CommitConfigItem> configArtSet = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      for (CommitConfigItem configArt : configArtSet) {
         CommitStatus xCommitStatus = branchService.getCommitStatus(reqTeam, configArt);
         Assert.assertTrue(
            "XCommitManager Status not as expected: " + CommitStatus.Working_Branch_Not_Created.name() + " [" + configArt.getCommitFullDisplayName() + "]",
            xCommitStatus.equals(CommitStatus.Working_Branch_Not_Created));
      }
   }

   public void testCreateBranchSecondTime() {
      AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(reqTeam, true);
      workingBranch = reqTeam.getWorkingBranchForceCacheUpdate();
      Assert.assertNotNull("workingBranch returned null", workingBranch);
   }

   public void testXWorkingBranchAfterSecondCreateBranch() {

      // Verify the new status of the XWorkingBranch
      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Changes_InProgress, enablement.getStatus());
      Assert.assertFalse("Create Branch Button should be disabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertTrue("Show Artifact Explorer Button should be enabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertTrue("Show Change Report Button should be enabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertTrue("Delete Button should be enabled", enablement.isDeleteBranchButtonEnabled());
   }

   public void testBranchesListedInXCommitManager() {
      IAtsBranchService branchService = AtsApiService.get().getBranchService();
      Collection<CommitConfigItem> configItems = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      // Verify the Parallel Branches listed in the XCommitManager
      Assert.assertTrue("parallel workingBranch check failed => " + configItems.size(),
         configItems.size() == getBranchNames().size());
      for (CommitConfigItem configItem : configItems) {
         BranchId branch = branchService.getBranch(configItem);
         Assert.assertTrue("Missing parallel workingBranch => " + branchService.getBranchName(branch),
            getBranchNames().contains(branchService.getBranchName(branch)));
      }
   }

   protected Artifact getOrCreateArtifact(ArtifactTypeToken artifactType, String artifactName, BranchToken branch, Artifact parent, boolean persist) {
      Artifact art = ArtifactQuery.checkArtifactFromTypeAndName(artifactType, artifactName, branch);
      if (art == null) {
         art = ArtifactTypeManager.addArtifact(artifactType, branch, artifactName);
         if (parent != null) {
            parent.addChild(art);
         }
         if (persist) {
            art.persist(getClass().getSimpleName());
         }
      }
      return art;
   }

   protected void setAttribute(Artifact artifact, AttributeTypeId attributeType, String... values) {
      artifact.setAttributeValues(attributeType, Arrays.asList(values));
   }

   private void deletePreBranchArt() {
      preBranchArt = softReqArt.getChild(PRE_BRANCH_ARTIFACT_NAME);
      Assert.assertNotNull("Couldn't retrieve pre-workingBranch artifact", preBranchArt);
      preBranchArt.deleteAndPersist(getClass().getSimpleName());
   }

   private void createSubsystemArt() {
      subsystemArt = createSubsystemArtifact(softReqArt, SUBSYSTEM_ARTIFACT);
      Assert.assertNotNull(subsystemArt);

      subsystemArt.setSingletonAttributeValue(CoreAttributeTypes.StaticId, "Test");
      subsystemArt.persist(getClass().getSimpleName());
   }

   private void deleteReqArtToDelete() {
      // Delete this one; Since created and deleted in same workingBranch,
      createAndDeleteArt.deleteAndPersist(getClass().getSimpleName());
   }

   private void makeNameChangeToReqArtToDelete() {
      createAndDeleteArt.setName(DemoArtifactToken.InBranchArtifactToDelete.getName() + " Changed");
      createAndDeleteArt.persist(getClass().getSimpleName());
   }

   private void createReqArtToDelete() {
      createAndDeleteArt = createSoftwareArtifact(DemoArtifactToken.InBranchArtifactToDelete, softReqArt,
         getInBranchArtifactCscis(), workingBranch);
      Assert.assertNotNull(createAndDeleteArt);
   }

   protected void createThirdFourthFifthReqArt() {
      thirdArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementProcedureMsWord, softReqArt,
         THIRD_ARTIFACT, getThirdArtifactCscis(), workingBranch);
      Assert.assertNotNull(thirdArt);
      fourthArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementFunctionMsWord, softReqArt,
         FOURTH_ARTIFACT, null, workingBranch);
      Assert.assertNotNull(fourthArt);
      fifthArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementFunctionMsWord, softReqArt, FIFTH_ARTIFACT,
         new String[] {AttributeId.UNSPECIFIED}, workingBranch);
      Assert.assertNotNull(fifthArt);

   }

   private void createFirstAndSecondReqArt() {
      softReqArt = getSoftwareReqFolder(workingBranch);
      Artifact firstArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, softReqArt,
         FIRST_ARTIFACT, getFirstArtifactCscis(), workingBranch);
      Assert.assertNotNull(firstArt);
      secondArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, softReqArt, SECOND_ARTIFACT,
         getSecondArtifactCscis(), workingBranch);
      Assert.assertNotNull(secondArt);
   }

   protected Artifact getSoftwareReqFolder(BranchId branch) {
      return ArtifactQuery.getArtifactFromId(CoreArtifactTokens.SoftwareRequirementsFolder, branch);
   }

   public void createParentArtsOnWorkingBranch() {
      // Create set of software requirement changes
      Artifact softReqArt = getSoftwareReqFolder(workingBranch);

      // No Add/Mode task cause wrong artifact type; Will get Relation task cause related to Soft Req
      Artifact systemReqArt = createSoftwareArtifact(DemoArtifactToken.SystemReqArtifact, softReqArt,
         getFirstArtifactCscis(), workingBranch);
      Assert.assertNotNull(systemReqArt);
      Artifact firstArt =
         ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name, FIRST_ARTIFACT, workingBranch);
      Artifact secondArt =
         ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name, SECOND_ARTIFACT, workingBranch);
      systemReqArt.addRelation(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, firstArt);
      systemReqArt.addRelation(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, secondArt);
      systemReqArt.persist("System Req for Arts");
   }

   public void testWorkingBranchCommit() {
      IAtsBranchService branchService = AtsApiService.get().getBranchService();
      Collection<CommitConfigItem> configItems = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      // Since commit workingBranch is a separate job, a callback will resume this thread
      // commit all of the branches
      Assert.assertTrue(
         "Epected " + getExpectedBranchConfigItems() + " artifacts configured to commit to;  Actual " + configItems.size(),
         configItems.size() == getExpectedBranchConfigItems());

      // Commit parent workingBranch first
      for (CommitConfigItem configItem : configItems) {
         if (branchService.isBranchValid(configItem) && BranchManager.getParentBranch(workingBranch).equals(
            configItem.getBaselineBranchId())) {
            BranchId branch = branchService.getBranch(configItem);

            XResultData rd = AtsApiService.get().getBranchServiceIde().commitWorkingBranch(reqTeam, false, true, branch,
               branchService.isBranchesAllCommittedExcept(reqTeam, branch), new XResultData());
            Assert.assertTrue("Commit Failed " + rd.toString(), rd.isSuccess());
         }
      }

      // Then commit rest
      int commitCount = 0;
      for (CommitConfigItem configItem : configItems) {
         if (branchService.isBranchValid(
            configItem) && !BranchManager.getParentBranch(workingBranch).equals(configItem.getBaselineBranchId())) {
            BranchId branch = branchService.getBranch(configItem);
            XResultData rd = AtsApiService.get().getBranchServiceIde().commitWorkingBranch(reqTeam, false, true, branch,
               branchService.isBranchesAllCommittedExcept(reqTeam, branch), new XResultData());
            Assert.assertTrue("Commit Failed " + rd.toString(), rd.isSuccess());
            commitCount++;
         }
      }
      Assert.assertTrue(
         "Expected to commit " + (getExpectedBranchConfigItems() - 1) + " other branches;  only committed " + commitCount,
         commitCount == getExpectedBranchConfigItems() - 1);
   }

   public void testXWorkingBranchAfterBranchCommit() {

      WorkflowEditor.editArtifact(reqTeam);

      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Changes_NotPermitted__BranchCommitted, enablement.getStatus());
      Assert.assertFalse("Create Branch Button should be disabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertFalse("Show Artifact Explorer Button should be disabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertTrue("Show Change Report Button should be enabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertFalse("Delete Button should be disabled", enablement.isDeleteBranchButtonEnabled());

      // Verify the XCommitManager's status; All branches should be CommitStatus.Committed
      IAtsBranchService branchService = AtsApiService.get().getBranchService();
      Collection<CommitConfigItem> configItems = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      for (CommitConfigItem configItem : configItems) {
         CommitStatus xCommitStatus = branchService.getCommitStatus(reqTeam, configItem);
         Assert.assertTrue(
            "XCommitManager Status not as expected: " + CommitStatus.Committed.name() + " [" + configItem.getCommitFullDisplayName() + "]",
            xCommitStatus.equals(CommitStatus.Committed));
      }
   }

   public void testChangesMadeWereCommitted() {
      // Verify that the changes made on the workingBranch were committed to the main workingBranch
      // TODO This needs to be updated do handle multiple config artifacts when test gets updated to test for multiples
      ChangeData changeData = AtsApiService.get().getBranchServiceIde().getChangeDataFromEarliestTransactionId(reqTeam);

      // Check for modified artifacts
      Collection<Artifact> artifactsModified =
         changeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.NEW, ModificationType.MODIFIED);
      for (Artifact artifact : artifactsModified) {
         Assert.assertTrue("Unexpected Modified Artifact named \"" + artifact.getName() + "\"",
            ArtifactModifiedNames.contains(artifact.getName()));
      }
      for (String artifactName : ArtifactModifiedNames) {
         boolean found = false;
         for (Artifact artifact : artifactsModified) {
            if (artifact.getName().equals(artifactName)) {
               found = true;
            }
         }
         Assert.assertTrue("Modified Artifact expected but not found; named \"" + artifactName + "\"", found);
      }

      Collection<Artifact> nonRelationArtifactsModified =
         changeData.getArtifacts(KindType.Artifact, ModificationType.NEW, ModificationType.MODIFIED);
      for (Artifact artifact : nonRelationArtifactsModified) {
         Assert.assertTrue("Unexpected Non-Relation Modified Artifact named \"" + artifact.getName() + "\"",
            NonRelArtifactModifedNames.contains(artifact.getName()));
      }
      for (String artifactName : NonRelArtifactModifedNames) {
         boolean found = false;
         for (Artifact artifact : nonRelationArtifactsModified) {
            if (artifact.getName().equals(artifactName)) {
               found = true;
            }
         }
         Assert.assertTrue("Non-Relation Modified Artifact expected but not found; named \"" + artifactName + "\"",
            found);
      }

      /**
       * Check that the single pre-workingBranch artifact that was deleted in this workingBranch comes back AND that the
       * in-workingBranch artifact that was created and deleted in the workingBranch DOES NOT come back
       */
      Collection<Artifact> deleted = changeData.getArtifacts(KindType.Artifact, ModificationType.DELETED);
      if (deleted.size() != 1) {
         Assert.fail("Deleted Artifacts should be 1; Actual is " + deleted.size());
      } else {
         Artifact art = deleted.iterator().next();
         Assert.assertTrue("Pre-Branch Artifact should have been returned as deleted",
            art.getName().equals(PRE_BRANCH_ARTIFACT_NAME));
         Assert.assertFalse("In-Branch Artifact should NOT have been returned as deleted, and was",
            art.getName().equals(DemoArtifactToken.InBranchArtifactToDelete.getName()));
      }
   }

   public void testRequirementsWorkflowCompletion() {
      // Complete Requirements and Start Code/Test
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("testRequirementsWorkflowCompletion");
      TransitionHelper helper =
         new TransitionHelper("Branch Regression Test", Arrays.asList(reqTeam), TeamState.Completed.getName(), null,
            null, changes, AtsApiService.get(), TransitionOption.OverrideAssigneeCheck);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(helper);
      if (!results.isEmpty()) {
         Assert.fail("Complete Requirements Failed " + results.toString());
      }
   }

   // Override to provide additional checks
   protected void testCodeTaskCreationAfterCreateReqArtToDelete() {
      // do nothing
   }

   // Override to provide additional checks
   protected void testCodeTestCreationAfterChangeToReqArtifactToDelete() {
      // do nothing
   }

   // Override to provide additional checks
   protected void testCodeTestCreationAfterDeleteReqArtifactToDelete() {
      // do nothing
   }

   // Override to provide additional checks
   protected void testCodeTaskCreationAfterThirdFourthFifth() {
      // do nothing
   }

   // Override to provide additional checks
   protected void testCodeTaskCreationAfterFirstAndSecond() {
      // do nothing
   }

   /**
    * Used to test tasks that exist after requirements completion
    */
   protected Collection<String> getFinalTaskNames() {
      return java.util.Collections.emptyList();
   }

   /**
    * Uses getFinalTaskNames() to test final tasks after req transition
    */
   protected void testCodeTaskCreationAfterReqCompletion() {

      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(actionArt)) {
         if (team.getTeamDefinition().toString().contains("Req")) {
            continue;
         }
         IAtsTeamWorkflow testWf = null, codeWf = null;
         if (team.isOfType(getTestTeamWfArtType())) {
            testWf = team;
         } else if (team.isOfType(getCodeTeamWfArtType())) {
            codeWf = team;
         }
         Assert.assertTrue(testWf != null || codeWf != null);

         int loopCount = 0;
         int count = 0;
         Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(team);
         while (getFinalTaskNames().size() != count && loopCount < 10) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex) {
               // do nothing
            }
            AtsApiService.get().getStoreService().reload(Arrays.asList(team));

            tasks = AtsApiService.get().getTaskService().getTasks(team);
            count = tasks.size();

            loopCount++;
         }
         Assert.assertEquals(getFinalTaskNames().size(), count);

         XResultData results = new XResultData();
         for (IAtsTask task : tasks) {
            if (!getFinalTaskNames().contains(task.getName())) {
               results.errorf("Task named [%s]; not found for task %s", task.getName(), task.toStringWithId());
            }
         }
         for (String taskName : getFinalTaskNames()) {
            boolean found = false;
            for (IAtsTask task : tasks) {
               if (task.getName().equals(taskName)) {
                  found = true;
                  break;
               }
            }
            if (!found) {
               results.errorf("Expected Task named [%s] not found", taskName);
            }
         }
         Assert.assertTrue(results.toString(), results.isSuccess());

      }
   }

   // Override to provide additional checks
   protected void testShowRequirementDiffsAction() {
      // do nothing
   }

   public void testSevereLoggingMonitorResults() {
      List<IHealthStatus> stats = monitorLog.getAllLogs();
      for (IHealthStatus stat : new ArrayList<>(stats)) {
         if (stat.getException() != null) {
            Assert.fail("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
   }

   protected Artifact createSoftwareArtifact(ArtifactToken artifactToken, Artifact parent, String[] partitions, BranchToken branch) throws MultipleAttributesExist {
      SkynetTransaction tx = TransactionManager.createTransaction(branch, "Create " + artifactToken.getName());
      Artifact newArt = ArtifactTypeManager.addArtifact(artifactToken, branch);
      Artifact parentArt = setParent(parent, partitions, newArt, tx);
      tx.addArtifact(newArt);
      tx.addArtifact(parent);
      tx.execute();
      return parentArt;
   }

   private Artifact setParent(Artifact parent, String[] partitions, Artifact art1, SkynetTransaction tx) {
      if (partitions != null) {
         art1.setAttributeValues(getCsciAttribute(), Arrays.asList(partitions));
      }
      parent.addChild(art1);
      return art1;
   }

   protected Artifact createSoftwareArtifact(ArtifactTypeToken artifactType, Artifact parent, String title, String[] partitions, BranchToken branch) {
      Artifact parentArt = null;
      try {
         SkynetTransaction.setOverrideAccess(true);
         SkynetTransaction tx = TransactionManager.createTransaction(branch, "Create " + title);
         Artifact newArt = ArtifactTypeManager.addArtifact(artifactType, branch, title);
         parentArt = setParent(parent, partitions, newArt, tx);
         tx.addArtifact(newArt);
         tx.addArtifact(parent);
         tx.execute();
      } finally {
         SkynetTransaction.setOverrideAccess(false);
      }
      return parentArt;
   }

   public Artifact createSubsystemArtifact(Artifact parent, String title) {
      Artifact art1 = null;

      try {
         art1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SubsystemRequirementMsWord, workingBranch);
      } catch (IllegalArgumentException ex) {
         Assert.fail("Can't find descriptor for " + CoreArtifactTypes.SubsystemRequirementMsWord);
         return null;
      }

      art1.setName(title);
      art1.persist(getClass().getSimpleName());
      parent.addChild(art1);
      parent.persist(getClass().getSimpleName());
      return art1;
   }

   protected void testTasksAgainstExpected(IAtsTeamWorkflow teamWf, Collection<String> expected) {
      Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(teamWf);
      Assert.assertEquals(expected.size(), tasks.size());

      for (IAtsTask task : tasks) {
         boolean contains = expected.contains(task.getName());
         Assert.assertTrue(String.format("Expected task [%s] and not found in %s", task.getName(), expected), contains);

         boolean deReferenced = AtsApiService.get().getTaskService().isAutoGenDeReferenced(task);
         boolean autoGen = AtsApiService.get().getTaskService().isAutoGen(task);

         if (deReferenced) {
            Assert.assertFalse(autoGen);
         } else {
            Assert.assertTrue(autoGen);
         }

         if (autoGen) {
            Assert.assertFalse(deReferenced);
         } else {
            Assert.assertTrue(deReferenced);
         }
      }
   }

   public static enum XWorkingBranchButtonState {
      CreateBranch,
      ArtExplore,
      ShowChangeReport,
      DeleteBranch
   };

   public abstract String getLegacyPcrId();

   public String getBranchNameContains() {
      return null;
   }

   public abstract String[] getPreBranchCscis();

   public abstract String[] getFirstArtifactCscis();

   public abstract String[] getSecondArtifactCscis();

   public abstract String[] getThirdArtifactCscis();

   public abstract String[] getInBranchArtifactCscis();

   public abstract AttributeTypeId getCsciAttribute();

   public abstract BranchToken getProgramBranch();

   public abstract ArtifactTypeToken getCodeTeamWfArtType();

   public abstract ArtifactTypeToken getTestTeamWfArtType();

   // Override to provide additional checks
   protected void testAfterCreateParentArtsOnWorkingBranch() {
      // do nothing
   }

   // Override to provide additional checks
   protected void testShowRelatedTasksAction() {
      // do nothing
   }

   // Override to provide additional checks
   protected void testTransitionCreatedTasks(Collection<IAtsTask> tasks) {
      // do nothing
   }

   // Override to provide additional checks
   protected int getExpectedBranchConfigItems() {
      return 1;
   }

   // Override to provide additional checks
   protected void testShowRelatedRequirementAction() {
      // do nothing
   }

   public abstract List<String> getBranchNames();

   protected void testTaskWorkDefinition(StringBuffer sb, IAtsTask taskArt) {
      String taskWorkDefName = taskArt.getWorkDefinition().getName();
      boolean isTest = taskWorkDefName.contains("Test");
      if (isTest && !taskWorkDefName.equals("WorkDef_Task_Lba_Test_ForRpcr")) {
         sb.append(
            "Error: Test " + taskArt + " work definition should be WorkDef_Task_Lba_Test_ForRpcr and not " + taskWorkDefName + "\n");
      } else if (!isTest && !taskWorkDefName.equals("WorkDef_Task_Lba_Code_ForRpcr")) {
         sb.append(
            "Error: Code " + taskArt + " work definition should be WorkDef_Task_Lba_Code_ForRpcr and not " + taskWorkDefName + "\n");
      }
   }
}
