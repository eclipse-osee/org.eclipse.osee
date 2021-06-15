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

package org.eclipse.osee.ats.api.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface ITransitionHelper {

   public String getName();

   public boolean isOverrideTransitionValidityCheck();

   public Collection<IAtsWorkItem> getWorkItems();

   /**
    * @return with cancellationReason and cancellationDetails set
    * @return TransitionData.isDialogCancelled to cancel transition
    */
   public TransitionData getCancellationReason(TransitionData transitionData);

   public Collection<? extends AtsUser> getToAssignees(IAtsWorkItem workItem);

   public String getToStateName();

   boolean isOverrideAssigneeCheck();

   boolean isOverrideWorkingBranchCheck();

   boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf);

   boolean isBranchInCommit(IAtsTeamWorkflow teamWf);

   public boolean isSystemUser();

   public boolean isSystemUserAssingee(IAtsWorkItem workItem);

   public IAtsChangeSet getChangeSet();

   public boolean isExecuteChanges();

   public Collection<IAtsTransitionHook> getTransitionListeners();

   public AtsUser getTransitionUser();

   public void setTransitionUser(AtsUser user);

   public AtsApi getServices();

   public void handleWorkflowReload(TransitionResults results);

   public boolean isReload();

   public TransitionData getTransData();

   public void setAtsApi(AtsApi atsApi);

   public String getCancellationReasonDetails();

   public String getCancellationReason();

   public AttributeTypeToken getCancellationReasonAttrType();

   default void setCancellationReasonDetails(String cancelReasonDetails) {
      // do nothing
   }

   default void setCancellationReason(String cancelReason) {
      // do nothing
   }

}
