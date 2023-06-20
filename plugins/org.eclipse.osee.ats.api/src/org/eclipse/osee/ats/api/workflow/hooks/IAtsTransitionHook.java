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

package org.eclipse.osee.ats.api.workflow.hooks;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;

/**
 * Provides hooks for interacting with ATS state transitions. NOTE: HOOKS MUST BE IMPLEMENTED in ats.core OR ats.rest
 * since transitions are on the server.
 *
 * @author Donald G. Dunne
 */
public interface IAtsTransitionHook {

   public String getDescription();

   /**
    * Allows subclass to add changes to transition before persist.<br/>
    * ONLY USE IF NEED TO ADD SOMETHING TO TRANSITION PERSIST
    */
   default public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
      Collection<? extends AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
      // Provided for subclass implementation
   }

   /**
    * Allows subclass to do operation after transition and persist.<br/>
    * USE IF NEED TO DO SOMETHING AFTER TRANSITION PERSIST; OPERATION WILL BE RUN IN BACKGROUND SO TRANSITION ISN'T
    * SLOWED
    */
   default public void transitionPersisted(Collection<? extends IAtsWorkItem> workItems,
      Map<IAtsWorkItem, String> workItemFromStateMap, String toStateName, AtsUser asUser, AtsApi atsApi) {
      // Provided for subclass implementation
   }

   /**
    * Log errors in results if transition should fail<br/>
    * ONLY USE IF NEED TO CHECK SOMETHING BEFORE TRANSITION; CHECK SHOULD BE QUICK
    */
   default public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState,
      IStateToken toState, Collection<? extends AtsUser> toAssignees, AtsUser asUser, AtsApi atsApi) {
      // Provided for subclass implementation
   }

   default public String getOverrideTransitionToStateName(IAtsWorkItem workItem) {
      return null;
   }

}
