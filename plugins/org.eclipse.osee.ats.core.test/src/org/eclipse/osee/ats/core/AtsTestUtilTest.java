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
package org.eclipse.osee.ats.core;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

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
   public void testTransitionTo() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Analyze.getPageName());

      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), "test");

      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Implement, UserManager.getUser(), transaction,
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Implement.getPageName());

      result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Completed, UserManager.getUser(), transaction,
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Completed.getPageName());

      teamArt.reloadAttributesAndRelations();
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testTransitionToCancelled() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Analyze.getPageName());

      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), "test");

      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Cancelled, UserManager.getUser(), transaction,
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Cancelled.getPageName());

      teamArt.reloadAttributesAndRelations();
      AtsTestUtil.cleanup();
   }
}
