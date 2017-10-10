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
package org.eclipse.osee.ats.core.client.task;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class TaskMover {

   private final IAtsTeamWorkflow newParent;
   private final List<? extends IAtsTask> tasks;

   public TaskMover(IAtsTeamWorkflow newParent, List<? extends IAtsTask> tasks) {
      this.newParent = newParent;
      this.tasks = tasks;
   }

   public Result moveTasks() {
      // Move Tasks
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Drop Add Tasks");
      for (IAtsTask task : tasks) {
         TaskArtifact taskArt = (TaskArtifact) task;
         taskArt.clearCaches();
         if (taskArt.getParentAWA() != null) {
            changes.unrelateAll(taskArt, AtsRelationTypes.TeamWfToTask_TeamWf);
         }
         changes.relate(newParent, AtsRelationTypes.TeamWfToTask_Task, taskArt);
         taskArt.clearCaches();
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return Result.TrueResult;
   }

}
