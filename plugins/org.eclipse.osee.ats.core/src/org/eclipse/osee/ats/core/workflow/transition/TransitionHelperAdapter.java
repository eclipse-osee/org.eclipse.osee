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
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;

/**
 * @author Donald G. Dunne
 */
public abstract class TransitionHelperAdapter implements ITransitionHelper {

   private final AtsApi atsApi;
   private IAtsUser transitionUser;
   private boolean workflowsReloaded = false;

   public TransitionHelperAdapter(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public boolean isOverrideTransitionValidityCheck() {
      return false;
   }

   @Override
   public boolean isReload() {
      return true;
   }

   @Override
   public boolean isOverrideAssigneeCheck() {
      return false;
   }

   @Override
   public boolean isOverrideWorkingBranchCheck() {
      return false;
   }

   @Override
   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) {
      return atsApi.getBranchService().isWorkingBranchInWork(teamWf);
   }

   @Override
   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf) {
      return atsApi.getBranchService().isBranchInCommit(teamWf);
   }

   @Override
   public boolean isSystemUser() {
      return AtsCoreUsers.isAtsCoreUser(getTransitionUser());
   }

   @Override
   public boolean isSystemUserAssingee(IAtsWorkItem workItem) {
      return workItem.getStateMgr().getAssignees().contains(
         AtsCoreUsers.ANONYMOUS_USER) || workItem.getStateMgr().getAssignees().contains(AtsCoreUsers.SYSTEM_USER);
   }

   @Override
   public boolean isExecuteChanges() {
      return false;
   }

   @Override
   public IAtsUser getTransitionUser() {
      IAtsUser user = transitionUser;
      if (user == null) {
         user = atsApi.getUserService().getCurrentUser();
      }
      return user;
   }

   @Override
   public void setTransitionUser(IAtsUser user) {
      transitionUser = user;
   }

   @Override
   public abstract Collection<IAtsWorkItem> getWorkItems();

   @Override
   public void handleWorkflowReload(TransitionResults results) {
      if (!workflowsReloaded) {
         // Only reload work items that have been changed in the database and not updated locally
         List<IAtsWorkItem> workItemsToReload = new LinkedList<>();
         for (IAtsWorkItem workItem : getWorkItems()) {
            boolean changed = atsApi.getStoreService().isChangedInDb(workItem);
            if (changed) {
               workItemsToReload.add(workItem);
            }
         }
         if (!workItemsToReload.isEmpty()) {
            atsApi.getStoreService().reload(workItemsToReload);
         }
         for (IAtsWorkItem workItem : getWorkItems()) {
            if (atsApi.getStoreService().isDeleted(workItem)) {
               results.addResult(workItem, TransitionResult.WORKITEM_DELETED);
            }
         }
         workflowsReloaded = true;
      }
   }

}
