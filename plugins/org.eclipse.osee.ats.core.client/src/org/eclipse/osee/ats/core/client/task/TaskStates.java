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
package org.eclipse.osee.ats.core.client.task;

import java.util.List;
import org.eclipse.osee.ats.core.workflow.WorkPageAdapter;
import org.eclipse.osee.ats.core.workflow.WorkPageType;

public class TaskStates extends WorkPageAdapter {
   public static TaskStates InWork = new TaskStates("InWork", WorkPageType.Working);
   public static TaskStates Completed = new TaskStates("Completed", WorkPageType.Completed);
   public static TaskStates Cancelled = new TaskStates("Cancelled", WorkPageType.Cancelled);

   private TaskStates(String pageName, WorkPageType workPageType) {
      super(TaskStates.class, pageName, workPageType);
   }

   public static TaskStates valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(TaskStates.class, pageName);
   }

   public List<TaskStates> values() {
      return WorkPageAdapter.pages(TaskStates.class);
   }

};
