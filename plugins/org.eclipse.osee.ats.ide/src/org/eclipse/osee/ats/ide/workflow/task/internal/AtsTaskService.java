/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskNameProviderToken;
import org.eclipse.osee.ats.api.task.create.IAtsChangeReportTaskNameProvider;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.AbstractAtsTaskServiceCore;
import org.eclipse.osee.ats.core.task.ChangeReportTaskNameProviderService;
import org.eclipse.osee.ats.core.workflow.Task;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.workflow.task.IAtsTaskServiceIde;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryComboComboDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryComboDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskService extends AbstractAtsTaskServiceCore implements IAtsTaskServiceIde {

   private final AtsApi atsApi;

   public AtsTaskService(AtsApiIde atsClient) {
      super(atsClient);
      this.atsApi = atsClient;
   }

   @Override
   public NewTaskSet createTasks(NewTaskSet newTaskSet) {
      AtsTaskEndpointApi taskEp = atsApi.getServerEndpoints().getTaskEp();
      newTaskSet = taskEp.create(newTaskSet);
      List<Artifact> arts = new ArrayList<>();
      for (NewTaskData newTaskData : newTaskSet.getNewTaskDatas()) {
         Long teamWfId = newTaskData.getTeamWfId();

         Artifact art = ArtifactCache.getActive(ArtifactToken.valueOf(teamWfId, atsApi.getAtsBranch()));
         if (art != null) {
            arts.add(art);
         }
         for (JaxAtsTask task : newTaskData.getTasks()) {
            task.setAtsApi(atsApi);
         }
      }
      ArtifactQuery.reloadArtifacts(arts);
      return newTaskSet;
   }

   @Override
   public TaskArtifact createNewTaskWithDialog(IAtsTeamWorkflow teamWf) {
      TaskArtifact taskArt = null;
      try {
         EntryComboDialog ed = null;
         EntryComboComboDialog ed2 = null;

         // Determine if multiple work defs and/or miss-matched work defs
         Collection<WorkDefinition> taskWorkDefs = atsApi.getTaskService().calculateTaskWorkDefs(teamWf);
         String comment = "Create New Task";
         if (taskWorkDefs.size() == 0 || taskWorkDefs.size() == 1) {
            ed = new EntryComboDialog(comment, "Enter Task Title", RelatedToStateColumn.RELATED_TO_STATE_SELECTION);
            List<String> validStates =
               RelatedToStateColumn.getValidInWorkStates((TeamWorkFlowArtifact) teamWf.getStoreObject());
            ed.setOptions(validStates);
         } else {
            ed2 = new EntryComboComboDialog(comment, "Enter Task Title and Select Task Work Definition",
               RelatedToStateColumn.RELATED_TO_STATE_SELECTION, "Select Task Work Definition");
            ed = ed2;
            List<String> validStates =
               RelatedToStateColumn.getValidInWorkStates((TeamWorkFlowArtifact) teamWf.getStoreObject());
            ed2.setOptions(validStates);
            ed2.setOptions2(taskWorkDefs);
            ed2.setCombo2Required(true);
         }

         if (ed.open() == 0) {
            NewTaskSet newTaskSet =
               NewTaskSet.createWithData(comment, teamWf.getId(), atsApi.getUserService().getCurrentUser().getUserId());
            NewTaskData newTaskData = newTaskSet.getTaskData();
            String title = ed.getEntry();
            JaxAtsTask task =
               JaxAtsTask.create(newTaskData, title, atsApi.getUserService().getCurrentUser(), new Date());
            task.setId(ArtifactId.SENTINEL.getId());
            if (ed2 != null) {
               task.setWorkDef(((WorkDefinition) ed2.getSelection2()).getIdString());
            } else if (taskWorkDefs.size() == 1) {
               task.setWorkDef(taskWorkDefs.iterator().next().getIdString());
            }
            if (Strings.isValid(ed.getSelection())) {
               task.setRelatedToState(ed.getSelection());
            }
            NewTaskSet taskSet = atsApi.getTaskService().createTasks(newTaskSet);
            if (newTaskSet.isErrors()) {
               XResultDataUI.report(newTaskSet.getResults(), title);
               return null;
            } else {
               JaxAtsTask jTask = taskSet.getTaskData().getTasks().iterator().next();
               taskArt = (TaskArtifact) atsApi.getQueryService().getArtifact(jTask.getId());
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return taskArt;
   }

   @Override
   public ChangeReportTaskData createTasks(ChangeReportTaskData changeReportTaskData) {
      ChangeReportTaskData crtd = atsApi.getServerEndpoints().getTaskEp().create(changeReportTaskData);
      reloadTasksIfNecessary(crtd);
      return crtd;
   }

   private void reloadTasksIfNecessary(ChangeReportTaskData crtd) {
      if (crtd.getTransaction().isValid()) {
         List<ArtifactToken> toReload = new ArrayList<>();
         for (ArtifactId id : crtd.getIds()) {
            Artifact cachedArt = ArtifactCache.getActive(id, atsApi.getAtsBranch());
            if (cachedArt != null) {
               toReload.add(cachedArt);
            }
         }
         if (!toReload.isEmpty()) {
            ArtifactQuery.reloadArtifacts(toReload);
         }
      }
   }

   @Override
   public IAtsChangeReportTaskNameProvider getChangeReportOptionNameProvider(ChangeReportTaskNameProviderToken token) {
      return ChangeReportTaskNameProviderService.getChangeReportOptionNameProvider(token);
   }

   @Override
   public IAtsTask getTask(ArtifactToken artifact) {
      IAtsTask task = null;
      if (artifact instanceof IAtsTask) {
         task = (IAtsTask) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.Task)) {
         task = new Task(atsApi.getLogger(), atsApi, artifact);
      } else {
         throw new OseeArgumentException("Artifact %s must be of type Task", artifact.toStringWithId());
      }
      return task;
   }

   @Override
   public NewTaskSet createTasks(NewTaskSet newTaskSet, Map<Long, IAtsTeamWorkflow> idToTeamWf) {
      throw new UnsupportedOperationException("Not supported on IDE client");
   }

}
