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

import java.util.Arrays;
import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamState;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.users.AtsUsers;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Shawn F. Cook
 */
public class TaskOpCreate implements ITaskOperation {
   private final ITaskTitleProvider taskTitleProvider;

   public TaskOpCreate(ITaskTitleProvider taskTitleProvider) {
      super();
      this.taskTitleProvider = taskTitleProvider;
   }

   @Override
   public IStatus execute(TaskMetadata metadata) throws OseeCoreException {
      TeamWorkFlowArtifact parentTeamWf = metadata.getParentTeamWf();
      String taskTitle = taskTitleProvider.getTaskTitle(metadata);
      Date creationDate = new Date();
      TaskArtifact taskArt =
         parentTeamWf.createNewTask(Arrays.asList(AtsUsers.getUnAssigned()), taskTitle, creationDate,
            AtsUsers.getSystemUser());

      // create for Implement state regardless of which state workflow is in
      taskArt.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, TeamState.Implement.getPageName());
      taskArt.setSingletonAttributeValue(CoreAttributeTypes.StaticId, AUTO_GENERATED_STATIC_ID);
      return Status.OK_STATUS;
   }

}
