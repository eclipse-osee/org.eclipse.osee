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
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.ev.JaxWorkPackageData;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.ColorTeam;
import org.eclipse.osee.ats.api.util.ColorTeams;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkPackageEndpointImpl implements AtsWorkPackageEndpointApi {

   private static final String COLOR_TEAM_KEY = "colorTeam";
   private final AtsApi atsApi;
   private final Log logger;

   public AtsWorkPackageEndpointImpl(AtsApi atsApi, Log logger) {
      this.atsApi = atsApi;
      this.logger = logger;
   }

   @GET
   @Path("{workPackageId}/workitem")
   @Produces({MediaType.APPLICATION_JSON})
   @Override
   public Collection<IAtsWorkItem> getWorkItems(@PathParam("workPackageId") long workPackageId) {
      ArtifactReadable workPackageArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(workPackageId);
      if (workPackageArt == null) {
         throw new OseeArgumentException("Work Package with id [%s] Not Found", workPackageId);
      }
      List<IAtsWorkItem> arts =
         atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andAttr(AtsAttributeTypes.WorkPackageReference,
            workPackageArt.getIdString()).getResults().getList();
      return arts;
   }

   @PUT
   @Path("{workPackageId}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Override
   public Response setWorkPackage(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData) {
      ArtifactReadable workPackageArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(workPackageId);
      if (workPackageArt == null) {
         throw new OseeArgumentException("Work Package with id [%s] Not Found", workPackageId);
      }
      IAtsUser asUser = atsApi.getUserService().getUserById(workPackageData.getAsUserId());
      if (asUser == null) {
         throw new OseeArgumentException("Author with id [%s] Not Found", workPackageData.getAsUserId());
      }
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Set Work Package", asUser);
      for (Long workItemId : workPackageData.getWorkItemIds()) {
         IAtsWorkItem workItem = atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andIds(
            workItemId).getResults().getAtMostOneOrNull();
         if (workItem == null) {
            throw new OseeArgumentException("Work Item with id [%s] Not Found", workItemId);
         }
         if (!workItem.isTask() && !workItem.isTeamWorkflow()) {
            throw new OseeArgumentException("Work Packages can only be set on Team Workflow or Task, not [%s]",
               workItem.getArtifactTypeName());
         }
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.WorkPackageReference, workPackageArt);
         autoAddWorkItemToColorTeamGoals(getColorTeams(), workPackageArt, workPackageId, workItem, changes);
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return Response.ok().build();
   }

   private void autoAddWorkItemToColorTeamGoals(ColorTeams colorTeams, ArtifactReadable workPackageArt, long workPackageId, IAtsWorkItem workItem, IAtsChangeSet changes) {
      try {
         String workPackageColorTeam =
            atsApi.getAttributeResolver().getSoleAttributeValue(workPackageArt, AtsAttributeTypes.ColorTeam, null);
         if (Strings.isValid(workPackageColorTeam)) {
            for (ColorTeam colorTeam : colorTeams.getTeams()) {
               if (!colorTeam.getGoalIds().isEmpty() && colorTeam.getName().equals(workPackageColorTeam)) {
                  for (Long id : colorTeam.getGoalIds()) {
                     ArtifactReadable goalArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(id);
                     if (goalArt != null) {
                        IAtsWorkItem goalWorkItem = atsApi.getWorkItemFactory().getWorkItem(goalArt);
                        if (!atsApi.getRelationResolver().areRelated(goalWorkItem, AtsRelationTypes.Goal_Member,
                           workItem)) {
                           changes.relate(goalWorkItem, AtsRelationTypes.Goal_Member, workItem);
                        }
                     } else {
                        logger.error(
                           "Goal Id [%d] invalid in Color Team [%s] for Work Package [%s] Work Item [%s]; Skipping...",
                           id, colorTeam, workPackageArt.toStringWithId(), workItem.toStringWithId());
                     }
                  }
               }
            }
         }
      } catch (Exception ex) {
         logger.error(ex,
            "Error adding Work ITem to Color Team goals. Color Teams [%s] Work Package [%s] Work Item [%s]", colorTeams,
            workPackageArt.toStringWithId(), workItem.toStringWithId());
      }
   }

   @Override
   @GET
   @Path("colorteam")
   @Produces({MediaType.APPLICATION_JSON})
   public ColorTeams getColorTeams() {
      String colorTeamStr = atsApi.getConfigValue(COLOR_TEAM_KEY);
      ColorTeams teams = null;
      if (Strings.isValid(colorTeamStr)) {
         teams = JsonUtil.readValue(colorTeamStr, ColorTeams.class);
      } else {
         teams = new ColorTeams();
      }
      return teams;
   }

   @DELETE
   @Path("{workPackageId}/workitem")
   @Consumes({MediaType.APPLICATION_JSON})
   @Override
   public Response deleteWorkPackageItems(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData) {
      IAtsUser asUser = atsApi.getUserService().getUserById(workPackageData.getAsUserId());
      if (asUser == null) {
         throw new OseeArgumentException("Author with id [%s] Not Found", workPackageData.getAsUserId());
      }
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Remove Work Package", asUser);
      for (Long workItemId : workPackageData.getWorkItemIds()) {
         IAtsWorkItem workItem = atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andIds(
            workItemId).getResults().getAtMostOneOrNull();
         if (workItem == null) {
            throw new OseeArgumentException("Work Item with id [%s] Not Found", workItemId);
         }
         if (atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.WorkPackageReference,
            null) != null) {
            changes.deleteAttributes(workItem, AtsAttributeTypes.WorkPackageReference);
         }
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return Response.ok().build();
   }
}
