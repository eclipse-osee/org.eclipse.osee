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

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowManager;
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
import org.eclipse.osee.ats.util.Favorites;
import org.eclipse.osee.ats.util.Subscribe;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportJob;
import org.eclipse.osee.framework.ui.skynet.Import.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.NewArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.WordOutlineExtractor;
import org.eclipse.osee.framework.ui.skynet.handler.GeneralWordOutlineHandler;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
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

   private final String[] TITLE_PREFIX =
         new String[] {"Problem with the", "Can't see the", "Button A doesn't work on", "Add to the",
               "Make new Button for ", "User can't load "};
   private final ChangeType[] CHANGE_TYPE =
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

         Branch saw1Branch = BranchManager.getKeyedBranch(SawBuilds.SAW_Bld_1.name());
         DemoDbUtil.setDefaultBranch(saw1Branch);

         // Import all requirements on SAW_Bld_1 Branch
         demoDbImportReqsTx();

         DemoDbUtil.sleep(5000);

         // Create traceability between System, Subsystem and Software requirements
         SkynetTransaction demoDbTraceability = new SkynetTransaction(saw1Branch);
         demoDbTraceabilityTx(demoDbTraceability);
         demoDbTraceability.execute();

         DemoDbUtil.sleep(5000);

         // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
         createMainWorkingBranchTx();

         // Create SAW_Bld_2 Actions 
         SkynetTransaction sawActionsTransaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         Set<ActionArtifact> actionArts =
               createActions(DemoDbActionData.getReqSawActionsData(),
                     DemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(), null, sawActionsTransaction);
         sawActionsTransaction.execute();
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
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Add Favorites");
         for (Artifact art : ArtifactQuery.getArtifactsFromTypeAndName(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME,
               "%Diagram View%", AtsPlugin.getAtsBranch())) {
            new Favorites((StateMachineArtifact) art).toggleFavorite(false);
         }

         // Mark all Tools Team "Team Workflows" as Subscribed for "Joe Smith"
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Add Subscribed");
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

         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Populate Complete");

      }
   }

   private void createMainWorkingBranchTx() throws OseeCoreException {
      try {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Creating SAW_Bld_2 branch off SAW_Bld_1");
         // Create SAW_Bld_2 branch off SAW_Bld_1
         createChildMainWorkingBranch(SawBuilds.SAW_Bld_1.name(), SawBuilds.SAW_Bld_2.name());
         DemoDbUtil.sleep(5000);
         // Map team definitions versions to their related branches
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         DemoDatabaseConfig.mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW),
               SawBuilds.SAW_Bld_2.name(), SawBuilds.SAW_Bld_2.name(), transaction);
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.SEVERE, ex);
      }
   }

   private Branch createChildMainWorkingBranch(String parentBrachName, String childBranchName) throws Exception {
      Branch parentBranch = BranchManager.getKeyedBranch(parentBrachName);

      Branch childBranch =
            BranchManager.createWorkingBranch(parentBranch, childBranchName, childBranchName,
                  UserManager.getUser(SystemUser.OseeSystem));
      return childBranch;
   }

   private void makeAction1ReqChanges(ActionArtifact actionArt) throws Exception {
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Making Action 1 Requirement Changes");
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
      }

      if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      DemoDbUtil.sleep(5000);

      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Robot)) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, (new StringBuilder("Modifying artifact => ")).append(
               art).toString());
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Navigation.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "A");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Navigation.name());
         Artifact navArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Navigation",
                     BranchManager.getDefaultBranch());
         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, navArt);
         art.persistAttributesAndRelations();
      }

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Event)) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, (new StringBuilder("Modifying artifact => ")).append(
               art).toString());
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         Artifact robotArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Robot API",
                     BranchManager.getDefaultBranch());
         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, robotArt);
         art.persistAttributesAndRelations();
      }

      // Delete two artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.daVinci)) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,
               (new StringBuilder("Deleting artifact => ")).append(art).toString());
         art.delete();
      }

      // Add three new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement();
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Adding artifact => " + name);
         Artifact newArt =
               ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         newArt.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         newArt.persistAttributesAndRelations();
         parentArt.addChild(newArt);
         parentArt.persistAttributesAndRelations();
      }

      DemoDbUtil.sleep(2000L);
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Committing branch");
      reqTeam.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true);

      DemoDbUtil.sleep(5000);

      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Completing Action");
   }

   private void makeAction3ReqChanges(ActionArtifact actionArt) throws Exception {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
      }

      if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      DemoDbUtil.sleep(5000);

      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      Artifact branchArtifact =
            DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ).iterator().next();
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,
            (new StringBuilder("Modifying branch artifact => ")).append(branchArtifact).toString());
      branchArtifact.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
      branchArtifact.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
      branchArtifact.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
      Artifact comArt =
            ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Robot API",
                  BranchManager.getDefaultBranch());
      branchArtifact.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, comArt);
      branchArtifact.persistAttributesAndRelations();

      // Set to parent branch to make some conflicting changes
      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch().getParentBranch());

      Artifact parentArtifact =
            DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ).iterator().next();
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,
            (new StringBuilder("Modifying parent artifact => ")).append(parentArtifact).toString());
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
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      DemoDbUtil.sleep(5000);

      DemoDbUtil.setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Functional)) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, (new StringBuilder("Modifying artifact => ")).append(
               art).toString());
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         Artifact comArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, "Robot API",
                     BranchManager.getDefaultBranch());

         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, comArt);
         art.persistAttributesAndRelations();
      }

      // Delete one artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.CISST)) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,
               (new StringBuilder("Deleting artifact => ")).append(art).toString());
         art.delete();
      }

      // Add two new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement();
      for (int x = 15; x < 17; x++) {
         String name = "Claw Interface Init " + x;
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Adding artifact => " + name);
         Artifact newArt =
               ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.toString(), "D");
         newArt.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         parentArt.addChild(newArt);

         newArt.persistAttributesAndRelations();
      }

   }

   private void createNonReqChangeDemoActions() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_3");
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoDatabaseConfig.SawBuilds.SAW_Bld_3.toString(), null,
            transaction);
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_2");
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(), null,
            transaction);
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_1");
      createActions(DemoDbActionData.getNonReqSawActionData(), DemoDatabaseConfig.SawBuilds.SAW_Bld_1.toString(),
            DefaultTeamState.Completed, transaction);
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "createNonReqChangeDemoActions - getGenericActionData");
      createActions(DemoDbActionData.getGenericActionData(), null, null, transaction);
      transaction.execute();
   }

   private Set<ActionArtifact> createActions(Set<DemoDbActionData> actionDatas, String versionStr, DefaultTeamState toStateOverride, SkynetTransaction transaction) throws Exception {
      Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
      int currNum = 1;
      for (DemoDbActionData aData : actionDatas) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Creating " + currNum++ + "/" + actionDatas.size());
         int x = 0;
         for (String prefixTitle : aData.prefixTitles) {
            ActionArtifact actionArt =
                  NewActionJob.createAction(null, prefixTitle + " " + aData.postFixTitle,
                        TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x], PriorityType.Priority_1,
                        aData.getUserCommunities(), false, null, aData.getActionableItems(), transaction);
            actionArts.add(actionArt);
            for (TeamWorkFlowArtifact teamWf : actionArt.getTeamWorkFlowArtifacts()) {
               TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);
               // Add validation required flag if Decision review is required
               if (aData.getCreateReviews().length > 0) {
                  for (CreateReview createReview : aData.getCreateReviews()) {
                     if (createReview == CreateReview.Decision) teamWf.setSoleAttributeValue(
                           ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), true);
                  }
               }
               // Transition to desired state
               dtwm.transitionTo((toStateOverride != null ? toStateOverride : aData.toState), null, false, transaction);
               teamWf.persistAttributesAndRelations(transaction);
               if (versionStr != null && !versionStr.equals("")) {
                  VersionArtifact verArt =
                        (VersionArtifact) ArtifactQuery.getArtifactFromTypeAndName(VersionArtifact.ARTIFACT_NAME,
                              versionStr, AtsPlugin.getAtsBranch());
                  teamWf.addRelation(AtsRelation.TeamWorkflowTargetedForVersion_Version, verArt);
                  teamWf.persistAttributesAndRelations(transaction);
               }
            }
         }
      }
      return actionArts;
   }

   private void demoDbImportReqsTx() throws OseeCoreException {
      try {
         importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SOFTWARE_REQUIREMENT + "s",
               Requirements.SOFTWARE_REQUIREMENT, "support/SAW-SoftwareRequirements.xml");
         importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SYSTEM_REQUIREMENT + "s",
               Requirements.SYSTEM_REQUIREMENT, "support/SAW-SystemRequirements.xml");
         importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SUBSYSTEM_REQUIREMENT + "s",
               Requirements.SUBSYSTEM_REQUIREMENT, "support/SAW-SubsystemRequirements.xml");
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.SEVERE, ex);
      }
   }

   private void importRequirements(String buildName, String rootArtifactName, String requirementArtifactName, String filename) throws Exception {

      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,
            "Importing \"" + rootArtifactName + "\" requirements on branch \"" + buildName + "\"");
      Branch branch = BranchManager.getKeyedBranch(buildName);
      Artifact systemReq = ArtifactQuery.getArtifactFromTypeAndName("Folder", rootArtifactName, branch);

      File file = OseeAtsConfigDemoPlugin.getInstance().getPluginFile(filename);
      IArtifactImportResolver artifactResolver = new NewArtifactImportResolver();
      ArtifactType mainDescriptor = ArtifactTypeManager.getType(requirementArtifactName);
      ArtifactExtractor extractor =
            new WordOutlineExtractor(mainDescriptor, branch, 0, new GeneralWordOutlineHandler());
      Job job = new ArtifactImportJob(file, systemReq, extractor, branch, artifactResolver);
      job.setPriority(Job.LONG);
      job.schedule();
      job.join();
      // Validate that something was imported
      if (systemReq.getChildren().size() == 0) throw new IllegalStateException("Artifacts were not imported");

   }

   private void relate(IRelationEnumeration relationSide, Artifact artifact, Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   private void demoDbTraceabilityTx(SkynetTransaction transaction) throws OseeCoreException {
      try {
         Collection<Artifact> systemArts = DemoDbUtil.getArtTypeRequirements(Requirements.SYSTEM_REQUIREMENT, "Robot");

         Collection<Artifact> component = DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "API");
         component.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "Hardware"));
         component.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "Sensor"));

         Collection<Artifact> subSystemArts =
               DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Robot");
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Video"));
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Interface"));

         Collection<Artifact> softArts = DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Robot");
         softArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Interface"));

         // Relate System to SubSystem to Software Requirements
         for (Artifact systemArt : systemArts) {
            relate(CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL, systemArt, subSystemArts);
            systemArt.persistRelations(transaction);

            for (Artifact subSystemArt : subSystemArts) {
               relate(CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL, subSystemArt, softArts);
               subSystemArt.persistRelations(transaction);
            }
         }

         // Relate System, SubSystem and Software Requirements to Componets
         for (Artifact art : systemArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
            art.persistRelations(transaction);
         }
         for (Artifact art : subSystemArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
            art.persistRelations(transaction);
         }
         for (Artifact art : softArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
         }

         // Create Test Script Artifacts
         Set<Artifact> verificationTests = new HashSet<Artifact>();
         Artifact verificationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Verification Tests",
                     BranchManager.getDefaultBranch());
         if (verificationHeader == null) throw new IllegalStateException("Could not find Verification Tests header");
         for (String str : new String[] {"A", "B", "C"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_SCRIPT, verificationHeader.getBranch(),
                        "Verification Test " + str);
            verificationTests.add(newArt);
            verificationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persistAttributesAndRelations(transaction);
         }
         Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

         // Create Validation Test Procedure Artifacts
         Set<Artifact> validationTests = new HashSet<Artifact>();
         Artifact validationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Validation Tests", BranchManager.getDefaultBranch());
         if (validationHeader == null) throw new IllegalStateException("Could not find Validation Tests header");
         for (String str : new String[] {"1", "2", "3"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, validationHeader.getBranch(),
                        "Validation Test " + str);
            validationTests.add(newArt);
            validationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persistAttributesAndRelations(transaction);
         }
         Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

         // Create Integration Test Procedure Artifacts
         Set<Artifact> integrationTests = new HashSet<Artifact>();
         Artifact integrationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Integration Tests", BranchManager.getDefaultBranch());
         if (integrationHeader == null) throw new IllegalStateException("Could not find integration Tests header");
         for (String str : new String[] {"X", "Y", "Z"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, integrationHeader.getBranch(),
                        "integration Test " + str);
            integrationTests.add(newArt);
            integrationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persistAttributesAndRelations(transaction);
         }
         Artifact integrationTestsArray[] = integrationTests.toArray(new Artifact[integrationTests.size()]);

         // Relate Software Artifacts to Tests
         Artifact softReqsArray[] = softArts.toArray(new Artifact[softArts.size()]);
         softReqsArray[0].addRelation(CoreRelationEnumeration.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[0].addRelation(CoreRelationEnumeration.Validation__Validator, verificationTestsArray[1]);
         softReqsArray[1].addRelation(CoreRelationEnumeration.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[1].addRelation(CoreRelationEnumeration.Validation__Validator, validationTestsArray[1]);
         softReqsArray[2].addRelation(CoreRelationEnumeration.Validation__Validator, validationTestsArray[0]);
         softReqsArray[2].addRelation(CoreRelationEnumeration.Validation__Validator, integrationTestsArray[1]);
         softReqsArray[3].addRelation(CoreRelationEnumeration.Validation__Validator, integrationTestsArray[0]);
         softReqsArray[4].addRelation(CoreRelationEnumeration.Validation__Validator, integrationTestsArray[2]);
         softReqsArray[5].addRelation(CoreRelationEnumeration.Validation__Validator, validationTestsArray[2]);

         for (Artifact artifact : softArts) {
            artifact.persistAttributesAndRelations(transaction);
         }

      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.SEVERE, ex);
      }
   }

}
