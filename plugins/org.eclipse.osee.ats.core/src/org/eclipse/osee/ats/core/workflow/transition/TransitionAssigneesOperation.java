/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.core.workflow.transition;

import static org.eclipse.osee.ats.api.user.AtsCoreUsers.UNASSIGNED_USER;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;

/**
 * @author Donald G. Dunne
 */
public class TransitionAssigneesOperation {

   private TransitionAssigneesOperation() {
      // Utility Class
   }

   public static Set<AtsUser> getToAssignees(Collection<AtsUser> currentAssignees, StateType fromStateType,
      StateType toStateType, TransitionData transData, Collection<AtsUser> reviewRollAssignees) {
      Set<AtsUser> toAssignees = new HashSet<>();
      if (toStateType.isWorking()) {

         // toAssignees overrides all
         Collection<AtsUser> requestedAssignees = transData.getToAssignees();
         if (requestedAssignees != null) {
            for (AtsUser user : requestedAssignees) {
               toAssignees.add(user);
            }
         }

         if (toAssignees.isEmpty() && currentAssignees != null) {
            toAssignees.addAll(currentAssignees);
            // Remove UnAssigned so remaining checks/sets can operate correctly
            toAssignees.remove(UNASSIGNED_USER);
         }

         // Always add review roles if specified
         if (!reviewRollAssignees.isEmpty()) {
            toAssignees.addAll(reviewRollAssignees);
         }

         if (toAssignees.size() > 1 && toAssignees.contains(UNASSIGNED_USER)) {
            toAssignees.remove(UNASSIGNED_USER);
         }

         if (toAssignees.isEmpty()) {
            if (fromStateType.isWorking() && transData.getTransitionUser() != null && !transData.isSystemUser()) {
               toAssignees.add(transData.getTransitionUser());
            } else {
               toAssignees.add(UNASSIGNED_USER);
            }
         }

      }
      return toAssignees;
   }

}
