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
package org.eclipse.osee.ats.core.workflow.state;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.internal.state.StateManager;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G Dunne
 */
public class StateManagerUtility {

   /**
    * Initializes state machine and sets the current state to stateName
    */
   public static void initializeStateMachine(IAtsStateManager stateMgr, IStateToken workPage, List<? extends IAtsUser> assignees, IAtsUser currentUser, IAtsChangeSet changes) {
      stateMgr.createState(workPage.getName());
      stateMgr.setCurrentStateName(workPage.getName());
      if (assignees == null) {
         assignees = new LinkedList<>();
      }
      ((StateManager) stateMgr).setStateType(workPage.getStateType());
      if (workPage.getStateType().isWorkingState()) {
         if (assignees.isEmpty()) {
            if (currentUser.notEqual(AtsCoreUsers.SYSTEM_USER)) {
               stateMgr.setAssignees(workPage.getName(), workPage.getStateType(), Arrays.asList(currentUser));
            } else {
               stateMgr.setAssignees(workPage.getName(), workPage.getStateType(),
                  Arrays.asList(AtsCoreUsers.UNASSIGNED_USER));
            }
         } else {
            stateMgr.setAssignees(workPage.getName(), workPage.getStateType(), assignees);
         }
      }
   }

   public static Result isDirtyResult(IAtsWorkItem workItem, IAtsStateManager stateMgr, IAttributeResolver attrResolver, IAtsWorkStateFactory workStateFactory) {
      if (attrResolver.getAttributeCount(workItem, AtsAttributeTypes.CurrentState) == 0) {
         return new Result(true, "StateManager: Current State new");
      }
      if (!workStateFactory.toStoreStr(stateMgr, stateMgr.getCurrentStateName()).equals(
         attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null))) {
         return new Result(true, "StateManager: Current State modified");
      }
      for (String stateName : stateMgr.getVisitedStateNames()) {
         if (!stateName.equals(stateMgr.getCurrentStateName())) {
            boolean found = false;
            // Update attribute if it already exists
            for (String storedStateXml : attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.State)) {
               WorkState storedState = workStateFactory.fromStoreStr(storedStateXml);
               if (stateName.equals(storedState.getName())) {
                  found = true;
                  String currentStateXml = workStateFactory.toStoreStr(stateMgr, stateName);
                  if (!currentStateXml.equals(storedStateXml)) {
                     return new Result(true, String.format("StateManager: State [%s] modified was [%s] is [%s]",
                        stateName, storedStateXml, currentStateXml));
                  }
               }
            }
            // Else, doesn't exist yet so it's dirty
            if (!found) {
               return new Result(true, String.format("StateManager: State [%s] added", stateName));
            }
         }
      }
      return Result.FalseResult;
   }
}
