/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksOperation {

   private XResultData resultData;
   private final NewTaskData newTaskData;
   private final IAtsServer atsServer;
   private ArtifactReadable teamWfArt;
   private IAtsUser asUser;
   private final List<JaxAtsTask> tasks = new ArrayList<>();
   private IAtsTeamWorkflow teamWf;
   private Date createdByDate;

   public CreateTasksOperation(NewTaskData newTaskData, IAtsServer atsServer, XResultData resultData) {
      this.newTaskData = newTaskData;
      this.atsServer = atsServer;
      this.resultData = resultData;
   }

   public XResultData validate() {
      if (resultData == null) {
         resultData = new XResultData(false);
      }
      Long teamWfUuid = newTaskData.getTeamWfUuid();
      if (teamWfUuid == null) {
         resultData.logError("Team Workflow uuid not specified");
      }
      teamWfArt = atsServer.getArtifactByUuid(teamWfUuid);
      if (teamWfArt == null) {
         resultData.logErrorWithFormat("Team Workflow uuid %d does not exist", teamWfUuid);
      }
      teamWf = atsServer.getWorkItemFactory().getTeamWf(teamWfArt);
      String asUserId = newTaskData.getAsUserId();
      if (asUserId == null) {
         resultData.logError("As User Id uuid not specified");
      }
      asUser = atsServer.getUserService().getUserById(asUserId);
      if (asUser == null) {
         resultData.logErrorWithFormat("As User Id uuid %d does not exist", asUserId);
      }
      if (!Strings.isValid(newTaskData.getCommitComment())) {
         resultData.logErrorWithFormat("Inavlidate Commit Comment [%s]", newTaskData.getCommitComment());
      }

      for (JaxAtsTask task : newTaskData.getNewTasks()) {
         Long taskUuid = task.getUuid();
         if (teamWfUuid != null && taskUuid > 0L) {
            ArtifactReadable taskArt = atsServer.getArtifactByUuid(taskUuid);
            if (taskArt != null) {
               resultData.logErrorWithFormat("Task with uuid %d already exists for %s", taskUuid, task);
            }
         }
         if (!Strings.isValid(task.getName())) {
            resultData.logErrorWithFormat("Task name [%s] is invalid for %s", task.getName(), task);
         }
         IAtsUser createdBy = atsServer.getUserService().getUserById(task.getCreatedByUserId());
         if (createdBy == null) {
            resultData.logErrorWithFormat("Task Created By user id %d does not exist in %s", createdBy, task);
         }
         createdByDate = task.getCreatedDate();
         if (createdByDate == null) {
            resultData.logErrorWithFormat("Task Created By Date %s does not exist in %s", createdByDate, task);
         }
         IAtsTeamWorkflow teamWorkflow = atsServer.getWorkItemFactory().getTeamWf(teamWfArt);
         String relatedToState = task.getRelatedToState();
         if (Strings.isValid(relatedToState)) {
            if (teamWorkflow.getWorkDefinition().getStateByName(relatedToState) == null) {
               resultData.logErrorWithFormat("Task Related To State %s invalid for Team Workflow %d", relatedToState,
                  teamWfUuid);
            }
         }
         List<String> assigneeUserIds = task.getAssigneeUserIds();
         if (assigneeUserIds == null || assigneeUserIds.isEmpty()) {
            resultData.logErrorWithFormat("Task Assignees can not be empty in %s", createdByDate, task);
         }
         if (assigneeUserIds == null) {
            resultData.logErrorWithFormat("Task Assignees must be specified in %s", createdByDate, task);
         }

         Collection<IAtsUser> assignees = atsServer.getUserService().getUsersByUserIds(assigneeUserIds);
         if (assigneeUserIds.size() != assignees.size()) {
            resultData.logErrorWithFormat("Task Assignees [%s] not all valid in %s", String.valueOf(assigneeUserIds),
               task);
         }

         for (Entry<String, Object> entry : task.getAttrTypeToObject().entrySet()) {
            IAttributeType attrType = getAttributeType(atsServer, entry);
            if (attrType == null) {
               resultData.logErrorWithFormat("Attribute Type [%s] not valid for Task creation in %s", entry.getKey(),
                  task);
            }
         }
      }

      return resultData;
   }

   private static IAttributeType getAttributeType(IAtsServer atsServer, Entry<String, Object> entry) {
      for (IAttributeType attrType : atsServer.getOrcsApi().getOrcsTypes().getArtifactTypes().getAttributeTypes(
         AtsArtifactTypes.Task, AtsUtilCore.getAtsBranch())) {
         if (attrType.getName().equals(entry.getKey())) {
            return attrType;
         }
      }
      return null;
   }

   public List<JaxAtsTask> getTasks() {
      return tasks;
   }

   public void run() {
      XResultData results = validate();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }

      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet(newTaskData.getCommitComment(), asUser);

      createTasks(changes);
      if (changes.isEmpty()) {
         throw new OseeStateException(getClass().getSimpleName() + " Error - No Tasks to Create");
      }
      changes.execute();

      for (JaxAtsTask jaxTask : newTaskData.getNewTasks()) {
         JaxAtsTask newJaxTask = createNewJaxTask(jaxTask.getUuid(), atsServer);
         if (newJaxTask == null) {
            throw new OseeStateException("Unable to create return New Task for uuid " + jaxTask.getUuid());
         }
         this.tasks.add(newJaxTask);
      }
   }

   private void createTasks(IAtsChangeSet changes) {
      for (JaxAtsTask jaxTask : newTaskData.getNewTasks()) {

         Long uuid = jaxTask.getUuid();
         if (uuid <= 0L) {
            uuid = Lib.generateArtifactIdAsInt();
            jaxTask.setUuid(uuid);
         }
         ArtifactId taskArt = changes.createArtifact(AtsArtifactTypes.Task, jaxTask.getName(), GUID.create(), uuid);
         IAtsTask task = atsServer.getWorkItemFactory().getTask(taskArt);

         atsServer.getUtilService().setAtsId(atsServer.getSequenceProvider(), task, teamWf.getTeamDefinition(),
            changes);

         changes.relate(teamWf, AtsRelationTypes.TeamWfToTask_Task, taskArt);

         List<IAtsUser> assignees = new ArrayList<>();
         if (jaxTask.getAssigneeUserIds() != null) {
            assignees.addAll(atsServer.getUserService().getUsersByUserIds(jaxTask.getAssigneeUserIds()));
         }

         if (Strings.isValid(jaxTask.getDescription())) {
            changes.setSoleAttributeValue(task, AtsAttributeTypes.Description, jaxTask.getDescription());
         }
         atsServer.getActionFactory().initializeNewStateMachine(task, assignees, createdByDate, asUser, changes);

         // Set parent state task is related to if set
         if (Strings.isValid(jaxTask.getRelatedToState())) {
            changes.setSoleAttributeValue(task, AtsAttributeTypes.RelatedToState, jaxTask.getRelatedToState());
         }

         for (Entry<String, Object> entry : jaxTask.getAttrTypeToObject().entrySet()) {
            IAttributeType attrType = getAttributeType(atsServer, entry);
            changes.setSoleAttributeValue(task, attrType, entry.getValue());
         }

         changes.add(taskArt);

      }
   }

   public static JaxAtsTask createNewJaxTask(Long uuid, IAtsServer atsServer) {
      ArtifactReadable taskArt = atsServer.getArtifactByUuid(uuid);
      if (taskArt != null) {
         JaxAtsTask newJaxTask = new JaxAtsTask();
         newJaxTask.setName(taskArt.getName());
         newJaxTask.setDescription(taskArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
         newJaxTask.setUuid(taskArt.getUuid());
         newJaxTask.setActive(true);
         String createdByUserId = taskArt.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
         newJaxTask.setCreatedByUserId(createdByUserId);
         newJaxTask.setCreatedDate(taskArt.getSoleAttributeValue(AtsAttributeTypes.CreatedDate));
         newJaxTask.setRelatedToState(taskArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, ""));
         IAtsWorkItem workItem = atsServer.getWorkItemFactory().getWorkItem(taskArt);
         for (IAtsUser user : workItem.getAssignees()) {
            newJaxTask.getAssigneeUserIds().add(user.getUserId());
         }
         for (IAttributeType type : taskArt.getExistingAttributeTypes()) {
            newJaxTask.getAttrTypeToObject().put(type.getName(),
               Collections.toString(", ", taskArt.getAttributeValues(type)));
         }
         return newJaxTask;
      }
      return null;
   }

}
