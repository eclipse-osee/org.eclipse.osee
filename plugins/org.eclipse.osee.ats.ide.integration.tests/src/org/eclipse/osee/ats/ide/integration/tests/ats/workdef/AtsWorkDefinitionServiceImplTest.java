/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workdef;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImplTest {

   @AfterClass
   public static void cleanup() {
      try {
         AtsTestUtil.cleanupSimpleTest(AtsWorkDefinitionServiceImplTest.class.getSimpleName());
      } catch (Exception ex) {
         OseeLog.log(AtsWorkDefinitionServiceImplTest.class, Level.SEVERE, "Exception cleaning test.", ex);
      }
   }

   @Test
   public void teamDefinitionRelatedPeerWorkDefinitionTest() {

      // Test Peer Work Def
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      List<IAtsActionableItem> aias = new LinkedList<IAtsActionableItem>();
      aias.add(
         AtsApiService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_SW_Design_AI));
      ActionResult actionArt = AtsApiService.get().getActionService().createAction(null,
         getClass().getSimpleName() + " relatedPeerTest", "description", ChangeType.Improvement, "3", false, null, aias,
         new Date(), AtsApiService.get().getUserService().getCurrentUser(), null, changes);
      IAtsTeamWorkflow teamWf = actionArt.getFirstTeam();

      IAtsPeerToPeerReview peerReview = AtsApiService.get().getReviewService().createNewPeerToPeerReview(teamWf,
         getClass().getSimpleName() + " - Peer Review", null, changes);

      changes.execute();

      Assert.assertEquals(DemoWorkDefinitions.WorkDef_Review_Demo_Peer_SwDesign.getId(),
         peerReview.getWorkDefinition().getId());

      // Test Task Work Def
      NewTaskSet newTaskSet = NewTaskSet.createWithData(teamWf, getClass().getSimpleName(),
         AtsApiService.get().getUserService().getCurrentUser());
      NewTaskData taskData = newTaskSet.getTaskData();

      JaxAtsTask jTask = new JaxAtsTask();
      jTask.setName(getClass().getSimpleName() + " - My Task");
      jTask.setDescription("description");
      jTask.setCreatedByUserId(AtsApiService.get().getUserService().getCurrentUserId());
      jTask.setCreatedDate(new Date());
      jTask.setTaskWorkDef(DemoWorkDefinitions.WorkDef_Task_Demo_SwDesign.getIdString());
      taskData.getTasks().add(jTask);

      newTaskSet = AtsApiService.get().getServerEndpoints().getTaskEp().create(newTaskSet);
      JaxAtsTask task = newTaskSet.getTaskData().getTasks().iterator().next();
      IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(task.getId());

      Assert.assertTrue(workItem.isTask());
      Assert.assertEquals(DemoWorkDefinitions.WorkDef_Task_Demo_SwDesign.getId(), workItem.getWorkDefinition().getId());
   }

}
