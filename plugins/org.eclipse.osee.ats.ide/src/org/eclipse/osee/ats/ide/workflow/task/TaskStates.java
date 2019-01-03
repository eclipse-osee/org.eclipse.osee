/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
