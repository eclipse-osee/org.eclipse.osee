/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.world;

import java.util.Arrays;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Donald G. Dunne
 */
@Path("world")
public class WorldResource {

   private final IAtsServer atsServer;

   public WorldResource(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @GET
   @Path("my/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getMyWorld(@PathParam("uuid") int uuid) throws Exception {
      IAtsUser userById = atsServer.getUserService().getUserById(new Long(uuid));
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      return myWorldItems;
   }

   @GET
   @Path("my/{uuid}/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUI(@PathParam("uuid") int uuid) throws Exception {
      StringBuilder sb = new StringBuilder();
      IAtsUser userById = atsServer.getUserService().getUserById(new Long(uuid));
      sb.append(AHTML.heading(2, "MY World - " + userById.getName()));
      sb.append(AHTML.beginMultiColumnTable(97, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Team", "State", "Priority", "Change Type", "Assignee",
         "Title", "AI", "Created", "Targted Version", "Notes")));
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      for (IAtsWorkItem workItem : myWorldItems) {
         sb.append(AHTML.addRowMultiColumnTable(getTeam(workItem), getState(workItem), getPriority(workItem),
            getChangeType(workItem), getAssignee(workItem), getTitle(workItem), getAI(workItem),
            getCreatedDate(workItem), getTargetedVersion(workItem), getNotes(workItem)));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private String getAI(IAtsWorkItem workItem) {
      return "TBD";
   }

   private String getNotes(IAtsWorkItem workItem) {
      return atsServer.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.SmaNote, "");
   }

   private String getTargetedVersion(IAtsWorkItem workItem) {
      String result = "";
      IAtsVersion version = atsServer.getVersionService().getTargetedVersion(workItem);
      if (version != null) {
         result = version.toString();
      }
      return result;
   }

   private String getCreatedDate(IAtsWorkItem workItem) {
      return workItem.getCreatedDate().toString();
   }

   private String getTitle(IAtsWorkItem workItem) {
      return atsServer.getColumnService().getColumnText(AtsColumnId.Title, workItem);
   }

   private String getAssignee(IAtsWorkItem workItem) {
      return atsServer.getColumnService().getColumnText(AtsColumnId.Assignees, workItem);
   }

   private String getChangeType(IAtsWorkItem workItem) {
      return atsServer.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.ChangeType, "");
   }

   private String getPriority(IAtsWorkItem workItem) {
      return atsServer.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.PriorityType, "");
   }

   private String getState(IAtsWorkItem workItem) {
      return workItem.getStateMgr().getCurrentStateName();
   }

   private String getTeam(IAtsWorkItem workItem) {
      return atsServer.getColumnService().getColumnText(AtsColumnId.Team, workItem);
   }

}
