/*
 * Created on Jan 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.editor.stateItem.AtsForceAssigneesToTeamLeadsStateItem;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsForceAssigneesToTeamLeadsStateItem}
 * 
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsStateItemTest {

   public static TeamWorkFlowArtifact teamArt;

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

      DemoTestUtil.cleanupSimpleTest(AtsForceAssigneesToTeamLeadsStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioned() throws OseeCoreException {
      Assert.assertNotNull(teamArt);

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUserByName("Joe Smith"),
         teamArt.getStateMgr().getAssignees().iterator().next());

      // set assignee to Alex Kay
      teamArt.getStateMgr().setAssignee(UserManager.getUserByName("Alex Kay"));
      teamArt.persist();
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUserByName("Alex Kay"), teamArt.getStateMgr().getAssignees().iterator().next());

      IWorkPage fromState = teamArt.getWorkDefinition().getStateByName(TeamState.Analyze.getPageName());
      IWorkPage toState = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getPageName());

      StateDefinition authStateDef = teamArt.getWorkDefinition().getStateByName(TeamState.Authorize.getPageName());
      authStateDef.getRules().add(new RuleDefinition(RuleDefinitionOption.ForceAssigneesToTeamLeads));

      // make call to state item that should set options based on artifact's attribute value
      AtsForceAssigneesToTeamLeadsStateItem stateItem = new AtsForceAssigneesToTeamLeadsStateItem();
      stateItem.transitioned(teamArt, fromState, toState, Arrays.asList(UserManager.getUser()), null);

      // assignee should be Joe Smith
      Assert.assertEquals(1, teamArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUserByName("Joe Smith"),
         teamArt.getStateMgr().getAssignees().iterator().next());
   }
}
