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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Donald G. Dunne
 */
@Path("world")
public class WorldResource {

   private final IAtsServer atsServer;
   public final static String NAMESPACE = "org.eclipse.osee.ats.WorldXViewer";

   public WorldResource(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @GET
   @Path("cust/global")
   @Produces(MediaType.TEXT_PLAIN)
   public String getCustomizationsGlobal() throws Exception {
      StringBuilder sb = new StringBuilder();
      for (CustomizeData customization : atsServer.getCustomizationsGlobal(NAMESPACE)) {
         sb.append(customization.toString() + "\n");
      }
      return sb.toString();
   }

   @GET
   @Path("cust")
   @Produces(MediaType.TEXT_PLAIN)
   public String getCustomizations() throws Exception {
      StringBuilder sb = new StringBuilder();
      for (CustomizeData customization : atsServer.getCustomizations(NAMESPACE)) {
         sb.append(customization.toString() + "\n");
      }
      return sb.toString();
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
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      getDefaultUiTable(uuid, sb, "My World - " + userById.getName(), myWorldItems);
      return sb.toString();
   }

   @GET
   @Path("my/{uuid}/ui/{customize_guid}")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUICustomized(@PathParam("uuid") int uuid, @PathParam("customize_guid") String customize_guid) throws Exception {
      ElapsedTime time = new ElapsedTime("start");
      IAtsUser userById = atsServer.getUserService().getUserById(new Long(uuid));
      Conditions.checkNotNull(userById, "User by Id " + uuid);

      ElapsedTime getCustomization = new ElapsedTime("getCustomizationByGuid");
      CustomizeData customization = atsServer.getCustomizationByGuid(customize_guid);
      getCustomization.end();

      ElapsedTime getWorkItems = new ElapsedTime("get work items");
      // get work items
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      getWorkItems.end();

      String table =
         getCustomizedTable("MY World - " + userById.getName() + " - Customization: " + customization.getName(),
            customization, myWorldItems);
      time.end();
      return table;
   }

   @GET
   @Path("coll/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getCollection(@PathParam("uuid") long uuid) throws Exception {
      ArtifactReadable collectorArt = atsServer.getArtifact(uuid);
      return getCollection(collectorArt);
   }

   private Collection<IAtsWorkItem> getCollection(ArtifactReadable collectorArt) {
      Collection<IAtsWorkItem> myWorldItems = new ArrayList<>();
      if (collectorArt != null) {
         if (collectorArt.isOfType(AtsArtifactTypes.Goal)) {
            myWorldItems.addAll(atsServer.getWorkItemFactory().getWorkItems(
               collectorArt.getRelated(AtsRelationTypes.Goal_Member).getList()));
         } else if (collectorArt.isOfType(AtsArtifactTypes.AgileSprint)) {
            myWorldItems.addAll(atsServer.getWorkItemFactory().getWorkItems(
               collectorArt.getRelated(AtsRelationTypes.AgileSprintToItem_AtsItem).getList()));
         }
      }
      return myWorldItems;
   }

   @GET
   @Path("coll/{uuid}/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getCollectionUI(@PathParam("uuid") long uuid) throws Exception {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable collectorArt = atsServer.getArtifact(uuid);
      getDefaultUiTable(uuid, sb, "Collection - " + collectorArt.getName(), getCollection(uuid));
      return sb.toString();
   }

   @GET
   @Path("coll/{uuid}/ui/{customize_guid}")
   @Produces(MediaType.TEXT_HTML)
   public String getCollectionUICustomized(@PathParam("uuid") long uuid, @PathParam("customize_guid") String customize_guid) throws Exception {
      ElapsedTime time = new ElapsedTime("start");

      ElapsedTime getCustomization = new ElapsedTime("getCustomizationByGuid");
      CustomizeData customization = atsServer.getCustomizationByGuid(customize_guid);
      getCustomization.end();

      // get work items
      ElapsedTime getWorkItems = new ElapsedTime("get work items");
      ArtifactReadable collectorArt = atsServer.getArtifact(uuid);
      Collection<IAtsWorkItem> collectorItems = getCollection(collectorArt);
      getWorkItems.end();

      String table =
         getCustomizedTable("Collector - " + collectorArt.getName() + " - Customization: " + customization.getName(),
            customization, collectorItems);
      time.end();
      return table;

   }

   private String getCustomizedTable(String title, CustomizeData customization, Collection<IAtsWorkItem> workItems) {
      Conditions.checkNotNull(customization, "Cuatomization" + customization);
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(2, title));
      sb.append(AHTML.beginMultiColumnTable(97, 1));

      ElapsedTime getHeaders = new ElapsedTime("get column headers");
      // get column headers
      List<String> headers = new ArrayList<>();
      for (XViewerColumn col : customization.getColumnData().getColumns()) {
         System.err.println(col);
         if (col.isShow()) {
            headers.add(col.getName());
         }
      }
      headers.add("Link");
      sb.append(AHTML.addHeaderRowMultiColumnTable(headers));
      getHeaders.end();

      AtsConfigurations configurations = atsServer.getConfigurations();
      ElapsedTime getRows = new ElapsedTime("get rows");
      for (IAtsWorkItem workItem : workItems) {

         // create row
         List<String> rowStrs = new ArrayList<>();
         List<String> colOptions = new ArrayList<>();
         for (XViewerColumn col : customization.getColumnData().getColumns()) {
            if (col.isShow()) {
               String text = "";
               if (Strings.isValid(col.getId())) {
                  text = atsServer.getColumnService().getColumnText(configurations, col.getId(), workItem);
               }
               rowStrs.add(text);
               colOptions.add("");
            }
         }

         // add link column (on all customizations)
         rowStrs.add(AHTML.getHyperlink("/ats/ui/action/" + workItem.getAtsId(), "open"));
         colOptions.add("");

         sb.append(AHTML.addRowMultiColumnTable(rowStrs.toArray(new String[rowStrs.size()]), null));
      }
      getRows.end();

      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private void getDefaultUiTable(long uuid, StringBuilder sb, String tableName, Collection<IAtsWorkItem> workItems) throws Exception {
      sb.append(AHTML.heading(2, tableName));
      sb.append(AHTML.beginMultiColumnTable(97, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Team", "State", "Priority", "Change Type", "Assignee",
         "Title", "AI", "Created", "Targted Version", "Notes", "ID")));
      for (IAtsWorkItem workItem : workItems) {
         sb.append(AHTML.addRowMultiColumnTable(getTeam(workItem), getState(workItem), getPriority(workItem),
            getChangeType(workItem), getAssignee(workItem), getTitle(workItem), getAI(workItem),
            getCreatedDate(workItem), getTargetedVersion(workItem), getNotes(workItem), getId(workItem)));
      }
      sb.append(AHTML.endMultiColumnTable());
   }

   private String getId(IAtsWorkItem workItem) {
      return atsServer.getColumnService().getColumnText(AtsColumnId.AtsId, workItem);
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
      return atsServer.getColumnService().getColumnText(AtsColumnId.State, workItem);
   }

   private String getTeam(IAtsWorkItem workItem) {
      return atsServer.getColumnService().getColumnText(AtsColumnId.Team, workItem);
   }

}
