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
package org.eclipse.osee.ats.api.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface ITransitionHelper {

   public String getName();

   public boolean isOverrideTransitionValidityCheck();

   public Collection<IAtsWorkItem> getWorkItems();

   /**
    * @return Result.isTrue with text if reason provided
    * @return Result.isFalse if no reason given
    * @return Result.isCancelled to cancel transition
    */
   public Result getCompleteOrCancellationReason();

   /**
    * @param changes JavaTip
    * @return Result.isTrue with text if hours provided
    * @return Result.isFalse if no extra hours given
    * @return Result.isCancelled to cancel transition
    */
   public Result handleExtraHoursSpent(IAtsChangeSet changes);

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

}
