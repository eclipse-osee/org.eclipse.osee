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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;

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
   public String getImplementersStr(IAtsWorkItem workItem) {
      Collection<AtsUser> implementers = getImplementers(workItem);
      return implementers.isEmpty() ? "" : AtsObjects.toString("; ", implementers);
   }

   @Override
   public Collection<AtsUser> getImplementers(IAtsWorkItem workItem) {
      Collection<AtsUser> implementers = getImplementersNew(workItem);
      // TBD This should be removed when databases are converted to have ats.Implementers
      if (implementers.isEmpty()) {
         for (AtsUser user : getWorkItemImplementers(workItem)) {
            if (!implementers.contains(user)) {
               implementers.add(user);
            }
         }
      }
      implementers.remove(AtsCoreUsers.UNASSIGNED_USER);
      return implementers;
   }

   /**
    * Return implementers from attribute. This will be the only way in the future, all other methods will be removed
    * once the attribute is set for all workitems
    */
   private Collection<AtsUser> getImplementersNew(IAtsWorkItem workItem) {
      Set<AtsUser> implementers = new HashSet<>();
      Collection<String> attributes =
         AtsApiService.get().getAttributeResolver().getAttributeValues(workItem, AtsAttributeTypes.Implementer);
      for (String artIdStr : attributes) {
         AtsUser user = AtsApiService.get().getUserService().getUserById(ArtifactId.valueOf(artIdStr));
         if (user != null) {
            implementers.add(user);
         }
      }
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
      IStateToken fromState = workItem.getStateDefinition();
      for (AtsUser user : workItem.getAssignees(fromState)) {
         if (!implementers.contains(user)) {
            implementers.add(user);
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

   public List<AtsUser> getImplementersByState(IAtsWorkItem workItem, IStateToken state) {
      List<AtsUser> users = new ArrayList<>();
      if (workItem.isCancelled()) {
         users.add(workItem.getCancelledBy());
      } else {
         for (AtsUser user : workItem.getAssignees(state)) {
            if (!users.contains(user)) {
               users.add(user);
            }
         }
         if (workItem.isCompleted()) {
            AtsUser user = workItem.getCompletedBy();
            if (user != null && !users.contains(user)) {
               users.add(user);
            }
         }
      }
      return users;
   }

}
