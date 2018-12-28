/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile.kanban;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.agile.JaxAgileSprint;

/**
 * @author Donald G. Dunne
 */
public class JaxKbSprint extends JaxAgileSprint {

   List<JaxKbRowIdToTaskId> rowToTaskIds = new LinkedList<>();
   Map<String, String> rowIdToName = new HashMap<>();
   Map<String, String> userIdToName = new HashMap<>();
   Map<String, String> userNameToId = new HashMap<>();
   List<String> teamMembersOrdered = new LinkedList<>();
   List<String> otherMembersOrdered = new LinkedList<>();
   List<JaxKbState> statesToTaskIds = new LinkedList<>();
   List<JaxKbAvailableState> availableStates = new LinkedList<>();
   Map<String, JaxKbTask> tasks = new HashMap<>();
   KanbanRowType rowType;

   public void addRowIdToTaskId(String rowId, String taskId) {
      JaxKbRowIdToTaskId rowToId = null;
      for (JaxKbRowIdToTaskId item : rowToTaskIds) {
         if (item.getRowId().equals(rowId)) {
            rowToId = item;
            break;
         }
      }
      if (rowToId == null) {
         rowToId = new JaxKbRowIdToTaskId();
         rowToId.setRowId(rowId);
         rowToTaskIds.add(rowToId);
      }
      rowToId.getTaskIds().add(taskId);
   }

   public Map<String, String> getUserIdToName() {
      return userIdToName;
   }

   public void setUserIdToName(Map<String, String> userIdToName) {
      this.userIdToName = userIdToName;
   }

   public void addStateNameToTaskId(String stateName, String taskId) {
      JaxKbState state = null;
      for (JaxKbState state2 : statesToTaskIds) {
         if (state2.getName().equals(stateName)) {
            state = state2;
            break;
         }
      }
      if (state == null) {
         state = new JaxKbState();
         state.setName(stateName);
         statesToTaskIds.add(state);
      }
      state.getTaskIds().add(taskId);
   }

   public Map<String, JaxKbTask> getTasks() {
      return tasks;
   }

   public void setTasks(Map<String, JaxKbTask> tasks) {
      this.tasks = tasks;
   }

   public List<JaxKbAvailableState> getAvailableStates() {
      return availableStates;
   }

   public void setAvailableStates(List<JaxKbAvailableState> availableStates) {
      this.availableStates = availableStates;
   }

   public List<JaxKbRowIdToTaskId> getAssigneesToTaskIds() {
      return rowToTaskIds;
   }

   public List<JaxKbState> getStatesToTaskIds() {
      return statesToTaskIds;
   }

   public void setStatesToTaskIds(List<JaxKbState> statesToTaskIds) {
      this.statesToTaskIds = statesToTaskIds;
   }

   public Map<String, String> getUserNameToId() {
      return userNameToId;
   }

   public void setUserNameToId(Map<String, String> userNameToId) {
      this.userNameToId = userNameToId;
   }

   public List<JaxKbRowIdToTaskId> getRowToTaskIds() {
      return rowToTaskIds;
   }

   public void setRowToTaskIds(List<JaxKbRowIdToTaskId> rowToTaskIds) {
      this.rowToTaskIds = rowToTaskIds;
   }

   public Map<String, String> getRowIdToName() {
      return rowIdToName;
   }

   public void setRowIdToName(Map<String, String> rowIdToName) {
      this.rowIdToName = rowIdToName;
   }

   public KanbanRowType getRowType() {
      return rowType;
   }

   public void setRowType(KanbanRowType rowType) {
      this.rowType = rowType;
   }

   public List<String> getTeamMembersOrdered() {
      return teamMembersOrdered;
   }

   public void setTeamMembersOrdered(List<String> teamMembersOrdered) {
      this.teamMembersOrdered = teamMembersOrdered;
   }

   public List<String> getOtherMembersOrdered() {
      return otherMembersOrdered;
   }

   public void setOtherMembersOrdered(List<String> otherMembersOrdered) {
      this.otherMembersOrdered = otherMembersOrdered;
   }

}
