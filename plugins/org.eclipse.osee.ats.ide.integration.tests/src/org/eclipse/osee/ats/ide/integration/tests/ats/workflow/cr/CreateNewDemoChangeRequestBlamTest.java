/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.cr;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.workflow.cr.CreateNewDemoChangeRequestBlam;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.cr.CreateNewChangeRequestTestUtility;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.junit.Assert;
import org.junit.Before;

/**
 * Test for CreateNewDemoChangeRequestBlam. This test will launch the Demo Change Request Blam,
 * handlePopulateWithDebugInfo, run the BLAM and then check that the change request workflow was created.
 *
 * @author Donald G. Dunne
 */
public class CreateNewDemoChangeRequestBlamTest {

   AtsApi atsApi;
   public static String TITLE = "New CR - CreateNewDemoChangeRequestTest";

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      BlamEditor.closeAll();
   }

   @org.junit.Test
   public void testCreate() throws InterruptedException {

      CreateNewDemoChangeRequestBlam blam = new CreateNewDemoChangeRequestBlam();

      ActionResult actionResult = CreateNewChangeRequestTestUtility.testCreate(blam, TITLE);
      Assert.assertTrue(actionResult.getResults().isSuccess());

      // Use "explicit wait" technique to reduce erroneous and unpredictable test failure

      int timeoutSeconds = 3;
      int pollingIntervalMilliseconds = 100;
      int elapsedTime = 0;

      ArtifactToken artifactByName =
         atsApi.getQueryService().getArtifactByName(DemoArtifactTypes.DemoChangeRequestTeamWorkflow, TITLE);

      while (elapsedTime < (timeoutSeconds * 1000) && artifactByName == null) {
         Thread.sleep(pollingIntervalMilliseconds);
         elapsedTime += pollingIntervalMilliseconds;

         artifactByName =
            atsApi.getQueryService().getArtifactByName(DemoArtifactTypes.DemoChangeRequestTeamWorkflow, TITLE);
      }

      System.out.println("Waited for " + pollingIntervalMilliseconds + " milliseconds...");
      Assert.assertNotNull(artifactByName);

      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(artifactByName);
      Assert.assertEquals("CR1001", teamWf.getAtsId());
      Assert.assertEquals(TITLE, teamWf.getName());
      Assert.assertEquals(ChangeTypes.Problem.name(),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, ""));
      Assert.assertEquals("3",
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""));
      Assert.assertNotNull(atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.NeedBy, ""));
      Assert.assertEquals(true,
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.CrashOrBlankDisplay, null));

   }

}
