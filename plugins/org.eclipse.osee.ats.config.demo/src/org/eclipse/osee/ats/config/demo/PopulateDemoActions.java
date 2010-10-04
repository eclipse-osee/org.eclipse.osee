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
package org.eclipse.osee.ats.config.demo;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowManager;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDatabaseConfig;
import org.eclipse.osee.ats.config.demo.config.DemoDbActionData;
import org.eclipse.osee.ats.config.demo.config.DemoDbActionData.CreateReview;
import org.eclipse.osee.ats.config.demo.config.DemoDbGroups;
import org.eclipse.osee.ats.config.demo.config.DemoDbReviews;
import org.eclipse.osee.ats.config.demo.config.DemoDbTasks;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil.SoftwareRequirementStrs;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.ats.util.SubscribeManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.NewArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.support.test.util.DemoArtifactTypes;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Run from the ATS Navigator after the DB is configured for "OSEE Demo Database", this class will populate the database
 * with sample actions written against XYZ configured teams
 * 
 * @author Donald G. Dunne
 */
public class PopulateDemoActions extends XNavigateItemAction {

   private final String[] TITLE_PREFIX = new String[] {
      "Problem with the",
      "Can't see the",
      "Button A doesn't work on",
      "Add to the",
      "Make new Button for ",
      "User can't load "};
   private final ChangeType[] CHANGE_TYPE = new ChangeType[] {
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

   public void run(boolean prompt) throws Exception {
      AtsUtil.setEmailEnabled(false);
      if (AtsUtil.isProductionDb()) {
         throw new IllegalStateException("PopulateDemoActions should not be run on production DB");
      }
      if (DbUtil.isDbInit() || !prompt || prompt && MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         getName())) {

         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

         Branch saw1Branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);

         // Import all requirements on SAW_Bld_1 Branch
         demoDbImportReqsTx();

         DemoDbUtil.sleep(5000);

         // Create traceability between System, Subsystem and Software requirements
         SkynetTransaction demoDbTraceability =
            new SkynetTransaction(saw1Branch, "Populate Demo DB - Create Traceability");
         demoDbTraceabilityTx(demoDbTraceability, saw1Branch);
         demoDbTraceability.execute();

         DemoDbUtil.sleep(5000);

         // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
         createMainWorkingBranchTx();

         // Create SAW_Bld_2 Actions
         SkynetTransaction sawActionsTransaction =
            new SkynetTransaction(AtsUtil.getAtsBranch(), "Populate Demo DB - Create Actions");
         Set<ActionArtifact> actionArts =
            createActions(DemoDbActionData.getReqSawActionsData(), DemoSawBuilds.SAW_Bld_2.toString(), null,
               sawActionsTransaction);
         sawActionsTransaction.execute();
         // Sleep to wait for the persist of the actions
         DemoDbUtil.sleep(3000);

         for (ActionArtifact actionArt : actionArts) {
            if (actionArt.getName().contains("(committed)")) {
               // Working Branch off SAW_Bld_2, Make Changes, Commit
               makeAction1ReqChanges(actionArt);
            } else if (actionArt.getName().contains("(uncommitted)")) {
               // Working Branch off SAW_Bld_2, Make Changes, DON'T Commit
               makeAction2ReqChanges(actionArt);
            } else if (actionArt.getName().contains("(uncommitted-conflicted)")) {
               // Working Branch off SAW_Bld_2, Make Conflicted Changes, DON'T Commit
               makeAction3ReqChanges(actionArt);
            }
         }

         // Create actions against non-requirement AIs and Teams
         createNonReqChangeDemoActions();

         // Mark all CIS Code "Team Workflows" as Favorites for "Joe Smith"
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Add Favorites");
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoArtifactTypes.DemoCodeTeamWorkflow,
            "%Diagram View%", AtsUtil.getAtsBranch())) {
            new FavoritesManager((AbstractWorkflowArtifact) art).toggleFavorite(false);
         }

         // Mark all Tools Team "Team Workflows" as Subscribed for "Joe Smith"
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Add Subscribed");
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoArtifactTypes.DemoCodeTeamWorkflow,
            "%Even%", AtsUtil.getAtsBranch())) {
            new SubscribeManager((AbstractWorkflowArtifact) art).toggleSubscribe(false);
         }

         // Create some tasks off sample workflows
         DemoDbTasks.createTasks();

         // Create group of sample artifacts
         DemoDbGroups.createGroups();

         // Create and transition reviews off sample workflows
         DemoDbReviews.createReviews();

         TestUtil.severeLoggingEnd(monitorLog);
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Populate Complete");
      }
   }

   private void createMainWorkingBranchTx() {
      try {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating SAW_Bld_2 branch off SAW_Bld_1");
         // Create SAW_Bld_2 branch off SAW_Bld_1
         Branch parentBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
         Branch childBranch =
            BranchManager.createBaselineBranch(parentBranch, DemoSawBuilds.SAW_Bld_2,
               UserManager.getUser(SystemUser.OseeSystem));

         DemoDbUtil.sleep(5000);
         // need to update the branch type;
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_TYPE, new Object[] {
            BranchType.BASELINE.getValue(),
            childBranch.getId()});
         BranchManager.refreshBranches();
         // Map team definitions versions to their related branches
         SkynetTransaction transaction =
            new SkynetTransaction(AtsUtil.getAtsBranch(), "Populate Demo DB - Create Branch");
         DemoDatabaseConfig.mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW),
            DemoSawBuilds.SAW_Bld_2.getName(), DemoSawBuilds.SAW_Bld_2.getName(), transaction);
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.SEVERE, ex);
      }
   }

   private void makeAction1ReqChanges(ActionArtifact actionArt) throws OseeCoreException {
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Making Action 1 Requirement Changes");
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getName().contains("Req")) {
            reqTeam = team;
         }
      }

      if (reqTeam == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }

      DemoDbUtil.sleep(5000);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Robot, reqTeam.getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            new StringBuilder("Modifying artifact => ").append(art).toString());
         art.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Navigation.name());
         art.setSoleAttributeValue(CoreAttributeTypes.SafetyCriticality, "I");
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Navigation.name());
         Artifact navArt =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component, DemoSubsystems.Navigation.name(),
               reqTeam.getWorkingBranch());
         art.addRelation(CoreRelationTypes.Allocation__Component, navArt);
         art.persist();
      }

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Event, reqTeam.getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            new StringBuilder("Modifying artifact => ").append(art).toString());
         art.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Interface.name());
         art.setSoleAttributeValue(CoreAttributeTypes.SafetyCriticality, "IV");
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         Artifact robotArt =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component, DemoSubsystems.Robot_API.name(),
               reqTeam.getWorkingBranch());
         art.addRelation(CoreRelationTypes.Allocation__Component, robotArt);
         art.persist();
      }

      // Delete two artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.daVinci,
         reqTeam.getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            new StringBuilder("Deleting artifact => ").append(art).toString());
         art.deleteAndPersist();
      }

      // Add three new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement(reqTeam.getWorkingBranch());
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Adding artifact => " + name);
         Artifact newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(CoreAttributeTypes.SafetyCriticality, "IV");
         newArt.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         newArt.persist();
         parentArt.addChild(newArt);
         parentArt.persist();
      }

      DemoDbUtil.sleep(2000L);
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Committing branch");
      Job job =
         reqTeam.getBranchMgr().commitWorkingBranch(false, true, reqTeam.getTargetedForVersion().getParentBranch(),
            true);
      try {
         job.join();
      } catch (InterruptedException ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Completing Action");
      }
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Completing Action");
   }

   private void makeAction3ReqChanges(ActionArtifact actionArt) throws Exception {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getName().contains("Req")) {
            reqTeam = team;
         }
      }

      if (reqTeam == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }

      DemoDbUtil.sleep(5000);

      Artifact branchArtifact =
         DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SoftwareRequirement, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ,
            reqTeam.getWorkingBranch()).iterator().next();
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
         new StringBuilder("Modifying branch artifact => ").append(branchArtifact).toString());
      branchArtifact.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Interface.name());
      branchArtifact.setSoleAttributeValue(CoreAttributeTypes.SafetyCriticality, "IV");
      branchArtifact.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
      Artifact comArt =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component, DemoSubsystems.Robot_API.name(),
            reqTeam.getWorkingBranch());
      branchArtifact.addRelation(CoreRelationTypes.Allocation__Component, comArt);
      branchArtifact.persist();

      Artifact parentArtifact =
         DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SoftwareRequirement, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ,
            reqTeam.getWorkingBranch()).iterator().next();
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
         new StringBuilder("Modifying parent artifact => ").append(parentArtifact).toString());
      parentArtifact.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Navigation.name());
      parentArtifact.setSoleAttributeValue(CoreAttributeTypes.SafetyCriticality, "V");
      parentArtifact.setSoleAttributeValue(CoreAttributeTypes.Subsystem,
         DemoSubsystems.Cognitive_Decision_Aiding.name());
      parentArtifact.persist();

   }

   private void makeAction2ReqChanges(ActionArtifact actionArt) throws Exception {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getName().contains("Req")) {
            reqTeam = team;
         }
      }

      if (reqTeam == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }

      DemoDbUtil.sleep(5000);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Functional,
         reqTeam.getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            new StringBuilder("Modifying artifact => ").append(art).toString());
         art.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Interface.name());
         art.setSoleAttributeValue(CoreAttributeTypes.SafetyCriticality, "IV");
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         Artifact comArt =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component, DemoSubsystems.Robot_API.name(),
               reqTeam.getWorkingBranch());

         art.addRelation(CoreRelationTypes.Allocation__Component, comArt);
         art.persist();
      }

      // Delete one artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.CISST, reqTeam.getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            new StringBuilder("Deleting artifact => ").append(art).toString());
         art.deleteAndPersist();
      }

      // Add two new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement(reqTeam.getWorkingBranch());
      for (int x = 15; x < 17; x++) {
         String name = "Claw Interface Init " + x;
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Adding artifact => " + name);
         Artifact newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(CoreAttributeTypes.SafetyCriticality, "IV");
         newArt.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         parentArt.addChild(newArt);

         newArt.persist();
      }

   }

   private void createNonReqChangeDemoActions() throws Exception {
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), "Populate Demo DB - Create Actions");
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_3");
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoSawBuilds.SAW_Bld_3.toString(), null, transaction);
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_2");
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoSawBuilds.SAW_Bld_2.toString(), null, transaction);
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_1");
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoSawBuilds.SAW_Bld_1.toString(),
         DefaultTeamState.Completed, transaction);
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "createNonReqChangeDemoActions - getGenericActionData");
      createActions(DemoDbActionData.getGenericActionData(), null, null, transaction);
      transaction.execute();
   }

   private Set<ActionArtifact> createActions(Set<DemoDbActionData> actionDatas, String versionStr, DefaultTeamState toStateOverride, SkynetTransaction transaction) throws Exception {
      Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
      int currNum = 1;
      for (DemoDbActionData aData : actionDatas) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating " + currNum++ + "/" + actionDatas.size());
         int x = 0;
         for (String prefixTitle : aData.prefixTitles) {
            ActionArtifact actionArt =
               ActionManager.createAction(null, prefixTitle + " " + aData.postFixTitle,
                  TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x], PriorityType.Priority_1, false, null,
                  aData.getActionableItems(), transaction);
            actionArts.add(actionArt);
            for (TeamWorkFlowArtifact teamWf : actionArt.getTeamWorkFlowArtifacts()) {
               TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);
               // Add validation required flag if Decision review is required
               if (aData.getCreateReviews().length > 0) {
                  for (CreateReview createReview : aData.getCreateReviews()) {
                     if (createReview == CreateReview.Decision) {
                        teamWf.setSoleAttributeValue(AtsAttributeTypes.ValidationRequired, true);
                     }
                  }
               }
               // Transition to desired state
               dtwm.transitionTo((toStateOverride != null ? toStateOverride : aData.toState), null, false, transaction);
               teamWf.persist(transaction);
               if (Strings.isValid(versionStr)) {
                  VersionArtifact verArt =
                     (VersionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, versionStr,
                        AtsUtil.getAtsBranch());
                  teamWf.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, verArt);
                  teamWf.persist(transaction);
               }
            }
         }
      }
      return actionArts;
   }

   private void demoDbImportReqsTx() {
      try {
         importRequirements(DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement.getName() + "s",
            CoreArtifactTypes.SoftwareRequirement, "support/SAW-SoftwareRequirements.xml");
         importRequirements(DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SystemRequirement.getName() + "s",
            CoreArtifactTypes.SystemRequirement, "support/SAW-SystemRequirements.xml");
         importRequirements(DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SubsystemRequirement.getName() + "s",
            CoreArtifactTypes.SubsystemRequirement, "support/SAW-SubsystemRequirements.xml");
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.SEVERE, ex);
      }
   }

   private void importRequirements(IOseeBranch branch, String rootArtifactName, IArtifactType requirementType, String filename) throws Exception {

      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
         String.format("Importing \"%s\" requirements on branch \"%s\"", rootArtifactName, branch));
      Artifact systemReq = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, rootArtifactName, branch);

      File file = OseeAtsConfigDemoActivator.getInstance().getPluginFile(filename);
      IArtifactImportResolver artifactResolver =
         new NewArtifactImportResolver(requirementType, CoreArtifactTypes.Heading);
      IArtifactExtractor extractor = new WordOutlineExtractor();
      extractor.setDelegate(new WordOutlineExtractorDelegate());

      IOperation operation =
         ArtifactImportOperationFactory.createOperation(file, systemReq, extractor, artifactResolver, false);
      Operations.executeWorkAndCheckStatus(operation);

      // Validate that something was imported
      if (systemReq.getChildren().isEmpty()) {
         throw new IllegalStateException("Artifacts were not imported");
      }
   }

   private void relate(IRelationEnumeration relationSide, Artifact artifact, Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   private void demoDbTraceabilityTx(SkynetTransaction transaction, Branch branch) {
      try {
         Collection<Artifact> systemArts =
            DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SystemRequirement, "Robot", branch);

         Collection<Artifact> component = DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.Component, "API", branch);
         component.addAll(DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.Component, "Hardware", branch));
         component.addAll(DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.Component, "Sensor", branch));

         Collection<Artifact> subSystemArts =
            DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SubsystemRequirement, "Robot", branch);
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SubsystemRequirement, "Video", branch));
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SubsystemRequirement, "Interface",
            branch));

         Collection<Artifact> softArts =
            DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SoftwareRequirement, "Robot", branch);
         softArts.addAll(DemoDbUtil.getArtTypeRequirements(CoreArtifactTypes.SoftwareRequirement, "Interface", branch));

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
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.SEVERE, ex);
      }
   }

}
