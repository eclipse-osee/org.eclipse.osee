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
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IAtsColumnId;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("world")
public class WorldResource {

   private final IAtsServer atsServer;
   public final static List<String> namespaces =
      Arrays.asList("org.eclipse.osee.ats.WorldXViewer", "org.eclipse.osee.ats.BacklogXViewer",
         "org.eclipse.osee.ats.SprintXViewer", "org.eclipse.osee.ats.GoalXViewer", "org.eclipse.osee.ats.TaskXViewer");

   public WorldResource(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @GET
   @Path("cust/global")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CustomizeData> getCustomizationsGlobal() throws Exception {
      List<CustomizeData> datas = new LinkedList<>();
      for (String namespace : namespaces) {
         datas.addAll(atsServer.getCustomizationsGlobal(namespace));
      }
      return datas;
   }

   @GET
   @Path("cust")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CustomizeData> getCustomizations() throws Exception {
      List<CustomizeData> datas = new LinkedList<>();
      for (String namespace : namespaces) {
         datas.addAll(atsServer.getCustomizations(namespace));
      }
      return datas;
   }

   @GET
   @Path("my/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getMyWorld(@PathParam("id") int id) throws Exception {
      ArtifactReadable userArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(Long.valueOf(id));
      IAtsUser userById =
         atsServer.getUserService().getUserById(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      return myWorldItems;
   }

   @GET
   @Path("my/{id}/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUI(@PathParam("id") int id) throws Exception {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable userArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(Long.valueOf(id));
      IAtsUser userById =
         atsServer.getUserService().getUserById(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      getDefaultUiTable(id, sb, "My World - " + userById.getName(), myWorldItems);
      return sb.toString();
   }

   @GET
   @Path("my/{id}/ui/{customize_guid}")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUICustomized(@PathParam("id") int id, @PathParam("customize_guid") String customize_guid) throws Exception {
      ElapsedTime time = new ElapsedTime("start");
      ArtifactReadable userArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(Long.valueOf(id));
      IAtsUser userById =
         atsServer.getUserService().getUserById(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Conditions.checkNotNull(userById, "User by Id " + id);

      ElapsedTime getCustomization = new ElapsedTime("getCustomizationByGuid");
      CustomizeData customization = atsServer.getStoreService().getCustomizationByGuid(customize_guid);
      getCustomization.end();

      ElapsedTime getWorkItems = new ElapsedTime("get work items");
      // get work items
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      getWorkItems.end();

      String table = getCustomizedTable(atsServer,
         "MY World - " + userById.getName() + " - Customization: " + customization.getName(), customization,
         myWorldItems);
      time.end();
      return table;
   }

   @GET
   @Path("coll/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getCollection(@PathParam("id") long id) throws Exception {
      ArtifactReadable collectorArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(id);
      return getCollection(collectorArt);
   }

   private Collection<IAtsWorkItem> getCollection(ArtifactReadable collectorArt) {
      Collection<IAtsWorkItem> myWorldItems = new ArrayList<>();
      if (collectorArt != null) {
         if (collectorArt.isOfType(AtsArtifactTypes.Goal)) {
            myWorldItems.addAll(atsServer.getWorkItemService().getWorkItems(
               collectorArt.getRelated(AtsRelationTypes.Goal_Member).getList()));
         } else if (collectorArt.isOfType(AtsArtifactTypes.AgileSprint)) {
            myWorldItems.addAll(atsServer.getWorkItemService().getWorkItems(
               collectorArt.getRelated(AtsRelationTypes.AgileSprintToItem_AtsItem).getList()));
         }
      }
      return myWorldItems;
   }

   @GET
   @Path("coll/{id}/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getCollectionUI(@PathParam("id") long id) throws Exception {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable collectorArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(id);
      getDefaultUiTable(id, sb, "Collection - " + collectorArt.getName(), getCollection(id));
      return sb.toString();
   }

   @GET
   @Path("coll/{id}/ui/{customize_guid}")
   @Produces(MediaType.TEXT_HTML)
   public String getCollectionUICustomized(@PathParam("id") long id, @PathParam("customize_guid") String customize_guid) throws Exception {
      ElapsedTime time = new ElapsedTime("start");

      ElapsedTime getCustomization = new ElapsedTime("getCustomizationByGuid");
      CustomizeData customization = atsServer.getStoreService().getCustomizationByGuid(customize_guid);
      getCustomization.end();

      // get work items
      ElapsedTime getWorkItems = new ElapsedTime("get work items");
      ArtifactReadable collectorArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(id);
      Collection<IAtsWorkItem> collectorItems = getCollection(collectorArt);
      getWorkItems.end();

      String table = getCustomizedTable(atsServer,
         "Collector - " + collectorArt.getName() + " - Customization: " + customization.getName(), customization,
         collectorItems);
      time.end();
      return table;

   }

   public static String getCustomizedTable(AtsApi atsApi, String title, CustomizeData customization, Collection<IAtsWorkItem> workItems) {
      Conditions.checkNotNull(customization, "Customization " + customization + " ");
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(2, title));
      sb.append(AHTML.beginMultiColumnTable(97, 1));

      // get column headers
      List<String> headers = new ArrayList<>();
      for (XViewerColumn col : customization.getColumnData().getColumns()) {
         if (col.isShow()) {
            headers.add(col.getName());
         }
      }
      headers.add("Link");
      sb.append(AHTML.addHeaderRowMultiColumnTable(headers));

      AtsConfigurations configurations = atsApi.getConfigService().getConfigurations();
      for (IAtsWorkItem workItem : workItems) {

         // create row
         List<String> rowStrs = new ArrayList<>();
         List<String> colOptions = new ArrayList<>();
         for (XViewerColumn col : customization.getColumnData().getColumns()) {
            if (col.isShow()) {
               String text = "";
               if (Strings.isValid(col.getId())) {
                  text = atsApi.getColumnService().getColumnText(configurations, col.getId(), workItem);
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

      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private void getDefaultUiTable(long id, StringBuilder sb, String tableName, Collection<IAtsWorkItem> workItems) throws Exception {
      List<IAtsColumnId> columns = Arrays.asList(AtsColumnId.Team, AtsColumnId.State, AtsColumnId.Priority,
         AtsColumnId.ChangeType, AtsColumnId.Assignees, AtsColumnId.Title, AtsColumnId.ActionableItem,
         AtsColumnId.CreatedDate, AtsColumnId.TargetedVersion, AtsColumnId.Notes, AtsColumnId.AtsId);
      sb.append(AHTML.heading(2, tableName));
      sb.append(AHTML.beginMultiColumnTable(97, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Team", "State", "Priority", "Change Type", "Assignee",
         "Title", "AI", "Created", "Targted Version", "Notes", "ID")));
      for (IAtsWorkItem workItem : workItems) {
         List<String> values = new LinkedList<>();
         for (IAtsColumnId columnId : columns) {
            values.add(atsServer.getColumnService().getColumnText(columnId, workItem));
         }
         sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
      }
      sb.append(AHTML.endMultiColumnTable());
   }

}
