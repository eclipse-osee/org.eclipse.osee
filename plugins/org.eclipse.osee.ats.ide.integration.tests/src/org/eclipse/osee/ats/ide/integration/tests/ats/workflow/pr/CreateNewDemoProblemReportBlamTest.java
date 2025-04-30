/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.pr;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.pr.PrViewData;
import org.eclipse.osee.ats.ide.demo.workflow.pr.BuildImpactDataSampleDemo;
import org.eclipse.osee.ats.ide.demo.workflow.pr.CreateNewDemoProblemReportBlam;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.cr.CreateNewChangeRequestTestUtility;
import org.eclipse.osee.framework.core.data.ArtifactResultRow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.junit.Assert;
import org.junit.Before;

/**
 * Test for CreateNewDemoProblemReportBlam. This test will launch the Demo Problem Report Blam,
 * handlePopulateWithDebugInfo, run the BLAM and then check that the change request workflow was created.
 *
 * @author Donald G. Dunne
 */
public class CreateNewDemoProblemReportBlamTest {

   AtsApi atsApi;
   public static String TITLE = "New PR - CreateNewAmsProblemReportBlamTest";

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      BlamEditor.closeAll();
   }

   @org.junit.Test
   public void testCreate() {

      CreateNewDemoProblemReportBlam blam = new CreateNewDemoProblemReportBlam();
      blam.setOverrideTitle(TITLE);

      ActionResult actionResult = CreateNewChangeRequestTestUtility.testCreate(blam, TITLE);
      Assert.assertTrue(actionResult.getResults().toString(), actionResult.getResults().isSuccess());

      ArtifactToken artifactByName =
         atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.DemoProblemReportTeamWorkflow, TITLE);
      Assert.assertNotNull(artifactByName);

      IAtsTeamWorkflow prTeamWf = atsApi.getWorkItemService().getTeamWf(artifactByName);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsUser joeUser = AtsApiService.get().getUserService().getUserById(DemoUsers.Joe_Smith);
      changes.addAssignee(prTeamWf, joeUser);
      changes.execute();

      Assert.assertTrue(prTeamWf.getAssignees().contains(joeUser));

      Assert.assertEquals("PR1001", prTeamWf.getAtsId());
      Assert.assertEquals(TITLE, prTeamWf.getName());

      Assert.assertEquals(ChangeTypes.Problem.name(),
         atsApi.getAttributeResolver().getSoleAttributeValue(prTeamWf, AtsAttributeTypes.ChangeType, ""));

      ArtifactToken version =
         atsApi.getRelationResolver().getRelatedOrNull(prTeamWf, AtsRelationTypes.TeamWorkflowToFoundInVersion_Version);
      Assert.assertNotNull(version);

      testCreateBids(prTeamWf);

      testProblemReportView(prTeamWf);
   }

   private void testCreateBids(IAtsTeamWorkflow prTeamWf) {
      BuildImpactDatas debugBids = BuildImpactDataSampleDemo.get();
      debugBids.setBidArtType(AtsArtifactTypes.BuildImpactData);
      debugBids.setTeamWf(prTeamWf.getArtifactToken());
      BuildImpactDatas bids =
         atsApi.getServerEndpoints().getActionEndpoint().updateBids(prTeamWf.getArtifactToken(), debugBids);
      Assert.assertTrue(bids.getResults().isSuccess());

      Assert.assertEquals(3, bids.getBuildImpacts().size());

      boolean foundSbvt1 = false, foundSbvt2 = false, foundSbvt3 = false;
      for (BuildImpactData bid : bids.getBuildImpacts()) {
         if (bid.getName().equals(DemoArtifactToken.SAW_PL_SBVT1.getName())) {
            foundSbvt1 = true;
            Assert.assertEquals(AtsAttributeTypes.BitState.InWork.getName(), bid.getState());
            Assert.assertEquals(3, bid.getTeamWfs().size());
         }
         if (bid.getName().equals(DemoArtifactToken.SAW_PL_SBVT2.getName())) {
            foundSbvt2 = true;
            Assert.assertEquals(AtsAttributeTypes.BitState.InWork.getName(), bid.getState());
            Assert.assertEquals(3, bid.getTeamWfs().size());
         }
         if (bid.getName().equals(DemoArtifactToken.SAW_PL_SBVT3.getName())) {
            foundSbvt3 = true;
            Assert.assertEquals(AtsAttributeTypes.BitState.Open.getName(), bid.getState());
            Assert.assertEquals(0, bid.getTeamWfs().size());
         }
      }
      Assert.assertTrue(foundSbvt1);
      Assert.assertTrue(foundSbvt2);
      Assert.assertTrue(foundSbvt3);
   }

   private void testProblemReportView(IAtsTeamWorkflow prTeamWf) {
      PrViewData prData = new PrViewData();
      prData.addPrWf(prTeamWf.getArtifactToken());
      prData = AtsApiService.get().getServerEndpoints().getPrEp().generatePrView(prData);
      Assert.assertTrue(prData.getRd().isSuccess());
      Assert.assertEquals(1, prData.getArtRows().size());
      ArtifactResultRow prRow = prData.getArtRows().iterator().next();
      Assert.assertEquals(3, prRow.getChildren().size());
      for (ArtifactResultRow bitRow : prRow.getChildren()) {
         if (bitRow.getArtifact().getName().contains("SBVT1")) {
            Assert.assertEquals(3, bitRow.getChildren().size());
         } else if (bitRow.getArtifact().getName().contains("SBVT2")) {
            Assert.assertEquals(3, bitRow.getChildren().size());
         } else if (bitRow.getArtifact().getName().contains("SBVT3")) {
            Assert.assertEquals(0, bitRow.getChildren().size());
         }
      }
   }

}
