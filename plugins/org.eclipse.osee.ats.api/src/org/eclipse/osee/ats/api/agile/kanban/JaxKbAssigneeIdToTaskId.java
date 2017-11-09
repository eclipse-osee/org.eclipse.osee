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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class JaxKbAssigneeIdToTaskId {

   private String assigneeId;
   private Set<String> taskIds = new HashSet<String>();

   public Set<String> getTaskIds() {
      return taskIds;
   }

   public void setTaskIds(Set<String> taskIds) {
      this.taskIds = taskIds;
   }

   public String getAssigneeId() {
      return assigneeId;
   }

   public void setAssigneeId(String assigneeId) {
      this.assigneeId = assigneeId;
   }

}
