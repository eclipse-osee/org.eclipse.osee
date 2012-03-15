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

import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.users.AtsUsers;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public abstract class TransitionHelperAdapter implements ITransitionHelper {

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
   public boolean isWorkingBranchInWork(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return AtsBranchManagerCore.isWorkingBranchInWork(teamArt);
   }

   @Override
   public boolean isBranchInCommit(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return AtsBranchManagerCore.isBranchInCommit(teamArt);
   }

   @Override
   public boolean isSystemUser() throws OseeCoreException {
      return AtsUsers.isGuestUser(AtsUsersClient.getUser()) || AtsUsers.isSystemUser(AtsUsersClient.getUser());
   }

   @Override
   public boolean isSystemUserAssingee(AbstractWorkflowArtifact awa) throws OseeCoreException {
      return awa.getStateMgr().getAssignees().contains(AtsUsers.getGuestUser()) || awa.getStateMgr().getAssignees().contains(
         AtsUsers.getSystemUser());
   }
}
