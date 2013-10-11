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

import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
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

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testCreateAndCleanupWithBranch() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName() + "-testCleanupAndResetWithBranch");
      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      Assert.assertTrue(result.getText(), result.isTrue());
      Thread.sleep(2000);
      Branch branch = AtsTestUtil.getTeamWf().getWorkingBranch();
      Assert.assertNotNull(branch);
      AtsTestUtil.cleanup();
      Assert.assertTrue(branch.isDeleted());
   }

   @org.junit.Test
   public void testGetTeamWf2() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetTeamWf2");
      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getTeamWf2());
      Assert.assertNotNull(AtsTestUtil.getTeamWf2().getStateDefinition());
      Assert.assertNotSame(AtsTestUtil.getTeamWf(), AtsTestUtil.getTeamWf2());
      Assert.assertNotSame(AtsTestUtil.getActionArt(), AtsTestUtil.getActionArt2());
      Assert.assertNotSame(AtsTestUtil.getTeamWf().getActionableItemsDam().getActionableItems().iterator().next(),
         AtsTestUtil.getTeamWf2().getActionableItemsDam().getActionableItems().iterator().next());

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testGetTeamWf4() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetTeamWf2");
      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getTeamWf4());
      Assert.assertNotNull(AtsTestUtil.getTeamWf4().getStateDefinition());
      Assert.assertNotSame(AtsTestUtil.getTeamWf(), AtsTestUtil.getTeamWf4());
      Assert.assertNotSame(AtsTestUtil.getActionArt(), AtsTestUtil.getActionArt4());
      Assert.assertNotSame(AtsTestUtil.getTeamWf().getActionableItemsDam().getActionableItems().iterator().next(),
         AtsTestUtil.getTeamWf4().getActionableItemsDam().getActionableItems().iterator().next());
      Assert.assertEquals(AtsVersionService.get().getTargetedVersion(AtsTestUtil.getTeamWf4()),
         AtsTestUtil.getVerArt4());

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testGetDecisionReview() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetDecisionReview");
      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze));

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testGetPeerReview() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest.testGetPeerReview");
      AtsTestUtil.validateArtifactCache();

      Assert.assertNotNull(AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze, null));

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testTransitionTo() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Analyze.getName());

      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "test");

      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Implement, AtsClientService.get().getUserAdmin().getCurrentUser(), transaction,
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Implement.getName());

      result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Completed, AtsClientService.get().getUserAdmin().getCurrentUser(), transaction,
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Completed.getName());

      teamArt.reloadAttributesAndRelations();

      transaction.execute();

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testTransitionToCancelled() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Analyze.getName());

      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "test");

      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Cancelled, AtsClientService.get().getUserAdmin().getCurrentUser(), transaction,
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Cancelled.getName());

      teamArt.reloadAttributesAndRelations();

      transaction.execute();

      AtsTestUtil.cleanup();
   }
}
