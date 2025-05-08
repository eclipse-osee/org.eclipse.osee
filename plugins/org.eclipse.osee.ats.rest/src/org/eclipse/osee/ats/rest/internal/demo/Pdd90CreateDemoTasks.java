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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class Pdd90CreateDemoTasks extends AbstractPopulateDemoDatabase {

   public Pdd90CreateDemoTasks(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();
      boolean firstTaskWorkflow = true;
      NewTaskSet newTaskSet = NewTaskSet.create("Populate Demo DB - Create Tasks", createdBy);
      List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
      teamWfs.add(DemoUtil.getSawCodeCommittedWf());
      teamWfs.add(DemoUtil.getSawCodeUnCommittedWf());
      for (IAtsTeamWorkflow codeArt : teamWfs) {
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
      newTaskSet = atsApi.getTaskService().createTasks(newTaskSet);
      if (newTaskSet.isErrors()) {
         throw new OseeStateException("Task gen did not succeed [%s]", newTaskSet.getResults().toString());
      }
   }

}
