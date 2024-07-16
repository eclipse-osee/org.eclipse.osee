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
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
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

   private static TeamWorkFlowArtifact teamArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsApiService.get().getStoreService().isProductionDb());

      if (teamArt == null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
         teamArt = (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(getClass().getSimpleName(),
            changes).getStoreObject();
         changes.execute();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      // Test adds the atsForceAssigneesToTeamLeads; remove it before and after test
      if (teamArt != null) {
         // IAtsStateDefinition authStateDef = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getName());
         // authStateDef.removeRule(RuleDefinitionOption.ForceAssigneesToTeamLeads.name());
      }

      AtsTestUtil.cleanupSimpleTest(AtsForceAssigneesToTeamLeadsStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioned() {
      Assert.assertNotNull(teamArt);

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getUserByName(JOE_SMITH),
         teamArt.getStateMgr().getAssignees().iterator().next());

      // set assignee to Alex Kay
      teamArt.getStateMgr().setAssignee(AtsApiService.get().getUserService().getUserByName(ALEX_KAY));
      teamArt.persist(getClass().getSimpleName());
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getUserByName(ALEX_KAY),
         teamArt.getStateMgr().getAssignees().iterator().next());

      IStateToken fromState = teamArt.getWorkDefinition().getStateByName(TeamState.Analyze.getName());
      IStateToken toState = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getName());

      IAtsStateDefinition authStateDef = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getName());
      authStateDef.getRules().add(RuleDefinitionOption.ForceAssigneesToTeamLeads.name());

      // make call to state item that should set options based on artifact's attribute value
      AtsForceAssigneesToTeamLeadsWorkItemHook stateItem = new AtsForceAssigneesToTeamLeadsWorkItemHook();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      stateItem.transitioned(teamArt, fromState, toState,
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()),
         AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getUserByName(JOE_SMITH),
         teamArt.getStateMgr().getAssignees().iterator().next());
   }
}
