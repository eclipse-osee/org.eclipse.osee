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

import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.ev.JaxWorkPackageData;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkPackageEndpointImpl implements AtsWorkPackageEndpointApi {

   private final IAtsServer atsServer;

   public AtsWorkPackageEndpointImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @GET
   @Path("{workPackageId}/workitem")
   @Produces({MediaType.APPLICATION_JSON})
   @Override
   public Collection<IAtsWorkItem> getWorkItems(@PathParam("workPackageId") long workPackageId) {
      ArtifactReadable workPackageArt = atsServer.getArtifactByUuid(workPackageId);
      if (workPackageArt == null) {
         throw new OseeArgumentException("Work Package with id [%s] Not Found", workPackageId);
      }
      return atsServer.getQueryService().createQuery().andAttr(AtsAttributeTypes.WorkPackageGuid,
         workPackageArt.getGuid()).getResults().getList();
   }

   @PUT
   @Path("{workPackageId}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Override
   public Response setWorkPackage(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData) {
      ArtifactReadable workPackageArt = atsServer.getArtifactByUuid(workPackageId);
      if (workPackageArt == null) {
         throw new OseeArgumentException("Work Package with id [%s] Not Found", workPackageId);
      }
      IAtsUser asUser = atsServer.getUserService().getUserById(workPackageData.getAsUserId());
      if (asUser == null) {
         throw new OseeArgumentException("Author with id [%s] Not Found", workPackageData.getAsUserId());
      }
      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Set Work Package", asUser);
      for (Long workItemUuid : workPackageData.getWorkItemUuids()) {
         IAtsWorkItem workItem =
            atsServer.getQueryService().createQuery().andUuids(workItemUuid).getResults().getAtMostOneOrNull();
         if (workItem == null) {
            throw new OseeArgumentException("Work Item with id [%s] Not Found", workItemUuid);
         }
         if (!workItem.isTask() && !workItem.isTeamWorkflow()) {
            throw new OseeArgumentException("Work Packages can only be set on Team Workflow or Task, not [%s]",
               workItem.getArtifactTypeName());
         }
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.WorkPackageGuid, workPackageArt.getGuid());
      }
      changes.execute();
      return Response.ok().build();
   }

   @DELETE
   @Path("{workPackageId}/workitem")
   @Consumes({MediaType.APPLICATION_JSON})
   @Override
   public Response deleteWorkPackageItems(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData) {
      ArtifactReadable workPackageArt = atsServer.getArtifactByUuid(workPackageId);
      if (workPackageArt == null) {
         throw new OseeArgumentException("Work Package with id [%s] Not Found", workPackageId);
      }
      IAtsUser asUser = atsServer.getUserService().getUserById(workPackageData.getAsUserId());
      if (asUser == null) {
         throw new OseeArgumentException("Author with id [%s] Not Found", workPackageData.getAsUserId());
      }
      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Remove Work Package", asUser);
      for (Long workItemUuid : workPackageData.getWorkItemUuids()) {
         IAtsWorkItem workItem =
            atsServer.getQueryService().createQuery().andUuids(workItemUuid).getResults().getAtMostOneOrNull();
         if (workItem == null) {
            throw new OseeArgumentException("Work Item with id [%s] Not Found", workItemUuid);
         }
         changes.deleteAttributes(workItem, AtsAttributeTypes.WorkPackageGuid);
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return Response.ok().build();
   }
}
