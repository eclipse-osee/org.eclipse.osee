/*
 * Created on Aug 7, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.task;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public abstract class AbstractAtsTaskService implements IAtsTaskService {

   private static final String UNASSIGNED_USERID = "99999997";

   public AbstractAtsTaskService() {
   }

   @Override
   public Collection<IAtsTask> createTasks(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<String>> attributes, String commitComment) {
      NewTaskData newTaskData = getNewTaskData(teamWf, titles, assignees, createdDate, createdBy, relatedToState,
         taskWorkDef, attributes, commitComment);
      return createTasks(newTaskData);
   }

   @Override
   public NewTaskData getNewTaskData(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<String>> attributes, String commitComment) {
      NewTaskData newTaskData = new NewTaskData();
      newTaskData.setCommitComment(commitComment);
      newTaskData.setAsUserId(createdBy.getUserId());
      newTaskData.setTeamWfUuid(teamWf.getUuid());
      if (createdDate == null) {
         createdDate = new Date();
      }

      for (String title : titles) {
         JaxAtsTask task = new JaxAtsTask();
         task.setName(title);
         if (assignees != null) {
            for (IAtsUser assignee : assignees) {
               task.getAssigneeUserIds().add(assignee.getUserId());
            }
         } else {
            task.getAssigneeUserIds().add(UNASSIGNED_USERID);
         }
         if (Strings.isValid(relatedToState)) {
            task.setRelatedToState(relatedToState);
         }
         task.setCreatedByUserId(createdBy.getUserId());
         task.setCreatedDate(createdDate);
         if (Strings.isValid(taskWorkDef)) {
            task.setTaskWorkDef(taskWorkDef);
         }
         newTaskData.getNewTasks().add(task);
         if (attributes != null) {
            for (Entry<String, List<String>> entry : attributes.entrySet()) {
               task.addAttributes(entry.getKey(), entry.getValue());
            }
         }
      }
      return newTaskData;
   }

}
