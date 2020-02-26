/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.AfterClass;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class TransitionListenersTest {

   @AfterClass
   public static void cleanup() {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testHandleTransitionValidation__TransitionHookCheck() {

      AtsTestUtil.cleanupAndReset("TransitionListenersTest-7");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(
            org.eclipse.osee.ats.ide.integration.tests.AtsClientService.get().getUserService().getCurrentUser()),
         null, AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // add transition listeners and verify can't transition
      final String reason1 = "Don't want you to transition";
      final String reason2 = "Don't transition yet";
      final String exceptionStr = "This is the exception message";
      IAtsTransitionHook listener1 = new IAtsTransitionHook() {

         @Override
         public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends AtsUser> toAssignees) {
            results.addResult(new TransitionResult(reason1));
         }

         @Override
         public String getDescription() {
            return null;
         }

      };
      IAtsTransitionHook listener2 = new IAtsTransitionHook() {

         @Override
         public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends AtsUser> toAssignees) {
            results.addResult(workItem, new TransitionResult(reason2));
         }

         @Override
         public String getDescription() {
            return null;
         }

      };
      IAtsTransitionHook listener3 = new IAtsTransitionHook() {

         @Override
         public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends AtsUser> toAssignees) {
            // do nothing
         }

         @Override
         public String getDescription() {
            return null;
         }

      };
      IAtsTransitionHook listener4 = new IAtsTransitionHook() {

         @Override
         public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends AtsUser> toAssignees) {
            throw new OseeCoreException(exceptionStr);
         }

         @Override
         public String getDescription() {
            return null;
         }

      };
      try {
         AtsClientService.get().getWorkItemService().addTransitionHook(listener1);
         AtsClientService.get().getWorkItemService().addTransitionHook(listener2);
         AtsClientService.get().getWorkItemService().addTransitionHook(listener3);
         AtsClientService.get().getWorkItemService().addTransitionHook(listener4);

         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.contains(reason1));
         Assert.assertTrue(results.contains(reason2));
         Assert.assertTrue(results.contains(exceptionStr));
      } finally {
         AtsClientService.get().getWorkItemService().removeListener(listener1);
         AtsClientService.get().getWorkItemService().removeListener(listener2);
         AtsClientService.get().getWorkItemService().removeListener(listener3);
         AtsClientService.get().getWorkItemService().removeListener(listener4);
      }
   }

}
