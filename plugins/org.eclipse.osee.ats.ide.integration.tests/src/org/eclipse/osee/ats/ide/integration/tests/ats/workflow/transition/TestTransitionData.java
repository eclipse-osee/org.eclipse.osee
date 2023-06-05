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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;

/**
 * Allows setting of workingBranchInWork and branchInCommit for testing purposes. If not set, uses default from
 * TransitionData
 *
 * @author Donald G. Dunne
 */
public class TestTransitionData extends TransitionData {
   public Boolean workingBranchInWork = null;
   public Boolean branchInCommit = null;
   public Boolean systemUser = null;
   public Boolean systemUserAssigned = null;
   public Boolean overrideTransitionValidityCheck = null;

   public TestTransitionData(String name, Collection<IAtsWorkItem> awas, String toStateName, Collection<AtsUser> toAssignees, String cancellationReason, IAtsChangeSet changes, TransitionOption... transitionOption) {
      super(name, awas, toStateName, toAssignees, cancellationReason, changes, transitionOption);
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

   public void setSystemUser(Boolean systemUser) {
      this.systemUser = systemUser;
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

   @Override
   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      if (workingBranchInWork != null) {
         return workingBranchInWork;
      }
      return super.isWorkingBranchInWork(teamWf, atsApi);
   }

   @Override
   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      if (branchInCommit != null) {
         return branchInCommit;
      }
      return super.isBranchInCommit(teamWf, atsApi);
   }

   @Override
   public boolean isSystemUserAssingee(IAtsWorkItem workItem) {
      if (systemUserAssigned != null) {
         return systemUserAssigned;
      }
      return super.isSystemUser();
   }

   @Override
   public boolean isSystemUser() {
      if (systemUser != null) {
         return systemUser;
      }
      return super.isSystemUser();
   }

}
