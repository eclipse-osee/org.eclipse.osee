/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.model.IActionGroup;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
public class ImplementersColumn implements ImplementersStringProvider {

   public static ImplementersColumn instance = new ImplementersColumn();

   @Override
   public String getImplementersStr(IAtsObject atsObject) throws OseeCoreException {
      List<IAtsUser> implementers = getImplementers(atsObject);
      return implementers.isEmpty() ? "" : AtsObjects.toString("; ", implementers);
   }

   public List<IAtsUser> getImplementers(IAtsObject atsObject) throws OseeCoreException {
      List<IAtsUser> implementers = new LinkedList<>();
      if (atsObject instanceof IActionGroup) {
         implementers.addAll(getActionGroupImplementers((IActionGroup) atsObject));
      } else if (atsObject instanceof IAtsWorkItem) {
         implementers.addAll(getWorkItemImplementers((IAtsWorkItem) atsObject));
      }
      implementers.remove(AtsCoreUsers.UNASSIGNED_USER);
      Collections.sort(implementers);
      return implementers;
   }

   public List<IAtsUser> getWorkItemImplementers(IAtsWorkItem workItem) throws OseeCoreException {
      List<IAtsUser> implementers = new ArrayList<>();
      getImplementers_fromWorkItem(workItem, implementers);
      getImplementers_fromCompletedCancelledBy(workItem, implementers);
      getImplementers_fromCompletedCancelledFrom(workItem, implementers);
      return implementers;
   }

   public void getImplementers_fromCompletedCancelledFrom(IAtsWorkItem workItem, List<IAtsUser> implementers) throws OseeCoreException {
      String fromStateName = null;
      if (workItem.getStateMgr().getStateType().isCompleted()) {
         fromStateName = workItem.getCompletedFromState();
      } else if (workItem.getStateMgr().getStateType().isCancelled()) {
         fromStateName = workItem.getCancelledFromState();
      }
      if (Strings.isValid(fromStateName)) {
         for (IAtsUser user : workItem.getStateMgr().getAssigneesForState(fromStateName)) {
            if (!implementers.contains(user)) {
               implementers.add(user);
            }
         }
      }
   }

   public void getImplementers_fromCompletedCancelledBy(IAtsWorkItem workItem, List<IAtsUser> implementers) throws OseeCoreException {
      if (workItem.getStateMgr().getStateType().isCompletedOrCancelled()) {
         if (workItem.getStateMgr().getStateType().isCompleted()) {
            IAtsUser completedBy = workItem.getCompletedBy();
            if (completedBy != null && !implementers.contains(completedBy)) {
               implementers.add(completedBy);
            }
         }
         if (workItem.getStateMgr().getStateType().isCancelled()) {
            IAtsUser cancelledBy = workItem.getCancelledBy();
            if (cancelledBy != null && !implementers.contains(cancelledBy)) {
               implementers.add(cancelledBy);
            }
         }
      }
   }

   public void getImplementers_fromWorkItem(IAtsWorkItem workItem, List<IAtsUser> implementers) throws OseeCoreException {
      for (IAtsUser user : workItem.getImplementers()) {
         if (!implementers.contains(user)) {
            implementers.add(user);
         }
      }
   }

   public List<IAtsUser> getActionGroupImplementers(IActionGroup actionGroup) throws OseeCoreException {
      List<IAtsUser> implementers = new LinkedList<>();
      for (IAtsWorkItem action : actionGroup.getActions()) {
         if (action.getStateMgr().getStateType().isCompletedOrCancelled()) {
            for (IAtsUser user : getWorkItemImplementers(action)) {
               if (!implementers.contains(user)) {
                  implementers.add(user);
               }
            }
         }
      }
      return implementers;
   }
}
