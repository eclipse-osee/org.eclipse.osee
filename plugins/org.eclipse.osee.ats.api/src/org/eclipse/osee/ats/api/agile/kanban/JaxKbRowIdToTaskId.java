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
public class JaxKbRowIdToTaskId {

   private String rowId;
   private Set<String> taskIds = new HashSet<>();

   public Set<String> getTaskIds() {
      return taskIds;
   }

   public void setTaskIds(Set<String> taskIds) {
      this.taskIds = taskIds;
   }

   public String getRowId() {
      return rowId;
   }

   public void setRowId(String rowId) {
      this.rowId = rowId;
   }

}
