/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkflowHookIde;

/**
 * All client transitions should go through this service which handles transitioning on server, reloading client work
 * items and kicking events.
 *
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceClientImpl extends AtsWorkItemServiceImpl implements IAtsWorkItemServiceClient {

   private static Set<IAtsWorkflowHookIde> workflowHooksIde = new HashSet<>();

   @Override
   public void addWorkflowHookIde(IAtsWorkflowHookIde hook) {
      workflowHooksIde.add(hook);
   }

   public AtsWorkItemServiceClientImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsApi, teamWorkflowProvidersLazy);
   }

   @Override
   public Set<IAtsWorkflowHookIde> getWorkflowHooksIde() {
      return workflowHooksIde;
   }

   //   @Override
   //   public TransitionResults transition(TransitionData transData) {
   //      populateTransitionData(transData);
   //      TransitionResults results = atsApi.getServerEndpoints().getActionEndpoint().transition(transData);
   //      return postEventAndReturn(transData, results);
   //   }

   private void populateTransitionData(TransitionData transData) {
      for (IAtsWorkItem workItem : transData.getWorkItems()) {
         transData.getWorkItemIds().add(workItem.getStoreObject());
      }
      if (transData.getTransitionUser() == null) {
         transData.setTransitionUser(atsApi.getUserService().getCurrentUser());
      }
   }

   //   @Override
   //   public TransitionResults transition(ITransitionHelper helper) {
   //      return transition(helper.getTransData());
   //   }
   //
   //   @Override
   //   public TransitionResults transitionValidate(TransitionData transData) {
   //      populateTransitionData(transData);
   //      TransitionResults results = atsApi.getServerEndpoints().getActionEndpoint().transitionValidate(transData);
   //      return postEventAndReturn(transData, results);
   //   }

   /**
    * Since transition on server, reload and post transition event for listeners to refresh. TransitionManager should
    * NOT be used on client.
    */
   private TransitionResults postEventAndReturn(TransitionData transData, TransitionResults results) {
      if (results.isSuccess()) {
         reload(results);
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITIONED,
            transData.getWorkItems());
      } else {
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED,
            transData.getWorkItems());
      }
      return results;
   }

   private void reload(TransitionResults transResults) {
      List<IAtsWorkItem> workItemsToReload = new LinkedList<>();
      for (IAtsWorkItem workItem : transResults.getWorkItemToResults().keySet()) {
         boolean changed = AtsClientService.get().getStoreService().isChangedInDb(workItem);
         if (changed) {
            workItemsToReload.add(workItem);
         }
      }
      if (!workItemsToReload.isEmpty()) {
         AtsClientService.get().getStoreService().reload(workItemsToReload);
      }
   }

}
