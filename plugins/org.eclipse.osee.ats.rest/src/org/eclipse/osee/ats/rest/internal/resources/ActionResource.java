/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ChangeType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("action")
public final class ActionResource {

   private final IAtsServer atsServer;
   private final OrcsApi orcsApi;
   private final IResourceRegistry resourceRegistry;

   public ActionResource(IAtsServer atsServer, OrcsApi orcsApi, IResourceRegistry resourceRegistry) {
      this.resourceRegistry = resourceRegistry;
      this.atsServer = atsServer;
      this.orcsApi = orcsApi;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception {
      return AHTML.simplePage("Action Resource");
   }

   /**
    * @param id (guid, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{id}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAction(@PathParam("id") String id) throws Exception {
      ArtifactReadable action = atsServer.getActionById(id);
      if (action == null) {
         return AHTML.simplePage(String.format("Action with id [%s] can not be found", id));
      }
      return atsServer.getWorkItemPage().getHtml(action, "Action - " + id, resourceRegistry, false);
   }

   /**
    * @param id (guid, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{id}/details")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getActionWithDetails(@PathParam("id") String id) throws Exception {
      ArtifactReadable action = atsServer.getActionById(id);
      if (action == null) {
         return AHTML.simplePage(String.format("Action with id [%s] can not be found", id));
      }
      return atsServer.getWorkItemPage().getHtml(action, "Action - " + id, resourceRegistry, true);
   }

   /**
    * @param form containing information to create a new action
    * @param form.ats_title - title of new action
    * @param form.desc - description of the action
    * @param form.actionableItems - actionable item name
    * @param form.changeType - Improvement, Refinement, Problem, Support
    * @param form.priority - 1-5
    * @param uriInfo
    * @return html representation of action created
    * @throws Exception
    */
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response createAction(MultivaluedMap<String, String> form, @Context UriInfo uriInfo) throws Exception {
      // get parameters
      String htmlStr = "";
      String query = uriInfo.getPath();
      System.out.println("query [" + query + "]");
      String searchId = form.getFirst("searchId");

      if (Strings.isValid(searchId)) {
         htmlStr = getSearchResults(searchId);
      } else {
         String title = form.getFirst("ats_title");
         String description = form.getFirst("desc");
         String actionableItemName = form.getFirst("actionableItems");
         String changeTypeStr = form.getFirst("changeType");
         String priority = form.getFirst("priority");
         String userId = form.getFirst("userId");

         Conditions.checkNotNullOrEmpty(userId, "userId");
         IAtsUser atsUser = atsServer.getUserService().getUserById(userId);
         if (atsUser == null) {
            throw new OseeStateException("User by id [%s] does not exist", userId);
         }

         IAtsChangeSet changes = atsServer.getStoreFactory().createAtsChangeSet("Create Action - Server", atsUser);
         orcsApi.getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(),
            ((ArtifactReadable) atsUser.getStoreObject()), "Create Action - Server");

         List<IAtsActionableItem> aias = new ArrayList<IAtsActionableItem>();

         ArtifactReadable aiArt =
            orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andTypeEquals(
               AtsArtifactTypes.ActionableItem).andNameEquals(actionableItemName).getResults().getExactlyOne();
         IAtsActionableItem aia = (IAtsActionableItem) atsServer.getConfig().getSoleByGuid(aiArt.getGuid());
         aias.add(aia);

         ChangeType changeType = ChangeType.valueOf(changeTypeStr);

         IAtsAction action =
            atsServer.getActionFactory().createAction(atsUser, title, description, changeType, priority, false, null,
               aias, new Date(), atsUser, null, changes).getFirst();
         changes.execute();

         htmlStr =
            atsServer.getWorkItemPage().getHtml(
               ((ArtifactReadable) action.getTeamWorkflows().iterator().next().getStoreObject()),
               "Action Created - " + action.getGuid(), resourceRegistry, false);
      }

      return Response.status(200).entity(htmlStr).build();
   }

   private String getSearchResults(String searchId) throws Exception {
      String results = null;
      ArtifactReadable action = null;
      if (GUID.isValid(searchId)) {
         action = atsServer.getArtifactByGuid(searchId);
      }
      if (action != null) {
         IAtsWorkItem workItem = atsServer.getWorkItemFactory().getWorkItem(action);
         if (workItem != null) {
            results = atsServer.getWorkItemPage().getHtml(action, "Action - " + searchId, resourceRegistry, false);
         } else {
            results = AHTML.simplePage(String.format("Undisplayable %s", action));
         }
      }
      if (!Strings.isValid(results)) {
         for (IAttributeType attrType : Arrays.asList(AtsAttributeTypes.AtsId, AtsAttributeTypes.LegacyPcrId)) {
            ResultSet<ArtifactReadable> legacyQueryResults =
               orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).and(attrType,
                  org.eclipse.osee.framework.core.enums.Operator.EQUAL, searchId).getResults();
            if (legacyQueryResults.size() == 1) {
               results =
                  atsServer.getWorkItemPage().getHtml(legacyQueryResults.getExactlyOne(), "Action - " + searchId,
                     resourceRegistry, false);
               break;
            } else if (legacyQueryResults.size() > 1) {
               results = getGuidListHtml(legacyQueryResults);
               break;
            }
         }
      }
      if (!Strings.isValid(results)) {
         results = AHTML.simplePage(String.format("Unknown Id [%s]", searchId));
      }
      return results;
   }

   private String getGuidListHtml(ResultSet<ArtifactReadable> results2) {
      StringBuilder sb = new StringBuilder("Returned ");
      sb.append(results2.size());
      sb.append(" results.");
      sb.append(AHTML.newline(2));
      for (ArtifactReadable art : results2) {
         sb.append(AHTML.getHyperlink("/ats/action/" + art.getGuid(), art.toString()));
         sb.append(AHTML.newline());
      }
      return AHTML.simplePage(sb.toString());
   }
}
