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

package org.eclipse.osee.activity.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;

/**
 * @author Ryan D. Brooks
 */
@Path("activity-log")
public interface ActivityLogEndpoint {

   /**
    * Get activity entry data
    *
    * @param entryId activity entry id
    * @return JSON stream containing activity entry data
    */
   @GET
   @Path("/entry/{entry-id}")
   @Produces({MediaType.APPLICATION_JSON})
   ActivityEntry getEntry(@PathParam("entry-id") ActivityEntryId entryId);

   /**
    * Create a new activity entry
    *
    * @param type activity type id
    * @param parentId of the parent activity
    * @param status of the activity
    * @param message to log for the activity
    * @return entryId
    */
   @POST
   @Path("/entry/{activity-type}/{parent-id}/{status}")
   @Produces({MediaType.APPLICATION_JSON})
   ActivityEntryId createEntry(@PathParam("activity-type") ActivityTypeId type, @PathParam("parent-id") Long parentId, @PathParam("status") Integer status, String message);

   /**
    * Create a new activity type with the given token fields. If id is not valid then a new one will be generated and
    * used.
    */
   @POST
   @Path("/type")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   ActivityTypeToken createIfAbsent(ActivityTypeToken activityType);

   /**
    * Modify an entries status
    *
    * @param entryId entry to modify
    * @param statusId to set entry status to
    * @return response whether entry was modified
    * @response.representation.200.doc entry status modified
    * @response.representation.304.doc entry status not modified
    */
   @PUT
   @Path("/entry/{entry-id}/status/{status-id}")
   Response updateEntry(@PathParam("entry-id") Long entryId, @PathParam("status-id") Integer statusId);

}
