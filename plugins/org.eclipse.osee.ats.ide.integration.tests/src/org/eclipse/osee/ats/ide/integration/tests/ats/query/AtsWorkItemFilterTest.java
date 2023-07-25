/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.query.AtsWorkItemFilter;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemFilterTest {

   private static Collection<IAtsWorkItem> workItems;
   private Collection<IAtsTask> tasks;
   private Collection<IAtsTeamWorkflow> teamWfs;

   @BeforeClass
   public static void setup() {
      workItems =
         AtsApiService.get().getQueryService().createQuery(AtsArtifactTypes.AbstractWorkflowArtifact).getWorkItems();
   }

   public void loadTasksTeamWfs() {
      AtsWorkItemFilter filter = new AtsWorkItemFilter(workItems);
      teamWfs = filter.isOfType(AtsArtifactTypes.TeamWorkflow).getItems();

      AtsWorkItemFilter filter2 = new AtsWorkItemFilter(workItems);
      tasks = filter2.isOfType(AtsArtifactTypes.Task).getItems();
   }

   @Test
   public void testIsOfType() {
      loadTasksTeamWfs();
      Assert.assertEquals(14, tasks.size());
      Assert.assertEquals(26, teamWfs.size());
   }

   @Test
   public void testUnion() {
      loadTasksTeamWfs();
      AtsWorkItemFilter filter1 = new AtsWorkItemFilter(tasks);
      AtsWorkItemFilter filter2 = new AtsWorkItemFilter(teamWfs);
      filter1.union(filter2);
      Assert.assertEquals(40, filter1.getItems().size());
   }

   @Test
   public void testFromTeam() {
      loadTasksTeamWfs();
      AtsWorkItemFilter filter = new AtsWorkItemFilter(teamWfs);
      filter.fromTeam(DemoUtil.getSawCodeCommittedWf().getTeamDefinition());
      Assert.assertEquals(4, filter.getItems().size());
   }

   @Test
   public void testIsStateType() {
      loadTasksTeamWfs();
      AtsWorkItemFilter filter1 = new AtsWorkItemFilter(teamWfs);
      filter1.isStateType(StateType.Completed);
      Assert.assertEquals(3, filter1.getItems().size());

      AtsWorkItemFilter filter2 = new AtsWorkItemFilter(teamWfs);
      filter2.isStateType(StateType.Completed, StateType.Cancelled);
      Assert.assertEquals(3, filter2.getItems().size());

      AtsWorkItemFilter filter3 = new AtsWorkItemFilter(teamWfs);
      filter3.isStateType(StateType.Completed, StateType.Cancelled, StateType.Working);
      Assert.assertEquals(teamWfs.size(), filter3.getItems().size());
   }

   @Test
   public void testWithOrValue() {
      loadTasksTeamWfs();
      AtsWorkItemFilter filter1 = new AtsWorkItemFilter(teamWfs);
      filter1.withOrValue(AtsAttributeTypes.CurrentStateName, Collections.singleton("Endorse"));
      Assert.assertEquals(3, filter1.getItems().size());
   }

   @Test
   public void testGetActions() {
      loadTasksTeamWfs();
      AtsWorkItemFilter filter1 = new AtsWorkItemFilter(teamWfs);
      Assert.assertEquals(18, filter1.getActions().size());
   }

}
