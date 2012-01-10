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
package org.eclipse.osee.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsForceAssigneesToTeamLeadsStateItem}
 * 
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsStateItemTest {

   private static final String JOE_SMITH = "Joe Smith";
   private static final String ALEX_KAY = "Alex Kay";

   private static TeamWorkFlowArtifact teamArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsUtil.isProductionDb());

      if (teamArt == null) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getClass().getSimpleName());
         teamArt = DemoTestUtil.createSimpleAction(getClass().getSimpleName(), transaction);
         transaction.execute();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      // Test adds the atsForceAssigneesToTeamLeads; remove it before and after test
      if (teamArt != null) {
         StateDefinition authStateDef = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getPageName());
         List<RuleDefinition> rules = authStateDef.getRules();
         List<RuleDefinition> toRemove = new ArrayList<RuleDefinition>();
         for (RuleDefinition ruleDef : rules) {
            if (ruleDef.getName().equals(RuleDefinitionOption.ForceAssigneesToTeamLeads.name())) {
               toRemove.add(ruleDef);
            }
         }
         authStateDef.getRules().removeAll(toRemove);
      }

      AtsTestUtil.cleanupSimpleTest(AtsForceAssigneesToTeamLeadsStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioned() throws OseeCoreException {
      Assert.assertNotNull(teamArt);

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUserByName(JOE_SMITH), teamArt.getStateMgr().getAssignees().iterator().next());

      // set assignee to Alex Kay
      teamArt.getStateMgr().setAssignee(UserManager.getUserByName(ALEX_KAY));
      teamArt.persist(getClass().getSimpleName());
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUserByName(ALEX_KAY), teamArt.getStateMgr().getAssignees().iterator().next());

      IWorkPage fromState = teamArt.getWorkDefinition().getStateByName(TeamState.Analyze.getPageName());
      IWorkPage toState = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getPageName());

      StateDefinition authStateDef = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getPageName());
      authStateDef.getRules().add(new RuleDefinition(RuleDefinitionOption.ForceAssigneesToTeamLeads));

      // make call to state item that should set options based on artifact's attribute value
      AtsForceAssigneesToTeamLeadsStateItem stateItem = new AtsForceAssigneesToTeamLeadsStateItem();
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getClass().getSimpleName());
      stateItem.transitioned(teamArt, fromState, toState, Arrays.asList((IBasicUser) UserManager.getUser()),
         transaction);
      transaction.execute();

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUserByName(JOE_SMITH), teamArt.getStateMgr().getAssignees().iterator().next());
   }
}
