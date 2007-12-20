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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.config.AtsConfigDemoDatabaseConfig.SawBuilds;
import org.eclipse.osee.ats.config.demo.util.Cscis;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.ProgramAttributes;
import org.eclipse.osee.ats.config.demo.util.Subsystems;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.DefaultTeamWorkflowManager;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch.SearchOperator;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportJob;
import org.eclipse.osee.framework.ui.skynet.Import.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.WordOutlineExtractor;
import org.eclipse.osee.framework.ui.skynet.handler.GeneralWordOutlineHandler;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.UserCommunity;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
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
   private enum DemoAIs {
      Computers,
      Network,
      Config_Mgmt,
      Reviews,
      Timesheet,
      Website,
      Reader,
      CIS_Code,
      CIS_Test,
      CIS_Requirements,
      CIS_SW_Design,
      SAW_Code,
      SAW_Test,
      SAW_Requirements,
      SAW_SW_Design,
      Adapter;
      public String getAIName() {
         return name().replaceAll("_", " ");
      }
   }

   public PopulateDemoActions(XNavigateItem parent) {
      super(parent, "Populate Demo Actions");
   }

   @Override
   public void run() throws SQLException {
      if (SkynetDbInit.isDbInit() || (!SkynetDbInit.isDbInit() && MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(), getName(), getName()))) {
         PopulateTx populateTx =
               new PopulateTx(BranchPersistenceManager.getInstance().getAtsBranch(), !SkynetDbInit.isDbInit());
         try {
            populateTx.execute();
         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         }
      }
   }

   public class PopulateTx extends AbstractSkynetTxTemplate {
      int total = (getNonReqSawActionData().size() * 3) + getGenericActionData().size();
      int currNum = 1;

      public PopulateTx(Branch branch, boolean popup) {
         super(branch);
      }

      @Override
      protected void handleTxWork() throws Exception {
         populateDemoActions(false);
      }

      public void populateDemoActions(boolean popup) {
         try {
            AtsPlugin.setEmailEnabled(false);

            importSoftwareRequirementsToSawBld1();
            //
            // Create SAW_Bld_2 branch off SAW_Bld_1
            createChildMainWorkingBranch(SawBuilds.SAW_Bld_1.name(), SawBuilds.SAW_Bld_2.name());

            //            // Map team definitions versions to their related branches
            AtsConfigDemoDatabaseConfig.mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW),
                  SawBuilds.SAW_Bld_2.name(), SawBuilds.SAW_Bld_2.name());
            //
            //            // Sleep to wait for the persist of the version artifact
            Thread.sleep(3000);
            //            
            //            // Create SawBld2 actions
            createSawBld2ReqChangeDemoActions();

            // importOtherRequirements();
            // createNonReqChangeDemoActions();

            if (popup) AWorkbench.popup(getName(), getName() + " Completed");
         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         } finally {
            AtsPlugin.setEmailEnabled(true);
         }
      }

      private Branch createChildMainWorkingBranch(String parentBrachName, String childBranchName) throws Exception {
         Branch parentBranch = BranchPersistenceManager.getInstance().getKeyedBranch(parentBrachName);

         Branch childBranch =
               BranchPersistenceManager.getInstance().createWorkingBranch(
                     TransactionIdManager.getInstance().getEditableTransactionId(parentBranch), childBranchName,
                     childBranchName, SkynetAuthentication.getInstance().getUser(UserEnum.NoOne));
         return childBranch;
      }

      private void createSawBld2ReqChangeDemoActions() throws Exception {
         Set<ActionArtifact> actionArts =
               createActions(getReqSawActionsData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(), null);

         // Sleep to wait for the persist of the actions
         Thread.sleep(3000);

         Iterator<ActionArtifact> iter = actionArts.iterator();
         makeAction1ReqChanges(iter.next());
         makeAction2ReqChanges(iter.next());
      }

      private void makeAction1ReqChanges(ActionArtifact actionArt) throws Exception {
         TeamWorkFlowArtifact reqTeam = null;
         for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
            if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
         }
         if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");

         // Create branch; pend to wait for completion
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating working branch", false);
         Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
         if (result.isFalse()) throw new IllegalArgumentException("Error creating working branch: " + result.getText());
         synchronized (reqTeam.getSmaMgr().getBranchMgr()) {
            reqTeam.getSmaMgr().getBranchMgr().wait();
         }

         // Set Default Branch
         Branch branch = BranchPersistenceManager.getInstance().getBranch(SawBuilds.SAW_Bld_2.name());
         BranchPersistenceManager.getInstance().setDefaultBranch(branch);

         // Make artifact changes
         for (Artifact art : getRobotSoftwareRequirements()) {
            OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Modifying artifact => " + art, false);
            art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Navigation.name());
            art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.name(), "A");
            art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Navigation.name());

            ArtifactTypeNameSearch srch =
                  new ArtifactTypeNameSearch("Component", "Navigation",
                        BranchPersistenceManager.getInstance().getDefaultBranch());
            art.relate(RelationSide.ALLOCATION__COMPONENT, srch.getSingletonArtifactOrException(Artifact.class));
         }

         // Make artifact changes
         for (Artifact art : getEventSoftwareRequirements()) {
            OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Modifying artifact => " + art, false);
            art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
            art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.name(), "D");
            art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());

            ArtifactTypeNameSearch srch =
                  new ArtifactTypeNameSearch("Component", "Robot API",
                        BranchPersistenceManager.getInstance().getDefaultBranch());
            art.relate(RelationSide.ALLOCATION__COMPONENT, srch.getSingletonArtifactOrException(Artifact.class));
         }

         // Commit branch
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Committing branch", false);
         result = reqTeam.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true);
         if (result.isFalse()) throw new IllegalArgumentException(
               "Error committing working branch: " + result.getText());
         synchronized (reqTeam.getSmaMgr().getBranchMgr()) {
            reqTeam.getSmaMgr().getBranchMgr().wait();
         }

         // Complete action
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Completing Action", false);
      }

      private Set<Artifact> getRobotSoftwareRequirements() {
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch("Software Requirement", "Robot", AtsPlugin.getAtsBranch(),
                     SearchOperator.LIKE);
         return srch.getArtifacts(Artifact.class);
      }

      private Set<Artifact> getEventSoftwareRequirements() {
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch("Software Requirement", "Event", AtsPlugin.getAtsBranch(),
                     SearchOperator.LIKE);
         return srch.getArtifacts(Artifact.class);
      }

      private void makeAction2ReqChanges(ActionArtifact actionArt) throws Exception {

      }

      //      private void createNonReqChangeDemoActions() throws Exception {
      //         createActions(getNonReqSawActionData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_3.toString(), null);
      //         createActions(getNonReqSawActionData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(), null);
      //         createActions(getNonReqSawActionData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_1.toString(),
      //               DefaultTeamState.Completed);
      //         createActions(getGenericActionData(), null, null);
      //      }
      //
      //      private void importOtherRequirements() throws Exception {
      //         // Import Requirements
      //         importRequirements(SawBuilds.SAW_Bld_1.name(), "System Requirements", "System Requirement",
      //               "support/SAW-SystemRequirements.xml");
      //         importRequirements(SawBuilds.SAW_Bld_1.name(), "Subsystem Requirements", "Subsystem Requirement",
      //               "support/SAW-SubsystemRequirements.xml");
      //      }

      private void importSoftwareRequirementsToSawBld1() throws Exception {
         // Import Requirements
         importRequirements(SawBuilds.SAW_Bld_1.name(), "Software Requirements", "Software Requirement",
               "support/SAW-SoftwareRequirements.xml");
      }

      private void importRequirements(String buildName, String rootArtifactName, String requirementArtifactName, String filename) throws Exception {
         Branch branch = BranchPersistenceManager.getInstance().getKeyedBranch(buildName);
         ArtifactTypeNameSearch srch = new ArtifactTypeNameSearch("Folder", rootArtifactName, branch);
         Artifact systemReq = srch.getSingletonArtifactOrException(Artifact.class);
         File file = OseeAtsConfigDemoPlugin.getInstance().getPluginFile(filename);
         IArtifactImportResolver artifactResolver = null;
         ArtifactSubtypeDescriptor mainDescriptor =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(requirementArtifactName,
                     branch);
         ArtifactExtractor extractor =
               new WordOutlineExtractor(mainDescriptor, branch, 0, new GeneralWordOutlineHandler());
         Job job = new ArtifactImportJob(file, systemReq, extractor, branch, artifactResolver);
         job.setPriority(Job.LONG);
         job.schedule();
         job.join();
      }

      private Set<ActionArtifact> createActions(Set<ActionData> actionDatas, String versionStr, DefaultTeamState toStateOverride) throws Exception {
         Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
         for (ActionData aData : actionDatas) {
            OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating " + currNum++ + "/" + total, false);
            int x = 0;
            for (String prefixTitle : aData.prefixTitles) {
               ActionArtifact actionArt =
                     NewActionJob.createAction(null, prefixTitle + " " + aData.postFixTitle,
                           TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x], PriorityType.Priority_1,
                           aData.getUserCommunities(), false, null, aData.getActionableItems());
               actionArts.add(actionArt);
               for (TeamWorkFlowArtifact teamWf : actionArt.getTeamWorkFlowArtifacts()) {
                  DefaultTeamWorkflowManager dtwm = new DefaultTeamWorkflowManager(teamWf);
                  dtwm.transitionTo((toStateOverride != null ? toStateOverride : aData.toState), null, false);
                  teamWf.persist(true);
                  if (versionStr != null && !versionStr.equals("")) {
                     VersionArtifact verArt =
                           ((new ArtifactTypeNameSearch(VersionArtifact.ARTIFACT_NAME, versionStr,
                                 BranchPersistenceManager.getInstance().getAtsBranch())).getSingletonArtifactOrException(VersionArtifact.class));
                     teamWf.relate(RelationSide.TeamWorkflowTargetedForVersion_Version, verArt);
                     teamWf.persist(true);
                  }
               }
            }
         }
         return actionArts;
      }

      private Set<ActionData> getReqSawActionsData() {
         Set<ActionData> actionDatas = new HashSet<ActionData>();
         actionDatas.add(new ActionData(new String[] {"Req Changes for"}, "Diagram View", PriorityType.Priority_1,
               new String[] {DemoAIs.SAW_Requirements.getAIName(), DemoAIs.SAW_Code.getAIName(),
                     DemoAIs.SAW_Test.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement, false, false, false));
         actionDatas.add(new ActionData(new String[] {"More Req Changes for"}, "Diagram View", PriorityType.Priority_3,
               new String[] {DemoAIs.SAW_Code.getAIName(), DemoAIs.SAW_SW_Design.getAIName(),
                     DemoAIs.SAW_Requirements.getAIName(), DemoAIs.SAW_Test.getAIName()}, new Integer[] {1},
               DefaultTeamState.Implement, false, false, false));
         return actionDatas;
      }

      private Set<ActionData> getNonReqSawActionData() {
         Set<ActionData> actionDatas = new HashSet<ActionData>();
         actionDatas.add(new ActionData(new String[] {"Workaround for", "Window problems"}, "Graph View",
               PriorityType.Priority_1, new String[] {DemoAIs.Adapter.getAIName(), DemoAIs.SAW_Code.getAIName()},
               new Integer[] {1}, DefaultTeamState.Implement, false, false, false));
         actionDatas.add(new ActionData(new String[] {"Working with"}, "Diagram Tree", PriorityType.Priority_3,
               new String[] {DemoAIs.SAW_Code.getAIName(), DemoAIs.SAW_SW_Design.getAIName(),
                     DemoAIs.SAW_Requirements.getAIName(), DemoAIs.SAW_Test.getAIName()}, new Integer[] {0, 2},
               DefaultTeamState.Endorse, false, false, false));
         actionDatas.add(new ActionData(new String[] {"Problem with the", "Can't see the", "Button A doesn't work on"},
               "Situation Page", PriorityType.Priority_3, new String[] {DemoAIs.SAW_Code.getAIName(),
                     DemoAIs.SAW_SW_Design.getAIName(), DemoAIs.SAW_Requirements.getAIName(),
                     DemoAIs.SAW_Test.getAIName()}, new Integer[] {1, 4}, DefaultTeamState.Analyze, false, false, false));
         return actionDatas;
      }

      private Set<ActionData> getGenericActionData() {
         Set<ActionData> actionDatas = new HashSet<ActionData>();
         actionDatas.add(new ActionData(new String[] {"Problem with the", "Can't see the", "Button A doesn't work on",
               "Add to the", "Make new Button for ", "User can't load "}, "Graph View", PriorityType.Priority_1,
               new String[] {DemoAIs.Adapter.getAIName(), DemoAIs.CIS_Code.getAIName()}, new Integer[] {1},
               DefaultTeamState.Implement, false, false, false));
         actionDatas.add(new ActionData(new String[] {"Problem in", "Can't load", "Button K doesn't work on",
               "Add more in", "Make same Button for "}, "Diagram Tree", PriorityType.Priority_3, new String[] {
               DemoAIs.CIS_Code.getAIName(), DemoAIs.CIS_SW_Design.getAIName(), DemoAIs.CIS_Requirements.getAIName(),
               DemoAIs.CIS_Test.getAIName()}, new Integer[] {0, 2}, DefaultTeamState.Endorse, false, false, false));
         actionDatas.add(new ActionData(new String[] {"Button W doesn't work on", "Pusing the", "Can't do report"},
               "Situation Page", PriorityType.Priority_3, new String[] {DemoAIs.CIS_Code.getAIName(),
                     DemoAIs.CIS_SW_Design.getAIName(), DemoAIs.CIS_Requirements.getAIName(),
                     DemoAIs.CIS_Test.getAIName()}, new Integer[] {0, 2}, DefaultTeamState.Analyze, false, false, false));
         actionDatas.add(new ActionData(new String[] {"Problem with the"}, "user window", PriorityType.Priority_4,
               new String[] {DemoAIs.Timesheet.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement, false,
               false, false));
         actionDatas.add(new ActionData(new String[] {"Problem with the"}, "dialog", PriorityType.Priority_2,
               new String[] {DemoAIs.Reviews.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement, false, false,
               false));
         actionDatas.add(new ActionData(new String[] {"Problem with the"}, "results", PriorityType.Priority_3,
               new String[] {DemoAIs.Timesheet.getAIName()}, new Integer[] {1}, DefaultTeamState.Analyze, false, false,
               false));
         actionDatas.add(new ActionData(new String[] {"Button S doesn't work on", "Pusing wont cause",
               "Reporting needs"}, "help", PriorityType.Priority_3, new String[] {DemoAIs.Reader.getAIName()},
               new Integer[] {1}, DefaultTeamState.Completed, false, false, false));
         return actionDatas;
      }

   }

   public class ActionData {
      public final String postFixTitle;
      public final PriorityType priority;
      public final String[] actionableItems;
      public final boolean createTasks;
      public final boolean decisionReview;
      public final boolean peerReview;
      public final DefaultTeamState toState;
      public final Integer[] userCommunityIndecies;
      public String[] configuredUserCommunities;
      private final String[] prefixTitles;

      public ActionData(String[] prefixTitles, String postFixTitle, PriorityType priority, String[] actionableItems, Integer[] userCommunityIndecies, DefaultTeamState toState, boolean createTasks, boolean decisionReview, boolean peerReview) {
         this.prefixTitles = prefixTitles;
         this.postFixTitle = postFixTitle;
         this.priority = priority;
         this.actionableItems = actionableItems;
         this.userCommunityIndecies = userCommunityIndecies;
         this.toState = toState;
         this.createTasks = createTasks;
         this.decisionReview = decisionReview;
         this.peerReview = peerReview;
      }

      public Set<String> getUserCommunities() {
         if (configuredUserCommunities == null) {
            configuredUserCommunities =
                  UserCommunity.getInstance().getUserCommunityNames().toArray(
                        new String[UserCommunity.getInstance().getUserCommunityNames().size()]);
         }
         Set<String> userComms = new HashSet<String>();
         for (Integer index : userCommunityIndecies)
            userComms.add(configuredUserCommunities[index]);
         return userComms;
      }

      public Collection<ActionableItemArtifact> getActionableItems() throws SQLException {
         Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
         for (String str : actionableItems) {
            for (ActionableItemArtifact aia : ActionableItemArtifact.getActionableItems()) {
               if (str.equals(aia.getDescriptiveName())) aias.add(aia);
            }
         }
         return aias;
      }
   }
}
