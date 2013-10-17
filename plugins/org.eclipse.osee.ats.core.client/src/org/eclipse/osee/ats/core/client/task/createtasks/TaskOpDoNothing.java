/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.task.createtasks;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;

/**
 * @author Shawn F. Cook
 */
public class TaskOpDoNothing extends AbstractTaskOp {
   private final ITaskTitleProvider taskTitleProvider;

   public TaskOpDoNothing(ITaskTitleProvider taskTitleProvider) {
      super();
      this.taskTitleProvider = taskTitleProvider;
   }

   @Override
   public IStatus execute(TaskMetadata metadata, IAtsChangeSet changes) {
      String taskTitle = taskTitleProvider.getTaskTitle(metadata);
      String changedArt = "[no changed artifact]";
      return generateGenericOkStatus(metadata.getTaskEnum(), taskTitle, metadata.getParentTeamWf().toStringWithId(),
         changedArt);
   }

}
