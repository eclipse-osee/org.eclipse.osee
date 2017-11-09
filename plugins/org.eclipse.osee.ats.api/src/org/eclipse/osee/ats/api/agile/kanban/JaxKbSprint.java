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

   List<JaxKbAssigneeIdToTaskId> assigneesToTaskIds = new LinkedList<JaxKbAssigneeIdToTaskId>();
   List<JaxKbAssigneeIdToTaskId> implementersToTaskIds = new LinkedList<JaxKbAssigneeIdToTaskId>();
   Map<String, String> userIdToName = new HashMap<String, String>();
   List<JaxKbState> statesToTaskIds = new LinkedList<JaxKbState>();
   List<JaxKbAvailableState> availableStates = new LinkedList<JaxKbAvailableState>();
   Map<String, JaxKbTask> tasks = new HashMap<String, JaxKbTask>();

   public void addAssigneeIdToTaskId(String assigneeId, String taskId) {
      JaxKbAssigneeIdToTaskId assigneeToId = null;
      for (JaxKbAssigneeIdToTaskId item : assigneesToTaskIds) {
         if (item.getAssigneeId().equals(assigneeId)) {
            assigneeToId = item;
            break;
         }
      }
      if (assigneeToId == null) {
         assigneeToId = new JaxKbAssigneeIdToTaskId();
         assigneeToId.setAssigneeId(assigneeId);
         assigneesToTaskIds.add(assigneeToId);
      }
      assigneeToId.getTaskIds().add(taskId);
   }

   public void addImplementerIdToTaskId(String implementerId, String taskId) {
      JaxKbAssigneeIdToTaskId implementerToId = null;
      for (JaxKbAssigneeIdToTaskId item : implementersToTaskIds) {
         if (item.getAssigneeId().equals(implementerId)) {
            implementerToId = item;
            break;
         }
      }
      if (implementerToId == null) {
         implementerToId = new JaxKbAssigneeIdToTaskId();
         implementerToId.setAssigneeId(implementerId);
         implementersToTaskIds.add(implementerToId);
      }
      implementerToId.getTaskIds().add(taskId);
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

   public List<JaxKbAssigneeIdToTaskId> getAssigneesToTaskIds() {
      return assigneesToTaskIds;
   }

   public void setAssigneesToTaskIds(List<JaxKbAssigneeIdToTaskId> assigneesToTaskIds) {
      this.assigneesToTaskIds = assigneesToTaskIds;
   }

   public List<JaxKbState> getStatesToTaskIds() {
      return statesToTaskIds;
   }

   public void setStatesToTaskIds(List<JaxKbState> statesToTaskIds) {
      this.statesToTaskIds = statesToTaskIds;
   }

   public List<JaxKbAssigneeIdToTaskId> getImplementersToTaskIds() {
      return implementersToTaskIds;
   }

}
