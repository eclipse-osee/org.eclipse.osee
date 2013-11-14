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
package org.eclipse.osee.ats.rest.internal.action;

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
import org.eclipse.osee.ats.rest.internal.AtsServerImpl;
import org.eclipse.osee.ats.rest.internal.action.ActionUtility.ActionLoadLevel;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("action")
public final class ActionResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry registry;

   public ActionResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      registry = orcsApi.getResourceRegistry();
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception {
      return AHTML.simplePage("Action Resource");
   }

   /**
    * @param guid of action to display
    * @return html representation of the action
    */
   @Path("{id}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAction(@PathParam("id") String guid) throws Exception {
      ArtifactReadable action = AtsServerImpl.get().getArtifactByGuid(guid);
      return ActionUtility.displayAction(registry, action, "Action - " + guid, ActionLoadLevel.HEADER);
   }

   /**
    * @param guid of action to display
    * @return html representation of the action w/ all ids resolved
    */
   @Path("{id}/full")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getActionFull(@PathParam("id") String guid) throws Exception {
      ArtifactReadable action = AtsServerImpl.get().getArtifactByGuid(guid);
      return ActionUtility.displayAction(registry, action, "Action - " + guid, ActionLoadLevel.HEADER_FULL);
   }

   /**
    * @param guid of action to operate on
    * @return StateResource for the give action
    */
   @Path("{id}/state")
   public StateResource transitionAction(@PathParam("id") String guid) throws Exception {
      return new StateResource(orcsApi, guid);
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
   public Response addOrUpdateAction(MultivaluedMap<String, String> form, @Context UriInfo uriInfo) throws Exception {
      // get parameters
      String query = uriInfo.getPath();
      System.out.println("query [" + query + "]");
      String title = form.getFirst("ats_title");
      String description = form.getFirst("desc");
      String actionableItemName = form.getFirst("actionableItems");
      String changeType = form.getFirst("changeType");
      String priority = form.getFirst("priority");

      // create action
      ArtifactId actionId =
         ActionUtility.createAction(orcsApi, title, description, actionableItemName, changeType, priority);
      ArtifactReadable action = AtsServerImpl.get().getArtifactByGuid(actionId.getGuid());

      String htmlStr =
         ActionUtility.displayAction(registry, action, "Action Created - " + action.getGuid(), ActionLoadLevel.HEADER);
      return Response.status(200).entity(htmlStr).build();
   }

}