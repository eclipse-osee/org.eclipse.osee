/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsHoldOrBlockedTransitionHook implements IAtsTransitionHook {

   public String getName() {
      return AtsHoldOrBlockedTransitionHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Can not transition with hold or blocked set.";
   }

   @Override
   public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<AtsUser> toAssignees, AtsUser asUser, AtsApi atsApi) {
      boolean isBlocked = workItem.getAtsApi().getWorkItemService().isBlocked(workItem);
      if (isBlocked) {
         String reason = workItem.getAtsApi().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.BlockedReason, "unknown");
         results.addResult(new TransitionResult("Can not transition a Blocked Workflow.\nBlock Reason: [%s]", reason));
      }
      boolean isHold = workItem.getAtsApi().getWorkItemService().isOnHold(workItem);
      if (isHold) {
         String reason = workItem.getAtsApi().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.HoldReason, "");
         results.addResult(new TransitionResult("Can not transition a Workflow on Hold.\nHold Reason: [%s]", reason));
      }
   }

}
