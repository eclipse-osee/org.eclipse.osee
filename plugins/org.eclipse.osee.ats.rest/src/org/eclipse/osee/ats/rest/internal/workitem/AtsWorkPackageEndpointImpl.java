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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.ev.JaxWorkPackageData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.ColorTeams;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkPackageEndpointImpl implements AtsWorkPackageEndpointApi {

   private static final String COLOR_TEAM_KEY = "colorTeam";
   private final AtsApi atsApi;

   public AtsWorkPackageEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
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
   @Produces({MediaType.APPLICATION_JSON})
   @Override
   public XResultData setWorkPackage(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData) {
      XResultData rd = new XResultData();
      ArtifactReadable workPackageArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(workPackageId);
      if (workPackageArt == null) {
         rd.errorf("Work Package with id [%s] Not Found", workPackageId);
      }
      AtsUser asUser = atsApi.getUserService().getUserByUserId(workPackageData.getAsUserId());
      if (asUser == null) {
         rd.errorf("Author with id [%s] Not Found", workPackageData.getAsUserId());
      }
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Set Work Package", asUser);
      for (Long workItemId : workPackageData.getWorkItemIds()) {
         IAtsWorkItem workItem = atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andIds(
            workItemId).getResults().getAtMostOneOrDefault(IAtsWorkItem.SENTINEL);
         if (workItem.isInvalid()) {
            rd.errorf("Work Item with id [%s] Not Found", workItemId);
         }
         if (!workItem.isTask() && !workItem.isTeamWorkflow()) {
            rd.errorf("Work Packages can only be set on Team Workflow or Task, not [%s]",
               workItem.getArtifactTypeName());
         }
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.WorkPackageReference, workPackageArt);
         rd.getIds().add(workItemId.toString());
      }
      if (!changes.isEmpty()) {
         TransactionId transactionId = changes.execute();
         rd.setTxId(transactionId.getIdString());
      }
      return rd;
   }

   @Override
   @GET
   @Path("colorteam")
   @Produces({MediaType.APPLICATION_JSON})
   public ColorTeams getColorTeams() {
      String colorTeamStr = atsApi.getConfigValue(COLOR_TEAM_KEY);
      ColorTeams teams = null;
      if (Strings.isValid(colorTeamStr)) {
         teams = atsApi.jaxRsApi().readValue(colorTeamStr, ColorTeams.class);
      } else {
         teams = new ColorTeams();
      }
      return teams;
   }

   @DELETE
   @Path("{workPackageId}/workitem")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Override
   public XResultData deleteWorkPackageItems(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData) {
      XResultData rd = new XResultData();
      AtsUser asUser = atsApi.getUserService().getUserByUserId(workPackageData.getAsUserId());
      if (asUser == null) {
         rd.errorf("Author with id [%s] Not Found", workPackageData.getAsUserId());
      }
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Remove Work Package", asUser);
      for (Long workItemId : workPackageData.getWorkItemIds()) {
         IAtsWorkItem workItem = atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andIds(
            workItemId).getResults().getAtMostOneOrDefault(IAtsWorkItem.SENTINEL);
         if (workItem.isInvalid()) {
            rd.errorf("Work Item with id [%s] Not Found", workItemId);
         }
         if (atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.WorkPackageReference,
            null) != null) {
            changes.deleteAttributes(workItem, AtsAttributeTypes.WorkPackageReference);
            rd.getIds().add(workItemId.toString());
         }
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return rd;
   }
}
