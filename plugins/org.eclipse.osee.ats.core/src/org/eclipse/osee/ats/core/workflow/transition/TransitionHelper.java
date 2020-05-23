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

package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TransitionHelper extends TransitionHelperAdapter {

   private IAtsChangeSet changes;
   private IAtsWorkItemService workItemService;

   public TransitionHelper(TransitionData transData, IAtsChangeSet changes, AtsApi atsApi) {
      super(atsApi, transData);
      this.changes = changes;
      this.workItemService = atsApi.getWorkItemService();
   }

   public TransitionHelper(String name, Collection<IAtsWorkItem> workItems, String toStateName, Collection<AtsUser> toAssignees, String cancellationReason, IAtsChangeSet changes, AtsApi atsApi, TransitionOption... transitionOption) {
      super(atsApi, new TransitionData());
      transData.setName(name);
      transData.setWorkItems(workItems);
      transData.setToStateName(toStateName);
      transData.setCancellationReason(cancellationReason);
      transData.setToAssignees(toAssignees);
      for (TransitionOption opt : transitionOption) {
         transData.getTransitionOptions().add(opt);
      }
      this.changes = changes;
      handleSetAtsApi(atsApi);
   }

   @Override
   public boolean isOverrideAssigneeCheck() {
      return getTransitionOptions().contains(TransitionOption.OverrideAssigneeCheck);
   }

   @Override
   public boolean isOverrideWorkingBranchCheck() {
      return transData.getTransitionOptions().contains(TransitionOption.OverrideWorkingBranchCheck);
   }

   @Override
   public boolean isReload() {
      return !transData.getTransitionOptions().contains(TransitionOption.OverrideReload);
   }

   @Override
   public boolean isOverrideTransitionValidityCheck() {
      return transData.getTransitionOptions().contains(TransitionOption.OverrideTransitionValidityCheck);
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItems() {
      return transData.getWorkItems();
   }

   @Override
   public TransitionData getCancellationReason(TransitionData transitionData) {
      return transitionData;
   }

   @Override
   public String getName() {
      return transData.getName();
   }

   @Override
   public Collection<AtsUser> getToAssignees(IAtsWorkItem workItem) {
      return transData.getToAssignees();
   }

   @Override
   public String getToStateName() {
      return transData.getToStateName();
   }

   public void addTransitionOption(TransitionOption transitionOption) {
      transData.getTransitionOptions().add(transitionOption);
   }

   public void removeTransitionOption(TransitionOption transitionOption) {
      transData.getTransitionOptions().remove(transitionOption);
   }

   public void setToStateName(String toStateName) {
      transData.setToStateName(toStateName);
   }

   @Override
   public IAtsChangeSet getChangeSet() {
      if (changes == null) {
         changes = atsApi.createChangeSet(getName(), getTransitionUser());
      }
      return changes;
   }

   @Override
   public boolean isExecuteChanges() {
      return transData.isExecuteChanges();
   }

   public void setExecuteChanges(boolean executeChanges) {
      transData.setExecuteChanges(executeChanges);
   }

   @Override
   public Collection<IAtsTransitionHook> getTransitionListeners() {
      try {
         return workItemService.getTransitionHooks();
      } catch (OseeCoreException ex) {
         OseeLog.log(TransitionHelper.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public AtsApi getServices() {
      return atsApi;
   }

   public Collection<TransitionOption> getTransitionOptions() {
      return transData.getTransitionOptions();
   }

   @Override
   public void setAtsApi(AtsApi atsApi) {
      handleSetAtsApi(atsApi);
   }

   private void handleSetAtsApi(AtsApi atsApi) {
      if (atsApi != null) {
         this.atsApi = atsApi;
         this.workItemService = atsApi.getWorkItemService();
         if (transData.getTransitionUser() == null) {
            transData.setTransitionUser(atsApi.getUserService().getCurrentUser());
         }
      } else {
         this.workItemService = null;
      }
   }

   @Override
   public void setCancellationReasonDetails(String cancelReasonDetails) {
      transData.setCancellationReasonDetails(cancelReasonDetails);
   }

   @Override
   public void setCancellationReason(String cancelReason) {
      transData.setCancellationReason(cancelReason);
   }

   @Override
   public String getCancellationReasonDetails() {
      return transData.getCancellationReasonDetails();
   }

   @Override
   public String getCancellationReason() {
      return transData.getCancellationReason();
   }

}
