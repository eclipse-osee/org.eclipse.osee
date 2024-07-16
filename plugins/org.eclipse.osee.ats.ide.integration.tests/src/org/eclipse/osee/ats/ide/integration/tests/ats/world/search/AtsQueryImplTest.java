/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.junit.Test;

/**
 * Test Case for {@link AtsQueryImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsQueryImplTest {

   @Test
   public void test() {
      AtsApi atsApi = AtsApiService.get();
      IAtsQueryService queryService = atsApi.getQueryService();

      AtsUser joeSmith = atsApi.getUserService().getUserByUserId("3333");

      // test by type
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      assertEquals(33, query.getResults().size());

      query = queryService.createQuery(WorkItemType.Task);
      assertEquals(55, query.getResults().size());

      // assignee
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(atsApi.getUserService().getUserByUserId("3333"));
      assertEquals(13, query.getResults().size());

      // team
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andTeam(Arrays.asList(30013695L));
      assertEquals(6, query.getResults().size());

      // ai
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId ai = atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.ActionableItem, "SAW Requirements");
      query.andActionableItem(Arrays.asList(ai.getId()));
      assertEquals(6, query.getResults().size());

      // by ids (hijack two workflows from previous search)
      List<Long> ids = new LinkedList<>();
      for (IAtsWorkItem workItem : query.getResults()) {
         ids.add(workItem.getId());
      }
      query = queryService.createQuery(WorkItemType.WorkItem);
      Iterator<Long> iterator = ids.iterator();
      query.andIds(iterator.next(), iterator.next());
      assertEquals(2, query.getResults().size());

      // by state name
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.isOfType(WorkItemType.PeerReview);
      query.andState("Prepare");
      assertEquals(6, query.getResults().size());

      // by state type
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.andStateType(StateType.Working);
      assertEquals(102, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      assertEquals(29, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed);
      assertEquals(4, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed, StateType.Working);
      assertEquals(33, query.getResults().size());

      // by version
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId version = atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.Version, "SAW_Bld_2");
      query.andVersion(version.getId());
      assertEquals(20, query.getResults().size());

      // by assignee
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(joeSmith);
      assertEquals(13, query.getResults().size());

      // by originator
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andOriginator(joeSmith);
      assertEquals(33, query.getResults().size());

      // by favorite
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andFavorite(joeSmith);
      assertEquals(3, query.getResults().size());

      // by subscribed
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andSubscribed(joeSmith);
      assertEquals(1, query.getResults().size());

      // setup code workflow and task to have a work package

      IAtsProgramService programService = atsApi.getProgramService();

      IAtsWorkPackage wp =
         atsApi.getProgramService().getWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId()); // Work Pkg 01
      IAtsInsertionActivity activity = programService.getInsertionActivity(wp); // COMM Page
      if (activity == null) {
         throw new RuntimeException("activity is null");
      }
      IAtsInsertion insertion = programService.getInsertion(activity); // COMM
      IAtsProgram program = programService.getProgram(insertion); // SAW Program

      IAtsTeamWorkflow codeWf =
         AtsApiService.get().getWorkItemService().getTeamWf(DemoTestUtil.getCommittedActionWorkflow(DemoWorkType.Code));
      IAtsTask codeTask = (IAtsTask) AtsApiService.get().getQueryService().createQuery(WorkItemType.Task).andAttr(
         CoreAttributeTypes.Name, "Create test plan").getItems().iterator().next();

      Conditions.checkNotNull(codeWf, "Code Team Workflow");
      Conditions.checkNotNull(codeTask, "Code Team Workflow");

      atsApi.getProgramService().setWorkPackage(wp, Arrays.asList(codeWf, codeTask), AtsCoreUsers.SYSTEM_USER);

      // by program
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andProgram(program.getId());
      assertEquals(3, query.getResults().size());

      query = queryService.createQuery(WorkItemType.Task);
      query.andProgram(program.getId());
      assertEquals(1, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andProgram(program.getId());
      assertEquals(3, query.getResults().size());

      // by insertion
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertion(insertion.getId());
      assertEquals(3, query.getResults().size());

      // by insertion activity
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertionActivity(activity.getId());
      assertEquals(2, query.getResults().size());

      // by work package
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andWorkPackage(wp.getId());
      assertEquals(2, query.getResults().size());
   }

   @Test
   public void testWorkPackage() {
      AtsApiIde client = AtsApiService.get();

      IAtsWorkPackage workPackage =
         (IAtsWorkPackage) client.getQueryService().createQuery(AtsArtifactTypes.WorkPackage).andName(
            "Work Pkg 0A").getConfigObjectResultSet().getAtMostOneOrDefault(IAtsWorkPackage.SENTINEL);
      Conditions.assertNotSentinel(workPackage);
   }
}
