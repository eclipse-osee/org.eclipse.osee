/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.demo.populate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class Pdd90CreateDemoTasks {

   public void run() throws Exception {
      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      boolean firstTaskWorkflow = true;
      NewTaskSet newTaskSet = NewTaskSet.create("Populate Demo DB - Create Tasks", createdBy);
      List<TeamWorkFlowArtifact> teamWfs = new ArrayList<>();
      teamWfs.add((TeamWorkFlowArtifact) DemoUtil.getSawCodeCommittedWf());
      teamWfs.add((TeamWorkFlowArtifact) DemoUtil.getSawCodeUnCommittedWf());
      for (TeamWorkFlowArtifact codeArt : teamWfs) {
         NewTaskData newTaskData = NewTaskData.create(newTaskSet, codeArt);
         List<String> assigneeUserIds = new ArrayList<>();
         if (firstTaskWorkflow) {
            assigneeUserIds.add(DemoUsers.Joe_Smith.getUserId());
            assigneeUserIds.add(DemoUsers.Kay_Jones.getUserId());
         } else {
            assigneeUserIds.add(DemoUsers.Joe_Smith.getUserId());
         }
         for (String title : firstTaskWorkflow ? DemoUtil.Saw_Code_Committed_Task_Titles : DemoUtil.Saw_Code_UnCommitted_Task_Titles) {
            JaxAtsTask task = JaxAtsTask.create(newTaskData, title, createdBy, createdDate);
            task.setRelatedToState(codeArt.getCurrentStateName());
            task.setAssigneeUserIds(assigneeUserIds);
         }
         firstTaskWorkflow = false;
      }
      newTaskSet = AtsApiService.get().getTaskService().createTasks(newTaskSet);
      if (newTaskSet.isErrors()) {
         throw new OseeStateException("Task gen did not succeed [%s]", newTaskSet.getResults().toString());
      }
   }

}
