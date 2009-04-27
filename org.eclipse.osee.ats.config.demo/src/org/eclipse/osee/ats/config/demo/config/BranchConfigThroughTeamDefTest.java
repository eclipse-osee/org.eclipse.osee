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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowManager;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchControlled;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchState;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * Run from the ATS Navigator after the DB is configured for either ATS - Dev or Demo
 * 
 * @author Donald G. Dunne
 */
public class BranchConfigThroughTeamDefTest extends XNavigateItemAction {

   public enum TestType {
      BranchViaTeamDef, BranchViaVersions, BranchViaParallelVersions
   }

   public BranchConfigThroughTeamDefTest(XNavigateItem parent) {
      super(parent, "ATS Branch Config Test - TestDb or DemoDb");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      run(true);
   }

   public Branch createRootBranch(String branchName) throws Exception {
      List<String> skynetTypeImport = new ArrayList<String>();
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.OseeTypes_ProgramAndCommon");
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.OseeTypes_ProgramBranch");
      skynetTypeImport.add("org.eclipse.osee.ats.config.demo.OseeTypes_DemoProgram");

      return BranchManager.createRootBranch(null, branchName, branchName, skynetTypeImport, true);
   }

   public void testBranchViaTeamDefinition_cleanup(XResultData rd) throws Exception {
      rd.log("Cleanup from previous run of ATS for team org.branchTest.viaTeamDefs");
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         ActionArtifact aArt =
               (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(ActionArtifact.ARTIFACT_NAME,
                     TestType.BranchViaTeamDef.name() + " Req Changes", AtsPlugin.getAtsBranch());
         for (TeamWorkFlowArtifact teamArt : aArt.getTeamWorkFlowArtifacts()) {
            teamArt.delete(transaction);
         }
         aArt.delete(transaction);
         transaction.execute();
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }

      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         Artifact art =
               ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME,
                     TestType.BranchViaTeamDef.name(), AtsPlugin.getAtsBranch());
         art.delete(transaction);
         art =
               ArtifactQuery.getArtifactFromTypeAndName(ActionableItemArtifact.ARTIFACT_NAME,
                     TestType.BranchViaTeamDef.name(), AtsPlugin.getAtsBranch());
         for (Artifact childArt : art.getChildren()) {
            childArt.delete(transaction);
         }
         art.delete(transaction);
         transaction.execute();
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }

      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      for (Artifact workArt : ArtifactQuery.getArtifactsFromType(WorkPageDefinition.ARTIFACT_NAME,
            AtsPlugin.getAtsBranch())) {
         if (workArt.getDescriptiveName().startsWith("org.branchTest.viaTeamDefs")) {
            workArt.delete(transaction);
         }
      }
      for (Artifact workArt : ArtifactQuery.getArtifactsFromType(WorkFlowDefinition.ARTIFACT_NAME,
            AtsPlugin.getAtsBranch())) {
         if (workArt.getDescriptiveName().startsWith("org.branchTest.viaTeamDefs")) {
            workArt.delete(transaction);
         }
      }
      transaction.execute();

      try {
         // delete working branches
         for (Branch workingBranch : BranchManager.getBranches(BranchState.ALL, BranchControlled.ALL,
               BranchType.WORKING)) {
            if (workingBranch.getBranchName().contains(TestType.BranchViaTeamDef.name())) {
               BranchManager.deleteBranch(workingBranch);
               DemoDbUtil.sleep(2000);
            }
         }
         // delete baseline branch
         Branch branch = BranchManager.getKeyedBranch(TestType.BranchViaTeamDef.name());
         if (branch != null) {
            BranchManager.deleteBranch(branch);
            DemoDbUtil.sleep(2000);
         }

      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   public void testBranchViaTeamDefinition(XResultData rd) throws Exception {
      rd.log("Running testBranchViaTeamDefinition...");

      // Cleanup from previous run
      testBranchViaTeamDefinition_cleanup(rd);

      rd.log("Configuring ATS for team org.branchTest.viaTeamDefs");
      // create team definition and actionable item
      AtsConfig.configureAtsForDefaultTeam("org.branchTest.viaTeamDefs", TestType.BranchViaTeamDef.name(), null,
            Arrays.asList(TestType.BranchViaTeamDef.name() + "- A1", TestType.BranchViaTeamDef.name() + "- A2"),
            "org.branchTest.viaTeamDefs");

      DemoDbUtil.sleep(2000);

      // create main branch
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,
            "Creating " + TestType.BranchViaTeamDef.name() + " branch...");
      rd.log("Creating root branch");
      // Create SAW_Bld_2 branch off SAW_Bld_1
      Branch viaTeamDefBranch = createRootBranch(TestType.BranchViaTeamDef.name());

      DemoDbUtil.sleep(2000);

      // configure team def to use branch
      rd.log("Configuring team def to use branch and allow create/commit");
      TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME,
                  TestType.BranchViaTeamDef.name(), AtsPlugin.getAtsBranch());
      teamDef.setSoleAttributeFromString(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(),
            viaTeamDefBranch.getBranchId() + "");
      // setup team def to allow create/commit of branch
      teamDef.setSoleAttributeValue(ATSAttributes.ALLOW_COMMIT_BRANCH.getStoreName(), true);
      teamDef.setSoleAttributeValue(ATSAttributes.ALLOW_CREATE_BRANCH.getStoreName(), true);
      teamDef.setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), false);
      teamDef.persistAttributes();

      DemoDbUtil.sleep(2000);

      // setup workflow page to have create/commit branch widgets
      rd.log("Setup new workflow page to have create/commit branch widgets");
      String implementPageId = "org.branchTest.viaTeamDefs.Implement";
      Artifact implementPageDef = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(implementPageId);
      implementPageDef.addRelation(AtsRelation.WorkItem__Child,
            WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(ATSAttributes.WORKING_BRANCH_WIDGET.getStoreName()));
      implementPageDef.addRelation(AtsRelation.WorkItem__Child,
            WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(ATSAttributes.COMMIT_MANAGER_WIDGET.getStoreName()));
      implementPageDef.persistAttributesAndRelations();

      // create action, 
      rd.log("Create new Action");
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      ActionArtifact actionArt =
            NewActionJob.createAction(null, TestType.BranchViaTeamDef.name() + " Req Changes", "description",
                  ChangeType.Problem, PriorityType.Priority_1, Arrays.asList("Other"), false, null,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(TestType.BranchViaTeamDef.name() + "- A1")),
                  transaction);
      transaction.execute();

      TeamWorkFlowArtifact teamWf = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      TeamWorkflowManager dtwm = new TeamWorkflowManager(teamWf);

      // Transition to desired state
      rd.log("Transitioning to Implement state");
      transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      dtwm.transitionTo(DefaultTeamState.Implement, null, false, transaction);
      teamWf.persistAttributesAndRelations(transaction);
      transaction.execute();

      // Verify XWorkingBranch and XCommitManger widgets exist in editor
      rd.logError("TTD: Verify XWorkingBranch and XCommitManger widgets exist in editor");

      // create branch
      rd.log("Creating working branch");
      Result result = teamWf.getSmaMgr().getBranchMgr().createWorkingBranch(implementPageId, false);
      if (result.isFalse()) {
         result.popup();
         return;
      }
      DemoDbUtil.sleep(2000);

      // make changes
      rd.log("Make new requirement artifact");
      Artifact rootArtifact =
            ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(
                  teamWf.getSmaMgr().getBranchMgr().getWorkingBranch(), true);
      Artifact blk3MainArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT,
                  teamWf.getSmaMgr().getBranchMgr().getWorkingBranch(),
                  TestType.BranchViaTeamDef.name() + " Requirement");
      rootArtifact.addChild(blk3MainArt);
      blk3MainArt.persistAttributesAndRelations();

      // commit branch
      rd.log("Commit Branch");
      teamWf.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true,
            teamWf.getSmaMgr().getBranchMgr().getWorkingBranch().getParentBranch(), true);
      DemoDbUtil.sleep(2000);

      // test change report
      rd.logError("TTD: Test change report results");

   }

   public void run(boolean prompt) throws Exception {
      AtsPlugin.setEmailEnabled(false);
      if (AtsPlugin.isProductionDb()) throw new IllegalStateException(
            "BranchConfigThroughTeamDefTest should not be run on production DB");
      if (SkynetDbInit.isDbInit() || (!SkynetDbInit.isDbInit() && (!prompt || (prompt && MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(), getName(), getName()))))) {

         XResultData rd = new XResultData();
         rd.log(getName() + "\n\n");
         try {
            testBranchViaTeamDefinition(rd);
         } catch (Exception ex) {
            rd.logError("Exception: " + ex.getLocalizedMessage());
         }
         rd.report(getName());
      }
   }

}
