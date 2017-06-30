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
package org.eclipse.osee.ats.core.client.task;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.AbstractAtsTaskService;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTasks;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsTaskCache;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskService extends AbstractAtsTaskService {

   private final IAtsClient atsClient;

   public AtsTaskService(IAtsClient atsClient) {
      super(atsClient.getServices());
      this.atsClient = atsClient;
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskDatas newTaskDatas) {
      AtsTaskEndpointApi taskEp = AtsClientService.getTaskEp();
      Response response = taskEp.create(newTaskDatas);
      if (response.getStatus() != Status.OK.getStatusCode()) {
         throw new OseeCoreException("Error creating task [%s] [%s]", newTaskDatas, response.toString());
      }

      List<IAtsTask> tasks = new LinkedList<>();

      ArtifactEvent artifactEvent = new ArtifactEvent(AtsClientService.get().getAtsBranch());
      for (NewTaskData newTaskData : newTaskDatas.getTaskDatas()) {
         processForEvents(newTaskData, response, tasks, artifactEvent);
      }

      OseeEventManager.kickPersistEvent(getClass(), artifactEvent);
      return tasks;
   }

   private void processForEvents(NewTaskData newTaskData, Response response, List<IAtsTask> tasks, ArtifactEvent artifactEvent) {
      Artifact teamWf = atsClient.getArtifact(newTaskData.getTeamWfUuid());

      JaxAtsTasks jaxTasks = response.readEntity(JaxAtsTasks.class);
      List<Long> artUuids = new LinkedList<>();

      teamWf.reloadAttributesAndRelations();
      AtsTaskCache.decache((TeamWorkFlowArtifact) teamWf);

      for (JaxAtsTask task : jaxTasks.getTasks()) {
         String guid = ArtifactQuery.getGuidFromUuid(task.getUuid(), AtsClientService.get().getAtsBranch());
         artifactEvent.addArtifact(new EventBasicGuidArtifact(EventModType.Added, AtsClientService.get().getAtsBranch(),
            AtsArtifactTypes.Task, guid));
         artUuids.add(task.getUuid());

         RelationLink relation = getRelation(teamWf, task);
         if (relation != null) {
            Artifact taskArt = atsClient.getArtifact(task.getUuid());

            DefaultBasicUuidRelation guidRelation = new DefaultBasicUuidRelation(AtsClientService.get().getAtsBranch(),
               AtsRelationTypes.TeamWfToTask_Task.getGuid(), relation.getId(), relation.getGammaId(),
               teamWf.getBasicGuidArtifact(), taskArt.getBasicGuidArtifact());

            artifactEvent.getRelations().add(new EventBasicGuidRelation(RelationEventType.Added,
               newTaskData.getTeamWfUuid().intValue(), new Long(task.getUuid()).intValue(), guidRelation));
         }
      }

      for (Long uuid : artUuids) {
         tasks.add(AtsClientService.get().getWorkItemFactory().getTask(AtsClientService.get().getArtifact(uuid)));
      }
   }

   private RelationLink getRelation(Artifact teamWf, JaxAtsTask task) {
      for (RelationLink relation : teamWf.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
         if (relation.getBArtifactId() == task.getUuid().intValue()) {
            return relation;
         }
      }
      return null;
   }

   @Override
   public Collection<IAtsTask> createTasks(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, IAtsChangeSet changes) {
      throw new UnsupportedOperationException("Not Supported on Client");
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskData newTaskData, IAtsChangeSet changes) {
      throw new UnsupportedOperationException("Not Supported on Client");
   }

}
