/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.workflow;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * All client transitions should go through this service which handles transitioning on server, reloading client work
 * items and kicking events.
 *
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceClientImpl extends AtsWorkItemServiceImpl implements IAtsWorkItemServiceIde {

   public AtsWorkItemServiceClientImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsApi, teamWorkflowProvidersLazy);
   }

   @Override
   public Set<IAtsWorkItemHookIde> getWorkItemHooksIde() {
      Set<IAtsWorkItemHookIde> hooks = new HashSet<>();
      for (IAtsWorkItemHook hook : workflowHooks) {
         if (hook instanceof IAtsWorkItemHookIde) {
            hooks.add((IAtsWorkItemHookIde) hook);
         }
      }
      return hooks;
   }

   @Override
   public TransitionResults transition(TransitionData transData) {
      populateTransitionData(transData);
      TransitionResults results = atsApi.getServerEndpoints().getActionEndpoint().transition(transData);
      results.setAtsApi(atsApi);
      if (results.isErrors()) {
         return results;
      }
      results = postEventAndReturn(transData, results);
      return results;
   }

   private void populateTransitionData(TransitionData transData) {
      for (IAtsWorkItem workItem : transData.getWorkItems()) {
         transData.getWorkItemIds().add(workItem.getStoreObject());
      }
      if (transData.getTransitionUser() == null) {
         transData.setTransitionUser(atsApi.getUserService().getCurrentUser());
      }
   }

   @Override
   public TransitionResults transitionValidate(TransitionData transData) {
      populateTransitionData(transData);
      TransitionResults results = atsApi.getServerEndpoints().getActionEndpoint().transitionValidate(transData);
      results.setAtsApi(atsApi);
      return results;
   }

   /**
    * Since transition on server, reload and post transition event for listeners to refresh. TransitionManager should
    * NOT be used on client. This is the only way transition events get sent to other clients
    */
   private TransitionResults postEventAndReturn(TransitionData transData, TransitionResults results) {
      Conditions.assertNotNullOrEmpty(results.getWorkItemIds(), "workItemIds");
      atsApi.getStoreService().reload(transData.getWorkItems());

      if (results.isSuccess()) {
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, transData.getWorkItems(),
            results.getTransaction());
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITIONED,
            transData.getWorkItems(), results.getTransaction());
      } else {
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED,
            transData.getWorkItems(), results.getTransaction());
      }
      return results;
   }

}
