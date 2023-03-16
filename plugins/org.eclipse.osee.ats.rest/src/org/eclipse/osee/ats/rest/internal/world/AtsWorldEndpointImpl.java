/*********************************************************************
 * Copyright (c) 2014 Boeing
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
import org.eclipse.osee.ats.api.column.AtsColumnToken;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.ResultRows;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@Path("world")
public class AtsWorldEndpointImpl implements AtsWorldEndpointApi {

   private final AtsApiServer atsApiServer;
   private final AtsApi atsApi;
   public final static List<String> namespaces =
      Arrays.asList("WorldXViewer", "BacklogXViewer", "SprintXViewer", "GoalXViewer", "TaskXViewer");

   public AtsWorldEndpointImpl(AtsApiServer atsApiServer) {
      this.atsApiServer = atsApiServer;
      this.atsApi = atsApiServer;
   }

   @Override
   @GET
   @Path("cust/global")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CustomizeData> getCustomizationsGlobal() {
      List<CustomizeData> datas = new LinkedList<>();
      for (String namespace : namespaces) {
         datas.addAll(atsApiServer.getCustomizationsGlobal(namespace));
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
         datas.addAll(atsApiServer.getCustomizations(namespace));
      }
      return datas;
   }

   @Override
   @GET
   @Path("my/{userArtId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getMyWorld(@PathParam("userArtId") ArtifactId userArtId) {
      ArtifactReadable userArt = (ArtifactReadable) atsApiServer.getQueryService().getArtifact(userArtId);
      AtsUser userById =
         atsApiServer.getUserService().getUserByUserId(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Collection<IAtsWorkItem> myWorldItems =
         atsApiServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      return myWorldItems;
   }

   @Override
   @GET
   @Path("my/{userArtId}/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUI(@PathParam("userArtId") ArtifactId userArtId) {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable userArt = (ArtifactReadable) atsApiServer.getQueryService().getArtifact(userArtId);
      AtsUser userById =
         atsApiServer.getUserService().getUserByUserId(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Collection<IAtsWorkItem> myWorldItems =
         atsApiServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);
      getDefaultUiTable(sb, "My World - " + userById.getName(), myWorldItems);
      return sb.toString();
   }

   @Override
   @GET
   @Path("my/{userArtId}/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   public String getMyWorldUICustomized(@PathParam("userArtId") ArtifactId userArtId, @PathParam("customizeGuid") String customizeGuid) {
      ArtifactReadable userArt = (ArtifactReadable) atsApiServer.getQueryService().getArtifact(userArtId);
      AtsUser userById =
         atsApiServer.getUserService().getUserByUserId(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
      Conditions.checkNotNull(userById, "User by Id " + userArtId);

      CustomizeData customization = atsApiServer.getStoreService().getCustomizationByGuid(customizeGuid);
      if (customization == null) {
         return AHTML.simplePage(String.format("No customization found with id [%s]", customizeGuid));
      }

      Collection<IAtsWorkItem> myWorldItems =
         atsApiServer.getQueryService().createQuery(WorkItemType.WorkItem).andAssignee(userById).getItems(
            IAtsWorkItem.class);

      String table = getCustomizedTable(atsApiServer,
         "MY World - " + userById.getName() + " - Customization: " + customization.getName(), customization,
         myWorldItems);
      return table;
   }

   @Override
   @GET
   @Path("coll/{collectorId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsWorkItem> getCollection(@PathParam("collectorId") ArtifactId collectorId) {
      ArtifactReadable collectorArt = (ArtifactReadable) atsApiServer.getQueryService().getArtifact(collectorId);
      return getCollection(collectorArt);
   }

   private Collection<IAtsWorkItem> getCollection(ArtifactReadable collectorArt) {
      Collection<IAtsWorkItem> myWorldItems = new ArrayList<>();
      if (collectorArt != null) {
         if (collectorArt.isOfType(AtsArtifactTypes.Goal)) {
            myWorldItems.addAll(atsApiServer.getWorkItemService().getWorkItems(
               collectorArt.getRelated(AtsRelationTypes.Goal_Member).getList()));
         } else if (collectorArt.isOfType(AtsArtifactTypes.AgileSprint)) {
            myWorldItems.addAll(atsApiServer.getWorkItemService().getWorkItems(
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
      ArtifactReadable collectorArt = (ArtifactReadable) atsApiServer.getQueryService().getArtifact(collectorId);
      getDefaultUiTable(sb, "Collection - " + collectorArt.getName(), getCollection(collectorId));
      return sb.toString();
   }

   @Override
   @GET
   @Path("coll/{collectorId}/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   public String getCollectionUICustomized(@PathParam("collectorId") ArtifactId collectorId, @PathParam("customizeGuid") String customizeGuid) {

      CustomizeData customization = atsApiServer.getStoreService().getCustomizationByGuid(customizeGuid);

      // get work items
      ArtifactReadable collectorArt = (ArtifactReadable) atsApiServer.getQueryService().getArtifact(collectorId);
      Collection<IAtsWorkItem> collectorItems = getCollection(collectorArt);

      if (customization == null) {
         return "";
      }
      String table = getCustomizedTable(atsApiServer,
         "Collector - " + collectorArt.getName() + " - Customization: " + customization.getName(), customization,
         collectorItems);
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
         rowStrs.add(AHTML.getHyperlinkNewTab("/ats/ui/action/" + workItem.getAtsId(), "open"));
         colOptions.add("");

         sb.append(AHTML.addRowMultiColumnTable(rowStrs.toArray(new String[rowStrs.size()]), null));
      }

      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private void getDefaultUiTable(StringBuilder sb, String tableName, Collection<IAtsWorkItem> workItems) {
      List<AtsColumnToken> columns = Arrays.asList(AtsColumnTokens.TeamColumn, AtsColumnTokens.StateColumn,
         AtsColumnTokens.PriorityColumn, AtsColumnTokens.ChangeTypeColumn, AtsColumnTokens.AssigneeColumn,
         AtsColumnTokens.TitleColumn, AtsColumnTokens.ActionableItemsColumn, AtsColumnTokens.CreatedDateColumn,
         AtsColumnTokens.TargetedVersionColumn, AtsColumnTokens.NotesColumn, AtsColumnTokens.AtsIdColumn);
      sb.append(AHTML.heading(2, tableName));
      sb.append(AHTML.beginMultiColumnTable(97, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Team", "State", "Priority", "Change Type", "Assignee",
         "Title", "AI", "Created", "Targted Version", "Notes", "ID")));
      for (IAtsWorkItem workItem : workItems) {
         List<String> values = new LinkedList<>();
         for (AtsColumnToken columnId : columns) {
            values.add(atsApiServer.getColumnService().getColumnText(columnId, workItem));
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

   @Override
   public ResultRows searchNew(AtsSearchData atsSearchData) {
      AtsWorldResultRowOperation op = new AtsWorldResultRowOperation(atsApi, atsSearchData);
      op.setNew(true);
      ResultRows rows = op.run();
      return rows;
   }
}