/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.core.internal.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class StateManager implements IAtsStateManager {

   private final IAtsWorkItem workItem;
   private List<WorkState> states;
   private TransactionId transaction;
   private final AtsApi atsApi;

   public StateManager(IAtsWorkItem workItem, AtsApi atsApi) {
      this.workItem = workItem;
      this.atsApi = atsApi;
      this.transaction = atsApi.getStoreService().getTransactionId(workItem);
   }

   @Override
   public void transitionHelper(IStateToken fromState, IStateToken toState) {
      ensureLoaded();
      getOrCreateState(toState.getName());
   }

   private WorkState getStateOrNull(String stateName) {
      Conditions.assertNotNullOrEmpty(stateName, "state can not be null for %s", workItem.toStringWithId());
      ensureLoaded();
      for (WorkState state : states) {
         if (state.getName().equals(stateName)) {
            return state;
         }
      }
      return null;
   }

   @Override
   public Collection<AtsUser> getAssignees(String stateName) {
      ensureLoaded();
      WorkState state = getStateOrNull(stateName);
      if (state != null) {
         return state.getAssignees();
      }
      return Collections.emptyList();
   }

   private WorkState getOrCreateState(String stateName) {
      Conditions.assertNotNullOrEmpty(stateName, "stateName can not be null for %s", workItem.toStringWithId());
      ensureLoaded();
      WorkState state = getStateOrNull(stateName);
      if (state == null) {
         state = WorkState.create(workItem, stateName, false);
         addState(state);
      }
      return state;
   }

   @Override
   public void setCurrentState(String stateName) {
      Conditions.assertNotNullOrEmpty(stateName, "state can not be null for %s", workItem.toStringWithId());
      ensureLoaded();
      for (WorkState state : states) {
         if (state.getName().equals(stateName)) {
            state.setCurrentState(true);
         } else {
            state.setCurrentState(false);
         }
      }
   }

   @Override
   public Collection<AtsUser> getAssignees(IStateToken state) {
      ensureLoaded();
      return getAssignees(state.getName());
   }

   public void addState(WorkState workState) {
      ensureLoaded();
      addState(workState, true);
   }

   protected void addState(WorkState state, boolean logError) {
      WorkState state2 = getStateOrNull(state.getName());
      if (state2 != null) {
         String errorStr =
            String.format("Error: State [%s] already exists for [%s]", state.getName(), workItem.getAtsId());
         if (logError) {
            OseeLog.log(StateManager.class, Level.SEVERE, errorStr);
         }
         return;
      } else {
         states.add(state);
      }
   }

   /**
    * Just load attrs into WorkStates but don't resolve until needed
    */
   private synchronized void ensureLoaded() {
      TransactionId currTransactionId = TransactionId.SENTINEL;
      if (states != null) {
         currTransactionId = atsApi.getStoreService().getTransactionId(workItem);
         if (!currTransactionId.equals(transaction)) {
            states = null;
         }
      }
      if (states == null) {
         states = new ArrayList<>();
         for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(workItem,
            AtsAttributeTypes.CurrentState)) {
            WorkState currentState = WorkState.create(workItem, attr, true);
            states.add(currentState);
         }
         for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(workItem,
            AtsAttributeTypes.State)) {
            WorkState state = WorkState.create(workItem, attr, false);
            states.add(state);
         }
         transaction = currTransactionId;
      }
   }

   @Override
   public void createOrUpdateState(String stateName, Collection<AtsUser> assignees) {
      Conditions.assertNotNullOrEmpty(stateName, "state can not be null for %s", workItem.toStringWithId());
      ensureLoaded();
      WorkState state = getOrCreateState(stateName);
      if (assignees != null) {
         state.setAssignees(assignees);
      }
   }

   @Override
   public void clearCaches() {
      states = null;
      transaction = TransactionId.SENTINEL;
   }

   @Override
   public void addAssignee(AtsUser user) {
      ensureLoaded();
      WorkState state = getOrCreateState(workItem.getCurrentStateName());
      state.addAssignee(user);
   }

   @Override
   public void createOrUpdateState(IStateToken state) {
      Conditions.checkNotNull(state, "state");
      Conditions.assertNotNullOrEmpty(state.getName(), "state can not be null for %s", workItem.toStringWithId());
      ensureLoaded();
      getOrCreateState(state.getName());
   }

   @Override
   public void writeToStore(IAtsChangeSet changes) {
      WorkState currWorkState = getCurrentState();
      if (currWorkState == null) {
         throw new OseeStateException("Current WorkState can not be null for %s", workItem.toStringWithId());
      }
      for (WorkState state : states) {
         if (state.isCurrentState()) {
            if (state.getAttr() != null && state.getAttr().getAttributeType().equals(AtsAttributeTypes.State)) {
               changes.deleteAttribute(workItem, state.getAttr());
            }
            changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, state.getStoreStr());
         } else {
            if (state.getAttr() == null || state.getAttr().getAttributeType().equals(AtsAttributeTypes.CurrentState)) {
               changes.addAttribute(workItem, AtsAttributeTypes.State, state.getStoreStr());
            } else {
               changes.setAttribute(workItem, state.getAttr(), state.getStoreStr());
            }
         }
      }
      clearCaches();
   }

   private WorkState getCurrentState() {
      ensureLoaded();
      for (WorkState state : states) {
         if (state.isCurrentState()) {
            return state;
         }
      }
      return null;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Transaction: ");
      sb.append(transaction.toString());
      sb.append(" - ");
      for (WorkState state : states) {
         if (state.isCurrentState()) {
            sb.append("Curr ");
         }
         sb.append("State: ");
         sb.append(state.toString());
         sb.append(" - ");
      }
      return sb.toString();
   }
}
