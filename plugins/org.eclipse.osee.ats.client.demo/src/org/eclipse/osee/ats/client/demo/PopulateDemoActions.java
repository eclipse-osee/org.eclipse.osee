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
package org.eclipse.osee.ats.client.demo;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_3;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.client.demo.config.DemoDbActionData;
import org.eclipse.osee.ats.client.demo.config.DemoDbActionData.CreateReview;
import org.eclipse.osee.ats.client.demo.config.DemoDbGroups;
import org.eclipse.osee.ats.client.demo.config.DemoDbReviews;
import org.eclipse.osee.ats.client.demo.config.DemoDbTasks;
import org.eclipse.osee.ats.client.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionArtifactRollup;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.ats.util.SubscribeManagerUI;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationParameter;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Run from the ATS Navigator after the DB is configured for "OSEE Demo Database", this class will populate the database
 * with sample actions written against XYZ configured teams
 *
 * @author Donald G. Dunne
 */
public class PopulateDemoActions extends XNavigateItemAction {

   private static boolean DEBUG = false;
   private final String[] TITLE_PREFIX = {
      "Problem with the",
      "Can't see the",
      "Button A doesn't work on",
      "Add to the",
      "Make new Button for ",
      "User can't load "};
   private final ChangeType[] CHANGE_TYPE = {
      ChangeType.Problem,
      ChangeType.Problem,
      ChangeType.Problem,
      ChangeType.Improvement,
      ChangeType.Improvement,
      ChangeType.Support,
      ChangeType.Improvement,
      ChangeType.Support};

   private static final String UPDATE_BRANCH_TYPE = "update osee_branch set branch_type = ? where branch_id = ?";

   public PopulateDemoActions(XNavigateItem parent) {
      super(parent, "Populate Demo Actions", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      run(true);
   }

   private static void validateArtifactCache() throws OseeStateException {
      final Collection<Artifact> list = ArtifactCache.getDirtyArtifacts();
      if (!list.isEmpty()) {
         for (Artifact artifact : list) {
            System.err.println(String.format("Artifact [%s] is dirty [%s]", artifact.toStringWithId(),
               Artifacts.getDirtyReport(artifact)));
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate (see console for details)",
            list.size());
      }

   }

   public void run(boolean prompt) throws Exception {
      AtsUtilClient.setEmailEnabled(false);
      if (AtsUtil.isProductionDb()) {
         throw new IllegalStateException("PopulateDemoActions should not be run on production DB");
      }
      if (DbUtil.isDbInit() || !prompt || prompt && MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         getName())) {

         validateArtifactCache();

         OseeLog.log(Activator.class, Level.INFO, "Populate Demo Database");

         AtsBulkLoad.reloadConfig(true);

         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

         // Import all requirements on SAW_Bld_1 Branch
         demoDbImportReqsTx();

         //DemoDbUtil.sleep(5000);

         // Create traceability between System, Subsystem and Software requirements
         SkynetTransaction demoDbTraceability =
            TransactionManager.createTransaction(SAW_Bld_1, "Populate Demo DB - Create Traceability");
         demoDbTraceabilityTx(demoDbTraceability, SAW_Bld_1);
         demoDbTraceability.execute();

         //DemoDbUtil.sleep(5000);

         // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
         createMainWorkingBranchTx();

         // Create SWB_Bld_2 Actions and Reviews
         PopulateSawBuild2Actions.run();

         // Create actions against non-requirement AIs and Teams
         createNonReqChangeDemoActions();
         createGenericDemoActions();

         // Mark all CIS Code "Team Workflows" as Favorites for "Joe Smith"
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Add Favorites");
         }
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoArtifactTypes.DemoCodeTeamWorkflow,
            "Diagram View", AtsClientService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
            new FavoritesManager((AbstractWorkflowArtifact) art).toggleFavorite(false);
         }

         // Mark all Tools Team "Team Workflows" as Subscribed for "Joe Smith"
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Add Subscribed");
         }
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoArtifactTypes.DemoCodeTeamWorkflow,
            "Even", AtsClientService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
            new SubscribeManagerUI((AbstractWorkflowArtifact) art).toggleSubscribe(false);
         }

         // Create some tasks off sample workflows
         DemoDbTasks.createTasks(DEBUG);

         // Create group of sample artifacts
         DemoDbGroups.createGroups(DEBUG);

         // Create and transition reviews off sample workflows
         DemoDbReviews.createReviews(DEBUG);

         // Set Default Work Packages
         setDefaultWorkPackages();

         validateArtifactCache();
         TestUtil.severeLoggingEnd(monitorLog);
         OseeLog.log(Activator.class, Level.INFO, "Populate Complete");
      }
   }

   private void setDefaultWorkPackages() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Set Work Packages");

      // set work packages
      TeamWorkFlowArtifact commWf = DemoUtil.getSawCodeCommittedWf();
      commWf.setSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getGuid());
      commWf.persist(transaction);

      TeamWorkFlowArtifact unCommWf = DemoUtil.getSawCodeUnCommittedWf();
      unCommWf.setSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getGuid());
      unCommWf.persist(transaction);

      TeamWorkFlowArtifact noBranchWf = DemoUtil.getSawCodeNoBranchWf();
      noBranchWf.setSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_03.getGuid());
      noBranchWf.persist(transaction);

      transaction.execute();
   }

   private void createMainWorkingBranchTx() {
      try {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Creating SAW_Bld_2 branch off SAW_Bld_1");
         }
         // Create SAW_Bld_2 branch off SAW_Bld_1
         BranchId childBranch = BranchManager.createBaselineBranch(SAW_Bld_1, SAW_Bld_2);

         AccessControlManager.setPermission(UserManager.getUser(DemoUsers.Joe_Smith), SAW_Bld_2,
            PermissionEnum.FULLACCESS);

         DemoDbUtil.sleep(5000);
         // need to update the branch type;
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_TYPE, BranchType.BASELINE.getValue(), childBranch);
         BranchManager.refreshBranches();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createNonReqChangeDemoActions() throws Exception {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create Actions");
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_3");
      }
      Set<ActionArtifact> actions =
         createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_3, null, changes);
      appendBuildNameToTitles(actions, SAW_Bld_3.getName(), changes);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_2");
      }
      actions = createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_2, null, changes);
      appendBuildNameToTitles(actions, SAW_Bld_2.getName(), changes);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_1");
      }

      actions = createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_1,
         TeamState.Completed, changes);
      appendBuildNameToTitles(actions, SAW_Bld_1.toString(), changes);

      changes.execute();
   }

   private void createGenericDemoActions() throws Exception {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - getGenericActionData");
      }
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create Generic Actions");
      createActions(DemoDbActionData.getGenericActionData(), null, null, changes);
      changes.execute();
   }

   private void appendBuildNameToTitles(Set<ActionArtifact> actions, String buildName, IAtsChangeSet changes) throws OseeCoreException {
      for (ActionArtifact action : actions) {
         for (TeamWorkFlowArtifact team : action.getTeams()) {
            team.setName(team.getName() + " for " + buildName);
            changes.add(team);
         }
         ActionArtifactRollup rollup = new ActionArtifactRollup(action);
         rollup.resetAttributesOffChildren();
         changes.add(action);
      }
   }

   private Set<ActionArtifact> createActions(List<DemoDbActionData> actionDatas, ArtifactToken versionToken, TeamState toStateOverride, IAtsChangeSet changes) throws Exception {
      Set<ActionArtifact> actionArts = new HashSet<>();
      int currNum = 1;
      for (DemoDbActionData aData : actionDatas) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Creating " + currNum++ + "/" + actionDatas.size());
         }
         int x = 0;
         Date createdDate = new Date();
         IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();

         for (String prefixTitle : aData.prefixTitles) {
            ActionResult actionResult = AtsClientService.get().getActionFactory().createAction(null,
               prefixTitle + " " + aData.postFixTitle, TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x],
               aData.priority, false, null, aData.getActionableItems(), createdDate, createdBy, null, changes);
            actionArts.add((ActionArtifact) actionResult.getActionArt());
            for (IAtsTeamWorkflow teamWf : AtsClientService.get().getWorkItemService().getTeams(actionResult)) {
               TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices(),
                  TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
               // Add validation required flag if Decision review is required
               if (aData.getCreateReviews().length > 0) {
                  for (CreateReview createReview : aData.getCreateReviews()) {
                     if (createReview == CreateReview.Decision) {
                        ((TeamWorkFlowArtifact) teamWf.getStoreObject()).setSoleAttributeValue(
                           AtsAttributeTypes.ValidationRequired, true);
                     }
                  }
               }
               boolean isSwDesign = teamWf.getTeamDefinition().getName().contains("SW Design");
               if (isSwDesign) {
                  // set reviews to non-blocking so can transition to Completed
                  for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(teamWf)) {
                     reviewArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
                  }
               }

               // Transition to desired state
               Result result = dtwm.transitionTo(toStateOverride != null ? toStateOverride : aData.toState,
                  teamWf.getAssignees().iterator().next(), false, changes);
               if (result.isFalse()) {
                  throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
                     aData.toState.getName(), result.getText());
               }
               if (!teamWf.isCompletedOrCancelled()) {
                  // Reset assignees that may have been overwritten during transition
                  teamWf.getStateMgr().setAssignees(teamWf.getTeamDefinition().getLeads());
               }
               if (versionToken != null) {
                  IAtsVersion version = AtsClientService.get().getVersionService().getById(versionToken);
                  AtsClientService.get().getVersionService().setTargetedVersion(teamWf, version, changes);
               }
            }
         }
      }
      return actionArts;
   }

   private void demoDbImportReqsTx() {
      try {
         //@formatter:off
         importRequirements(SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement, "Software Requirements", "support/SAW-SoftwareRequirements.xml");
         importRequirements(SAW_Bld_1, CoreArtifactTypes.SystemRequirementMSWord, "System Requirements", "support/SAW-SystemRequirements.xml");
         importRequirements(SAW_Bld_1, CoreArtifactTypes.SubsystemRequirementMSWord, "Subsystem Requirements", "support/SAW-SubsystemRequirements.xml");
         //@formatter:on
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void importRequirements(BranchId branch, IArtifactType requirementType, String folderName, String filename) throws Exception {
      if (DEBUG) {
         OseeLog.logf(Activator.class, Level.INFO, "Importing \"%s\" requirements on branch \"%s\"", folderName,
            branch);
      }
      Artifact systemReq = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, folderName, branch);

      File file = Activator.getInstance().getPluginFile(filename);
      IArtifactImportResolver artifactResolver = ArtifactResolverFactory.createAlwaysNewArtifacts(requirementType);
      IArtifactExtractor extractor = new WordOutlineExtractor();
      extractor.setDelegate(new WordOutlineExtractorDelegate());

      ArtifactImportOperationParameter importOptions = new ArtifactImportOperationParameter();
      importOptions.setSourceFile(file);
      importOptions.setDestinationArtifact(systemReq);
      importOptions.setExtractor(extractor);
      importOptions.setResolver(artifactResolver);

      IOperation operation = ArtifactImportOperationFactory.completeOperation(importOptions);
      Operations.executeWorkAndCheckStatus(operation);

      // Validate that something was imported
      if (systemReq.getChildren().isEmpty()) {
         throw new IllegalStateException("Artifacts were not imported");
      }
   }

   private void relate(RelationTypeSide relationSide, Artifact artifact, Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   private void demoDbTraceabilityTx(SkynetTransaction transaction, BranchId branch) {
      try {
         Collection<Artifact> systemArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SystemRequirementMSWord, "Robot", branch);

         Collection<Artifact> component =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "API", branch);
         component.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Hardware", branch));
         component.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Sensor", branch));

         Collection<Artifact> subSystemArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord, "Robot", branch);
         subSystemArts.addAll(
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord, "Video", branch));
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord,
            "Interface", branch));

         Collection<Artifact> softArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement, "Robot", branch);
         softArts.addAll(
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement, "Interface", branch));

         // Relate System to SubSystem to Software Requirements
         for (Artifact systemArt : systemArts) {
            relate(CoreRelationTypes.Requirement_Trace__Lower_Level, systemArt, subSystemArts);
            systemArt.persist(transaction);

            for (Artifact subSystemArt : subSystemArts) {
               relate(CoreRelationTypes.Requirement_Trace__Lower_Level, subSystemArt, softArts);
               subSystemArt.persist(transaction);
            }
         }

         // Relate System, SubSystem and Software Requirements to Componets
         for (Artifact art : systemArts) {
            relate(CoreRelationTypes.Allocation__Component, art, component);
            art.persist(transaction);
         }
         for (Artifact art : subSystemArts) {
            relate(CoreRelationTypes.Allocation__Component, art, component);
            art.persist(transaction);
         }
         for (Artifact art : softArts) {
            relate(CoreRelationTypes.Allocation__Component, art, component);
         }

         // Create Test Script Artifacts
         Set<Artifact> verificationTests = new HashSet<>();
         Artifact verificationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Verification Tests", branch);
         if (verificationHeader == null) {
            throw new IllegalStateException("Could not find Verification Tests header");
         }
         for (String str : new String[] {"A", "B", "C"}) {
            Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestCase,
               verificationHeader.getBranch(), "Verification Test " + str);
            verificationTests.add(newArt);
            verificationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

         // Create Validation Test Procedure Artifacts
         Set<Artifact> validationTests = new HashSet<>();
         Artifact validationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Validation Tests", branch);
         if (validationHeader == null) {
            throw new IllegalStateException("Could not find Validation Tests header");
         }
         for (String str : new String[] {"1", "2", "3"}) {
            Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure,
               validationHeader.getBranch(), "Validation Test " + str);
            validationTests.add(newArt);
            validationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

         // Create Integration Test Procedure Artifacts
         Set<Artifact> integrationTests = new HashSet<>();
         Artifact integrationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Integration Tests", branch);
         if (integrationHeader == null) {
            throw new IllegalStateException("Could not find integration Tests header");
         }
         for (String str : new String[] {"X", "Y", "Z"}) {
            Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure,
               integrationHeader.getBranch(), "integration Test " + str);
            integrationTests.add(newArt);
            integrationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact integrationTestsArray[] = integrationTests.toArray(new Artifact[integrationTests.size()]);

         // Relate Software Artifacts to Tests
         Artifact softReqsArray[] = softArts.toArray(new Artifact[softArts.size()]);
         softReqsArray[0].addRelation(CoreRelationTypes.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[0].addRelation(CoreRelationTypes.Validation__Validator, verificationTestsArray[1]);
         softReqsArray[1].addRelation(CoreRelationTypes.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[1].addRelation(CoreRelationTypes.Validation__Validator, validationTestsArray[1]);
         softReqsArray[2].addRelation(CoreRelationTypes.Validation__Validator, validationTestsArray[0]);
         softReqsArray[2].addRelation(CoreRelationTypes.Validation__Validator, integrationTestsArray[1]);
         softReqsArray[3].addRelation(CoreRelationTypes.Validation__Validator, integrationTestsArray[0]);
         softReqsArray[4].addRelation(CoreRelationTypes.Validation__Validator, integrationTestsArray[2]);
         softReqsArray[5].addRelation(CoreRelationTypes.Validation__Validator, validationTestsArray[2]);

         for (Artifact artifact : softArts) {
            artifact.persist(transaction);
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
