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

import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * @author Donald G. Dunne
 */
@Path("action")
public interface AtsActionEndpointApi {

   @GET
   @Produces(MediaType.TEXT_HTML)
   public abstract String get() throws Exception;

   /**
    * @param ids (guid, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public abstract List<IAtsWorkItem> getAction(String ids) throws Exception;

   /**
    * @param ids (guid, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}/details")
   @GET
   public abstract List<IAtsWorkItem> getActionDetails(String ids) throws Exception;

   /**
    * @param ids (ats.Legacy PCR Id) of action to display
    * @return html representation of the stateType;state
    */
   @Path("{ids}/legacy/state")
   @GET
   public abstract String getActionStateFromLegacyPcrId(String ids) throws Exception;

   /**
    * @param ids (guid, atsId) of action to display
    * @return html representation of the stateType;state
    */
   @Path("{ids}/state")
   @GET
   public abstract String getActionState(String ids) throws Exception;

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Path("query")
   @GET
   public abstract Set<IAtsWorkItem> query(UriInfo uriInfo) throws Exception;

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
   public abstract Response createAction(MultivaluedMap<String, String> form) throws Exception;

   /**
    * @param actionId (guid, atsId, long) of workItem
    * @return html representation of the changed attribute
    */
   @Path("{actionId}/attributeType/{attrTypeId}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public Attribute getActionAttributeByType(@PathParam("actionId") String actionId, @PathParam("attrTypeId") String attrTypeId);

   /**
    * @param actionId (guid, atsId, long) of workItem
    * @return html representation of the changed attribute
    */
   @Path("{actionId}/attributeType/{attrTypeId}")
   @PUT
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Attribute setActionAttributeByType(@PathParam("actionId") String actionId, @PathParam("attrTypeId") String attrTypeId, List<String> values);

}