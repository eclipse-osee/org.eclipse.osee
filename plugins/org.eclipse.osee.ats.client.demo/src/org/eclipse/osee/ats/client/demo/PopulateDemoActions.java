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

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
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
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.ats.util.SubscribeManagerUI;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryOptions;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
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
      AtsUtilCore.setEmailEnabled(false);
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
            TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, "Populate Demo DB - Create Traceability");
         demoDbTraceabilityTx(demoDbTraceability, DemoSawBuilds.SAW_Bld_1);
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
            "Diagram View", AtsUtil.getAtsBranch(), QueryOptions.CONTAINS_MATCH_OPTIONS)) {
            new FavoritesManager((AbstractWorkflowArtifact) art).toggleFavorite(false);
         }

         // Mark all Tools Team "Team Workflows" as Subscribed for "Joe Smith"
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Add Subscribed");
         }
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoArtifactTypes.DemoCodeTeamWorkflow,
            "Even", AtsUtil.getAtsBranch(), QueryOptions.CONTAINS_MATCH_OPTIONS)) {
            new SubscribeManagerUI((AbstractWorkflowArtifact) art).toggleSubscribe(false);
         }

         // Create some tasks off sample workflows
         DemoDbTasks.createTasks(DEBUG);

         // Create group of sample artifacts
         DemoDbGroups.createGroups(DEBUG);

         // Create and transition reviews off sample workflows
         DemoDbReviews.createReviews(DEBUG);

         validateArtifactCache();
         TestUtil.severeLoggingEnd(monitorLog);
         OseeLog.log(Activator.class, Level.INFO, "Populate Complete");
      }
   }

   private void createMainWorkingBranchTx() {
      try {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Creating SAW_Bld_2 branch off SAW_Bld_1");
         }
         // Create SAW_Bld_2 branch off SAW_Bld_1
         Branch childBranch = BranchManager.createBaselineBranch(DemoSawBuilds.SAW_Bld_1, DemoSawBuilds.SAW_Bld_2);

         DemoDbUtil.sleep(5000);
         // need to update the branch type;
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_TYPE, new Object[] {
            BranchType.BASELINE.getValue(),
            childBranch.getId()});
         BranchManager.refreshBranches();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createNonReqChangeDemoActions() throws Exception {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Populate Demo DB - Create Actions");
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_3");
      }
      Set<ActionArtifact> actions =
         createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_3, null, transaction);
      appendBuildNameToTitles(actions, DemoSawBuilds.SAW_Bld_3.getName(), transaction);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_2");
      }
      actions =
         createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_2, null, transaction);
      appendBuildNameToTitles(actions, DemoSawBuilds.SAW_Bld_2.getName(), transaction);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_1");
      }

      actions =
         createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_1, TeamState.Completed,
            transaction);
      appendBuildNameToTitles(actions, DemoSawBuilds.SAW_Bld_1.toString(), transaction);

      transaction.execute();
   }

   private void createGenericDemoActions() throws Exception {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - getGenericActionData");
      }
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Populate Demo DB - Create Generic Actions");
      createActions(DemoDbActionData.getGenericActionData(), null, null, transaction);
      transaction.execute();
   }

   private void appendBuildNameToTitles(Set<ActionArtifact> actions, String buildName, SkynetTransaction transaction) throws OseeCoreException {
      for (ActionArtifact action : actions) {
         for (TeamWorkFlowArtifact team : action.getTeams()) {
            team.setName(team.getName() + " for " + buildName);
            team.persist(transaction);
         }
         ActionArtifactRollup rollup = new ActionArtifactRollup(action, transaction);
         rollup.resetAttributesOffChildren();
      }
   }

   private Set<ActionArtifact> createActions(List<DemoDbActionData> actionDatas, IArtifactToken versionToken, TeamState toStateOverride, SkynetTransaction transaction) throws Exception {
      Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
      int currNum = 1;
      for (DemoDbActionData aData : actionDatas) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Creating " + currNum++ + "/" + actionDatas.size());
         }
         int x = 0;
         Date createdDate = new Date();
         IAtsUser createdBy = AtsClientService.get().getUserAdmin().getCurrentUser();

         for (String prefixTitle : aData.prefixTitles) {
            ActionArtifact actionArt =
               ActionManager.createAction(null, prefixTitle + " " + aData.postFixTitle,
                  TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x], aData.priority, false, null,
                  aData.getActionableItems(), createdDate, createdBy, null, transaction);
            actionArts.add(actionArt);
            for (TeamWorkFlowArtifact teamWf : ActionManager.getTeams(actionArt)) {
               TeamWorkFlowManager dtwm =
                  new TeamWorkFlowManager(teamWf, TransitionOption.OverrideAssigneeCheck,
                     TransitionOption.OverrideTransitionValidityCheck);
               // Add validation required flag if Decision review is required
               if (aData.getCreateReviews().length > 0) {
                  for (CreateReview createReview : aData.getCreateReviews()) {
                     if (createReview == CreateReview.Decision) {
                        teamWf.setSoleAttributeValue(AtsAttributeTypes.ValidationRequired, true);
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
               Result result =
                  dtwm.transitionTo((toStateOverride != null ? toStateOverride : aData.toState),
                     teamWf.getAssignees().iterator().next(), false, transaction);
               if (result.isFalse()) {
                  throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
                     aData.toState.getName(), result.getText());
               }
               if (!teamWf.isCompletedOrCancelled()) {
                  // Reset assignees that may have been overwritten during transition 
                  teamWf.getStateMgr().setAssignees(teamWf.getTeamDefinition().getLeads());
               }

               teamWf.persist(transaction);
               if (versionToken != null) {
                  IAtsVersion version = AtsVersionService.get().getById(versionToken);
                  AtsVersionService.get().setTargetedVersionAndStore(teamWf, version);
                  teamWf.persist(transaction);
               }
            }
         }
      }
      return actionArts;
   }

   private void demoDbImportReqsTx() {
      try {
         //@formatter:off
         importRequirements(DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement, "Software Requirements", "support/SAW-SoftwareRequirements.xml");
         importRequirements(DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SystemRequirementMSWord, "System Requirements", "support/SAW-SystemRequirements.xml");
         importRequirements(DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SubsystemRequirementMSWord, "Subsystem Requirements", "support/SAW-SubsystemRequirements.xml");
         //@formatter:on
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void importRequirements(IOseeBranch branch, IArtifactType requirementType, String folderName, String filename) throws Exception {
      if (DEBUG) {
         OseeLog.logf(Activator.class, Level.INFO, "Importing \"%s\" requirements on branch \"%s\"", folderName, branch);
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

   private void relate(IRelationTypeSide relationSide, Artifact artifact, Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   private void demoDbTraceabilityTx(SkynetTransaction transaction, IOseeBranch branch) {
      try {
         Collection<Artifact> systemArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SystemRequirementMSWord, "Robot", branch);

         Collection<Artifact> component =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "API", branch);
         component.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Hardware", branch));
         component.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Sensor", branch));

         Collection<Artifact> subSystemArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord, "Robot", branch);
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord,
            "Video", branch));
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord,
            "Interface", branch));

         Collection<Artifact> softArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement, "Robot", branch);
         softArts.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement, "Interface",
            branch));

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
         Set<Artifact> verificationTests = new HashSet<Artifact>();
         Artifact verificationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Verification Tests", branch);
         if (verificationHeader == null) {
            throw new IllegalStateException("Could not find Verification Tests header");
         }
         for (String str : new String[] {"A", "B", "C"}) {
            Artifact newArt =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestCase, verificationHeader.getBranch(),
                  "Verification Test " + str);
            verificationTests.add(newArt);
            verificationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

         // Create Validation Test Procedure Artifacts
         Set<Artifact> validationTests = new HashSet<Artifact>();
         Artifact validationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Validation Tests", branch);
         if (validationHeader == null) {
            throw new IllegalStateException("Could not find Validation Tests header");
         }
         for (String str : new String[] {"1", "2", "3"}) {
            Artifact newArt =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure, validationHeader.getBranch(),
                  "Validation Test " + str);
            validationTests.add(newArt);
            validationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

         // Create Integration Test Procedure Artifacts
         Set<Artifact> integrationTests = new HashSet<Artifact>();
         Artifact integrationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Integration Tests", branch);
         if (integrationHeader == null) {
            throw new IllegalStateException("Could not find integration Tests header");
         }
         for (String str : new String[] {"X", "Y", "Z"}) {
            Artifact newArt =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure, integrationHeader.getBranch(),
                  "integration Test " + str);
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
