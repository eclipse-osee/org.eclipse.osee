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

package org.eclipse.osee.ats.api.task;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskNameProviderToken;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.task.create.IAtsChangeReportTaskNameProvider;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTaskService {

   NewTaskSet createTasks(NewTaskSet newTaskSet);

   NewTaskSet createTasks(NewTaskSet newTaskSet, Map<Long, IAtsTeamWorkflow> idToTeamWf);

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

   boolean hasTasks(IAtsTeamWorkflow teamWf);

   default boolean hasNoTasks(IAtsTeamWorkflow teamWf) {
      return !hasTasks(teamWf);
   }

   Collection<WorkDefinition> calculateTaskWorkDefs(IAtsTeamWorkflow teamWf);

   default CreateTasksDefinitionBuilder createTasksSetDefinitionBuilder(AtsTaskDefToken taskSetToken) {
      return new CreateTasksDefinitionBuilder(taskSetToken);
   }

   Collection<CreateTasksDefinitionBuilder> getTaskSets(IAtsTeamWorkflow teamWf);

   ChangeReportTaskData createTasks(ChangeReportTaskData changeReportTaskData);

   IAtsChangeReportTaskNameProvider getChangeReportOptionNameProvider(ChangeReportTaskNameProviderToken token);

   IAtsTask getTask(ArtifactToken artifact);

   boolean isAutoGen(IAtsTask task);

   /**
    * @return true if task is autogen, but de-referenced (no longer have matching change report artifact) so user can
    * delete
    */
   boolean isAutoGenDeReferenced(IAtsTask task);

   void addDeReferencedNote(IAtsTask task, IAtsChangeSet changes);

   boolean removeDeReferencedNote(IAtsTask task, IAtsChangeSet changes);

   Collection<IAtsTaskProvider> getTaskProviders();

}
