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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AtsQueryImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsQueryImplTest {

   private AtsApi atsApi;
   private IAtsQueryService queryService;
   private AtsUser joeSmith;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      queryService = atsApi.getQueryService();
      joeSmith = atsApi.getUserService().getUserByUserId("3333");
   }

   @Test
   public void testByAssignee() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(joeSmith);
      assertEquals(8, query.getResults().size());
   }

   @Test
   public void testByUserId() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(atsApi.getUserService().getUserByUserId("3333"));
      assertEquals(8, query.getResults().size());
   }

   @Test
   public void testByOriginator() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andOriginator(joeSmith);
      assertEquals(26, query.getResults().size());
   }

   @Test
   public void testByFavorite() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andFavorite(joeSmith);
      assertEquals(3, query.getResults().size());
   }

   @Test
   public void testBySubscribed() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andSubscribed(joeSmith);
      assertEquals(1, query.getResults().size());
   }

   @Test
   public void testByTeamWf() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      assertEquals(26, query.getResults().size());
   }

   @Test
   public void testByTask() {
      IAtsQuery query = queryService.createQuery(WorkItemType.Task);
      assertEquals(14, query.getResults().size());
   }

   @Test
   public void testByTeamId() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andTeam(Arrays.asList(30013695L));
      assertEquals(4, query.getResults().size());
   }

   @Test
   public void testByAis() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId ai = atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.ActionableItem, "SAW Requirements");
      query.andActionableItem(Arrays.asList(ai.getId()));
      assertEquals(4, query.getResults().size());

      // by ids (hijack two workflows from previous search)
      List<Long> ids = new LinkedList<>();
      for (IAtsWorkItem workItem : query.getResults()) {
         ids.add(workItem.getId());
      }
      query = queryService.createQuery(WorkItemType.WorkItem);
      Iterator<Long> iterator = ids.iterator();
      query.andIds(iterator.next(), iterator.next());
      assertEquals(2, query.getResults().size());
   }

   @Test
   public void testByStateName() {
      IAtsQuery query = queryService.createQuery(WorkItemType.WorkItem);
      query.isOfType(WorkItemType.PeerReview);
      query.andState("Prepare");
      assertEquals(6, query.getResults().size());

   }

   @Test
   public void testByWorkingWorkItem() {
      IAtsQuery query = queryService.createQuery(WorkItemType.WorkItem);
      query.andStateType(StateType.Working);
      assertEquals(56, query.getResults().size());
   }

   @Test
   public void testByWorkingGoal() {
      IAtsQuery query = queryService.createQuery(WorkItemType.Goal);
      query.andStateType(StateType.Working);
      assertEquals(1, query.getResults().size());
   }

   @Test
   public void testByWorkingTeamWf() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      assertEquals(23, query.getResults().size());
   }

   @Test
   public void testByCompletedTeamWf() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed);
      assertEquals(3, query.getResults().size());
   }

   @Test
   public void testByCompletedWorkingTeamWf() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed, StateType.Working);
      assertEquals(26, query.getResults().size());
   }

   @Test
   public void testByVersion() {
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId version = atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.Version, "SAW_Bld_2");
      query.andVersion(version.getId());
      assertEquals(14, query.getResults().size());
   }

}
