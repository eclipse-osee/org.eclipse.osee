/*********************************************************************
 * Copyright (c) 2017 Boeing
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
