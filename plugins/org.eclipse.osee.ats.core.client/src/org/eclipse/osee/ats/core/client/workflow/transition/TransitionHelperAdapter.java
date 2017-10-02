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
package org.eclipse.osee.ats.core.client.workflow.transition;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public abstract class TransitionHelperAdapter implements ITransitionHelper {

   IAtsUser transitionUser;

   @Override
   public boolean isPrivilegedEditEnabled() {
      return false;
   }

   @Override
   public boolean isOverrideTransitionValidityCheck() {
      return false;
   }

   @Override
   public boolean isOverrideAssigneeCheck() {
      return false;
   }

   @Override
   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf)  {
      return AtsClientService.get().getBranchService().isWorkingBranchInWork(teamWf);
   }

   @Override
   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf)  {
      return AtsClientService.get().getBranchService().isBranchInCommit(teamWf);
   }

   @Override
   public boolean isSystemUser()  {
      return AtsCoreUsers.isAtsCoreUser(getTransitionUser());
   }

   @Override
   public boolean isSystemUserAssingee(IAtsWorkItem workItem)  {
      return workItem.getStateMgr().getAssignees().contains(
         AtsCoreUsers.ANONYMOUS_USER) || workItem.getStateMgr().getAssignees().contains(AtsCoreUsers.SYSTEM_USER);
   }

   @Override
   public IAtsUser getTransitionUser()  {
      IAtsUser user = transitionUser;
      if (user == null) {
         user = AtsClientService.get().getUserService().getCurrentUser();
      }
      return user;
   }

   @Override
   public void setTransitionUser(IAtsUser user)  {
      transitionUser = user;
   }

}
