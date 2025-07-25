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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.access.IAccessControlService;
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

         Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI,
            DemoArtifactToken.SAW_Code_AI, DemoArtifactToken.SAW_Test_AI);

         AtsApi atsApi = AtsApiService.get();
         NewActionData data = atsApi.getActionService() //
            .createActionData(FrameworkAccessTestUtil.class.getSimpleName(),
               DemoArtifactToken.SAW_Access_Control_Req_TeamWf.getName(), "description") //
            .andAis(aias).andChangeType(ChangeTypes.Improvement).andPriority("1").andVersion(
               DemoArtifactToken.SAW_Bld_2);
         NewActionData newActionData = atsApi.getActionService().createAction(data);
         Assert.assertTrue(newActionData.getRd().toString(), newActionData.getRd().isSuccess());

         Assert.assertEquals(3, newActionData.getActResult().getAtsTeamWfs().size());
         for (IAtsTeamWorkflow teamWf : newActionData.getActResult().getAtsTeamWfs()) {
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
         Result result = AtsApiService.get().getBranchService().createWorkingBranchValidate(reqTeamWf);
         Assert.assertTrue(result.toString(), result.isTrue());

         AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(reqTeamWf, true);

         reqWorkBrch = atsApi.getBranchService().getWorkingBranch(reqTeamWf, true);
         Assert.assertTrue(reqWorkBrch.isValid());

         // Create Test working branch
         result = AtsApiService.get().getBranchService().createWorkingBranchValidate(testTeamWf);
         Assert.assertTrue(result.toString(), result.isTrue());

         AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(testTeamWf, true);

         testWorkBrch = atsApi.getBranchService().getWorkingBranch(testTeamWf, true);
         Assert.assertTrue(testWorkBrch.isValid());
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