/*
 * Created on May 31, 2016
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.workflow.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.core.workflow.util.DuplicateWorkflowAsIsOperation;
import org.eclipse.osee.ats.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link DuplicateWorkflowAsIsOperation}
 *
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowAsIsOperationTest {

   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(DuplicateWorkflowAsIsOperationTest.class.getSimpleName());
   }

   @Test
   public void testValidateAndRun() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      TaskArtifact taskOfTeamWf = AtsTestUtil.getOrCreateTaskOffTeamWf1();
      String teamWfName = teamWf.getName() + " - first";
      teamWf.setName(teamWfName);
      teamWf.setSoleAttributeFromString(AtsAttributeTypes.Description, "new description");
      teamWf.persist(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      String teamWf2Name = teamWf2.getName() + " - second";
      teamWf2.setName(teamWf2Name);
      teamWf2.persist(getClass().getSimpleName());

      IAtsUser user = AtsClientService.get().getUserService().getCurrentUser();

      List<IAtsTeamWorkflow> teamWfs = new LinkedList<>();

      DuplicateWorkflowAsIsOperation operation =
         new DuplicateWorkflowAsIsOperation(teamWfs, true, "", null, AtsClientService.get());
      XResultData results = operation.validate();

      Assert.assertEquals(2, results.getNumErrors());
      teamWfs.add(teamWf);
      teamWfs.add(teamWf2);

      results = operation.validate();
      Assert.assertEquals(1, results.getNumErrors());

      operation = new DuplicateWorkflowAsIsOperation(teamWfs, true, "", user, AtsClientService.get());

      results = operation.validate();
      Assert.assertEquals(0, results.getNumErrors());

      results = operation.run();
      Assert.assertEquals(0, results.getNumErrors());

      ActionArtifact action = teamWf.getParentActionArtifact();
      IAtsTeamWorkflow foundTeamWf = null;
      for (IAtsTeamWorkflow team : action.getTeams()) {
         if (team.getName().equals("Copy of " + teamWfName)) {
            foundTeamWf = team;
            break;
         }
      }
      Assert.assertNotNull("New Team Workflow \"first\" NOT Found", foundTeamWf);
      // Description attribute SHOULD be copied
      Assert.assertEquals("new description", AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         foundTeamWf, AtsAttributeTypes.Description, ""));
      Assert.assertFalse(((Artifact) foundTeamWf.getStoreObject()).isDirty());
      // Ensure task was duplicated
      Collection<IAtsTask> newTasks = AtsClientService.get().getTaskService().getTasks(foundTeamWf);
      Assert.assertEquals(1, newTasks.size());
      Assert.assertEquals(taskOfTeamWf.getName(), newTasks.iterator().next().getName());

      foundTeamWf = null;
      action = teamWf2.getParentActionArtifact();
      for (IAtsTeamWorkflow team : action.getTeams()) {
         if (team.getName().equals("Copy of " + teamWf2Name)) {
            foundTeamWf = team;
            break;
         }
      }
      Assert.assertNotNull("New Team Workflow \"second\" NOT Found", foundTeamWf);
      Assert.assertEquals("description", AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         foundTeamWf, AtsAttributeTypes.Description, "description"));
      Assert.assertFalse(((Artifact) foundTeamWf.getStoreObject()).isDirty());

   }

}
