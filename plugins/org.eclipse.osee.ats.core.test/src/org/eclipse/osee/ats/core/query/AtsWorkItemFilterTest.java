/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.config.TeamDefinition;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemFilterTest {

   // @formatter:off
   @Mock IAtsTeamWorkflow teamWf1;

   @Mock IAtsStateManager teamWf1StateMgr;
   @Mock IAtsAction action1;

   @Mock IAtsTeamWorkflow teamWf2;
   @Mock IAtsStateManager teamWf2StateMgr;
   @Mock IAtsAction action2;

   @Mock IAtsTask task1;
   @Mock AtsApi atsApi;
   @Mock IAtsStateManager task1StateMgr;

   @Mock IArtifactResolver artifactResolver;
   @Mock IAttributeResolver attributeResolver;
   // @formatter:on

   private final IAtsTeamDefinition teamDef1 = new TeamDefinition(null, atsApi, CoreArtifactTokens.UserGroups);
   private final IAtsTeamDefinition teamDef2 = new TeamDefinition(null, atsApi, CoreArtifactTokens.Everyone);

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(teamWf1.getTeamDefinition()).thenReturn(teamDef1);
      when(teamWf1.getParentTeamWorkflow()).thenReturn(teamWf1);

      when(teamWf2.getTeamDefinition()).thenReturn(teamDef2);
      when(teamWf2.getParentTeamWorkflow()).thenReturn(teamWf2);

      when(task1.getParentTeamWorkflow()).thenReturn(teamWf1);

   }

   @Test
   public void testIsOfType() {
      when(atsApi.getArtifactResolver()).thenReturn(artifactResolver);
      when(artifactResolver.isOfType(teamWf1, AtsArtifactTypes.TeamWorkflow)).thenReturn(true);
      when(artifactResolver.isOfType(teamWf1, AtsArtifactTypes.Task)).thenReturn(false);
      when(artifactResolver.isOfType(teamWf2, AtsArtifactTypes.TeamWorkflow)).thenReturn(true);
      when(artifactResolver.isOfType(teamWf2, AtsArtifactTypes.Task)).thenReturn(false);
      when(artifactResolver.isOfType(task1, AtsArtifactTypes.Task)).thenReturn(true);
      when(artifactResolver.isOfType(task1, AtsArtifactTypes.TeamWorkflow)).thenReturn(false);

      AtsWorkItemFilter filter = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);

      Collection<IAtsTask> tasks = filter.isOfType(AtsArtifactTypes.Task).getItems();
      Assert.assertEquals(1, tasks.size());
      Assert.assertEquals(task1, tasks.iterator().next());

      filter = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);

      Collection<IAtsWorkItem> workItems =
         filter.isOfType(AtsArtifactTypes.Task, AtsArtifactTypes.TeamWorkflow).getItems();
      Assert.assertEquals(3, workItems.size());
   }

   @Test
   public void testUnion() {
      AtsWorkItemFilter filter1 = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2), atsApi);
      AtsWorkItemFilter filter2 = new AtsWorkItemFilter(Arrays.asList(teamWf2, task1), atsApi);
      filter1.union(filter2);
      Assert.assertEquals(3, filter1.getItems().size());
   }

   @Test
   public void testFromTeam() {
      AtsWorkItemFilter filter = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      filter.fromTeam(teamDef1);
      Assert.assertEquals(2, filter.getItems().size());
   }

   @Test
   public void testIsStateType() {
      when(teamWf1.getStateMgr()).thenReturn(teamWf1StateMgr);
      when(teamWf1StateMgr.getStateType()).thenReturn(StateType.Completed);

      when(teamWf2.getStateMgr()).thenReturn(teamWf2StateMgr);
      when(teamWf2StateMgr.getStateType()).thenReturn(StateType.Cancelled);

      when(task1.getStateMgr()).thenReturn(task1StateMgr);
      when(task1StateMgr.getStateType()).thenReturn(StateType.Working);

      AtsWorkItemFilter filter1 = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      filter1.isStateType(StateType.Completed);
      Assert.assertEquals(1, filter1.getItems().size());

      AtsWorkItemFilter filter2 = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      filter2.isStateType(StateType.Completed, StateType.Cancelled);
      Assert.assertEquals(2, filter2.getItems().size());

      AtsWorkItemFilter filter3 = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      filter3.isStateType(StateType.Completed, StateType.Cancelled, StateType.Working);
      Assert.assertEquals(3, filter3.getItems().size());
   }

   @Test
   public void testWithOrValue() {
      when(atsApi.getAttributeResolver()).thenReturn(attributeResolver);
      when(attributeResolver.getAttributeValues(teamWf1, AtsAttributeTypes.Category1)).thenReturn(
         Arrays.asList("asdf", "green"));
      when(attributeResolver.getAttributeValues(teamWf2, AtsAttributeTypes.Category1)).thenReturn(
         Arrays.asList("asdf", "blue"));

      AtsWorkItemFilter filter = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      filter.withOrValue(AtsAttributeTypes.Category1, Collections.singleton("asdf"));
      Assert.assertEquals(2, filter.getItems().size());

      filter = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      filter.withOrValue(AtsAttributeTypes.Category1, Collections.singleton("blue"));
      Assert.assertEquals(1, filter.getItems().size());

      filter = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      filter.withOrValue(AtsAttributeTypes.Category1, Arrays.asList("green", "blue"));
      Assert.assertEquals(2, filter.getItems().size());

   }

   @Test
   public void testGetActions() {
      when(teamWf1.getParentAction()).thenReturn(action1);
      when(teamWf2.getParentAction()).thenReturn(action2);
      when(task1.getParentAction()).thenReturn(action1);

      AtsWorkItemFilter filter2 = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      Assert.assertEquals(2, filter2.getActions().size());
   }

   @Test
   public void testGetTeamWorkflows() {
      AtsWorkItemFilter filter2 = new AtsWorkItemFilter(Arrays.asList(teamWf1, teamWf2, task1), atsApi);
      Assert.assertEquals(2, filter2.getTeamWorkflows().size());
   }

}
