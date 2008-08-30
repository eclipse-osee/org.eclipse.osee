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
package org.eclipse.osee.ats.config.demo.config;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.artifact.DemoCodeTeamWorkflowArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDatabaseConfig.SawBuilds;
import org.eclipse.osee.ats.config.demo.config.DemoDbActionData.CreateReview;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil.SoftwareRequirementStrs;
import org.eclipse.osee.ats.config.demo.util.Cscis;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.ProgramAttributes;
import org.eclipse.osee.ats.config.demo.util.Subsystems;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.DefaultTeamWorkflowManager;
import org.eclipse.osee.ats.util.Favorites;
import org.eclipse.osee.ats.util.Subscribe;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * Run from the ATS Navigator after the DB is configured for "OSEE Demo Database", this class will populate the database
 * with sample actions written against XYZ configured teams
 * 
 * @author Donald G. Dunne
 */
public class PopulateDemoActions extends XNavigateItemAction {

   private String[] TITLE_PREFIX =
         new String[] {"Problem with the", "Can't see the", "Button A doesn't work on", "Add to the",
               "Make new Button for ", "User can't load "};
   private ChangeType[] CHANGE_TYPE =
         new ChangeType[] {ChangeType.Problem, ChangeType.Problem, ChangeType.Problem, ChangeType.Improvement,
               ChangeType.Improvement, ChangeType.Support, ChangeType.Improvement, ChangeType.Support};

   public PopulateDemoActions(XNavigateItem parent) {
      super(parent, "Populate Demo Actions");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      run(true);
   }

   public void run(boolean prompt) throws Exception {
      AtsPlugin.setEmailEnabled(false);
      if (AtsPlugin.isProductionDb()) throw new IllegalStateException(
            "PopulateDemoActions should not be run on production DB");
      if (SkynetDbInit.isDbInit() || (!SkynetDbInit.isDbInit() && (!prompt || (prompt && MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(), getName(), getName()))))) {

         DemoDbUtil.setDefaultBranch(BranchPersistenceManager.getKeyedBranch(SawBuilds.SAW_Bld_1.name()));

         // Import all requirements on SAW_Bld_1 Branch
         DemoDbImportReqsTx importTx =
               new DemoDbImportReqsTx(BranchPersistenceManager.getAtsBranch(), !SkynetDbInit.isDbInit());
         importTx.execute();

         DemoDbUtil.sleep(5000);

         // Create traceability between System, Subsystem and Software requirements
         DemoDbTraceabilityTx traceTx =
               new DemoDbTraceabilityTx(BranchPersistenceManager.getAtsBranch(), !SkynetDbInit.isDbInit());
         traceTx.execute();

         DemoDbUtil.sleep(5000);

         // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
         CreateMainWorkingBranchTx saw2BranchTx =
               new CreateMainWorkingBranchTx(BranchPersistenceManager.getAtsBranch(), !SkynetDbInit.isDbInit());
         saw2BranchTx.execute();

         // Create SAW_Bld_2 Actions 
         Set<ActionArtifact> actionArts =
               createActions(DemoDbActionData.getReqSawActionsData(),
                     DemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(), null);

         // Sleep to wait for the persist of the actions
         DemoDbUtil.sleep(3000);

         for (ActionArtifact actionArt : actionArts) {
            if (actionArt.getDescriptiveName().contains("(committed)")) {
               // Working Branch off SAW_Bld_2, Make Changes, Commit
               makeAction1ReqChanges(actionArt);
            } else if (actionArt.getDescriptiveName().contains("(uncommitted)")) {
               // Working Branch off SAW_Bld_2, Make Changes, DON'T Commit
               makeAction2ReqChanges(actionArt);
            } else if (actionArt.getDescriptiveName().contains("(uncommitted-conflicted)")) {
               // Working Branch off SAW_Bld_2, Make Conflicted Changes, DON'T Commit
               makeAction3ReqChanges(actionArt);
            }
         }

         // Create actions against non-requirement AIs and Teams
         createNonReqChangeDemoActions();

         // Mark all CIS Code "Team Workflows" as Favorites for "Joe Smith"
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Add Favorites", false);
         for (Artifact art : ArtifactQuery.getArtifactsFromTypeAndName(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME,
               "%Diagram View%", AtsPlugin.getAtsBranch())) {
            new Favorites((StateMachineArtifact) art).toggleFavorite(false);
         }

         // Mark all Tools Team "Team Workflows" as Subscribed for "Joe Smith"
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Add Subscribed", false);
         for (Artifact art : ArtifactQuery.getArtifactsFromTypeAndName(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME,
               "%Even%", AtsPlugin.getAtsBranch())) {
            new Subscribe((StateMachineArtifact) art).toggleSubscribe(false);
         }

         // Create some tasks off sample workflows
         DemoDbTasks.createTasks();

         // Create group of sample artifacts
         DemoDbGroups.createGroups();

         // Create and transition reviews off sample workflows
         DemoDbReviews.createReviews();

         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Populate Complete", false);

      }
   }

   public class CreateMainWorkingBranchTx extends AbstractSkynetTxTemplate {
      public CreateMainWorkingBranchTx(Branch branch, boolean popup) {
         super(branch);
      }

      @Override
      protected void handleTxWork() throws OseeCoreException, SQLException {
         try {
            OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating SAW_Bld_2 branch off SAW_Bld_1", false);
            // Create SAW_Bld_2 branch off SAW_Bld_1
            createChildMainWorkingBranch(SawBuilds.SAW_Bld_1.name(), SawBuilds.SAW_Bld_2.name());
            DemoDbUtil.sleep(5000);
            // Map team definitions versions to their related branches
            DemoDatabaseConfig.mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW),
                  SawBuilds.SAW_Bld_2.name(), SawBuilds.SAW_Bld_2.name());
         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         }
      }
   }

   private Branch createChildMainWorkingBranch(String parentBrachName, String childBranchName) throws Exception {
      Branch parentBranch = BranchPersistenceManager.getKeyedBranch(parentBrachName);

      Branch childBranch =
            BranchPersistenceManager.createWorkingBranch(TransactionIdManager.getInstance().getEditableTransactionId(
                  parentBranch), childBranchName, childBranchName, SkynetAuthentication.getUser(UserEnum.NoOne));
      return childBranch;
   }

   private void makeAction1ReqChanges(ActionArtifact actionArt) throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Making Action 1 Requirement Changes", false);
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
      }

      if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating working branch", false);
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      DemoDbUtil.sleep(5000);

      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Robot)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Modifying artifact => ")).append(art).toString(), false);
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Navigation.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "A");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Navigation.name());
         Artifact navArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Navigation",
                     BranchPersistenceManager.getDefaultBranch());
         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, navArt);
         art.persistAttributesAndRelations();
      }

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Event)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Modifying artifact => ")).append(art).toString(), false);
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         Artifact robotArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Robot API",
                     BranchPersistenceManager.getDefaultBranch());
         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, robotArt);
         art.persistAttributesAndRelations();
      }

      // Delete two artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.daVinci)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Deleting artifact => ")).append(art).toString(), false);
         art.delete();
      }

      // Add three new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement();
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Adding artifact => " + name, false);
         Artifact newArt =
               ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         newArt.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         newArt.persistAttributesAndRelations();
         parentArt.addChild(newArt);
         parentArt.persistAttributesAndRelations();
      }

      DemoDbUtil.sleep(2000L);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Committing branch", false);
      result = reqTeam.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error committing working branch: ")).append(result.getText()).toString());

      DemoDbUtil.sleep(5000);

      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Completing Action", false);
   }

   private void makeAction3ReqChanges(ActionArtifact actionArt) throws Exception {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
      }

      if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating working branch", false);
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      DemoDbUtil.sleep(5000);

      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      Artifact branchArtifact =
            DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ).iterator().next();
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, (new StringBuilder("Modifying branch artifact => ")).append(
            branchArtifact).toString(), false);
      branchArtifact.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
      branchArtifact.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
      branchArtifact.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
      Artifact comArt =
            ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Robot API",
                  BranchPersistenceManager.getDefaultBranch());
      branchArtifact.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, comArt);
      branchArtifact.persistAttributesAndRelations();

      // Set to parent branch to make some conflicting changes
      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch().getParentBranch());

      Artifact parentArtifact =
            DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ).iterator().next();
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, (new StringBuilder("Modifying parent artifact => ")).append(
            parentArtifact).toString(), false);
      parentArtifact.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Navigation.name());
      parentArtifact.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "E");
      parentArtifact.setSoleAttributeValue(ProgramAttributes.Subsystem.name(),
            Subsystems.Cognitive_Decision_Aiding.name());
      parentArtifact.persistAttributesAndRelations();

   }

   private void makeAction2ReqChanges(ActionArtifact actionArt) throws Exception {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
      }

      if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating working branch", false);
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      DemoDbUtil.sleep(5000);

      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Functional)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Modifying artifact => ")).append(art).toString(), false);
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         Artifact comArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Robot API",
                     BranchPersistenceManager.getDefaultBranch());

         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, comArt);
         art.persistAttributesAndRelations();
      }

      // Delete one artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.CISST)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Deleting artifact => ")).append(art).toString(), false);
         art.delete();
      }

      // Add two new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement();
      for (int x = 15; x < 17; x++) {
         String name = "Claw Interface Init " + x;
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Adding artifact => " + name, false);
         Artifact newArt =
               ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         newArt.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         parentArt.addChild(newArt);

         newArt.persistAttributesAndRelations();
      }

   }

   private void createNonReqChangeDemoActions() throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "createNonReqChangeDemoActions - SAW_Bld_3", false);
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoDatabaseConfig.SawBuilds.SAW_Bld_3.toString(), null);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "createNonReqChangeDemoActions - SAW_Bld_2", false);
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(), null);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "createNonReqChangeDemoActions - SAW_Bld_1", false);
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoDatabaseConfig.SawBuilds.SAW_Bld_1.toString(),
            DefaultTeamState.Completed);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "createNonReqChangeDemoActions - getGenericActionData", false);
      createActions(DemoDbActionData.getGenericActionData(), null, null);
   }

   private Set<ActionArtifact> createActions(Set<DemoDbActionData> actionDatas, String versionStr, DefaultTeamState toStateOverride) throws Exception {
      Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
      int currNum = 1;
      for (DemoDbActionData aData : actionDatas) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating " + currNum++ + "/" + actionDatas.size(), false);
         int x = 0;
         for (String prefixTitle : aData.prefixTitles) {
            ActionArtifact actionArt =
                  NewActionJob.createAction(null, prefixTitle + " " + aData.postFixTitle,
                        TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x], PriorityType.Priority_1,
                        aData.getUserCommunities(), false, null, aData.getActionableItems());
            actionArts.add(actionArt);
            for (TeamWorkFlowArtifact teamWf : actionArt.getTeamWorkFlowArtifacts()) {
               DefaultTeamWorkflowManager dtwm = new DefaultTeamWorkflowManager(teamWf);
               // Add validation required flag if Decision review is required
               if (aData.getCreateReviews().length > 0) {
                  for (CreateReview createReview : aData.getCreateReviews()) {
                     if (createReview == CreateReview.Decision) teamWf.setSoleAttributeValue(
                           ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), true);
                  }
               }
               // Transition to desired state
               dtwm.transitionTo((toStateOverride != null ? toStateOverride : aData.toState), null, false);
               teamWf.persistAttributesAndRelations();
               if (versionStr != null && !versionStr.equals("")) {
                  VersionArtifact verArt =
                        (VersionArtifact) ArtifactQuery.getArtifactFromTypeAndName(VersionArtifact.ARTIFACT_NAME,
                              versionStr, AtsPlugin.getAtsBranch());
                  teamWf.addRelation(AtsRelation.TeamWorkflowTargetedForVersion_Version, verArt);
                  teamWf.persistAttributesAndRelations();
               }
            }
         }
      }
      return actionArts;
   }

}
