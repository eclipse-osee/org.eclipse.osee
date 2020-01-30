/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class WorkflowManagerCoreTest {

   // @formatter:off
   @Mock private IAtsTeamWorkflow teamWf;
   @Mock private IAtsAbstractReview review;
   @Mock private IAtsTeamDefinition teamDef;
   @Mock private IAtsStateDefinition analyzeState, implementState;
   @Mock private IAtsTask task;
   @Mock private IAtsUser Joe, Mary;
   @Mock private AtsApi atsApi;
   @Mock private IAtsTeamDefinitionService teamDefinitionService;
   // @formatter:on
   List<IAtsUser> assignees = new ArrayList<>();

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(teamWf.getTeamDefinition()).thenReturn(teamDef);
      when(review.getParentTeamWorkflow()).thenReturn(teamWf);
      when(teamWf.getStateDefinition()).thenReturn(analyzeState);
      when(analyzeState.getName()).thenReturn("analyze");
      when(implementState.getName()).thenReturn("implement");
      when(teamWf.getAssignees()).thenReturn(assignees);
      when(atsApi.getTeamDefinitionService()).thenReturn(teamDefinitionService);
      when(teamDef.getAtsApi()).thenReturn(atsApi);
   }

   @Test
   public void testTeamDefHasRule() {
      WorkflowManagerCore wmc = new WorkflowManagerCore();
      RuleDefinitionOption option = RuleDefinitionOption.AllowEditToAll;

      Assert.assertFalse(wmc.teamDefHasRule(teamWf, option));
      Assert.assertFalse(wmc.teamDefHasRule(task, option));
      Assert.assertFalse(wmc.teamDefHasRule(review, option));

      when(teamDefinitionService.hasRule(teamDef, RuleDefinitionOption.AllowEditToAll.name())).thenReturn(true);

      Assert.assertTrue(wmc.teamDefHasRule(teamWf, option));
      Assert.assertTrue(wmc.teamDefHasRule(review, option));
      Assert.assertFalse(wmc.teamDefHasRule(task, option));

      when(review.getParentTeamWorkflow()).thenReturn(null);
      Assert.assertFalse(wmc.teamDefHasRule(review, option));
   }

   @Test
   public void testIsWorkItemEditable() {
      WorkflowManagerCore wmc = new WorkflowManagerCore();

      // current state equals state
      Assert.assertFalse(wmc.isWorkItemEditable(teamWf, null, Mary, false));
      Assert.assertFalse(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));
      Assert.assertTrue(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, true));
      Assert.assertFalse(wmc.isWorkItemEditable(teamWf, implementState, Mary, true));

      // assignee is current user
      assignees.add(Mary);
      Assert.assertTrue(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));
      assignees.add(Joe);
      Assert.assertTrue(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));
      assignees.remove(Mary);
      Assert.assertFalse(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));

      // isAtsAdmin
      assignees.clear();
      Assert.assertTrue(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, true));

      // state has rule
      assignees.clear();
      when(analyzeState.hasRule(RuleDefinitionOption.AllowEditToAll.name())).thenReturn(false);
      Assert.assertFalse(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));
      when(analyzeState.hasRule(RuleDefinitionOption.AllowEditToAll.name())).thenReturn(true);
      Assert.assertTrue(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));
      when(analyzeState.hasRule(RuleDefinitionOption.AllowEditToAll.name())).thenReturn(false);

      // teamDef has rule
      when(teamDefinitionService.hasRule(teamDef, RuleDefinitionOption.AllowEditToAll.name())).thenReturn(true);
      Assert.assertTrue(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));
      when(teamDefinitionService.hasRule(teamDef, RuleDefinitionOption.AllowEditToAll.name())).thenReturn(false);
      Assert.assertFalse(wmc.isWorkItemEditable(teamWf, analyzeState, Mary, false));

      // statics
      assignees.add(Mary);
      Assert.assertTrue(WorkflowManagerCore.isEditable(teamWf, analyzeState, Mary, false));
   }
}
