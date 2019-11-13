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
package org.eclipse.osee.ats.core.workdef.builder;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.transition.TransitionAdapter;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class CreateChangeReportTaskTransitionListener extends TransitionAdapter {

   private final AtsTaskDefToken taskDefToken;

   public CreateChangeReportTaskTransitionListener(AtsTaskDefToken taskDefToken) {
      this.taskDefToken = taskDefToken;
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes) {

      Thread thread = new Thread("Create/Update Tasks") {
         @Override
         public void run() {
            super.run();
            ChangeReportTaskData data = new ChangeReportTaskData();
            data.setTaskDefToken(taskDefToken);
            data.setHostTeamWf(workItem.getStoreObject());
            AtsUser atsUser =
               AtsApiService.get().getUserService().getAtsUser(AtsApiService.get().getUserService().getCurrentUser());
            data.setAsUser(atsUser);
            ChangeReportTaskData createTasks = AtsApiService.get().getTaskService().createTasks(data);

            // Reload art to get new children
            ArtifactToken actionArt = AtsApiService.get().getQueryService().getArtifact(createTasks.getActionId());
            AtsApiService.get().getStoreService().reloadArts(Collections.singleton(actionArt));
            IAtsAction action = AtsApiService.get().getWorkItemService().getAction(actionArt);
            // Reload children team Wfs
            AtsApiService.get().getStoreService().reload(
               org.eclipse.osee.framework.jdk.core.util.Collections.castAll(action.getTeamWorkflows()));
         }
      };
      thread.start();
   }

}
