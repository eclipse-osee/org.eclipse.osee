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
package org.eclipse.osee.ats.api.task;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTaskService {

   Collection<IAtsTask> createTasks(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, String commitComment);

   Collection<IAtsTask> createTasks(NewTaskData newTaskData);

   Collection<IAtsTask> createTasks(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, IAtsChangeSet changes);

   NewTaskData getNewTaskData(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, String commitComment);

   Collection<IAtsTask> createTasks(NewTaskData newTaskData, IAtsChangeSet changes);

   Collection<IAtsTask> createTasks(NewTaskDatas newTaskDatas);

   NewTaskData getNewTaskData(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes);

   Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf, IStateToken relatedToState);

   Collection<IAtsTask> getTask(IAtsWorkItem workItem);

   Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf);

   Collection<? extends IAtsTask> getTasks(IAtsWorkItem workItem, IStateToken state);

   boolean isRelatedToState(IAtsTask task, String stateName);

   default boolean hasTasks(IAtsTeamWorkflow teamWf, IStateToken forState) {
      return getTasks(teamWf, forState).size() > 0;
   }

   /**
    * @return related ArtifactId or ArtifactId.SENTINAL if not exists
    */
   ArtifactId getTaskToRelatedArtifactChanged(IAtsTask task);

   default void decache(IAtsTeamWorkflow teamWf) {
      throw new UnsupportedOperationException();
   }

   boolean hasTasks(IAtsTeamWorkflow teamWf);

   default boolean hasNoTasks(IAtsTeamWorkflow teamWf) {
      return !hasTasks(teamWf);
   }

}
