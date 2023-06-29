/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Implementers for a WorkItem are<br/>
 * <br/>
 * For In Work Item: blank<br/>
 * <br/>
 * For Completed or Cancelled: <br/>
 * 1) Assignees of CompletedFrom or CancelledFrom states <br/>
 * 2) CompletedBy or CancelledBy user of WorkItem <br/>
 * 3) Users identified by object's getImplementers() call, if any <br/>
 * <br/>
 * For ActionGroup, it's the set of users for each case above for each Action
 *
 * @author Donald G. Dunne
 */
public class AtsImplementersService implements IAtsImplementerService {

   @Override
   public String getImplementersStr(IAtsObject atsObject) {
      List<AtsUser> implementers = getImplementers(atsObject);
      return implementers.isEmpty() ? "" : AtsObjects.toString("; ", implementers);
   }

   @Override
   public List<AtsUser> getImplementers(IAtsObject atsObject) {
      List<AtsUser> implementers = new LinkedList<>();
      if (atsObject instanceof IAtsAction) {
         for (AtsUser user : getActionGroupImplementers((IAtsAction) atsObject)) {
            if (!implementers.contains(user)) {
               implementers.add(user);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         for (AtsUser user : getWorkItemImplementers((IAtsWorkItem) atsObject)) {
            if (!implementers.contains(user)) {
               implementers.add(user);
            }
         }
      }
      implementers.remove(AtsCoreUsers.UNASSIGNED_USER);
      Collections.sort(implementers);
      return implementers;
   }

   public List<AtsUser> getWorkItemImplementers(IAtsWorkItem workItem) {
      List<AtsUser> implementers = new ArrayList<>();
      if (workItem.isReview()) {
         getImplementers_fromReviews(workItem, implementers);
      }
      getImplementers_fromCompletedCancelledBy(workItem, implementers);
      getImplementers_fromCompletedCancelledFrom(workItem, implementers);
      return implementers;
   }

   public void getImplementers_fromCompletedCancelledFrom(IAtsWorkItem workItem, List<AtsUser> implementers) {
      String fromStateName = null;
      if (workItem.getCurrentStateType().isCompleted()) {
         fromStateName = workItem.getCompletedFromState();
      } else if (workItem.getCurrentStateType().isCancelled()) {
         fromStateName = workItem.getCancelledFromState();
      }
      if (Strings.isValid(fromStateName)) {
         for (AtsUser user : workItem.getStateMgr().getAssignees(fromStateName)) {
            if (!implementers.contains(user)) {
               implementers.add(user);
            }
         }
      }
   }

   public void getImplementers_fromCompletedCancelledBy(IAtsWorkItem workItem, List<AtsUser> implementers) {
      if (workItem.getCurrentStateType().isCompletedOrCancelled()) {
         if (workItem.getCurrentStateType().isCompleted()) {
            AtsUser completedBy = workItem.getCompletedBy();
            if (completedBy != null && !implementers.contains(completedBy)) {
               implementers.add(completedBy);
            }
         }
         if (workItem.getCurrentStateType().isCancelled()) {
            AtsUser cancelledBy = workItem.getCancelledBy();
            if (cancelledBy != null && !implementers.contains(cancelledBy)) {
               implementers.add(cancelledBy);
            }
         }
      }
   }

   /**
    * Add assignees from Reviews</br>
    * 1. If Peer Review, add review role assignees</br>
    * 2. If Decision Review, add assignees for Decision state
    */
   public void getImplementers_fromReviews(IAtsWorkItem workItem, List<AtsUser> implementers) {
      // add review implementers
      if (workItem.isDecisionReview()) {
         implementers.addAll(getImplementersByState(workItem, DecisionReviewState.Decision));
      } else {
         implementers.addAll(getImplementersByState(workItem, PeerToPeerReviewState.Review));
         IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) workItem).getRoleManager();
         List<UserRole> userRoles = roleMgr.getUserRoles();
         for (AtsUser user : roleMgr.getRoleUsers(userRoles)) {
            implementers.add(user);
         }
      }
   }

   public List<AtsUser> getActionGroupImplementers(IAtsAction actionGroup) {
      List<AtsUser> implementers = new LinkedList<>();
      for (IAtsWorkItem action : actionGroup.getTeamWorkflows()) {
         if (action.getCurrentStateType().isCompletedOrCancelled()) {
            for (AtsUser user : getWorkItemImplementers(action)) {
               if (!implementers.contains(user)) {
                  implementers.add(user);
               }
            }
         }
      }
      return implementers;
   }

   public List<AtsUser> getImplementersByState(IAtsWorkItem workflow, IStateToken state) {
      List<AtsUser> users = new ArrayList<>();
      if (workflow.isCancelled()) {
         users.add(workflow.getCancelledBy());
      } else {
         for (AtsUser user : workflow.getStateMgr().getAssignees(state.getName())) {
            if (!users.contains(user)) {
               users.add(user);
            }
         }
         if (workflow.isCompleted()) {
            AtsUser user = workflow.getCompletedBy();
            if (user != null && !users.contains(user)) {
               users.add(user);
            }
         }
      }
      return users;
   }

}
