/*
 * Created on Aug 29, 2005
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.ats.ide.branch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.branch.BranchStatus;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.widgets.XWorkingBranchEnablement;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.Requirements;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
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
   private static String IN_BRANCH_ARTIFACT_NAME = "In-Branch Artifact to Delete";
   protected static Artifact actionArt;
   protected static TeamWorkFlowArtifact reqTeam;
   protected static TeamWorkFlowArtifact codeTeam;
   protected static TeamWorkFlowArtifact testTeam;

   public static String FIRST_ARTIFACT = "First Artifact";
   public static String SECOND_ARTIFACT = "Second Artifact";
   public static String THIRD_ARTIFACT = "Third Artifact";
   public static String FOURTH_ARTIFACT = "Fourth Artifact - No CSCI";
   public static String FIFTH_ARTIFACT = "Fifth Artifact - Unspecified CSCI";
   public static String SUBSYSTEM_ARTIFACT = "Subsystem Artifact (no partition)";
   public static String PARENT_ARTIFACT = "Parent Artifact";
   protected final List<String> ArtifactModifiedNames = new ArrayList<>();
   protected final List<String> NonRelArtifactModifedNames = new ArrayList<>();

   private static SevereLoggingMonitor monitorLog;

   /**
    * Test creation of Action (rpcrNum), creation of branch, modification of some artifacts, commit of branch, and
    * verification that artifacts are now on main branch, and creation of code/test tasks. DB must be wiped.
    */
   public BranchRegressionTest() {
      ArtifactModifiedNames.addAll(Arrays.asList(FIRST_ARTIFACT, SECOND_ARTIFACT, THIRD_ARTIFACT, FOURTH_ARTIFACT,
         FIFTH_ARTIFACT, SUBSYSTEM_ARTIFACT, PARENT_ARTIFACT, Requirements.SOFTWARE_REQUIREMENTS));
      NonRelArtifactModifedNames.addAll(Arrays.asList(FIRST_ARTIFACT, SECOND_ARTIFACT, THIRD_ARTIFACT, FOURTH_ARTIFACT,
         FIFTH_ARTIFACT, SUBSYSTEM_ARTIFACT, PARENT_ARTIFACT));
   }

   @BeforeClass
   public static void setUp() throws Exception {
      RenderingUtil.setPopupsAllowed(false);
      OseeProperties.setIsInTest(true);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      OseeProperties.setIsInTest(false);
   }

   protected void testAfterCreateBranchSectionTime() {
      // do nothing
   }

   @org.junit.Test
   public void test() throws Exception {
      testCleanup();
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
      testAfterCreateBranchSectionTime();
      testCreateNewArtifactsOnWorkingBranch();
      testWorkingBranchCommitCheck();
      testCreateParentArtsOnWorkingBranch();
      testWorkingBranchCommit();
      testXWorkingBranchAfterBranchCommit();
      testChangesMadeWereCommitted();
      testRequirementsWorkflowCompletion();
      testCodeTaskCreationAfterReqCompletion();
      testShowRelatedTasksAction();
      testShowRelatedRequirementAction();
      testShowRequirementDiffsAction();
      testSevereLoggingMonitorResults();
      testCleanupFinal();
   }

   public void testCleanup() throws Exception {
      // Clear all listeners so events only processed by this test
      OseeEventManager.removeAllListeners();

      // Purge Action if already exists
      Collection<ActionArtifact> actionArts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         AtsClientService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).andAttr(
            AtsAttributeTypes.LegacyPcrId, getRpcrNumber()).createFilter().getActions());

      Set<Artifact> artsToDel = new HashSet<>();
      for (Artifact actionArt : actionArts) {
         artsToDel.add(actionArt);
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(actionArt)) {
            artsToDel.add((Artifact) team.getStoreObject());
            artsToDel.addAll(
               Collections.castAll(AtsObjects.getArtifacts(AtsClientService.get().getTaskService().getTasks(team))));
         }
      }
      // Purge pre-branch artifact
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artsToDel));

      for (Artifact art : ArtifactQuery.getArtifactListFromName(PRE_BRANCH_ARTIFACT_NAME, getProgramBranch())) {
         art.deleteAndPersist();
      }

      // Purge working branches
      purgeWorkingBranches(Arrays.asList(getRpcrNumber(), PRE_BRANCH_CHANGES));
   }

   private static void purgeWorkingBranches(Collection<String> branchNamesContain) throws Exception {
      try {
         // Delete working branches
         for (IOseeBranch workingBranch : BranchManager.getBranches(BranchArchivedState.ALL, BranchType.WORKING)) {
            for (String branchName : branchNamesContain) {
               if (workingBranch.getName().contains(branchName)) {
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
      monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
   }

   public void testMakePreBranchChanges() throws Exception {
      Assert.assertNotNull("Can't get program branch", getProgramBranch());

      Artifact softReqArt = ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name,
         Requirements.SOFTWARE_REQUIREMENTS, getProgramBranch());
      Assert.assertNotNull("Can't get softReqArt", softReqArt);

      AccessControlManager.setPermission(UserManager.getUser(), getProgramBranch(), PermissionEnum.FULLACCESS);

      createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirement, softReqArt, PRE_BRANCH_ARTIFACT_NAME,
         getPreBranchCscis(), getProgramBranch());
      Assert.assertNotNull("Can't get new software requirement artifact", softReqArt);

   }

   public abstract void testCreateAction();

   protected void testTeamWorkflows(Collection<IAtsTeamWorkflow> teamWfs) {
      for (IAtsTeamWorkflow teamWf : teamWfs) {
         if (teamWf.getTeamDefinition().getName().contains("Req")) {
            reqTeam = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
         if (teamWf.getTeamDefinition().getName().contains("Code")) {
            codeTeam = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
         if (teamWf.getTeamDefinition().getName().contains("Test")) {
            testTeam = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
      }

      actionArt = reqTeam.getParentActionArtifact();

      validateNoBoostrapUser();
      Assert.assertNotNull("Req workflow not created", reqTeam);
      Assert.assertNotNull("Code workflow not created", codeTeam);
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
      for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(actionArt)) {
         team.getStateMgr().validateNoBootstrapUser();
      }
   }

   public void testXCommitManagerAfterActionCreate() throws Exception {
      Assert.assertEquals("Should be no committed branches", 0,
         AtsClientService.get().getBranchService().getBranchesCommittedTo(reqTeam).size());
   }

   public void testCreateBranchFirstTime() throws Exception {
      AtsBranchUtil.createWorkingBranch_Create(reqTeam, true);
      Assert.assertNotNull("branch returned null", reqTeam.getWorkingBranchForceCacheUpdate());

      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Changes_InProgress, enablement.getStatus());
      Assert.assertFalse("Create Branch Button should be disabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertTrue("Show Artifact Explorer Button should be enabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertTrue("Show Change Report Button should be enabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertTrue("Delete Button should be enabled", enablement.isDeleteBranchButtonEnabled());
   }

   public void testDeleteBranch() throws Exception {
      // verify deletion of the branch
      AtsBranchManager.deleteWorkingBranch(reqTeam, false, true);
      Assert.assertTrue(reqTeam.getWorkingBranch().isInvalid());

      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Not_Started, enablement.getStatus());
      Assert.assertTrue("Create Branch Button should be enabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertFalse("Show Artifact Explorer Button should be disabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertFalse("Show Change Report Button should be disabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertFalse("Delete Button should be disabled", enablement.isDeleteBranchButtonEnabled());
   }

   public void testXCommitManagerAfterDeleteBranch() throws Exception {
      IAtsBranchService branchService = AtsClientService.get().getBranchService();
      Collection<ICommitConfigItem> configArtSet = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      for (ICommitConfigItem configArt : configArtSet) {
         CommitStatus xCommitStatus = branchService.getCommitStatus(reqTeam, configArt);
         Assert.assertTrue(
            "XCommitManager Status not as expected: " + CommitStatus.Working_Branch_Not_Created.name() + " [" + configArt.getCommitFullDisplayName() + "]",
            xCommitStatus.equals(CommitStatus.Working_Branch_Not_Created));
      }
   }

   public void testCreateBranchSecondTime() throws Exception {
      // create again
      AtsBranchUtil.createWorkingBranch_Create(reqTeam, true);
      Assert.assertNotNull("branch returned null", reqTeam.getWorkingBranchForceCacheUpdate());
   }

   public void testXWorkingBranchAfterSecondCreateBranch() throws Exception {

      // Verify the new status of the XWorkingBranch
      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Changes_InProgress, enablement.getStatus());
      Assert.assertFalse("Create Branch Button should be disabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertTrue("Show Artifact Explorer Button should be enabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertTrue("Show Change Report Button should be enabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertTrue("Delete Button should be enabled", enablement.isDeleteBranchButtonEnabled());
   }

   public void testBranchesListedInXCommitManager() throws Exception {
      IAtsBranchService branchService = AtsClientService.get().getBranchService();
      Collection<ICommitConfigItem> configArtSet = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      // Verify the Parallel Branches listed in the XCommitManager
      Assert.assertTrue("parallel branch check failed => " + configArtSet.size(),
         configArtSet.size() == getBranchNames().size());
      for (ICommitConfigItem configArt : configArtSet) {
         BranchId branch = branchService.getBranch(configArt);
         Assert.assertTrue("Missing parallel branch => " + branchService.getBranchName(branch),
            getBranchNames().contains(branchService.getBranchName(branch)));
      }
   }

   protected Artifact getOrCreateArtifact(ArtifactTypeToken artifactType, String artifactName, BranchId branch, Artifact parent, boolean persist) {
      Artifact art;

      try {
         art = ArtifactQuery.getArtifactFromTypeAndName(artifactType, artifactName, branch);
      } catch (ArtifactDoesNotExist ex) {
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

   public void testCreateNewArtifactsOnWorkingBranch() throws Exception {
      BranchId branch = reqTeam.getWorkingBranch();

      // Create set of software requirement changes
      Artifact softReqArt =
         ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name, Requirements.SOFTWARE_REQUIREMENTS, branch);
      Artifact newArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirement, softReqArt, FIRST_ARTIFACT,
         getFirstArtifactCscis(), branch);
      Assert.assertNotNull(newArt);
      newArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirement, softReqArt, SECOND_ARTIFACT,
         getSecondArtifactCscis(), branch);
      Assert.assertNotNull(newArt);

      testCodeTaskCreationAfterFirstAndSecond();

      newArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementProcedure, softReqArt, THIRD_ARTIFACT,
         getThirdArtifactCscis(), branch);
      // Task should be created for all CSCIs if no csci is specified
      newArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementFunction, softReqArt, FOURTH_ARTIFACT, null,
         branch);
      // Task should be created for all CSCIs if unspecified csci exists
      newArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirementFunction, softReqArt, FIFTH_ARTIFACT,
         new String[] {AttributeId.UNSPECIFIED}, branch);
      Assert.assertNotNull(newArt);

      testCodeTaskCreationAfterThirdFourthFifth();

      // Create artifact, make change and delete; this requirement shouldn't show in change report
      newArt = createSoftwareArtifact(CoreArtifactTypes.SoftwareRequirement, softReqArt, IN_BRANCH_ARTIFACT_NAME,
         getInBranchArtifactCscis(), branch);
      Assert.assertNotNull(newArt);

      testCodeTaskCreationAfterInBranchCreation();

      // Make attribute change
      newArt.setName(IN_BRANCH_ARTIFACT_NAME + " Changed");
      newArt.persist(getClass().getSimpleName());

      textCodeTestCreatinoAfterInBranchCreationModified();

      // Delete this one; Since created and deleted in same branch,
      newArt.deleteAndPersist();

      textCodeTestCreatinoAfterInBranchCreationDeleted();

      // Create a Subsystem artifact that has no partition
      newArt = createSubsystemArtifact(softReqArt, SUBSYSTEM_ARTIFACT);
      Assert.assertNotNull(newArt);

      // Get artifact created before branch, make change and delete artifact.  Should only show as deleted in change report
      Artifact preBranchArt = softReqArt.getChild(PRE_BRANCH_ARTIFACT_NAME);
      Assert.assertNotNull("Couldn't retrieve pre-branch artifact", preBranchArt);

      // Make attribute change
      newArt.setSingletonAttributeValue(CoreAttributeTypes.StaticId, "Test");
      newArt.persist(getClass().getSimpleName());
      // Delete
      preBranchArt.deleteAndPersist();
   }

   /**
    * Extend with checks for commit check
    */
   public void testWorkingBranchCommitCheck() {
      // do nothing
   }

   public void testCreateParentArtsOnWorkingBranch() throws Exception {
      BranchId branch = reqTeam.getWorkingBranch();

      // Create set of software requirement changes
      Artifact softReqArt =
         ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name, Requirements.SOFTWARE_REQUIREMENTS, branch);
      Artifact parentArt = createSoftwareArtifact(CoreArtifactTypes.SystemRequirementMsWord, softReqArt,
         PARENT_ARTIFACT, getFirstArtifactCscis(), branch);
      Assert.assertNotNull(parentArt);
      Artifact firstArt = ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name, FIRST_ARTIFACT, branch);
      Artifact secondArt = ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.Name, SECOND_ARTIFACT, branch);
      parentArt.addRelation(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, firstArt);
      parentArt.addRelation(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, secondArt);
      parentArt.persist("Parent for Arts");
   }

   public void testWorkingBranchCommit() throws Exception {
      IAtsBranchService branchService = AtsClientService.get().getBranchService();
      Collection<ICommitConfigItem> configArtSet = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      // Since commit branch is a separate job, a callback will resume this thread
      // commit all of the branches
      Assert.assertTrue(
         "Epected " + getExpectedBranchConfigArts() + " artifacts configured to commit to;  Actual " + configArtSet.size(),
         configArtSet.size() == getExpectedBranchConfigArts());

      IOseeBranch workingBranch = reqTeam.getWorkingBranch();

      // Commit parent branch first
      boolean committed = false;
      for (ICommitConfigItem configArt : configArtSet) {
         if (branchService.isBranchValid(configArt) && BranchManager.getParentBranch(workingBranch).equals(
            configArt.getBaselineBranchId())) {
            BranchId branch = branchService.getBranch(configArt);

            IOperation op = AtsBranchManager.commitWorkingBranch(reqTeam, false, true, branch,
               branchService.isBranchesAllCommittedExcept(reqTeam, branch));
            Operations.executeWorkAndCheckStatus(op);
            committed = true;
         }
      }
      Assert.assertTrue("Did not find parent branch to commit.", committed);

      // Then commit rest
      int commitCount = 0;
      for (ICommitConfigItem configArt : configArtSet) {
         if (branchService.isBranchValid(
            configArt) && !BranchManager.getParentBranch(workingBranch).equals(configArt.getBaselineBranchId())) {
            BranchId branch = branchService.getBranch(configArt);
            IOperation op = AtsBranchManager.commitWorkingBranch(reqTeam, false, true, branch,
               branchService.isBranchesAllCommittedExcept(reqTeam, branch));
            Operations.executeWorkAndCheckStatus(op);
            commitCount++;
         }
      }
      Assert.assertTrue(
         "Expected to commit " + (getExpectedBranchConfigArts() - 1) + " other branches;  only committed " + commitCount,
         commitCount == getExpectedBranchConfigArts() - 1);
   }

   public void testXWorkingBranchAfterBranchCommit() throws Exception {

      WorkflowEditor.editArtifact(reqTeam);

      XWorkingBranchEnablement enablement = new XWorkingBranchEnablement(reqTeam);
      Assert.assertEquals(BranchStatus.Changes_NotPermitted__BranchCommitted, enablement.getStatus());
      Assert.assertFalse("Create Branch Button should be disabled", enablement.isCreateBranchButtonEnabled());
      Assert.assertFalse("Show Artifact Explorer Button should be disabled",
         enablement.isShowArtifactExplorerButtonEnabled());
      Assert.assertTrue("Show Change Report Button should be enabled", enablement.isShowChangeReportButtonEnabled());
      Assert.assertFalse("Delete Button should be disabled", enablement.isDeleteBranchButtonEnabled());

      // Verify the XCommitManager's status; All branches should be CommitStatus.Committed
      IAtsBranchService branchService = AtsClientService.get().getBranchService();
      Collection<ICommitConfigItem> configArtSet = branchService.getConfigArtifactsConfiguredToCommitTo(reqTeam);
      for (ICommitConfigItem configArt : configArtSet) {
         CommitStatus xCommitStatus = branchService.getCommitStatus(reqTeam, configArt);
         Assert.assertTrue(
            "XCommitManager Status not as expected: " + CommitStatus.Committed.name() + " [" + configArt.getCommitFullDisplayName() + "]",
            xCommitStatus.equals(CommitStatus.Committed));
      }
   }

   public void testChangesMadeWereCommitted() throws Exception {
      // Verify that the changes made on the branch were committed to the main branch
      // TODO This needs to be updated do handle multiple config artifacts when test gets updated to test for multiples
      ChangeData changeData = AtsBranchManager.getChangeDataFromEarliestTransactionId(reqTeam);

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

      // Check that the single pre-branch artifact that was deleted in this branch comes back
      // AND that the in-branch artifact that was created and deleted in the branch DOES NOT come
      // back
      Collection<Artifact> deleted = changeData.getArtifacts(KindType.Artifact, ModificationType.DELETED);
      if (deleted.size() != 1) {
         Assert.fail("Deleted Artifacts should be 1; Actual is " + deleted.size());
      } else {
         Artifact art = deleted.iterator().next();
         Assert.assertTrue("Pre-Branch Artifact should have been returned as deleted",
            art.getName().equals(PRE_BRANCH_ARTIFACT_NAME));
         Assert.assertFalse("In-Branch Artifact should NOT have been returned as deleted, and was",
            art.getName().equals(IN_BRANCH_ARTIFACT_NAME));
      }
   }

   public void testRequirementsWorkflowCompletion() throws Exception {
      // Complete Requirements and Start Code/Test
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("testRequirementsWorkflowCompletion");
      TransitionHelper helper =
         new TransitionHelper("Branch Regression Test", Arrays.asList(reqTeam), TeamState.Completed.getName(), null,
            null, changes, AtsClientService.get().getServices(), TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      if (!results.isEmpty()) {
         Assert.fail("Complete Requirements Failed " + results.toString());
      }
   }

   public void testCodeTaskCreationAfterInBranchCreation() throws Exception {
      // Validate Code/Test tasks
      Result result = verifyCodeTaskCreationAfterInBranchCreation();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }

      // Give time for events to propagate through system
      Thread.sleep(8000);
   }

   public void textCodeTestCreatinoAfterInBranchCreationModified() throws Exception {
      // Validate Code/Test tasks
      Result result = verifyCodeTestCreatinoAfterInBranchCreationModified();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }

      // Give time for events to propagate through system
      Thread.sleep(8000);
   }

   public void textCodeTestCreatinoAfterInBranchCreationDeleted() throws Exception {
      // Validate Code/Test tasks
      Result result = verifyCodeTestCreatinoAfterInBranchCreationDeleted();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }

      // Give time for events to propagate through system
      Thread.sleep(8000);
   }

   public void testCodeTaskCreationAfterThirdFourthFifth() throws Exception {
      // Validate Code/Test tasks
      Result result = verifyCodeTestTasksAfterThirdFourthFifth();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }

      // Give time for events to propagate through system
      Thread.sleep(8000);
   }

   public void testCodeTaskCreationAfterFirstAndSecond() throws Exception {
      // Validate Code/Test tasks
      Result result = verifyCodeTestTasksAfterFirstAndSecond();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }

      // Give time for events to propagate through system
      Thread.sleep(8000);
   }

   public void testCodeTaskCreationAfterReqCompletion() throws Exception {
      // Validate Code/Test tasks
      Result result = verifyCodeTestTasksAfterReqCompletion();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }

      // Give time for events to propagate through system
      Thread.sleep(8000);
   }

   public void testShowRelatedTasksAction() throws Exception {
      Result result = verifyShowRelatedTasksAction();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }
   }

   public void testShowRelatedRequirementAction() throws Exception {
      Result result = verifyShowRelatedRequirementAction();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }
   }

   public void testShowRequirementDiffsAction() throws Exception {
      Result result = verifyShowRequirementDiffsAction();
      if (result.isFalse()) {
         if (!result.getText().equals("")) {
            Assert.fail(result.getText());
         }
      }
   }

   public void testSevereLoggingMonitorResults() throws Exception {
      List<IHealthStatus> stats = monitorLog.getAllLogs();
      for (IHealthStatus stat : new ArrayList<>(stats)) {
         if (stat.getException() != null) {
            Assert.fail("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
   }

   public void testCleanupFinal() throws Exception {
      AtsUtilClient.setEmailEnabled(true);
      OseeEventManager.removeAllListeners();
   }

   protected Artifact createSoftwareArtifact(ArtifactTypeToken artifactType, Artifact parent, String title, String[] partitions, BranchId branch) throws Exception, MultipleAttributesExist {
      Artifact art1 = ArtifactTypeManager.addArtifact(artifactType, branch, title);
      if (partitions != null) {
         art1.setAttributeValues(getCsciAttribute(), Arrays.asList(partitions));
      }
      art1.persist(getClass().getSimpleName());
      parent.addChild(art1);
      parent.persist(getClass().getSimpleName());
      return art1;
   }

   private Artifact createSubsystemArtifact(Artifact parent, String title) {
      Artifact art1 = null;

      try {
         art1 =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SubsystemRequirementMsWord, reqTeam.getWorkingBranch());
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

   public static enum XWorkingBranchButtonState {
      CreateBranch,
      ArtExplore,
      ShowChangeReport,
      DeleteBranch
   };

   public abstract String getRpcrNumber();

   public abstract String[] getPreBranchCscis();

   public abstract String[] getFirstArtifactCscis();

   public abstract String[] getSecondArtifactCscis();

   public abstract String[] getThirdArtifactCscis();

   public abstract String[] getInBranchArtifactCscis();

   public abstract AttributeTypeId getCsciAttribute();

   public abstract BranchId getProgramBranch();

   public Result verifyCodeTestTasksAfterFirstAndSecond() throws Exception {
      return Result.TrueResult;
   }

   public Result verifyCodeTestTasksAfterThirdFourthFifth() throws Exception {
      return Result.TrueResult;
   }

   public Result verifyCodeTaskCreationAfterInBranchCreation() throws Exception {
      return Result.TrueResult;
   }

   public Result verifyCodeTestCreatinoAfterInBranchCreationModified() throws Exception {
      return Result.TrueResult;
   }

   public Result verifyCodeTestCreatinoAfterInBranchCreationDeleted() throws Exception {
      return Result.TrueResult;
   }

   public abstract Result verifyCodeTestTasksAfterReqCompletion() throws Exception;

   public abstract ArtifactTypeToken getCodeTeamWfArtType();

   public abstract ArtifactTypeToken getTestTeamWfArtType();

   public Result verifyShowRelatedTasksAction() {

      for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(actionArt)) {
         if (team.getTeamDefinition().toString().contains("Req")) {
            continue;
         }
         IAtsTeamWorkflow testWf = null, codeWf = null;
         if (AtsClientService.get().getStoreService().isOfType(team, getTestTeamWfArtType())) {
            testWf = team;
         } else if (AtsClientService.get().getStoreService().isOfType(team, getCodeTeamWfArtType())) {
            codeWf = team;
         }
         Assert.assertTrue(testWf != null || codeWf != null);

         Collection<IAtsTask> tasks = AtsClientService.get().getTaskService().getTasks(team);
         int count = 0;
         /**
          * only count task belonging to these testWf/codeWf cause this count will go up as this test is run multiple
          * times.
          */
         for (IAtsTask task : tasks) {
            if ((testWf != null && task.getParentTeamWorkflow().equals(
               testWf)) || (codeWf != null && task.getParentTeamWorkflow().equals(codeWf))) {
               count++;
            }
         }
         Assert.assertEquals(18, count);

         XResultData result = verifyShowRelatedTasksAction(tasks);
         if (result.isErrors()) {
            return new Result(false, result.toString());
         }
      }
      return Result.TrueResult;
   }

   /**
    * Available for override to provide further checks on tasks created
    */
   protected XResultData verifyShowRelatedTasksAction(Collection<IAtsTask> tasks) {
      return new XResultData();
   }

   public abstract Result verifyShowRelatedRequirementAction();

   public abstract Result verifyShowRequirementDiffsAction();

   public abstract List<String> getBranchNames() throws Exception;

   public abstract int getExpectedBranchConfigArts();

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
