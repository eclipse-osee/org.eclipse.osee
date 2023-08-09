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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class DispoItemEndpoint {
   private final DispoApi dispoApi;
   private final BranchId branch;
   private final String setId;

   public DispoItemEndpoint(DispoApi dispoApi, BranchId branch, String setId) {
      this.dispoApi = dispoApi;
      this.branch = branch;
      this.setId = setId;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get all Dispo Items")
   @Tag(name = "items")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Iterable<DispoItem> getAllDispoItems(
      @Parameter(description = "Is detailed", required = true) @QueryParam("isDetailed") Boolean isDetailed) {
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
   @Operation(summary = "Get a specific Dispositionable Item given a itemId")
   @Tag(name = "item")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Dispositionable Item"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Dispositionable Items")})
   public DispoItem getDispoItemsById(
      @Parameter(description = "TThe Id of the Dispositionable Item to search for", required = true) @PathParam("itemId") String itemId) {
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
   @Operation(summary = "Get an Dispo Item ID given an Item name")
   @Tag(name = "item")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Item ID"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find Item ID")})
   public String getDispoItemId(
      @Parameter(description = "The Item name", required = true) @FormParam("name") String itemName) {
      return dispoApi.getDispoItemIdByName(branch, setId, itemName);
   }

   /**
    * Update all items in a given set
    *
    * @return response
    * @response.representation.200.doc OK, Found Dispositionable Item
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @Path("updateAllItems")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Update all Dispo Items")
   @Tag(name = "items")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response updateAllDispoItems() {
      Response response;
      boolean wasEdited = dispoApi.updateAllDispoItems(branch, setId);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_MODIFIED).entity(DispoMessages.Item_NotFound).build();
      }
      return response;
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
   @Operation(summary = "Edit a specific Dispositionable Item given a itemId and new Dispositionable Item Data")
   @Tag(name = "item")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Dispositionable Item"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not any Dispositionable Items")})
   public Response putDispoItem(@PathParam("itemId") String itemId, DispoItemData newDispoItem,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName,
      @Parameter(description = "Assign User", required = true) @QueryParam("assignUser") boolean assignUser) {
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
   @Operation(summary = "Delete a specific Dispositionable Item given a itemId")
   @Tag(name = "item")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Deleted Dispositionable Item"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not any Dispositionable Items")})
   public Response deleteDispoItem(
      @Parameter(description = "The Id of the Dispositionable Item to search for", required = true) @PathParam("itemId") String itemId,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
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
   @Operation(summary = "Get Annotation")
   @Tag(name = "annotation")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public DispoAnnotationEndpoint getAnnotation(
      @Parameter(description = "The Id of the Dispositionable Item", required = true) @PathParam("itemId") String itemId) {
      return new DispoAnnotationEndpoint(dispoApi, branch, setId, itemId);
   }
}
