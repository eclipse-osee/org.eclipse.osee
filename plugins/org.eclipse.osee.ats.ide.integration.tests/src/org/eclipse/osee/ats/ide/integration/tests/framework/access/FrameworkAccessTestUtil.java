/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.framework.access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class FrameworkAccessTestUtil {

   public static AtsApi atsApi;
   private static IAtsTeamWorkflow reqTeamWf, codeTeamWf, testTeamWf;
   private static BranchToken reqWorkBrch, testWorkBrch;

   /**
    * Setup workflow and working branch
    */
   public static void ensureLoaded() {
      atsApi = AtsApiService.get();

      if (reqTeamWf == null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("FrameworkAccessByAttrTypeTest");

         Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI,
            DemoArtifactToken.SAW_Code_AI, DemoArtifactToken.SAW_Test_AI);
         Date createdDate = new Date();
         AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
         String priority = "2";

         ActionResult actionResult = AtsApiService.get().getActionService().createAction(null,
            DemoArtifactToken.SAW_Access_Control_Req_TeamWf.getName(), "see title", ChangeTypes.Problem, priority,
            false, null, aias, createdDate, createdBy, Arrays.asList(new ArtifactTokenActionListener()), changes);

         changes.execute();

         Assert.assertTrue(actionResult.getResults().isSuccess());

         Assert.assertEquals(3, actionResult.getTeamWfs().size());
         for (IAtsTeamWorkflow teamWf : actionResult.getTeamWfs()) {
            if (teamWf.getTeamDefinition().equals(DemoArtifactToken.SAW_Requirements.getId())) {
               reqTeamWf = teamWf;
            } else if (teamWf.getTeamDefinition().equals(DemoArtifactToken.SAW_Code.getId())) {
               codeTeamWf = teamWf;
            } else if (teamWf.getTeamDefinition().equals(DemoArtifactToken.SAW_Test.getId())) {
               testTeamWf = teamWf;
            }
         }
         Assert.assertNotNull(reqTeamWf);
         Assert.assertNotNull(codeTeamWf);
         Assert.assertNotNull(testTeamWf);

         // Create Req working branch
         Result result = AtsApiService.get().getBranchServiceIde().createWorkingBranch_Validate(reqTeamWf);
         Assert.assertTrue(result.toString(), result.isTrue());

         AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(reqTeamWf, true);

         reqWorkBrch = atsApi.getBranchService().getWorkingBranch(reqTeamWf, true);
         Assert.assertTrue(reqWorkBrch.isValid());

         // Create Test working branch
         result = AtsApiService.get().getBranchServiceIde().createWorkingBranch_Validate(testTeamWf);
         Assert.assertTrue(result.toString(), result.isTrue());

         AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(testTeamWf, true);

         testWorkBrch = atsApi.getBranchService().getWorkingBranch(testTeamWf, true);
         Assert.assertTrue(testWorkBrch.isValid());
      }

   }

   private static class ArtifactTokenActionListener implements INewActionListener {

      @SuppressWarnings("unlikely-arg-type")
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Test_AI)) {
            return DemoArtifactToken.SAW_Access_Control_Test_TeamWf;
         } else if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Code_AI)) {
            return DemoArtifactToken.SAW_Access_Control_Code_TeamWf;
         } else if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Requirements_AI)) {
            return DemoArtifactToken.SAW_Access_Control_Req_TeamWf;
         }
         throw new UnsupportedOperationException();
      }

      @Override
      public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
         IAtsVersion sawBld2Ver = atsApi.getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
         atsApi.getVersionService().setTargetedVersion(teamWf, sawBld2Ver, changes);

         TeamWorkFlowManager mgr = new TeamWorkFlowManager(teamWf, AtsApiService.get());
         mgr.transitionTo(TeamState.Implement, AtsApiService.get().getUserService().getCurrentUser(), false, changes);

         if (teamWf.getTeamDefinition().equals(DemoArtifactToken.SAW_Requirements.getId())) {
            changes.setName(teamWf, DemoArtifactToken.SAW_Access_Control_Req_TeamWf.getName());
         } else if (teamWf.getTeamDefinition().equals(DemoArtifactToken.SAW_Code.getId())) {
            changes.setName(teamWf, DemoArtifactToken.SAW_Access_Control_Code_TeamWf.getName());
         } else if (teamWf.getTeamDefinition().equals(DemoArtifactToken.SAW_Test.getId())) {
            changes.setName(teamWf, DemoArtifactToken.SAW_Access_Control_Test_TeamWf.getName());
         }
      }
   }

   public static BranchToken getReqWorkBrch() {
      return reqWorkBrch;
   }

   public static BranchToken getTestWorkBrch() {
      return testWorkBrch;
   }

   public static AtsApi getAtsApi() {
      return atsApi;
   }

   public static IAtsTeamWorkflow getReqTeamWf() {
      return reqTeamWf;
   }

   public static IAtsTeamWorkflow getCodeTeamWf() {
      return codeTeamWf;
   }

   public static IAtsTeamWorkflow getTestTeamWf() {
      return testTeamWf;
   }

   public static BranchToken getOrCreateAccessBranch(IAccessControlService accessControlService) {
      BranchToken branch = BranchManager.getBranch(DemoBranches.SAW_PL_Access_Baseline_Test);
      if (branch == null) {
         branch = BranchManager.createBaselineBranch(DemoBranches.SAW_PL, DemoBranches.SAW_PL_Access_Baseline_Test);
      } else {
         accessControlService.removePermissions(branch);
         accessControlService.clearCaches();
      }
      return branch;
   }
}