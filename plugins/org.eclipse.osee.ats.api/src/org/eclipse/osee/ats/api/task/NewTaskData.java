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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class NewTaskData {

   Long teamWfUuid;
   List<JaxAtsTask> newTasks;
   String asUserId;
   String commitComment;

   public Long getTeamWfUuid() {
      return teamWfUuid;
   }

   public void setTeamWfUuid(Long teamWfUuid) {
      this.teamWfUuid = teamWfUuid;
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
      return "NewTaskData [teamUuid=" + teamWfUuid + ", tasks=" + newTasks + ", asUserId=" + asUserId + ", commitComment=" + commitComment + "]";
   }

   public boolean isEmpty() {
      return newTasks == null || newTasks.isEmpty();
   }
}
