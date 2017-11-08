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
package org.eclipse.osee.ats.api.task;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

/**
 * @author Donald G. Dunne
 */
public class NewTaskData {

   @JsonSerialize(using = ToStringSerializer.class)
   Long teamWfId;
   List<JaxAtsTask> newTasks;
   String asUserId;
   String commitComment;

   public Long getTeamWfId() {
      return teamWfId;
   }

   public void setTeamWfId(Long teamWfId) {
      this.teamWfId = teamWfId;
   }

   public List<JaxAtsTask> getNewTasks() {
      if (newTasks == null) {
         newTasks = new ArrayList<>();
      }
      return newTasks;
   }

   public void setNewTasks(List<JaxAtsTask> newTasks) {
      this.newTasks = newTasks;
   }

   public String getCommitComment() {
      return commitComment;
   }

   public void setCommitComment(String commitComment) {
      this.commitComment = commitComment;
   }

   public String getAsUserId() {
      return asUserId;
   }

   public void setAsUserId(String asUserId) {
      this.asUserId = asUserId;
   }

   @Override
   public String toString() {
      return "NewTaskData [teamId=" + teamWfId + ", tasks=" + newTasks + ", asUserId=" + asUserId + ", commitComment=" + commitComment + "]";
   }

   public boolean isEmpty() {
      return newTasks == null || newTasks.isEmpty();
   }
}
