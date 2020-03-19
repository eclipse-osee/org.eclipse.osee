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
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Arrays;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workdef.WorkDefTeamAtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link AtsTestUtil}
 *
 * @author Donald G. Dunne
 */
public class AtsTestUtilTest extends AtsTestUtil {

   @BeforeClass
   public static void cleanup() {
      AtsClientService.get().getWorkDefinitionService().addWorkDefinition(new WorkDefTeamAtsTestUtil());
   }

   @org.junit.Test
   public void test() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      testCleanupAndReset();
      testGetTeamWf2();
      testGetTeamWf4();
      testTransitionTo();
      testGetTask1();
      testGetDecisionReview();
      testGetPeerReview();
      testCreateAndCleanupWithBranch();

      AtsTestUtil.cleanup();
      AtsTestUtil.validateObjectsNull();
   }

   public void testCleanupAndReset() {
      AtsTestUtil.cleanup();
      boolean exceptionThrown = false;
      try {
         Assert.assertNull(AtsTestUtil.getTestTeamDef());
      } catch (OseeStateException ex) {
         Assert.assertEquals(ex.getMessage(), "Must call cleanAndReset before using this method");
         exceptionThrown = true;
      }
      Assert.assertTrue("Exeception should have been thrown", exceptionThrown);

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      Assert.assertNotNull(AtsTestUtil.getTeamWf());
      Assert.assertNotNull(AtsTestUtil.getTeamWf().getStateDefinition());
      Assert.assertNotNull(AtsTestUtil.getTeamWf().getTeamDefinition());
      String atsId = AtsTestUtil.getTeamWf().getAtsId();
      Assert.assertTrue(atsId.startsWith("TW"));
      Assert.assertEquals(1, AtsTestUtil.getTeamWf().getStateMgr().getAssignees().size());
      Assert.assertEquals(DemoUsers.Joe_Smith, AtsTestUtil.getTeamWf().getStateMgr().getAssignees().iterator().next());
   }

   public void testGetTeamWf2() {
      Assert.assertNotNull(AtsTestUtil.getTeamWf2());
      Assert.assertNotNull(AtsTestUtil.getTeamWf2().getStateDefinition());
      Assert.assertNotNull(AtsTestUtil.getTeamWf2().getTeamDefinition());
      Assert.assertNotSame(AtsTestUtil.getTeamWf(), AtsTestUtil.getTeamWf2());
      Assert.assertNotSame(AtsTestUtil.getActionArt(), AtsTestUtil.getActionArt2());
      Set<IAtsActionableItem> actionableItems =
         AtsClientService.get().getActionableItemService().getActionableItems(AtsTestUtil.getTeamWf());
      Set<IAtsActionableItem> actionableItems2 =
         AtsClientService.get().getActionableItemService().getActionableItems(AtsTestUtil.getTeamWf2());
      Assert.assertNotSame(actionableItems.iterator().next(), actionableItems2.iterator().next());
   }

   public void testGetTeamWf4() {
      Assert.assertNotNull(AtsTestUtil.getTeamWf4());
      Assert.assertNotNull(AtsTestUtil.getTeamWf4().getStateDefinition());
      Assert.assertNotNull(AtsTestUtil.getTeamWf4().getTeamDefinition());
      Assert.assertNotSame(AtsTestUtil.getTeamWf(), AtsTestUtil.getTeamWf4());
      Assert.assertNotSame(AtsTestUtil.getActionArt(), AtsTestUtil.getActionArt4());
      Assert.assertNotSame(
         AtsClientService.get().getActionableItemService().getActionableItems(
            AtsTestUtil.getTeamWf()).iterator().next(),
         AtsClientService.get().getActionableItemService().getActionableItems(
            AtsTestUtil.getTeamWf4()).iterator().next());
      Assert.assertEquals(AtsClientService.get().getVersionService().getTargetedVersion(AtsTestUtil.getTeamWf4()),
         AtsTestUtil.getVerArt4());
   }

   public void testGetTask1() {
      Assert.assertNotNull(AtsTestUtil.getOrCreateTaskOffTeamWf1());
   }

   public void testGetDecisionReview() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      Assert.assertNotNull(
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze, changes));
   }

   public void testGetPeerReview() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("testGetPeerReview");
      Assert.assertNotNull(
         AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze, changes));
   }

   public void testTransitionTo() {
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Analyze.getName());

      for (IStateToken toState : Arrays.asList(TeamState.Implement, TeamState.Completed, TeamState.Implement,
         TeamState.Cancelled, TeamState.Implement)) {
         TransitionHelper helper = new TransitionHelper("test", Arrays.asList(teamArt), toState.getName(),
            Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), "", null, null,
            TransitionOption.OverrideTransitionValidityCheck);
         TransitionResults results = AtsClientService.get().getWorkItemServiceClient().transition(helper);
         Assert.assertTrue(results.isSuccess());
         Assert.assertEquals(toState.getName(), teamArt.getCurrentStateName());
      }

   }

   public void testCreateAndCleanupWithBranch() {
      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      Assert.assertTrue(result.getText(), result.isTrue());
      BranchId branch = AtsTestUtil.getTeamWf().getWorkingBranch();
      Assert.assertTrue(branch.isValid());

      AtsTestUtil.cleanup();
      Assert.assertTrue(BranchManager.getState(branch).isDeleted());
   }

}
