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
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface ITransitionHelper {

   public String getName();

   public boolean isPrivilegedEditEnabled();

   public boolean isOverrideTransitionValidityCheck();

   public Collection<? extends IAtsWorkItem> getWorkItems();

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

   public Collection<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem) throws OseeCoreException;

   public String getToStateName();

   boolean isOverrideAssigneeCheck();

   boolean isOverrideWorkingBranchCheck();

   boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   boolean isBranchInCommit(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   public boolean isSystemUser() throws OseeCoreException;

   public boolean isSystemUserAssingee(IAtsWorkItem workItem) throws OseeCoreException;

   public IAtsChangeSet getChangeSet();

   public boolean isExecuteChanges();

   public Collection<ITransitionListener> getTransitionListeners();

   public IAtsUser getTransitionUser() throws OseeCoreException;

   public void setTransitionUser(IAtsUser user) throws OseeCoreException;

   public IAtsServices getServices();

   public void handleWorkflowReload(TransitionResults results);

   public boolean isReload();

}
