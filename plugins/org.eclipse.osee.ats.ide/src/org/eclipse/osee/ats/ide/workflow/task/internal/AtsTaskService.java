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
package org.eclipse.osee.ats.ide.workflow.task.internal;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.AbstractAtsTaskService;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTaskFactory;
import org.eclipse.osee.ats.api.task.JaxAtsTasks;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDataFactory;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskNameProviderToken;
import org.eclipse.osee.ats.api.task.create.IAtsChangeReportTaskNameProvider;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.ChangeReportTaskNameProviderService;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.IAtsClient;
import org.eclipse.osee.ats.ide.workflow.task.IAtsTaskServiceClient;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.event.DefaultBasicIdRelation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryComboComboDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryComboDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskService extends AbstractAtsTaskService implements IAtsTaskServiceClient {

   private final IAtsClient atsClient;

   public AtsTaskService(IAtsClient atsClient) {
      super(atsClient.getServices());
      this.atsClient = atsClient;
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskDatas newTaskDatas) {
      List<IAtsTask> tasks = new LinkedList<>();

      AtsTaskEndpointApi taskEp = AtsClientService.get().getServerEndpoints().getTaskEp();
      JaxAtsTasks jaxTasks = taskEp.create(newTaskDatas);
      newTaskDatas.setResults(jaxTasks.getResults());
      if (newTaskDatas.getResults().isErrors()) {
         return tasks;
      }

      ArtifactEvent artifactEvent = new ArtifactEvent(AtsClientService.get().getAtsBranch());
      for (NewTaskData newTaskData : newTaskDatas.getTaskDatas()) {
         processForEvents(newTaskData, jaxTasks, tasks, artifactEvent);
      }

      OseeEventManager.kickPersistEvent(getClass(), artifactEvent);
      return tasks;
   }

   private void processForEvents(NewTaskData newTaskData, JaxAtsTasks jaxTasks, List<IAtsTask> tasks, ArtifactEvent artifactEvent) {
      Artifact teamWf = atsClient.getQueryServiceClient().getArtifact(newTaskData.getTeamWfId());

      List<Long> artIds = new LinkedList<>();

      teamWf.reloadAttributesAndRelations();

      for (JaxAtsTask task : jaxTasks.getTasks()) {
         String guid = ArtifactQuery.getGuidFromId(task.getId(), AtsClientService.get().getAtsBranch());
         artifactEvent.addArtifact(new EventBasicGuidArtifact(EventModType.Added, AtsClientService.get().getAtsBranch(),
            AtsArtifactTypes.Task, guid));
         artIds.add(task.getId());

         RelationLink relation = getRelation(teamWf, task);
         if (relation != null) {
            Artifact taskArt = atsClient.getQueryServiceClient().getArtifact(task.getId());

            DefaultBasicIdRelation guidRelation = new DefaultBasicIdRelation(AtsClientService.get().getAtsBranch(),
               AtsRelationTypes.TeamWfToTask_Task.getGuid(), relation.getId(), relation.getGammaId(),
               teamWf.getBasicGuidArtifact(), taskArt.getBasicGuidArtifact());

            artifactEvent.getRelations().add(new EventBasicGuidRelation(RelationEventType.Added,
               ArtifactId.valueOf(newTaskData.getTeamWfId()), ArtifactId.valueOf(task.getId()), guidRelation));
         }
      }

      for (Long id : artIds) {
         tasks.add(AtsClientService.get().getWorkItemService().getTask(
            AtsClientService.get().getQueryService().getArtifact(id)));
      }
   }

   private RelationLink getRelation(Artifact teamWf, JaxAtsTask task) {
      for (RelationLink relation : teamWf.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
         if (relation.getArtifactB().equals(task.getId())) {
            return relation;
         }
      }
      return null;
   }

   @Override
   public Collection<IAtsTask> createTasks(IAtsTeamWorkflow teamWf, List<String> titles, List<AtsUser> assignees, Date createdDate, AtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, IAtsChangeSet changes) {
      throw new UnsupportedOperationException("Not Supported on Client");
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskData newTaskData, IAtsChangeSet changes, XResultData results) {
      throw new UnsupportedOperationException("Not Supported on Client");
   }

   @Override
   public TaskArtifact createNewTaskWithDialog(IAtsTeamWorkflow teamWf) {
      TaskArtifact taskArt = null;
      try {
         EntryComboDialog ed = null;
         EntryComboComboDialog ed2 = null;

         // Determine if multiple work defs and/or miss-matched work defs
         Collection<IAtsWorkDefinition> taskWorkDefs =
            AtsClientService.get().getTaskService().calculateTaskWorkDefs(teamWf);
         if (taskWorkDefs.size() == 0 || taskWorkDefs.size() == 1) {
            ed = new EntryComboDialog("Create New Task", "Enter Task Title",
               RelatedToStateColumn.RELATED_TO_STATE_SELECTION);
            List<String> validStates =
               RelatedToStateColumn.getValidInWorkStates((TeamWorkFlowArtifact) teamWf.getStoreObject());
            ed.setOptions(validStates);
         } else {
            ed2 = new EntryComboComboDialog("Create New Task", "Enter Task Title and Select Task Work Definition",
               RelatedToStateColumn.RELATED_TO_STATE_SELECTION, "Select Task Work Definition");
            ed = ed2;
            List<String> validStates =
               RelatedToStateColumn.getValidInWorkStates((TeamWorkFlowArtifact) teamWf.getStoreObject());
            ed2.setOptions(validStates);
            ed2.setOptions2(taskWorkDefs);
            ed2.setCombo2Required(true);
         }

         if (ed.open() == 0) {
            NewTaskData newTaskData = NewTaskDataFactory.get("Create New Task",
               AtsClientService.get().getUserService().getCurrentUser().getUserId(), teamWf.getId());
            String title = ed.getEntry();
            JaxAtsTask task = JaxAtsTaskFactory.get(newTaskData, title,
               AtsClientService.get().getUserService().getCurrentUser(), new Date());
            task.setId(Lib.generateArtifactIdAsInt());
            if (ed2 != null) {
               task.setTaskWorkDef(((IAtsWorkDefinition) ed2.getSelection2()).getIdString());
            } else if (taskWorkDefs.size() == 1) {
               task.setTaskWorkDef(taskWorkDefs.iterator().next().getIdString());
            }
            if (Strings.isValid(ed.getSelection())) {
               task.setRelatedToState(ed.getSelection());
            }
            AtsClientService.get().getTaskService().createTasks(new NewTaskDatas(newTaskData));

            taskArt = (TaskArtifact) AtsClientService.get().getQueryService().getArtifact(task.getId());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return taskArt;
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskData newTaskData, IAtsChangeSet changes, XResultData rd, Map<Long, IAtsTeamWorkflow> idToTeamWf) {
      throw new UnsupportedOperationException("Not supported on ide client");
   }

   @Override
   public ChangeReportTaskData createTasks(ChangeReportTaskData changeReportTaskData) {
      return AtsClientService.get().getServerEndpoints().getTaskEp().create(changeReportTaskData);
   }

   @Override
   public ChangeReportTaskData createTasks(ArtifactToken hostTeamWf, AtsTaskDefToken taskDefToken, ArtifactToken asUser) {
      ChangeReportTaskData data = new ChangeReportTaskData();
      data.setTaskDefToken(taskDefToken);
      data.setHostTeamWf(hostTeamWf);
      data.setAsUser(AtsClientService.get().getUserService().getCurrentUser());
      return AtsClientService.get().getServerEndpoints().getTaskEp().create(data);
   }

   @Override
   public ChangeReportTaskData createTasks(ChangeReportTaskData changeReportTaskData, IAtsChangeSet changes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public IAtsChangeReportTaskNameProvider getChangeReportOptionNameProvider(ChangeReportTaskNameProviderToken token) {
      return ChangeReportTaskNameProviderService.getChangeReportOptionNameProvider(token);
   }

   @Override
   public IAtsTask getTask(ArtifactToken artifact) {
      return new org.eclipse.osee.ats.core.workflow.Task(AtsClientService.get().getLogger(), AtsClientService.get(),
         artifact);
   }

}
