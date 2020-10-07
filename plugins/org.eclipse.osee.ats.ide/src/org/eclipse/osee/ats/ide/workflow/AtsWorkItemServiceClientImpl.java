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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * All client transitions should go through this service which handles transitioning on server, reloading client work
 * items and kicking events.
 *
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceClientImpl extends AtsWorkItemServiceImpl implements IAtsWorkItemServiceIde {

   private static Set<IAtsWorkItemHookIde> workflowHooksIde = new HashSet<>();

   @Override
   public void addWorkItemHookIde(IAtsWorkItemHookIde hook) {
      workflowHooksIde.add(hook);
   }

   public AtsWorkItemServiceClientImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsApi, teamWorkflowProvidersLazy);
   }

   @Override
   public Set<IAtsWorkItemHookIde> getWorkItemHooksIde() {
      return workflowHooksIde;
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
   public TransitionResults transition(ITransitionHelper helper) {
      // Have to handle validation separate so UI can be done before transition
      helper.setAtsApi(atsApi);
      TransitionData transData = helper.getTransData();
      if (helper.getWorkItems().size() == 1) {
         Collection<? extends AtsUser> toAssignees = helper.getToAssignees(helper.getWorkItems().iterator().next());
         if (toAssignees != null) {
            transData.setToAssignees(Collections.castAll(toAssignees));
         }
      }
      if (helper.getTransitionUser() != null) {
         transData.setTransitionUser(helper.getTransitionUser());
      }
      transData.setToStateName(helper.getToStateName());
      transData.setName(helper.getName());
      transData.setWorkItems(helper.getWorkItems());

      // Set dummy cancel reason
      IAtsStateDefinition toStateDef = AtsApiService.get().getWorkDefinitionService().getStateDefinitionByName(
         helper.getWorkItems().iterator().next(), helper.getToStateName());
      if (toStateDef.getStateType() == StateType.Cancelled) {
         transData.setCancellationReason("temp reason");
      }
      TransitionResults results = transitionValidate(transData);
      if (results.isErrors()) {
         return results;
      }

      helper.getCancellationReason(transData);
      if (transData.isDialogCancelled()) {
         results.setCancelled(true);
         return results;
      }

      results = transition(transData);
      results.setAtsApi(atsApi);
      return results;
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
      ArtifactQuery.reloadArtifacts(transData.getWorkItemIds());
      if (results.isSuccess()) {
         reload(results);
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITIONED,
            transData.getWorkItems(), results.getTransaction());
      } else {
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED,
            transData.getWorkItems(), results.getTransaction());
      }
      return results;
   }

   private void reload(TransitionResults transResults) {
      List<IAtsWorkItem> workItemsToReload = new LinkedList<>();
      for (IAtsWorkItem workItem : transResults.getWorkItems()) {
         boolean changed = AtsApiService.get().getStoreService().isChangedInDb(workItem);
         if (changed) {
            workItemsToReload.add(workItem);
         }
      }
      if (!workItemsToReload.isEmpty()) {
         AtsApiService.get().getStoreService().reload(workItemsToReload);
      }
   }

}
