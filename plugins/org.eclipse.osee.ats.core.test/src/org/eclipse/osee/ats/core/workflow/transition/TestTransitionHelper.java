/*
 * Created on Jun 6, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;

/**
 * Allows setting of workingBranchInWork and branchInCommit for testing purposes. If not set, uses default from
 * TransitionHelper
 * 
 * @author Donald G. Dunne
 */
public class TestTransitionHelper extends TransitionHelper {
   public Boolean workingBranchInWork = null;
   public Boolean branchInCommit = null;
   public Boolean systemUser = null;
   public Boolean systemUserAssigned = null;
   public Boolean overrideTransitionValidityCheck = null;

   public TestTransitionHelper(String name, Collection<? extends AbstractWorkflowArtifact> awas, String toStateName, Collection<? extends IBasicUser> toAssignees, String cancellationReason, TransitionOption... transitionOption) {
      super(name, awas, toStateName, toAssignees, cancellationReason, transitionOption);
   }

   @Override
   public boolean isWorkingBranchInWork(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      if (workingBranchInWork != null) {
         return workingBranchInWork;
      }
      return super.isWorkingBranchInWork(teamArt);
   }

   @Override
   public boolean isBranchInCommit(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      if (branchInCommit != null) {
         return branchInCommit;
      }
      return super.isBranchInCommit(teamArt);
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
   public boolean isSystemUser() throws OseeCoreException {
      if (systemUser != null) {
         return systemUser;
      }
      return super.isSystemUser();
   }

   public void setSystemUser(Boolean systemUser) {
      this.systemUser = systemUser;
   }

   @Override
   public boolean isSystemUserAssingee(AbstractWorkflowArtifact awa) throws OseeCoreException {
      if (systemUserAssigned != null) {
         return systemUserAssigned;
      }
      return super.isSystemUserAssingee(awa);
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
