/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Contributed through StateDefBuilder
 *
 * @author Donald G. Dunne
 */
public class CreateChangeReportTaskTransitionHook implements IAtsTransitionHook {

   private final AtsTaskDefToken taskDefToken;

   public CreateChangeReportTaskTransitionHook(AtsTaskDefToken taskDefToken) {
      this.taskDefToken = taskDefToken;
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes) {
      Thread thread = new Thread("Create/Update Tasks") {
         @Override
         public void run() {
            super.run();
            ChangeReportTaskData data = runChangeReportTaskOperation(workItem, taskDefToken, changes);
            if (data.getResults().isErrors()) {
               throw new OseeArgumentException(data.getResults().toString());
            }
         }

      };
      if (AtsUtil.isInTest()) {
         thread.run();
      } else {
         thread.start();
      }
   }

   public static ChangeReportTaskData runChangeReportTaskOperation(IAtsWorkItem workItem, AtsTaskDefToken taskDefToken, IAtsChangeSet changes) {
      ChangeReportTaskData data = new ChangeReportTaskData();
      data.setTaskDefToken(taskDefToken);
      data.setHostTeamWf(workItem.getStoreObject());
      AtsUser atsUser =
         AtsApiService.get().getUserService().getAtsUser(AtsApiService.get().getUserService().getCurrentUser());
      data.setAsUser(atsUser);

      /**
       * Until all transitions are done on server, need to directly call this operation so it's part of the full
       * IAtsChangeSet. Otherwise transitioning will reload teamWfs and tasks after task creation.
       */
      CreateChangeReportTasksOperation operation =
         new CreateChangeReportTasksOperation(data, AtsApiService.get(), changes);
      operation.run();
      return data;
   }

   @Override
   public String getDescription() {
      return "Checks for and runs Change Report Task Set Definitions during tranisition";
   }

}
