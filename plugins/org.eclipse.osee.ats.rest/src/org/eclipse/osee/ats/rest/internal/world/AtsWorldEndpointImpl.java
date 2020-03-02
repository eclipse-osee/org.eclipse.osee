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
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.ResultRows;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("world")
public class AtsWorldEndpointImpl implements AtsWorldEndpointApi {

   private final IAtsServer atsServer;
   private final AtsApi atsApi;
   public final static List<String> namespaces =
      Arrays.asList("WorldXViewer", "BacklogXViewer", "SprintXViewer", "GoalXViewer", "TaskXViewer");

   public AtsWorldEndpointImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
      this.atsApi = atsServer;
   }

   @Override
   @GET
   @Path("cust/global")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CustomizeData> getCustomizationsGlobal() {
      List<CustomizeData> datas = new LinkedList<>();
      for (String namespace : namespaces) {
         datas.addAll(atsServer.getCustomizationsGlobal(namespace));
      }
      return datas;
   }

   @Override
   @GET
   @Path("cust")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CustomizeData> getCustomizations() {
      List<CustomizeData> datas = new LinkedList<>();
      for (String namespace : namespaces) {
         datas.addAll(atsServer.getCustomizations(namespace));
      }
      return datas;
   }

   @Override
   @GET
   @Path("my/{userArtId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getMyWorld(@PathParam("userArtId") ArtifactId userArtId) {
      ArtifactReadable userArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(userArtId);
      AtsUser userById =
         atsServer.getUserService().getUserById(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      return myWorldItems;
   }

   @Override
   @GET
   @Path("my/{userArtId}/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUI(@PathParam("userArtId") ArtifactId userArtId) {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable userArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(userArtId);
      AtsUser userById =
         atsServer.getUserService().getUserById(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Collection<IAtsWorkItem> myWorldItems =
         atsServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      getDefaultUiTable(sb, "My World - " + userById.getName(), myWorldItems);
      return sb.toString();
   }

   @Override
   @GET
   @Path("my/{userArtId}/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUICustomized(@PathParam("userArtId") ArtifactId userArtId, @PathParam("customizeGuid") String customizeGuid) {
      ElapsedTime time = new ElapsedTime("start");
      ArtifactReadable userArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(userArtId);
      AtsUser userById =
         atsServer.getUserService().getUserById(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Conditions.checkNotNull(userById, "User by Id " + userArtId);

      ElapsedTime getCustomization = new ElapsedTime("getCustomizationByGuid");
      CustomizeData customization = atsServer.getStoreService().getCustomizationByGuid(customizeGuid);
      if (customization == null) {
         return AHTML.simplePage(String.format("No customization found with id [%s]", customizeGuid));
      }
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

   @Override
   @GET
   @Path("coll/{collectorId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getCollection(@PathParam("collectorId") ArtifactId collectorId) {
      ArtifactReadable collectorArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(collectorId);
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

   @Override
   @GET
   @Path("coll/{collectorId}/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getCollectionUI(@PathParam("collectorId") ArtifactId collectorId) {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable collectorArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(collectorId);
      getDefaultUiTable(sb, "Collection - " + collectorArt.getName(), getCollection(collectorId));
      return sb.toString();
   }

   @Override
   @GET
   @Path("coll/{collectorId}/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   public String getCollectionUICustomized(@PathParam("collectorId") ArtifactId collectorId, @PathParam("customizeGuid") String customizeGuid) {
      ElapsedTime time = new ElapsedTime("start");

      ElapsedTime getCustomization = new ElapsedTime("getCustomizationByGuid");
      CustomizeData customization = atsServer.getStoreService().getCustomizationByGuid(customizeGuid);
      getCustomization.end();

      // get work items
      ElapsedTime getWorkItems = new ElapsedTime("get work items");
      ArtifactReadable collectorArt = (ArtifactReadable) atsServer.getQueryService().getArtifact(collectorId);
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

   private void getDefaultUiTable(StringBuilder sb, String tableName, Collection<IAtsWorkItem> workItems) {
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

   @Override
   public ResultRows search(AtsSearchData atsSearchData) {
      AtsWorldResultRowOperation op = new AtsWorldResultRowOperation(atsApi, atsSearchData);
      ResultRows rows = op.run();
      return rows;
   }
}