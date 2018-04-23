/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo.populate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTaskFactory;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDataFactory;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;

/**
 * @author Donald G. Dunne
 */
public class Pdd90CreateDemoTasks {

   public void run() throws Exception {
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      boolean firstTaskWorkflow = true;
      NewTaskDatas newTaskDatas = new NewTaskDatas();
      for (TeamWorkFlowArtifact codeArt : Arrays.asList(DemoUtil.getSawCodeCommittedWf(),
         DemoUtil.getSawCodeUnCommittedWf())) {
         NewTaskData newTaskData = NewTaskDataFactory.get("Populate Demo DB - Create Tasks", createdBy, codeArt);
         List<String> assigneeUserIds = new ArrayList<>();
         if (firstTaskWorkflow) {
            assigneeUserIds.add(DemoUsers.Joe_Smith.getUserId());
            assigneeUserIds.add(DemoUsers.Kay_Jones.getUserId());
         } else {
            assigneeUserIds.add(DemoUsers.Joe_Smith.getUserId());
         }
         for (String title : firstTaskWorkflow ? DemoUtil.Saw_Code_Committed_Task_Titles : DemoUtil.Saw_Code_UnCommitted_Task_Titles) {
            JaxAtsTask task = JaxAtsTaskFactory.get(newTaskData, title, createdBy, createdDate);
            task.setRelatedToState(codeArt.getCurrentStateName());
            task.setAssigneeUserIds(assigneeUserIds);
         }
         firstTaskWorkflow = false;
         newTaskDatas.add(newTaskData);
      }
      AtsClientService.get().getTaskService().createTasks(newTaskDatas);
   }

}
