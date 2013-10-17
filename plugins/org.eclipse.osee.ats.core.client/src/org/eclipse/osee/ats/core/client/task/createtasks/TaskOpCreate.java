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

import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Shawn F. Cook
 */
public class TaskOpCreate extends AbstractTaskOp {
   private final ITaskTitleProvider taskTitleProvider;

   public TaskOpCreate(ITaskTitleProvider taskTitleProvider) {
      super();
      this.taskTitleProvider = taskTitleProvider;
   }

   @Override
   public IStatus execute(TaskMetadata metadata, IAtsChangeSet changes) throws OseeCoreException {
      TeamWorkFlowArtifact parentTeamWf = metadata.getParentTeamWf();
      Artifact changedArtifact = metadata.getChangedArtifact();
      String taskTitle = taskTitleProvider.getTaskTitle(metadata);
      Date creationDate = new Date();

      TaskArtifact taskArt = parentTeamWf.createNewTask(taskTitle, creationDate, AtsCoreUsers.SYSTEM_USER);

      // create for Implement state regardless of which state workflow is in
      taskArt.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, TeamState.Implement.getName());
      taskArt.setSingletonAttributeValue(CoreAttributeTypes.StaticId, AUTO_GENERATED_STATIC_ID);
      taskArt.setSoleAttributeFromString(AtsAttributeTypes.TaskToChangedArtifactReference, changedArtifact.getGuid());

      changes.add(taskArt);
      changes.add(parentTeamWf);

      return generateGenericOkStatus(metadata.getTaskEnum(), taskArt.toStringWithId(),
         metadata.getParentTeamWf().toStringWithId(), changedArtifact.toStringWithId());
   }
}
