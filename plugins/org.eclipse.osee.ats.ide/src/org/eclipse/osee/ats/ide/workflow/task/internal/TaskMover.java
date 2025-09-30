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

package org.eclipse.osee.ats.ide.workflow.task.internal;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
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
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Drop Add Tasks");
      for (IAtsTask task : tasks) {
         TaskArtifact taskArt = (TaskArtifact) task;
         taskArt.clearCaches();
         if (newParent.equals(taskArt.getParentAWA())) {
            continue;
         }
         if (taskArt.getParentAWA() != null) {
            changes.unrelateAll(taskArt, AtsRelationTypes.TeamWfToTask_TeamWorkflow);
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
