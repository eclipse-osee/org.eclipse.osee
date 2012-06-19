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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.core.client.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.version.VersionArtifact;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Shawn F. Cook
 */
public class CreateTasksOperationService {
   private static Set<ITaskTitleProvider> taskTitleProviders = new HashSet<ITaskTitleProvider>();

   public static CreateTasksOperation getCreateTasksOpForTaskTitleProvider(VersionArtifact destinationVersion, ActionableItemArtifact actionableItemArt, ChangeData changeData, TeamWorkFlowArtifact reqTeamWf, boolean reportOnly, XResultData resultData, SkynetTransaction transaction, OperationLogger logger, String taskTitleProviderKey) {
      CreateTasksOperation createTasksOp = null;

      if (taskTitleProviders.size() > 0) {
         for (ITaskTitleProvider taskTitleProvider : taskTitleProviders) {
            if (taskTitleProvider.isKeySupported(taskTitleProviderKey)) {
               createTasksOp =
                  new CreateTasksOperation(destinationVersion, actionableItemArt, changeData, reqTeamWf, reportOnly,
                     resultData, transaction, logger, taskTitleProvider);
               break;
            }
         }
      } else {
         createTasksOp =
            new CreateTasksOperation(destinationVersion, actionableItemArt, changeData, reqTeamWf, reportOnly,
               resultData, transaction, logger, new TaskTitleProviderDefault());
      }

      return createTasksOp;
   }

   public void setTaskTitleProvider(ITaskTitleProvider taskTitleProvider) {
      taskTitleProviders.add(taskTitleProvider);
   }

   public void removeTaskTitleProvider(ITaskTitleProvider taskTitleProvider) {
      taskTitleProviders.remove(taskTitleProvider);
   }
}
