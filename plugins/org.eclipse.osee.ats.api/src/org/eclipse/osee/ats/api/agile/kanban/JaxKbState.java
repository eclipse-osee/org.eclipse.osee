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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxKbState {

   private String name;
   private List<String> taskIds = new ArrayList<>();

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<String> getTaskIds() {
      return taskIds;
   }

   public void setTaskIds(List<String> taskIds) {
      this.taskIds = taskIds;
   }

}
