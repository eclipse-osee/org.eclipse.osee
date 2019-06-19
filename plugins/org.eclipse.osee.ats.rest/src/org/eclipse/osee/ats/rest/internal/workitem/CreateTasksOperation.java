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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.task.JaxRelation;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksOperation {

   private final NewTaskDatas newTaskDatas;
   private final AtsApi atsApi;
   private final OrcsApi orcsApi;
   private final List<JaxAtsTask> tasks = new ArrayList<>();
   private IAtsUser asUser;
   private XResultData resultData;
   private Date createdByDate;
   private Map<Long, IAtsTeamWorkflow> idToTeamWf;

   public CreateTasksOperation(NewTaskData newTaskData, AtsApi atsApi, OrcsApi orcsApi, XResultData resultData) {
      this(new NewTaskDatas(newTaskData), atsApi, orcsApi, resultData);
   }

   public CreateTasksOperation(NewTaskDatas newTaskDatas, AtsApi atsApi, OrcsApi orcsApi, XResultData resultData) {
      this.newTaskDatas = newTaskDatas;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.resultData = resultData;
   }

   public XResultData validate() {
      if (resultData == null) {
         resultData = new XResultData(false);
      }
      idToTeamWf = new HashMap<>();
      for (NewTaskData newTaskData : newTaskDatas.getTaskDatas()) {
         Long teamWfId = newTaskData.getTeamWfId();
         if (teamWfId == null) {
            resultData.error("Team Workflow id not specified");
            continue;
         }
         ArtifactReadable teamWfArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(teamWfId);
         if (teamWfArt == null) {
            resultData.errorf("Team Workflow id %d does not exist", teamWfId);
         }
         IAtsTeamWorkflow teamWf = idToTeamWf.get(teamWfId);
         if (teamWf == null) {
            teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);
            idToTeamWf.put(teamWfId, teamWf);
         }
         String asUserId = newTaskData.getAsUserId();
         if (asUserId == null) {
            resultData.error("As User Id id not specified");
         }
         asUser = atsApi.getUserService().getUserById(asUserId);
         if (asUser == null) {
            resultData.errorf("As User Id id %d does not exist", asUserId);
         }
         if (!Strings.isValid(newTaskData.getCommitComment())) {
            resultData.errorf("Inavlidate Commit Comment [%s]", newTaskData.getCommitComment());
         }

         for (JaxAtsTask task : newTaskData.getNewTasks()) {
            Long taskId = task.getId();
            if (taskId != null && taskId > 0L) {
               ArtifactReadable taskArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(taskId);
               if (taskArt != null) {
                  resultData.errorf("Task with id %d already exists for %s", taskId, task);
               }
            }
            if (!Strings.isValid(task.getName())) {
               resultData.errorf("Task name [%s] is invalid for %s", task.getName(), task);
            }
            IAtsUser createdBy = atsApi.getUserService().getUserById(task.getCreatedByUserId());
            if (createdBy == null) {
               resultData.errorf("Task Created By user id %d does not exist in %s", createdBy, task);
            }
            createdByDate = task.getCreatedDate();
            if (createdByDate == null) {
               resultData.errorf("Task Created By Date %s does not exist in %s", createdByDate, task);
            }
            IAtsTeamWorkflow teamWorkflow = atsApi.getWorkItemService().getTeamWf(teamWfArt);
            String relatedToState = task.getRelatedToState();
            if (Strings.isValid(relatedToState)) {
               if (teamWorkflow.getWorkDefinition().getStateByName(relatedToState) == null) {
                  resultData.errorf("Task Related To State %s invalid for Team Workflow %d", relatedToState, teamWfId);
               }
            }

            List<String> assigneeUserIds = task.getAssigneeUserIds();
            if (!assigneeUserIds.isEmpty()) {
               Collection<IAtsUser> assignees = atsApi.getUserService().getUsersByUserIds(assigneeUserIds);
               if (assigneeUserIds.size() != assignees.size()) {
                  resultData.errorf("Task Assignees [%s] not all valid in %s", String.valueOf(assigneeUserIds), task);
               }
            }

            IAtsWorkDefinition workDefinition = null;
            if (Strings.isValid(task.getTaskWorkDef())) {
               try {
                  workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(task.getTaskWorkDef());
                  if (workDefinition == null) {
                     resultData.errorf("Error finding Task Work Def [%s].  Exception: %s", task.getTaskWorkDef());
                  }
               } catch (Exception ex) {
                  resultData.errorf("Exception finding Task Work Def [%s].  Exception: %s", task.getTaskWorkDef(),
                     ex.getMessage());
               }
            }
            if (workDefinition == null) {
               workDefinition = atsApi.getWorkDefinitionService().computedWorkDefinitionForTaskNotYetCreated(teamWf);
            }
            Conditions.assertNotNull(workDefinition, "Work Definition can not be null for [%s]", task.getTaskWorkDef());

            for (JaxAttribute attribute : task.getAttributes()) {
               AttributeTypeId attrType = atsApi.getStoreService().getAttributeType(attribute.getAttrTypeName());

            }

            for (JaxRelation relation : task.getRelations()) {
               IRelationType relationType = getRelationType(atsApi, relation.getRelationTypeName());
               if (relationType == null) {
                  resultData.errorf("Relation Type [%s] not valid for Task creation in %s",
                     relation.getRelationTypeName(), task);
               }
               if (relation.getRelatedIds().isEmpty()) {
                  resultData.errorf("Relation [%s] Ids must be suplied Task creation in %s",
                     relation.getRelationTypeName(), task);
               }
               Collection<ArtifactId> foundIds = atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andIds(
                  relation.getRelatedIds().toArray(new Long[relation.getRelatedIds().size()])).getItemIds();
               List<Long> notFoundIds = relation.getRelatedIds();
               notFoundIds.removeAll(foundIds);
               if (foundIds.size() != relation.getRelatedIds().size()) {
                  resultData.errorf("Relation [%s] Ids [%s] do not match Work Items in task %s",
                     relation.getRelationTypeName(), notFoundIds, task);
               }
            }
         }
      }
      return resultData;
   }

   private RelationTypeToken getRelationType(AtsApi atsApi, String relationTypeName) {
      for (RelationTypeToken relation : orcsApi.getOrcsTypes().getRelationTypes().getAll()) {
         if (relation.getName().equals(relationTypeName)) {
            return relation;
         }
      }
      return RelationTypeToken.SENTINEL;
   }

   public List<JaxAtsTask> getTasks() {
      return tasks;
   }

   public void run() {
      XResultData results = validate();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }

      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
         newTaskDatas.getTaskDatas().iterator().next().getCommitComment(), asUser);
      run(changes);
      changes.execute();

      for (NewTaskData newTaskData : newTaskDatas.getTaskDatas()) {
         for (JaxAtsTask jaxTask : newTaskData.getNewTasks()) {
            JaxAtsTask newJaxTask = createNewJaxTask(jaxTask.getId(), atsApi);
            if (newJaxTask == null) {
               throw new OseeStateException("Unable to create return New Task for id " + jaxTask.getId());
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

            Long id = jaxTask.getId();
            if (id == null || id <= 0L) {
               id = Lib.generateArtifactIdAsInt();
               jaxTask.setId(id);
            }
            ArtifactToken taskArt = changes.createArtifact(AtsArtifactTypes.Task, jaxTask.getName(), id);
            IAtsTask task = atsApi.getWorkItemService().getTask(taskArt);

            IAtsTeamWorkflow teamWf = idToTeamWf.get(newTaskData.getTeamWfId());
            atsApi.getActionFactory().setAtsId(task, teamWf.getTeamDefinition(), changes);
            changes.relate(teamWf, AtsRelationTypes.TeamWfToTask_Task, taskArt);

            List<IAtsUser> assignees = new ArrayList<>();
            if (jaxTask.getAssigneeUserIds() != null) {
               assignees.addAll(atsApi.getUserService().getUsersByUserIds(jaxTask.getAssigneeUserIds()));
            }
            if (assignees.isEmpty()) {
               assignees.add(AtsCoreUsers.UNASSIGNED_USER);
            }

            IAtsWorkDefinition workDefinition =
               atsApi.getWorkDefinitionService().computeAndSetWorkDefinitionAttrs(task, null, changes);
            Conditions.assertNotNull(workDefinition, "Work Definition can not be null for [%s]", task);

            if (Strings.isValid(jaxTask.getDescription())) {
               changes.setSoleAttributeValue(task, AtsAttributeTypes.Description, jaxTask.getDescription());
            }
            IAtsUser createdBy = atsApi.getUserService().getUserById(jaxTask.getCreatedByUserId());
            atsApi.getActionFactory().initializeNewStateMachine(task, assignees, createdByDate, createdBy,
               workDefinition, changes);

            // Set parent state task is related to if set
            if (Strings.isValid(jaxTask.getRelatedToState())) {
               changes.setSoleAttributeValue(task, AtsAttributeTypes.RelatedToState, jaxTask.getRelatedToState());
            }

            for (JaxAttribute attribute : jaxTask.getAttributes()) {
               AttributeTypeToken attrType =
                  orcsApi.getOrcsTypes().getAttributeTypes().getByName(attribute.getAttrTypeName());

               changes.setAttributeValues(task, attrType, attribute.getValues());
            }

            for (JaxRelation relation : jaxTask.getRelations()) {
               RelationTypeToken relationType = getRelationType(atsApi, relation.getRelationTypeName());
               if (relationType == null) {
                  resultData.errorf("Relation Type [%s] not valid for Task creation in %s",
                     relation.getRelationTypeName(), task);
               }
               Collection<IAtsWorkItem> items = atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andIds(
                  relation.getRelatedIds().toArray(new Long[relation.getRelatedIds().size()])).getItems();
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

   public static JaxAtsTask createNewJaxTask(Long id, AtsApi atsApi) {
      ArtifactReadable taskArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(id);
      if (taskArt != null) {
         JaxAtsTask newJaxTask = new JaxAtsTask();
         newJaxTask.setName(taskArt.getName());
         newJaxTask.setDescription(taskArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
         newJaxTask.setId(taskArt.getId());
         newJaxTask.setActive(true);
         String createdByUserId = taskArt.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
         newJaxTask.setCreatedByUserId(createdByUserId);
         newJaxTask.setCreatedDate(taskArt.getSoleAttributeValue(AtsAttributeTypes.CreatedDate));
         newJaxTask.setRelatedToState(taskArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, ""));
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(taskArt);
         for (IAtsUser user : workItem.getAssignees()) {
            newJaxTask.addAssigneeUserIds(user.getUserId());
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
