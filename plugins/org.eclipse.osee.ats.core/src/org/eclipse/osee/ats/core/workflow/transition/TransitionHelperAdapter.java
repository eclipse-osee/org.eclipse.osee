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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public abstract class TransitionHelperAdapter implements ITransitionHelper {

   protected AtsApi atsApi;
   protected final TransitionData transData;

   public TransitionHelperAdapter(AtsApi atsApi) {
      this.atsApi = atsApi;
      transData = new TransitionData();
   }

   public TransitionHelperAdapter(AtsApi atsApi, TransitionData transData) {
      this.atsApi = atsApi;
      this.transData = transData;
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
   public boolean isExecuteChanges() {
      return false;
   }

   @Override
   public AtsUser getTransitionUser() {
      AtsUser user = transData.getTransitionUser();
      if (user == null) {
         user = atsApi.getUserService().getCurrentUser();
      }
      return user;
   }

   @Override
   public void setTransitionUser(AtsUser user) {
      transData.setTransitionUser(user);
   }

   @Override
   public abstract Collection<IAtsWorkItem> getWorkItems();

   @Override
   public void handleWorkflowReload(TransitionResults results) {
      if (!transData.isWorkflowsReloaded()) {
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
         transData.setWorkflowsReloaded(true);
      }
   }

   @Override
   public TransitionData getTransData() {
      return transData;
   }

   @Override
   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public AtsApi getServices() {
      return atsApi;
   }

   @Override
   public AttributeTypeToken getCancellationReasonAttrType() {
      return AtsAttributeTypes.CancelledReason;
   }

}
