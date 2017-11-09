/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileItem {

   private final List<Long> ids = new ArrayList<>();
   private final List<Long> features = new ArrayList<>();
   private long sprintId = 0;
   private long backlogId = 0;
   private boolean setFeatures = false;
   private boolean removeFeatures = false;
   private boolean setSprint = false;
   private boolean setBacklog = false;
   private String toState = null;
   private List<String> toStateUsers = new ArrayList<String>();

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

}
