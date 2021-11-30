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

package org.eclipse.osee.ats.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsTask extends JaxAtsWorkItem {

   private String relatedToState;
   private Double hoursSpent = 0.0;

   public JaxAtsTask() {
      attributes = new ArrayList<>();
   }

   @Override
   public String toString() {
      return "JaxAtsTask [title=" + getName() + ", createdDate=" + createdDate + ", createdById=" + createdByUserId + ", assigneeIds=" + assigneeUserIds + ", relatedToState=" + relatedToState + "]";
   }

   public static JaxAtsTask createt(String title, AtsUser createdBy, Date createdDate) {
      JaxAtsTask task = new JaxAtsTask();
      task.setCreatedByUserId(createdBy.getUserId());
      task.setCreatedDate(createdDate);
      task.setName(title);
      return task;
   }

   public static JaxAtsTask create(NewTaskData newTaskData, String title, AtsUser createdBy, Date createdDate) {
      JaxAtsTask task = createt(title, createdBy, createdDate);
      newTaskData.getTasks().add(task);
      return task;
   }

   @Override
   @JsonIgnore
   public ArtifactToken getToken() {
      return ArtifactToken.valueOf(getId(), CoreBranches.COMMON);
   }

   public String getRelatedToState() {
      return relatedToState;
   }

   public void setRelatedToState(String relatedToState) {
      this.relatedToState = relatedToState;
   }

   public Double getHoursSpent() {
      return hoursSpent;
   }

   public void setHoursSpent(Double hoursSpent) {
      this.hoursSpent = hoursSpent;
   }
}
