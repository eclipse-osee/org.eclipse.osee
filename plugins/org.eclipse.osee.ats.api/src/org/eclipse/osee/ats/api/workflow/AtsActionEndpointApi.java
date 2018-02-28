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
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
@Path("action")
public interface AtsActionEndpointApi {

   @GET
   @Produces(MediaType.TEXT_HTML)
   String get();

   /**
    * @param ids (atsId, artId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsWorkItem> getAction(@PathParam("ids") String ids);

   /**
    * @param ids (atsId, artId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}/details")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsWorkItem> getActionDetails(@PathParam("ids") String ids);

   /**
    * @param ids (atsId, artId) of action children to display
    * @return html representation of the action children
    */
   @Path("{ids}/child")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsWorkItem> getActionChildren(@PathParam("ids") String ids);

   /**
    * @param ids (ats.Legacy PCR Id) of action to display
    * @return html representation of the stateType;state
    */
   @Path("{ids}/legacy/state")
   @GET
   String getActionStateFromLegacyPcrId(@PathParam("ids") String ids);

   /**
    * @param ids (atsId, artId) of action to display
    * @return html representation of the stateType;state
    */
   @Path("{ids}/state")
   @GET
   String getActionState(@PathParam("ids") String ids);

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Path("query")
   @GET
   Set<IAtsWorkItem> query(UriInfo uriInfo);

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewActionResult createAction(NewActionData newActionData);

   @Path("createEmpty")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public String createEmptyAction(@QueryParam("userId") String userId, @QueryParam("ai") String actionItem, @QueryParam("title") String title);

   /**
    * @param form containing information to create a new action
    * @param form.ats_title - (required) title of new action
    * @param form.desc - description of the action
    * @param form.actionableItems - (required) actionable item name
    * @param form.changeType - (required) Improvement, Refinement, Problem, Support
    * @param form.priority - (required) 1-5
    * @param form.userId - (required)
    * @return html representation of action created
    */
   @POST
   @Consumes("application/x-www-form-urlencoded")
   Response createAction(MultivaluedMap<String, String> form);

   /**
    * @param actionId (atsId, artId) of workItem
    * @param attrTypeId can be the id of the attrType or one of (Title, Priority, ColorTeam, Assignee, IPT, Originator,
    * Version, State). If State is sent in, it will result in the "transition" of the workflow. Version can be either id
    * or name.
    * @return Attribute containing current values
    */
   @Path("{actionId}/attributeType/{attrTypeId}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   Attribute getActionAttributeByType(@PathParam("actionId") String actionId, @PathParam("attrTypeId") String attrTypeId);

   /**
    * @param actionId (atsId, artId) of workItem
    * @param attrTypeId can be the id of the attrType or one of (Title, Priority, ColorTeam, Assignee, IPT, Originator,
    * Version, State). If State is sent in, it will result in the "transition" of the workflow. Version can be either id
    * or name.
    * @return Attribute containing current values after change
    */
   @Path("{actionId}/attributeType/{attrTypeId}")
   @PUT
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   Attribute setActionAttributeByType(@PathParam("actionId") String actionId, @PathParam("attrTypeId") String attrTypeId, List<String> values);

   /**
    * @param workItemId (atsId, artId)
    * @param changeType: Assignee, Version
    * @param artifacts: Assignee
    * @return artifacts changed to
    */
   @Path("{workItemId}/changeType/{changeType}")
   @PUT
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Collection<ArtifactToken> setByArtifactToken(@PathParam("workItemId") String workItemId, @PathParam("changeType") String attrTypeId, Collection<ArtifactToken> artifacts);

   /**
    * @return valid unreleased versions to select
    */
   @GET
   @Path("{id}/UnreleasedVersions")
   @Produces(MediaType.APPLICATION_JSON)
   List<String> getUnreleasedVersionNames(@PathParam("id") String id);

   /**
    * @return valid transition-to states in order of default state, other states and return states
    */
   @GET
   @Path("{id}/TransitionToStates")
   @Produces(MediaType.APPLICATION_JSON)
   List<String> getTransitionToStateNames(@PathParam("id") String id);

}