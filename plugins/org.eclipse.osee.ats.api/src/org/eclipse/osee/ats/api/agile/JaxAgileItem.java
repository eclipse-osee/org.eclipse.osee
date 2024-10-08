/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.agile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileItem extends JaxAgileObject {

   private final List<Long> ids = new ArrayList<>();
   private final List<Long> features = new ArrayList<>();
   private long sprintId = 0;
   private long backlogId = 0;
   private boolean setFeatures = false;
   private boolean removeFeatures = false;
   private boolean setSprint = false;
   private boolean setBacklog = false;
   private String toState = null;
   private List<String> toStateUsers = new ArrayList<>();

   private boolean setAssignees = false;
   private List<String> assigneesAccountIds = new ArrayList<>();
   private String assigneesStr = "";
   private String assigneesStrShort = "";

   public List<Long> getFeatures() {
      return features;
   }

   public long getSprintId() {
      return sprintId;
   }

   public void setSprintId(long sprintId) {
      this.sprintId = sprintId;
   }

   public List<Long> getIds() {
      return ids;
   }

   public boolean isSetFeatures() {
      return setFeatures;
   }

   public void setSetFeatures(boolean setFeatures) {
      this.setFeatures = setFeatures;
   }

   public boolean isSetSprint() {
      return setSprint;
   }

   public void setSetSprint(boolean setSprint) {
      this.setSprint = setSprint;
   }

   public boolean isSetBacklog() {
      return setBacklog;
   }

   public void setSetBacklog(boolean setBacklog) {
      this.setBacklog = setBacklog;
   }

   public long getBacklogId() {
      return backlogId;
   }

   public void setBacklogId(long backlogId) {
      this.backlogId = backlogId;
   }

   public boolean isRemoveFeatures() {
      return removeFeatures;
   }

   public void setRemoveFeatures(boolean removeFeatures) {
      this.removeFeatures = removeFeatures;
   }

   public void setToState(String toState) {
      this.toState = toState;
   }

   public List<String> getToStateUsers() {
      return toStateUsers;
   }

   public void setToStateUsers(List<String> toStateUsers) {
      this.toStateUsers = toStateUsers;
   }

   public String getToState() {
      return toState;
   }

   public boolean isSetAssignees() {
      return setAssignees;
   }

   public void setSetAssignees(boolean setAssignees) {
      this.setAssignees = setAssignees;
   }

   public List<String> getAssigneesAccountIds() {
      return assigneesAccountIds;
   }

   public void setAssigneesAccountIds(List<String> assigneesAccountIds) {
      this.assigneesAccountIds = assigneesAccountIds;
   }

   public String getAssigneesStr() {
      return assigneesStr;
   }

   public void setAssigneesStr(String assigneesStr) {
      this.assigneesStr = assigneesStr;
   }

   public String getAssigneesStrShort() {
      return assigneesStrShort;
   }

   public void setAssigneesStrShort(String assigneesStrShort) {
      this.assigneesStrShort = assigneesStrShort;
   }

}
