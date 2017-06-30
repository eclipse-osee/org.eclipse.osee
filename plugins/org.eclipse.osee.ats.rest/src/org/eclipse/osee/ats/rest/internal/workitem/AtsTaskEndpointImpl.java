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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTasks;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskEndpointImpl implements AtsTaskEndpointApi {

   private final IAtsServer atsServer;

   public AtsTaskEndpointImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public Response create(NewTaskDatas newTaskDatas) {
      CreateTasksOperation operation = new CreateTasksOperation(newTaskDatas, atsServer, new XResultData());
      XResultData results = operation.validate();

      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      operation.run();
      JaxAtsTasks tasks = new JaxAtsTasks();
      tasks.getTasks().addAll(operation.getTasks());
      return Response.ok().entity(tasks).build();
   }

   @GET
   @Path("{taskUuid}")
   @Override
   public Response get(@PathParam("taskUuid") long taskUuid) {
      IAtsWorkItem task =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).isOfType(WorkItemType.Task).andUuids(
            taskUuid).getResults().getOneOrNull();
      if (task == null) {
         throw new OseeArgumentException("No Task found with id %d", taskUuid);
      }
      JaxAtsTask jaxAtsTask = CreateTasksOperation.createNewJaxTask(task.getId(), atsServer);
      return Response.ok().entity(jaxAtsTask).build();
   }

   @DELETE
   @Path("{taskUuid}")
   @Override
   public void delete(@PathParam("taskUuid") long taskUuid) {
      IAtsWorkItem task =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).isOfType(WorkItemType.Task).andUuids(
            taskUuid).getResults().getOneOrNull();
      if (task != null) {
         IAtsChangeSet changes =
            atsServer.getStoreService().createAtsChangeSet("Delete Task", AtsCoreUsers.SYSTEM_USER);
         changes.deleteArtifact(task);
         changes.execute();
      }
   }

}
