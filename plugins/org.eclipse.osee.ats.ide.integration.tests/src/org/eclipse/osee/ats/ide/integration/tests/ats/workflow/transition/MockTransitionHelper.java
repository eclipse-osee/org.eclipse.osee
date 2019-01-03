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
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;

/**
 * Allows setting of workingBranchInWork and branchInCommit for testing purposes. If not set, uses default from
 * TransitionHelper
 * 
 * @author Donald G. Dunne
 */
public class MockTransitionHelper extends TransitionHelper {
   public Boolean workingBranchInWork = null;
   public Boolean branchInCommit = null;
   public Boolean systemUser = null;
   public Boolean systemUserAssigned = null;
   public Boolean overrideTransitionValidityCheck = null;

   public MockTransitionHelper(String name, Collection<? extends AbstractWorkflowArtifact> awas, String toStateName, Collection<? extends IAtsUser> toAssignees, String cancellationReason, IAtsChangeSet changes, TransitionOption... transitionOption) {
      super(name, awas, toStateName, toAssignees, cancellationReason, changes, AtsClientService.get().getServices(),
         transitionOption);
   }

   @Override
   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) {
      if (workingBranchInWork != null) {
         return workingBranchInWork;
      }
      return super.isWorkingBranchInWork(teamWf);
   }

   @Override
   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf) {
      if (branchInCommit != null) {
         return branchInCommit;
      }
      return super.isBranchInCommit(teamWf);
   }

   public Boolean getWorkingBranchInWork() {
      return workingBranchInWork;
   }

   public void setWorkingBranchInWork(Boolean workingBranchInWork) {
      this.workingBranchInWork = workingBranchInWork;
   }

   public Boolean getBranchInCommit() {
      return branchInCommit;
   }

   public void setBranchInCommit(Boolean branchInCommit) {
      this.branchInCommit = branchInCommit;
   }

   @Override
   public boolean isSystemUser() {
      if (systemUser != null) {
         return systemUser;
      }
      return super.isSystemUser();
   }

   public void setSystemUser(Boolean systemUser) {
      this.systemUser = systemUser;
   }

   @Override
   public boolean isSystemUserAssingee(IAtsWorkItem workItem) {
      if (systemUserAssigned != null) {
         return systemUserAssigned;
      }
      return super.isSystemUserAssingee(workItem);
   }

   public void setSystemUserAssigned(Boolean systemUserAssigned) {
      this.systemUserAssigned = systemUserAssigned;
   }

   @Override
   public boolean isOverrideTransitionValidityCheck() {
      if (overrideTransitionValidityCheck != null) {
         return overrideTransitionValidityCheck;
      }
      return super.isOverrideTransitionValidityCheck();
   }

   public void setOverrideTransitionValidityCheck(Boolean overrideTransitionValidityCheck) {
      this.overrideTransitionValidityCheck = overrideTransitionValidityCheck;
   }

}
