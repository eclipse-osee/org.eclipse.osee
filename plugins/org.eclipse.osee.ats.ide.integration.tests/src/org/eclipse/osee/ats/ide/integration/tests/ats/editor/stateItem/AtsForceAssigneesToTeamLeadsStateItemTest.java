/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.core.workflow.hooks.AtsForceAssigneesToTeamLeadsWorkItemHook;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsForceAssigneesToTeamLeadsWorkItemHook}
 *
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsStateItemTest {

   private static final String JOE_SMITH = DemoUsers.Joe_Smith.getName();
   private static final String ALEX_KAY = "Alex Kay";

   private static TeamWorkFlowArtifact teamWf;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsApiService.get().getStoreService().isProductionDb());

      if (teamWf == null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
         teamWf = (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(getClass().getSimpleName(),
            changes).getStoreObject();
         changes.execute();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      // Test adds the atsForceAssigneesToTeamLeads; remove it before and after test
      if (teamWf != null) {
         // StateDefinition authStateDef = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getName());
         // authStateDef.removeRule(RuleDefinitionOption.ForceAssigneesToTeamLeads.name());
      }

      AtsTestUtil.cleanupSimpleTest(AtsForceAssigneesToTeamLeadsStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioned() {
      Assert.assertNotNull(teamWf);

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamWf.getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getUserByName(JOE_SMITH),
         teamWf.getAssignees().iterator().next());

      // set assignee to Alex Kay
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setAssignee(teamWf, AtsApiService.get().getUserService().getUserByName(ALEX_KAY));
      changes.execute();

      Assert.assertEquals(1, teamWf.getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getUserByName(ALEX_KAY),
         teamWf.getAssignees().iterator().next());

      IStateToken fromState = teamWf.getWorkDefinition().getStateByName(TeamState.Analyze.getName());
      IStateToken toState = teamWf.getWorkDefinition().getStateByName(TeamState.Authorize.getName());

      StateDefinition authStateDef = teamWf.getWorkDefinition().getStateByName(TeamState.Authorize.getName());
      authStateDef.getRules().add(RuleDefinitionOption.ForceAssigneesToTeamLeads.name());

      // make call to state item that should set options based on artifact's attribute value
      AtsForceAssigneesToTeamLeadsWorkItemHook stateItem = new AtsForceAssigneesToTeamLeadsWorkItemHook();
      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      stateItem.transitioned(teamWf, fromState, toState,
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()),
         AtsApiService.get().getUserService().getCurrentUser(), changes, AtsApiService.get());
      changes.execute();

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamWf.getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getUserByName(JOE_SMITH),
         teamWf.getAssignees().iterator().next());
   }
}
