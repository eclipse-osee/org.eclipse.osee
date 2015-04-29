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

   List<JaxKbAssigneeIdToTaskUuid> assigneesToTaskUuids = new LinkedList<JaxKbAssigneeIdToTaskUuid>();
   List<JaxKbAssigneeIdToTaskUuid> implementersToTaskUuids = new LinkedList<JaxKbAssigneeIdToTaskUuid>();
   Map<String, String> userIdToName = new HashMap<String, String>();
   List<JaxKbState> statesToTaskUuids = new LinkedList<JaxKbState>();
   List<JaxKbAvailableState> availableStates = new LinkedList<JaxKbAvailableState>();
   Map<String, JaxKbTask> tasks = new HashMap<String, JaxKbTask>();

   public void addAssigneeIdToTaskUuid(String assigneeId, String taskUuid) {
      JaxKbAssigneeIdToTaskUuid assigneeToUuid = null;
      for (JaxKbAssigneeIdToTaskUuid item : assigneesToTaskUuids) {
         if (item.getAssigneeId().equals(assigneeId)) {
            assigneeToUuid = item;
            break;
         }
      }
      if (assigneeToUuid == null) {
         assigneeToUuid = new JaxKbAssigneeIdToTaskUuid();
         assigneeToUuid.setAssigneeId(assigneeId);
         assigneesToTaskUuids.add(assigneeToUuid);
      }
      assigneeToUuid.getTaskUuids().add(taskUuid);
   }

   public void addImplementerIdToTaskUuid(String implementerId, String taskUuid) {
      JaxKbAssigneeIdToTaskUuid implementerToUuid = null;
      for (JaxKbAssigneeIdToTaskUuid item : implementersToTaskUuids) {
         if (item.getAssigneeId().equals(implementerId)) {
            implementerToUuid = item;
            break;
         }
      }
      if (implementerToUuid == null) {
         implementerToUuid = new JaxKbAssigneeIdToTaskUuid();
         implementerToUuid.setAssigneeId(implementerId);
         implementersToTaskUuids.add(implementerToUuid);
      }
      implementerToUuid.getTaskUuids().add(taskUuid);
   }

   public Map<String, String> getUserIdToName() {
      return userIdToName;
   }

   public void setUserIdToName(Map<String, String> userIdToName) {
      this.userIdToName = userIdToName;
   }

   public void addStateNameToTaskUuid(String stateName, String taskUuid) {
      JaxKbState state = null;
      for (JaxKbState state2 : statesToTaskUuids) {
         if (state2.getName().equals(stateName)) {
            state = state2;
            break;
         }
      }
      if (state == null) {
         state = new JaxKbState();
         state.setName(stateName);
         statesToTaskUuids.add(state);
      }
      state.getTaskUuids().add(taskUuid);
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

   public List<JaxKbAssigneeIdToTaskUuid> getAssigneesToTaskUuids() {
      return assigneesToTaskUuids;
   }

   public void setAssigneesToTaskUuids(List<JaxKbAssigneeIdToTaskUuid> assigneesToTaskUuids) {
      this.assigneesToTaskUuids = assigneesToTaskUuids;
   }

   public List<JaxKbState> getStatesToTaskUuids() {
      return statesToTaskUuids;
   }

   public void setStatesToTaskUuids(List<JaxKbState> statesToTaskUuids) {
      this.statesToTaskUuids = statesToTaskUuids;
   }

   public List<JaxKbAssigneeIdToTaskUuid> getImplementersToTaskUuids() {
      return implementersToTaskUuids;
   }

}
