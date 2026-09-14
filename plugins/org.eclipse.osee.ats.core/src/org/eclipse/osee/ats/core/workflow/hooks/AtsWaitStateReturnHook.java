/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.core.workflow.hooks;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Contributed via AtsWorkItemServiceImpl. When a work item sits in a wait state (e.g. Monitor), defaults the transition
 * target back to the state it was entered from (LastStateName), so the user is offered "return to where you came from"
 * as the default.
 *
 * @author Donald G. Dunne
 */
public class AtsWaitStateReturnHook implements IAtsTransitionHook {

   public String getName() {
      return AtsWaitStateReturnHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Default a wait state's transition target back to the state it was entered from.";
   }

   @Override
   public String getOverrideTransitionToStateName(IAtsWorkItem workItem) {
      StateDefinition currentState = workItem.getStateDefinition();
      if (currentState == null || !currentState.isWaitState()) {
         return null;
      }
      String lastStateName = workItem.getLastStateName();
      if (!Strings.isValid(lastStateName)) {
         return null;
      }
      // Only default to the last state if it is still a valid transition target of the wait state
      for (StateDefinition toState : currentState.getToStates()) {
         if (toState.getName().equals(lastStateName)) {
            return lastStateName;
         }
      }
      return null;
   }

}
