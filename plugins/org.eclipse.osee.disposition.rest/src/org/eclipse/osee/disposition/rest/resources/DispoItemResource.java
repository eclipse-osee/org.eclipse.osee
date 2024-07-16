/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.rest.resources;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Angel Avila
 */
@Swagger
public class DispoItemResource {
   private final DispoApi dispoApi;
   private final BranchId branch;
   private final String setId;

   public DispoItemResource(DispoApi dispoApi, BranchId branch, String setId) {
      this.dispoApi = dispoApi;
      this.branch = branch;
      this.setId = setId;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Iterable<DispoItem> getAllDispoItems(@QueryParam("isDetailed") Boolean isDetailed) {
      List<DispoItem> allDispoItems = dispoApi.getDispoItems(branch, setId, isDetailed);
      return allDispoItems;
   }

   /**
    * Get a specific Dispositionable Item given a itemId
    *
    * @param itemId The Id of the Dispositionable Item to search for
    * @return The found Dispositionable Item if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Dispositionable Item
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @Path("{itemId}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public DispoItem getDispoItemsById(@PathParam("itemId") String itemId) {
      return dispoApi.getDispoItemById(branch, itemId);
   }

   /**
    * @return The found setId if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found branchId
    * @response.representation.404.doc Not Found, Could not find any branchId
    */
   @Path("getDispoItemId")
   @GET
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   public String getDispoItemId(@FormParam("name") String itemName) {
      return dispoApi.getDispoItemIdByName(branch, setId, itemName);
   }

   /**
    * Edit a specific Dispositionable Item given a itemId and new Dispositionable Item Data
    *
    * @param itemId The Id of the Dispositionable Item to search for
    * @param newDispoItem The data for the new Dispositionable Item
    * @param assignUser Whether or not a new user will be assigned to the Item
    * @return The updated Dispositionable Item if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Dispositionable Item
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @Path("{itemId}")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   public Response putDispoItem(@PathParam("itemId") String itemId, DispoItemData newDispoItem,
      @QueryParam("userName") String userName, @QueryParam("assignUser") boolean assignUser) {
      Response response;
      boolean wasEdited = dispoApi.editDispoItem(branch, itemId, newDispoItem, userName, assignUser);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_MODIFIED).entity(DispoMessages.Item_NotFound).build();
      }
      return response;
   }

   /**
    * Delete a specific Dispositionable Item given a itemId
    *
    * @param itemId The Id of the Dispositionable Item to search for
    * @return Response Code
    * @response.representation.200.doc OK, Found Dispositionable Item
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @Path("{itemId}")
   @DELETE
   public Response deleteDispoItem(@PathParam("itemId") String itemId, @QueryParam("userName") String userName) {
      Response response;
      boolean wasEdited = dispoApi.deleteDispoItem(branch, itemId, userName);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Item_NotFound).build();
      }
      return response;
   }

   @Path("{itemId}/annotation/")
   public AnnotationResource getAnnotation(@PathParam("itemId") String itemId) {
      return new AnnotationResource(dispoApi, branch, setId, itemId);
   }
}
