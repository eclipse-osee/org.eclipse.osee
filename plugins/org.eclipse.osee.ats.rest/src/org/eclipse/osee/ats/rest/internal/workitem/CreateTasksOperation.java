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
package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.task.JaxRelation;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksOperation {

   private XResultData resultData;
   private final NewTaskDatas newTaskDatas;
   private final IAtsServer atsServer;
   private IAtsUser asUser;
   private final List<JaxAtsTask> tasks = new ArrayList<>();
   private Date createdByDate;
   private Map<Long, IAtsTeamWorkflow> uuidToTeamWf;

   public CreateTasksOperation(NewTaskData newTaskData, IAtsServer atsServer, XResultData resultData) {
      newTaskDatas = new NewTaskDatas();
      newTaskDatas.add(newTaskData);
      this.atsServer = atsServer;
      this.resultData = resultData;
   }

   public CreateTasksOperation(NewTaskDatas newTaskDatas, IAtsServer atsServer, XResultData resultData) {
      this.newTaskDatas = newTaskDatas;
      this.atsServer = atsServer;
      this.resultData = resultData;
   }

   public XResultData validate() {
      if (resultData == null) {
         resultData = new XResultData(false);
      }
      uuidToTeamWf = new HashMap<>();
      for (NewTaskData newTaskData : newTaskDatas.getTaskDatas()) {
         Long teamWfUuid = newTaskData.getTeamWfUuid();
         if (teamWfUuid == null) {
            resultData.error("Team Workflow uuid not specified");
            continue;
         }
         ArtifactReadable teamWfArt = atsServer.getArtifact(teamWfUuid);
         if (teamWfArt == null) {
            resultData.errorf("Team Workflow uuid %d does not exist", teamWfUuid);
         }
         IAtsTeamWorkflow teamWf = uuidToTeamWf.get(teamWfUuid);
         if (teamWf == null) {
            teamWf = atsServer.getWorkItemFactory().getTeamWf(teamWfArt);
            uuidToTeamWf.put(teamWfUuid, teamWf);
         }
         String asUserId = newTaskData.getAsUserId();
         if (asUserId == null) {
            resultData.error("As User Id uuid not specified");
         }
         asUser = atsServer.getUserService().getUserById(asUserId);
         if (asUser == null) {
            resultData.errorf("As User Id uuid %d does not exist", asUserId);
         }
         if (!Strings.isValid(newTaskData.getCommitComment())) {
            resultData.errorf("Inavlidate Commit Comment [%s]", newTaskData.getCommitComment());
         }

         for (JaxAtsTask task : newTaskData.getNewTasks()) {
            Long taskUuid = task.getUuid();
            if (taskUuid != null && taskUuid > 0L) {
               ArtifactReadable taskArt = atsServer.getArtifact(taskUuid);
               if (taskArt != null) {
                  resultData.errorf("Task with uuid %d already exists for %s", taskUuid, task);
               }
            }
            if (!Strings.isValid(task.getName())) {
               resultData.errorf("Task name [%s] is invalid for %s", task.getName(), task);
            }
            IAtsUser createdBy = atsServer.getUserService().getUserById(task.getCreatedByUserId());
            if (createdBy == null) {
               resultData.errorf("Task Created By user id %d does not exist in %s", createdBy, task);
            }
            createdByDate = task.getCreatedDate();
            if (createdByDate == null) {
               resultData.errorf("Task Created By Date %s does not exist in %s", createdByDate, task);
            }
            IAtsTeamWorkflow teamWorkflow = atsServer.getWorkItemFactory().getTeamWf(teamWfArt);
            String relatedToState = task.getRelatedToState();
            if (Strings.isValid(relatedToState)) {
               if (teamWorkflow.getWorkDefinition().getStateByName(relatedToState) == null) {
                  resultData.errorf("Task Related To State %s invalid for Team Workflow %d", relatedToState,
                     teamWfUuid);
               }
            }

            List<String> assigneeUserIds = task.getAssigneeUserIds();
            if (!assigneeUserIds.isEmpty()) {
               Collection<IAtsUser> assignees = atsServer.getUserService().getUsersByUserIds(assigneeUserIds);
               if (assigneeUserIds.size() != assignees.size()) {
                  resultData.errorf("Task Assignees [%s] not all valid in %s", String.valueOf(assigneeUserIds), task);
               }
            }

            IAtsWorkDefinition workDefinition = null;
            if (Strings.isValid(task.getTaskWorkDef())) {
               try {
                  XResultData rd = new XResultData();
                  workDefinition = atsServer.getWorkDefinitionService().getWorkDefinition(task.getTaskWorkDef(), rd);
                  if (rd.isErrors()) {
                     resultData.errorf("Error finding Task Work Def [%s].  Exception: %s", task.getTaskWorkDef(),
                        rd.toString());
                  }
               } catch (Exception ex) {
                  resultData.errorf("Exception finding Task Work Def [%s].  Exception: %s", task.getTaskWorkDef(),
                     ex.getMessage());
               }
               if (workDefinition == null) {
                  resultData.errorf("Task Work Def [%s] does not exist", task.getTaskWorkDef());
               }
            }

            for (JaxAttribute attribute : task.getAttributes()) {
               AttributeTypeToken attrType = getAttributeType(atsServer, attribute.getAttrTypeName());
               if (attrType == null) {
                  resultData.errorf("Attribute Type [%s] not valid for Task creation in %s",
                     attribute.getAttrTypeName(), task);
               }
            }

            for (JaxRelation relation : task.getRelations()) {
               IRelationType relationType = getRelationType(atsServer, relation.getRelationTypeName());
               if (relationType == null) {
                  resultData.errorf("Relation Type [%s] not valid for Task creation in %s",
                     relation.getRelationTypeName(), task);
               }
               if (relation.getRelatedUuids().isEmpty()) {
                  resultData.errorf("Relation [%s] Uuids must be suplied Task creation in %s",
                     relation.getRelationTypeName(), task);
               }
               Collection<ArtifactId> foundUuids =
                  atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andUuids(
                     relation.getRelatedUuids().toArray(new Long[relation.getRelatedUuids().size()])).getItemIds();
               List<Long> notFoundUuids = relation.getRelatedUuids();
               notFoundUuids.removeAll(foundUuids);
               if (foundUuids.size() != relation.getRelatedUuids().size()) {
                  resultData.errorf("Relation [%s] Uuids [%s] do not match Work Items in task %s",
                     relation.getRelationTypeName(), notFoundUuids, task);
               }
            }
         }
      }
      return resultData;
   }

   private RelationTypeToken getRelationType(IAtsServer atsServer, String relationTypeName) {
      for (RelationTypeToken relation : atsServer.getOrcsApi().getOrcsTypes().getRelationTypes().getAll()) {
         if (relation.getName().equals(relationTypeName)) {
            return relation;
         }
      }
      return RelationTypeToken.SENTINEL;
   }

   private static AttributeTypeToken getAttributeType(IAtsServer atsServer, String attrTypeName) {
      for (AttributeTypeToken attrType : atsServer.getOrcsApi().getOrcsTypes().getArtifactTypes().getAttributeTypes(
         AtsArtifactTypes.Task, atsServer.getAtsBranch())) {
         if (attrType.getName().equals(attrTypeName)) {
            return attrType;
         }
      }
      return AttributeTypeToken.SENTINEL;
   }

   public List<JaxAtsTask> getTasks() {
      return tasks;
   }

   public void run() {
      XResultData results = validate();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }

      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet(
         newTaskDatas.getTaskDatas().iterator().next().getCommitComment(), asUser);
      run(changes);
      changes.execute();

      for (NewTaskData newTaskData : newTaskDatas.getTaskDatas()) {
         for (JaxAtsTask jaxTask : newTaskData.getNewTasks()) {
            JaxAtsTask newJaxTask = createNewJaxTask(jaxTask.getUuid(), atsServer);
            if (newJaxTask == null) {
               throw new OseeStateException("Unable to create return New Task for uuid " + jaxTask.getUuid());
            }
            this.tasks.add(newJaxTask);
         }
      }
   }

   public void run(IAtsChangeSet changes) {
      createTasks(changes);
      if (changes.isEmpty()) {
         throw new OseeStateException(getClass().getSimpleName() + " Error - No Tasks to Create");
      }
   }

   private void createTasks(IAtsChangeSet changes) {
      for (NewTaskData newTaskData : newTaskDatas.getTaskDatas()) {
         for (JaxAtsTask jaxTask : newTaskData.getNewTasks()) {

            Long uuid = jaxTask.getUuid();
            if (uuid == null || uuid <= 0L) {
               uuid = Lib.generateArtifactIdAsInt();
               jaxTask.setUuid(uuid);
            }
            ArtifactToken taskArt =
               changes.createArtifact(AtsArtifactTypes.Task, jaxTask.getName(), GUID.create(), uuid);
            IAtsTask task = atsServer.getWorkItemFactory().getTask(taskArt);

            IAtsTeamWorkflow teamWf = uuidToTeamWf.get(newTaskData.getTeamWfUuid());
            atsServer.getActionFactory().setAtsId(task, teamWf.getTeamDefinition(), changes);
            changes.relate(teamWf, AtsRelationTypes.TeamWfToTask_Task, taskArt);

            List<IAtsUser> assignees = new ArrayList<>();
            if (jaxTask.getAssigneeUserIds() != null) {
               assignees.addAll(atsServer.getUserService().getUsersByUserIds(jaxTask.getAssigneeUserIds()));
            }
            if (assignees.isEmpty()) {
               assignees.add(AtsCoreUsers.UNASSIGNED_USER);
            }

            IAtsWorkDefinition workDefinition = null;
            if (Strings.isValid(jaxTask.getTaskWorkDef())) {
               try {
                  workDefinition =
                     atsServer.getWorkDefinitionService().getWorkDefinition(jaxTask.getTaskWorkDef(), new XResultData());
               } catch (Exception ex) {
                  throw new OseeArgumentException("Exception finding Task Work Def [%s]", jaxTask.getTaskWorkDef(), ex);
               }
            }
            if (Strings.isValid(jaxTask.getDescription())) {
               changes.setSoleAttributeValue(task, AtsAttributeTypes.Description, jaxTask.getDescription());
            }
            IAtsUser createdBy = atsServer.getUserService().getUserById(jaxTask.getCreatedByUserId());
            atsServer.getActionFactory().initializeNewStateMachine(task, assignees, createdByDate, createdBy,
               workDefinition, changes);

            // Set parent state task is related to if set
            if (Strings.isValid(jaxTask.getRelatedToState())) {
               changes.setSoleAttributeValue(task, AtsAttributeTypes.RelatedToState, jaxTask.getRelatedToState());
            }

            for (JaxAttribute attribute : jaxTask.getAttributes()) {
               AttributeTypeToken attrType = getAttributeType(atsServer, attribute.getAttrTypeName());
               if (attrType.isInvalid()) {
                  resultData.errorf("Attribute Type [%s] not valid for Task creation in %s",
                     attribute.getAttrTypeName(), task);
               }
               changes.setAttributeValues(task, attrType, attribute.getValues());
            }

            for (JaxRelation relation : jaxTask.getRelations()) {
               RelationTypeToken relationType = getRelationType(atsServer, relation.getRelationTypeName());
               if (relationType == null) {
                  resultData.errorf("Relation Type [%s] not valid for Task creation in %s",
                     relation.getRelationTypeName(), task);
               }
               Collection<IAtsWorkItem> items = atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andUuids(
                  relation.getRelatedUuids().toArray(new Long[relation.getRelatedUuids().size()])).getItems();
               RelationTypeSide side = null;
               if (relation.isSideA()) {
                  side = new RelationTypeSide(relationType, RelationSide.SIDE_A);
               } else {
                  side = new RelationTypeSide(relationType, RelationSide.SIDE_B);
               }
               changes.setRelations(task, side, items);
            }
            changes.add(taskArt);
         }
      }

   }

   public static JaxAtsTask createNewJaxTask(Long uuid, IAtsServer atsServer) {
      ArtifactReadable taskArt = atsServer.getArtifact(uuid);
      if (taskArt != null) {
         JaxAtsTask newJaxTask = new JaxAtsTask();
         newJaxTask.setName(taskArt.getName());
         newJaxTask.setDescription(taskArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
         newJaxTask.setUuid(taskArt.getId());
         newJaxTask.setActive(true);
         String createdByUserId = taskArt.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
         newJaxTask.setCreatedByUserId(createdByUserId);
         newJaxTask.setCreatedDate(taskArt.getSoleAttributeValue(AtsAttributeTypes.CreatedDate));
         newJaxTask.setRelatedToState(taskArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, ""));
         IAtsWorkItem workItem = atsServer.getWorkItemFactory().getWorkItem(taskArt);
         for (IAtsUser user : workItem.getAssignees()) {
            newJaxTask.getAssigneeUserIds().add(user.getUserId());
         }

         for (AttributeTypeToken type : taskArt.getExistingAttributeTypes()) {
            List<Object> attributeValues = new LinkedList<>();

            for (Object value : taskArt.getAttributeValues(type)) {
               attributeValues.add(value);
            }
            newJaxTask.addAttributes(type.getName(), attributeValues);
         }
         return newJaxTask;
      }
      return null;
   }

}
