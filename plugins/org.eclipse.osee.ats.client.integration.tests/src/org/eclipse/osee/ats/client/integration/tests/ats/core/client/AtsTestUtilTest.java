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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client;

import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.Assert;

/**
 * Test unit for {@link AtsTestUtil}
 *
 * @author Donald G. Dunne
 */
public class AtsTestUtilTest extends AtsTestUtil {

   @org.junit.After
   public void validateCleanup() throws OseeCoreException {
      AtsTestUtil.validateObjectsNull();
   }

   @org.junit.Test
   public void testCleanupAndReset() throws OseeCoreException {
      boolean exceptionThrown = false;
      try {
         Assert.assertNull(AtsTestUtil.getWorkDef());
      } catch (OseeStateException ex) {
         Assert.assertEquals(ex.getMessage(), "Must call cleanAndReset before using this method");
         exceptionThrown = true;
      }
      Assert.assertTrue("Exeception should have been thrown", exceptionThrown);

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getTeamWf());
      Assert.assertNotNull(AtsTestUtil.getTeamWf().getStateDefinition());
      Assert.assertNotNull(AtsTestUtil.getTeamWf().getTeamDefinition());
      String atsId = AtsTestUtil.getTeamWf().getAtsId();
      Assert.assertTrue(atsId.startsWith("TW"));

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testCreateAndCleanupWithBranch() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName() + "-testCleanupAndResetWithBranch");
      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      Assert.assertTrue(result.getText(), result.isTrue());
      Thread.sleep(2000);
      BranchId branch = AtsTestUtil.getTeamWf().getWorkingBranch();
      Assert.assertTrue(branch.isValid());
      AtsTestUtil.cleanup();
      Assert.assertTrue(BranchManager.getState(branch).isDeleted());
   }

   @org.junit.Test
   public void testGetTeamWf2() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetTeamWf2");
      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getTeamWf2());
      Assert.assertNotNull(AtsTestUtil.getTeamWf2().getStateDefinition());
      Assert.assertNotNull(AtsTestUtil.getTeamWf2().getTeamDefinition());
      Assert.assertNotSame(AtsTestUtil.getTeamWf(), AtsTestUtil.getTeamWf2());
      Assert.assertNotSame(AtsTestUtil.getActionArt(), AtsTestUtil.getActionArt2());
      Assert.assertNotSame(
         AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
            AtsTestUtil.getTeamWf()).iterator().next(),
         AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
            AtsTestUtil.getTeamWf2()).iterator().next());

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testGetTeamWf4() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetTeamWf2");
      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getTeamWf4());
      Assert.assertNotNull(AtsTestUtil.getTeamWf4().getStateDefinition());
      Assert.assertNotNull(AtsTestUtil.getTeamWf4().getTeamDefinition());
      Assert.assertNotSame(AtsTestUtil.getTeamWf(), AtsTestUtil.getTeamWf4());
      Assert.assertNotSame(AtsTestUtil.getActionArt(), AtsTestUtil.getActionArt4());
      Assert.assertNotSame(
         AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
            AtsTestUtil.getTeamWf()).iterator().next(),
         AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
            AtsTestUtil.getTeamWf4()).iterator().next());
      Assert.assertEquals(AtsClientService.get().getVersionService().getTargetedVersion(AtsTestUtil.getTeamWf4()),
         AtsTestUtil.getVerArt4());

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testGetTask1() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetTask1");
      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getOrCreateTaskOffTeamWf1());

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testGetDecisionReview() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetDecisionReview");
      AtsTestUtil.validateArtifactCache();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      Assert.assertNotNull(
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze, changes));
      changes.execute();

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testGetPeerReview() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetPeerReview");
      AtsTestUtil.validateArtifactCache();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("testGetPeerReview");
      Assert.assertNotNull(
         AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze, changes));
      changes.execute();

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testTransitionTo() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Analyze.getName());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("test");

      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Implement, AtsClientService.get().getUserService().getCurrentUser(),
            changes, TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Implement.getName());

      result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Completed, AtsClientService.get().getUserService().getCurrentUser(),
            changes, TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Completed.getName());

      teamArt.reloadAttributesAndRelations();

      changes.execute();

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testTransitionToCancelled() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Analyze.getName());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("test");

      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Cancelled, AtsClientService.get().getUserService().getCurrentUser(),
            changes, TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Cancelled.getName());

      teamArt.reloadAttributesAndRelations();

      changes.execute();

      AtsTestUtil.cleanup();
   }
}
