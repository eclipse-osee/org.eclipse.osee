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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowManager;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.demo.artifact.DemoCodeTeamWorkflowArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDbActionData.CreateReview;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil.SoftwareRequirementStrs;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.ats.util.SubscribeManager;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.NewArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.support.test.util.DemoCscis;
import org.eclipse.osee.support.test.util.DemoProgramAttributes;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.DemoSubsystems;
import org.eclipse.osee.support.test.util.TestUtil;
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

   private static final String UPDATE_BRANCH_TYPE = "update osee_branch set branch_type = ? where branch_id = ?";

   public PopulateDemoActions(XNavigateItem parent) {
      super(parent, "Populate Demo Actions", FrameworkImage.ADMIN);
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
      if (DbUtil.isDbInit() || !prompt || prompt && MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
            getName(), getName())) {

         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

         Branch saw1Branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name());

         // Import all requirements on SAW_Bld_1 Branch
         demoDbImportReqsTx();

         DemoDbUtil.sleep(5000);

         // Create traceability between System, Subsystem and Software requirements
         SkynetTransaction demoDbTraceability = new SkynetTransaction(saw1Branch);
         demoDbTraceabilityTx(demoDbTraceability, saw1Branch);
         demoDbTraceability.execute();

         DemoDbUtil.sleep(5000);

         // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
         createMainWorkingBranchTx();

         // Create SAW_Bld_2 Actions 
         SkynetTransaction sawActionsTransaction = new SkynetTransaction(AtsUtil.getAtsBranch());
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
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME,
               "%Diagram View%", AtsUtil.getAtsBranch())) {
            new FavoritesManager((StateMachineArtifact) art).toggleFavorite(false);
         }

         // Mark all Tools Team "Team Workflows" as Subscribed for "Joe Smith"
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Add Subscribed");
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME,
               "%Even%", AtsUtil.getAtsBranch())) {
            new SubscribeManager((StateMachineArtifact) art).toggleSubscribe(false);
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

   private void createMainWorkingBranchTx() throws OseeCoreException {
      try {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating SAW_Bld_2 branch off SAW_Bld_1");
         // Create SAW_Bld_2 branch off SAW_Bld_1
         Branch branch = createChildMainWorkingBranch(DemoSawBuilds.SAW_Bld_1.name(), DemoSawBuilds.SAW_Bld_2.name());
         DemoDbUtil.sleep(5000);
         // need to update the branch type;  
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_TYPE, new Object[] {BranchType.BASELINE.getValue(),
               branch.getBranchId()});
         BranchManager.refreshBranches();
         // Map team definitions versions to their related branches
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
         DemoDatabaseConfig.mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW),
               DemoSawBuilds.SAW_Bld_2.name(), DemoSawBuilds.SAW_Bld_2.name(), transaction);
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.SEVERE, ex);
      }
   }

   public static Branch createChildMainWorkingBranch(String parentBrachName, String childBranchName) throws Exception {
      Branch parentBranch = BranchManager.getKeyedBranch(parentBrachName);
      Branch childBranch =
            BranchManager.createWorkingBranch(parentBranch, childBranchName, UserManager.getUser(SystemUser.OseeSystem));
      childBranch.setAliases(childBranchName);
      childBranch.persist();
      return childBranch;
   }

   private void makeAction1ReqChanges(ActionArtifact actionArt) throws Exception {
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Making Action 1 Requirement Changes");
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getName().contains("Req")) {
            reqTeam = team;
         }
      }

      if (reqTeam == null) {
         throw new IllegalArgumentException("Can't locate Req team.");
      }
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) {
         throw new IllegalArgumentException(new StringBuilder("Error creating working branch: ").append(
               result.getText()).toString());
      }

      DemoDbUtil.sleep(5000);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Robot,
            reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, new StringBuilder("Modifying artifact => ").append(
               art).toString());
         art.setSoleAttributeValue(DemoProgramAttributes.CSCI.name(), DemoCscis.Navigation.name());
         art.setSoleAttributeValue(DemoProgramAttributes.Safety_Criticality.toString(), "I");
         art.setSoleAttributeValue(DemoProgramAttributes.Subsystem.name(), DemoSubsystems.Navigation.name());
         Artifact navArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, DemoSubsystems.Navigation.name(),
                     reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());
         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, navArt);
         art.persist();
      }

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Event,
            reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, new StringBuilder("Modifying artifact => ").append(
               art).toString());
         art.setSoleAttributeValue(DemoProgramAttributes.CSCI.name(), DemoCscis.Interface.name());
         art.setSoleAttributeValue(DemoProgramAttributes.Safety_Criticality.toString(), "IV");
         art.setSoleAttributeValue(DemoProgramAttributes.Subsystem.name(), DemoSubsystems.Communications.name());
         Artifact robotArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, DemoSubsystems.Robot_API.name(),
                     reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());
         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, robotArt);
         art.persist();
      }

      // Delete two artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.daVinci,
            reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, new StringBuilder("Deleting artifact => ").append(
               art).toString());
         art.deleteAndPersist();
      }

      // Add three new artifacts
      Artifact parentArt =
            DemoDbUtil.getInterfaceInitializationSoftwareRequirement(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Adding artifact => " + name);
         Artifact newArt =
               ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(DemoProgramAttributes.Safety_Criticality.toString(), "IV");
         newArt.setSoleAttributeValue(DemoProgramAttributes.Subsystem.name(), DemoSubsystems.Communications.name());
         newArt.persist();
         parentArt.addChild(newArt);
         parentArt.persist();
      }

      DemoDbUtil.sleep(2000L);
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Committing branch");
      reqTeam.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true,
            reqTeam.getSmaMgr().getTargetedForVersion().getParentBranch(), true);

      DemoDbUtil.sleep(5000);

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
         throw new IllegalArgumentException("Can't locate Req team.");
      }
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) {
         throw new IllegalArgumentException(new StringBuilder("Error creating working branch: ").append(
               result.getText()).toString());
      }

      DemoDbUtil.sleep(5000);

      Artifact branchArtifact =
            DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ,
                  reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch()).iterator().next();
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            new StringBuilder("Modifying branch artifact => ").append(branchArtifact).toString());
      branchArtifact.setSoleAttributeValue(DemoProgramAttributes.CSCI.name(), DemoCscis.Interface.name());
      branchArtifact.setSoleAttributeValue(DemoProgramAttributes.Safety_Criticality.toString(), "IV");
      branchArtifact.setSoleAttributeValue(DemoProgramAttributes.Subsystem.name(), DemoSubsystems.Communications.name());
      Artifact comArt =
            ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, DemoSubsystems.Robot_API.name(),
                  reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());
      branchArtifact.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, comArt);
      branchArtifact.persist();

      Artifact parentArtifact =
            DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, DemoDbUtil.HAPTIC_CONSTRAINTS_REQ,
                  reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch()).iterator().next();
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            new StringBuilder("Modifying parent artifact => ").append(parentArtifact).toString());
      parentArtifact.setSoleAttributeValue(DemoProgramAttributes.CSCI.name(), DemoCscis.Navigation.name());
      parentArtifact.setSoleAttributeValue(DemoProgramAttributes.Safety_Criticality.toString(), "V");
      parentArtifact.setSoleAttributeValue(DemoProgramAttributes.Subsystem.name(),
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
         throw new IllegalArgumentException("Can't locate Req team.");
      }
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Creating working branch");
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) {
         throw new IllegalArgumentException(new StringBuilder("Error creating working branch: ").append(
               result.getText()).toString());
      }

      DemoDbUtil.sleep(5000);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Functional,
            reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, new StringBuilder("Modifying artifact => ").append(
               art).toString());
         art.setSoleAttributeValue(DemoProgramAttributes.CSCI.name(), DemoCscis.Interface.name());
         art.setSoleAttributeValue(DemoProgramAttributes.Safety_Criticality.toString(), "IV");
         art.setSoleAttributeValue(DemoProgramAttributes.Subsystem.name(), DemoSubsystems.Communications.name());
         Artifact comArt =
               ArtifactQuery.getArtifactFromTypeAndName(Requirements.COMPONENT, DemoSubsystems.Robot_API.name(),
                     reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

         art.addRelation(CoreRelationEnumeration.ALLOCATION__COMPONENT, comArt);
         art.persist();
      }

      // Delete one artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.CISST,
            reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch())) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, new StringBuilder("Deleting artifact => ").append(
               art).toString());
         art.deleteAndPersist();
      }

      // Add two new artifacts
      Artifact parentArt =
            DemoDbUtil.getInterfaceInitializationSoftwareRequirement(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());
      for (int x = 15; x < 17; x++) {
         String name = "Claw Interface Init " + x;
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Adding artifact => " + name);
         Artifact newArt =
               ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(DemoProgramAttributes.Safety_Criticality.toString(), "IV");
         newArt.setSoleAttributeValue(DemoProgramAttributes.Subsystem.name(), DemoSubsystems.Communications.name());
         parentArt.addChild(newArt);

         newArt.persist();
      }

   }

   private void createNonReqChangeDemoActions() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
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
                        TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x], PriorityType.Priority_1,
                        aData.getUserCommunities(), false, null, aData.getActionableItems(), transaction);
            actionArts.add(actionArt);
            for (TeamWorkFlowArtifact teamWf : actionArt.getTeamWorkFlowArtifacts()) {
               TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);
               // Add validation required flag if Decision review is required
               if (aData.getCreateReviews().length > 0) {
                  for (CreateReview createReview : aData.getCreateReviews()) {
                     if (createReview == CreateReview.Decision) {
                        teamWf.setSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), true);
                     }
                  }
               }
               // Transition to desired state
               dtwm.transitionTo((toStateOverride != null ? toStateOverride : aData.toState), null, false, transaction);
               teamWf.persist(transaction);
               if (versionStr != null && !versionStr.equals("")) {
                  VersionArtifact verArt =
                        (VersionArtifact) ArtifactQuery.getArtifactFromTypeAndName(VersionArtifact.ARTIFACT_NAME,
                              versionStr, AtsUtil.getAtsBranch());
                  teamWf.addRelation(AtsRelation.TeamWorkflowTargetedForVersion_Version, verArt);
                  teamWf.persist(transaction);
               }
            }
         }
      }
      return actionArts;
   }

   private void demoDbImportReqsTx() throws OseeCoreException {
      try {
         importRequirements(DemoSawBuilds.SAW_Bld_1.name(), Requirements.SOFTWARE_REQUIREMENT + "s",
               Requirements.SOFTWARE_REQUIREMENT, "support/SAW-SoftwareRequirements.xml");
         importRequirements(DemoSawBuilds.SAW_Bld_1.name(), Requirements.SYSTEM_REQUIREMENT + "s",
               Requirements.SYSTEM_REQUIREMENT, "support/SAW-SystemRequirements.xml");
         importRequirements(DemoSawBuilds.SAW_Bld_1.name(), Requirements.SUBSYSTEM_REQUIREMENT + "s",
               Requirements.SUBSYSTEM_REQUIREMENT, "support/SAW-SubsystemRequirements.xml");
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.SEVERE, ex);
      }
   }

   private void importRequirements(String buildName, String rootArtifactName, String requirementArtifactName, String filename) throws Exception {

      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            "Importing \"" + rootArtifactName + "\" requirements on branch \"" + buildName + "\"");
      Branch branch = BranchManager.getKeyedBranch(buildName);
      Artifact systemReq = ArtifactQuery.getArtifactFromTypeAndName("Folder", rootArtifactName, branch);

      File file = OseeAtsConfigDemoActivator.getInstance().getPluginFile(filename);
      IArtifactImportResolver artifactResolver =
            new NewArtifactImportResolver(ArtifactTypeManager.getType(requirementArtifactName),
                  ArtifactTypeManager.getType("Heading"));
      IArtifactExtractor extractor = new WordOutlineExtractor();
      extractor.setDelegate(new WordOutlineExtractorDelegate());

      IOperation operation =
            ArtifactImportOperationFactory.createOperation(file, systemReq, extractor, artifactResolver, false);
      Operations.executeWork(operation, new NullProgressMonitor(), -1);

      // Validate that something was imported
      if (systemReq.getChildren().size() == 0) {
         throw new IllegalStateException("Artifacts were not imported");
      }
   }

   private void relate(IRelationEnumeration relationSide, Artifact artifact, Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   private void demoDbTraceabilityTx(SkynetTransaction transaction, Branch branch) throws OseeCoreException {
      try {
         Collection<Artifact> systemArts =
               DemoDbUtil.getArtTypeRequirements(Requirements.SYSTEM_REQUIREMENT, "Robot", branch);

         Collection<Artifact> component = DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "API", branch);
         component.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "Hardware", branch));
         component.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "Sensor", branch));

         Collection<Artifact> subSystemArts =
               DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Robot", branch);
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Video", branch));
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Interface", branch));

         Collection<Artifact> softArts =
               DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Robot", branch);
         softArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Interface", branch));

         // Relate System to SubSystem to Software Requirements
         for (Artifact systemArt : systemArts) {
            relate(CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL, systemArt, subSystemArts);
            systemArt.persist(transaction);

            for (Artifact subSystemArt : subSystemArts) {
               relate(CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL, subSystemArt, softArts);
               subSystemArt.persist(transaction);
            }
         }

         // Relate System, SubSystem and Software Requirements to Componets
         for (Artifact art : systemArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
            art.persist(transaction);
         }
         for (Artifact art : subSystemArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
            art.persist(transaction);
         }
         for (Artifact art : softArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
         }

         // Create Test Script Artifacts
         Set<Artifact> verificationTests = new HashSet<Artifact>();
         Artifact verificationHeader = ArtifactQuery.getArtifactFromTypeAndName("Folder", "Verification Tests", branch);
         if (verificationHeader == null) {
            throw new IllegalStateException("Could not find Verification Tests header");
         }
         for (String str : new String[] {"A", "B", "C"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_CASE, verificationHeader.getBranch(),
                        "Verification Test " + str);
            verificationTests.add(newArt);
            verificationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persist(transaction);
         }
         Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

         // Create Validation Test Procedure Artifacts
         Set<Artifact> validationTests = new HashSet<Artifact>();
         Artifact validationHeader = ArtifactQuery.getArtifactFromTypeAndName("Folder", "Validation Tests", branch);
         if (validationHeader == null) {
            throw new IllegalStateException("Could not find Validation Tests header");
         }
         for (String str : new String[] {"1", "2", "3"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, validationHeader.getBranch(),
                        "Validation Test " + str);
            validationTests.add(newArt);
            validationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persist(transaction);
         }
         Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

         // Create Integration Test Procedure Artifacts
         Set<Artifact> integrationTests = new HashSet<Artifact>();
         Artifact integrationHeader = ArtifactQuery.getArtifactFromTypeAndName("Folder", "Integration Tests", branch);
         if (integrationHeader == null) {
            throw new IllegalStateException("Could not find integration Tests header");
         }
         for (String str : new String[] {"X", "Y", "Z"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, integrationHeader.getBranch(),
                        "integration Test " + str);
            integrationTests.add(newArt);
            integrationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persist(transaction);
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
            artifact.persist(transaction);
         }

      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.SEVERE, ex);
      }
   }

}
