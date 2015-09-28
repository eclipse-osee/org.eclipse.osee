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

import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;

/**
 * @author Shawn F. Cook
 */
public class GenerateTaskOpList {

   /**
    * Generate an ordered list of TaskMetadata objects which can be used to create or modify tasks based on the
    * following criteria:
    * <ul>
    * <li>If task doesn't exist yet for a change then create it</li>
    * <li>If task DOES exist but no change exists for the task, then modify the task note attribute to reflect this</li>
    * </ul>
    * If destTeamWf is NULL then all tasks will be CREATE.
    */
   public List<TaskMetadata> generate(ChangeData changeData, TeamWorkFlowArtifact destTeamWf) throws OseeCoreException {
      List<TaskMetadata> metadatas = new ArrayList<>();

      // If task doesn't exist yet for a change then create it
      for (Change change : changeData.getChanges()) {
         TaskArtifact taskArt = findTaskArtifactFor(change, destTeamWf);
         if (taskArt == null) {
            metadatas.add(new TaskMetadata(destTeamWf, null, change.getChangeArtifact(), TaskEnum.CREATE));
         }
      }

      if (destTeamWf != null) {
         // If task DOES exist but no change exists for the task, then modify the task note attribute to reflect this
         for (TaskArtifact taskArt : destTeamWf.getTaskArtifacts()) {
            Change change = findChangeFor(taskArt, changeData);
            if (change == null) {
               metadatas.add(new TaskMetadata(destTeamWf, taskArt, null, TaskEnum.MODIFY));
            }
         }
      }

      return metadatas;
   }

   private TaskArtifact findTaskArtifactFor(Change change, TeamWorkFlowArtifact destTeamWf) throws OseeCoreException {
      TaskArtifact retTaskArt = null;
      if (destTeamWf == null || change == null) {
         //Do nothing - return retTaskArt = null
      } else {
         Artifact changeArtifactFromChange = change.getChangeArtifact();
         if (changeArtifactFromChange == null) {
            OseeLog.log(
               Activator.class,
               Level.WARNING,
               "GenerateTaskOpList.findTaskArtifactFor() - WARNING #1: Change's ChangedArtifact is NULL.  Change:" + change.getName());
         } else {
            for (TaskArtifact taskArt : destTeamWf.getTaskArtifacts()) {
               Artifact changedArtFromTask =
                  taskArt.getSoleAttributeValue(AtsAttributeTypes.TaskToChangedArtifactReference);
               if (changeArtifactFromChange.equals(changedArtFromTask)) {
                  retTaskArt = taskArt;
                  break;
               }
            }
         }
      }
      return retTaskArt;
   }

   private Change findChangeFor(TaskArtifact taskArt, ChangeData changeData) throws OseeCoreException {
      Change retChange = null;
      Object changedArtFromTask = null;

      if (taskArt == null) {
         // Do nothing - return retChange = null
      } else {
         try {
            changedArtFromTask = taskArt.getSoleAttributeValue(AtsAttributeTypes.TaskToChangedArtifactReference);
         } catch (AttributeDoesNotExist e) {
            // Do nothing - return retChange = null
         }
         if (changedArtFromTask == null) {
            // Do nothing - return retChange = null
         } else {
            for (Change change : changeData.getChanges()) {
               Artifact changeArtFromChange = change.getChangeArtifact();
               if (changeArtFromChange.equals(changedArtFromTask)) {
                  retChange = change;
                  break;
               }
            }
         }
      }

      return retChange;
   }
}
