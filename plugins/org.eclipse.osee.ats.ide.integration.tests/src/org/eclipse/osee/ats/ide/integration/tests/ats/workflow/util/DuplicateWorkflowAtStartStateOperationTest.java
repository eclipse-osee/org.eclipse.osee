/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.util;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.util.DuplicateWorkflowAtStartStateOperation;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link DuplicateWorkflowAtStartStateOperation}
 *
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowAtStartStateOperationTest {

   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(DuplicateWorkflowAtStartStateOperationTest.class.getSimpleName());
   }

   @Test
   public void testValidateAndRun() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      String teamWfName = teamWf.getName() + " - first";
      teamWf.setName(teamWfName);
      teamWf.setSoleAttributeFromString(AtsAttributeTypes.Description, "new description");
      teamWf.persist(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      String teamWf2Name = teamWf2.getName() + " - second";
      teamWf2.setName(teamWf2Name);
      teamWf2.persist(getClass().getSimpleName());

      AtsUser user = AtsApiService.get().getUserService().getCurrentUser();

      List<IAtsTeamWorkflow> teamWfs = new LinkedList<>();

      DuplicateWorkflowAtStartStateOperation operation =
         new DuplicateWorkflowAtStartStateOperation(teamWfs, "", null, AtsApiService.get());
      XResultData results = operation.validate();

      Assert.assertEquals(2, results.getNumErrors());
      teamWfs.add(teamWf);
      teamWfs.add(teamWf2);

      results = operation.validate();
      Assert.assertEquals(1, results.getNumErrors());

      operation = new DuplicateWorkflowAtStartStateOperation(teamWfs, "", user, AtsApiService.get());

      results = operation.validate();
      Assert.assertEquals(0, results.getNumErrors());

      results = operation.run();
      Assert.assertEquals(0, results.getNumErrors());

      IAtsAction action = teamWf.getParentAction();
      IAtsTeamWorkflow foundTeamWf = null;
      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(action)) {
         if (team.getName().equals("Copy of " + teamWfName)) {
            foundTeamWf = team;
            break;
         }
      }
      Assert.assertNotNull("New Team Workflow \"first\" NOT Found", foundTeamWf);
      // Description attribute should not be copied
      Assert.assertEquals("description", AtsApiService.get().getAttributeResolver().getSoleAttributeValue(foundTeamWf,
         AtsAttributeTypes.Description, ""));
      Assert.assertFalse(AtsApiService.get().getQueryServiceIde().getArtifact(foundTeamWf).isDirty());

      foundTeamWf = null;
      action = teamWf2.getParentAction();
      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(action)) {
         if (team.getName().equals("Copy of " + teamWf2Name)) {
            foundTeamWf = team;
            break;
         }
      }
      Assert.assertNotNull("New Team Workflow \"second\" NOT Found", foundTeamWf);
      Assert.assertEquals("description", AtsApiService.get().getAttributeResolver().getSoleAttributeValue(foundTeamWf,
         AtsAttributeTypes.Description, ""));
      Assert.assertFalse(AtsApiService.get().getQueryServiceIde().getArtifact(foundTeamWf).isDirty());

   }

}
