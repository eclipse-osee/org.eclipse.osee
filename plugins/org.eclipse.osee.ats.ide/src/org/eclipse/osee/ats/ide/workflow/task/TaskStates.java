/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class TaskStates extends StateTypeAdapter {
   public static TaskStates InWork = new TaskStates("InWork", StateType.Working);
   public static TaskStates Completed = new TaskStates("Completed", StateType.Completed);
   public static TaskStates Cancelled = new TaskStates("Cancelled", StateType.Cancelled);

   private TaskStates(String pageName, StateType StateType) {
      super(TaskStates.class, pageName, StateType);
   }

   public static TaskStates valueOf(String pageName) {
      return StateTypeAdapter.valueOfPage(TaskStates.class, pageName);
   }

   public List<TaskStates> values() {
      return StateTypeAdapter.pages(TaskStates.class);
   }

};
