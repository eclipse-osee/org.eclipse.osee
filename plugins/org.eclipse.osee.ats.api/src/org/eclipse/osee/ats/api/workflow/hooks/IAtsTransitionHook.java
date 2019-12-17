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
package org.eclipse.osee.ats.api.workflow.hooks;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTransitionHook {

   public String getDescription();

   /**
    * Allows subclass to add changes to transition before commit.
    */
   default public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes) {
      // provided for subclass implementation
   }

   /**
    * Allows subclass to to operation after transition and persist.
    */
   default public void transitionPersisted(Collection<? extends IAtsWorkItem> workItems, Map<IAtsWorkItem, String> workItemFromStateMap, String toStateName) {
      // provided for subclass implementation
   }

   default public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees) {
      // provided for subclass implementation
   }

   default public String getOverrideTransitionToStateName(IAtsWorkItem workItem) {
      return null;
   }

}
