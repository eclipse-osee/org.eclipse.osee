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
import io.swagger.v3.oas.annotations.tags.Tags;
import java.util.Collection;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoRoles;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Angel Avila
 */
@Swagger
public class DispoSetEndpoint {

   private final DispoApi dispoApi;
   private final BranchId branch;

   public DispoSetEndpoint(DispoApi dispoApi, BranchId branch) {
      this.dispoApi = dispoApi;
      this.branch = branch;
   }

   /**
    * Create a new Disposition Set given a DispoSetDescriptor
    *
    * @param descriptor Descriptor Data which includes name and import path
    * @return The created Disposition Set if successful. Error Code otherwise
    * @response.representation.201.doc Created the Disposition Set
    * @response.representation.409.doc Conflict, tried to create a Disposition Set with same name
    * @response.representation.400.doc Bad Request, did not provide both a Name and a valid Import Path
    */
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Create a new Disposition Set given a DispoSetDescriptor")
   @Tags(value = {@Tag(name = "create"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "OK. Created the Disposition Set"),
      @ApiResponse(responseCode = "409", description = "Conflict. Tried to create a Disposition Set with same name"),
      @ApiResponse(responseCode = "400", description = "Bad Request. Did not provide both a Name and a valid Import Path")})
   public Response postDispoSet(DispoSetDescriptorData descriptor,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
      Response.Status status;
      Response response;
      String name = descriptor.getName();
      String importPath = descriptor.getImportPath();
      String dispoType = descriptor.getDispoType();

      if (!name.isEmpty() && !importPath.isEmpty() && !dispoType.isEmpty()) {
         boolean isUniqueSetName = dispoApi.isUniqueSetName(branch, name);
         if (isUniqueSetName) {
            ArtifactId createdSetId = dispoApi.createDispoSet(branch, descriptor, userName);
            DispoSet createdSet = dispoApi.getDispoSetById(branch, ArtifactId.valueOf(createdSetId).getIdString());
            status = Status.CREATED;
            response = Response.status(status).entity(createdSet).build();
         } else {
            status = Status.CONFLICT;
            response = Response.status(status).entity(DispoMessages.Set_ConflictingNames).build();
         }
      } else {
         status = Status.BAD_REQUEST;
         response = Response.status(status).entity(DispoMessages.Set_EmptyNameOrPath).build();
      }
      return response;
   }

   /**
    * Create a new Disposition Set given a name, dispoType, and path
    *
    * @param importPath String used to specify the directory to populate the set
    * @param name String used to name the set
    * @param dispoType String used to specify if using disposition vs coverage
    * @return Response type for success of call
    * @response.representation.201.doc Created the Disposition Set
    * @response.representation.409.doc Conflict, tried to create a Disposition Set with same name
    * @response.representation.400.doc Bad Request, did not provide both a Name and a valid Import Path
    */
   @Path("{name}")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Create a new Disposition Set given a name, dispoType, and path")
   @Tags(value = {@Tag(name = "create"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "OK. Created the Disposition Set"),
      @ApiResponse(responseCode = "409", description = "Conflict. Tried to create a Disposition Set with same name"),
      @ApiResponse(responseCode = "400", description = "Bad Request. Did not provide both a Name and a valid Import Path")})
   public Response postDispoSetByName(
      @Parameter(description = "String used to specify the directory to populate the set", required = true) @FormParam("path") String importPath,
      @Parameter(description = "String used to name the Set", required = true) @PathParam("name") String name,
      @Parameter(description = "String used to specify if using disposition vs coverage", required = true) @QueryParam("dispoType") String dispoType,
      @QueryParam("userName") String userName) {
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName(name);
      descriptor.setImportPath(importPath);
      descriptor.setDispoType(dispoType);

      return postDispoSet(descriptor, userName);
   }

   /**
    * Get a specific Disposition Set given a setId
    *
    * @param setId The Id of the Disposition Set to search for
    * @return The found Disposition Set if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Disposition Set
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    */
   @Path("{setId}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get a specific Disposition Set given a setId")
   @Tag(name = "set")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Disposition Set"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Disposition Sets")})
   public DispoSet getDispoSetById(
      @Parameter(description = "The Id of the Disposition Set to search for", required = true) @PathParam("setId") String setId) {
      return dispoApi.getDispoSetById(branch, setId);
   }

   /**
    * Get all Disposition Sets on the given branch
    *
    * @return The Disposition Sets found on the branch
    * @response.representation.200.doc OK, Found Disposition Sets
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get all Disposition Sets on the given branch")
   @Tag(name = "sets")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Disposition Set"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Disposition Sets")})
   public Iterable<DispoSet> getAllDispoSets(
      @Parameter(description = "Dispo Set type", required = true) @QueryParam("type") String type) {
      List<DispoSet> allDispoSets = dispoApi.getDispoSets(branch, type);
      return allDispoSets;
   }

   /**
    * @return The names of all items in a set that have "Rerun?" column checked. Error Code otherwise
    * @response.representation.200.doc OK, Found Disposition Set
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    */
   @Path("toRerun")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get the names of all items in a set that have \"Rerun?\" column checked")
   @Tag(name = "rerun")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Disposition Set"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Disposition Sets")})
   public List<String> getCheckedReruns(
      @Parameter(description = "The Set name", required = true) @QueryParam("name") String setName) {
      String setId = dispoApi.getDispoSetIdByName(branch, setName);
      if (setId == null) {
         return null;
      }
      List<String> reruns = dispoApi.getCheckedReruns(branch, setId);
      return reruns;
   }

   /**
    * @return The found setId if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found branchId
    * @response.representation.404.doc Not Found, Could not find any branchId
    */
   @Path("getDispoSetId")
   @GET
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get Set ID given the Set name")
   @Tag(name = "set")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Branch/Set ID"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Branch/Set ID")})
   public String getDispoSetId(
      @Parameter(description = "The Set name", required = true) @FormParam("name") String setName) {
      return dispoApi.getDispoSetIdByName(branch, setName);
   }

   /**
    * @return The updated Disposition Set if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Disposition Set
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    * @response.representation.415.doc Unsupported Media Type.
    */
   @Path("importDispoSet")
   @PUT
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Operation(summary = "Update Disposition Set given the Set name")
   @Tags(value = {@Tag(name = "update"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Updated the Disposition Set"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Disposition Sets to update"),
      @ApiResponse(responseCode = "415", description = "Unsupported Media Type")})
   public Response putDispoSetByName(
      @Parameter(description = "The Set name", required = true) @FormParam("name") String setName) {
      DispoSetData newDispositionSet = new DispoSetData();
      newDispositionSet.setOperation(DispoStrings.Operation_Import);
      String setId = dispoApi.getDispoSetIdByName(branch, setName);
      Response.Status status;
      if (setId != null) {
         dispoApi.editDispoSet(branch, setId, newDispositionSet, "OSEE System Auto-Import (Set)");
      }
      status = Status.OK;
      return Response.status(status).build();
   }

   /**
    * Edit a specific Disposition Set given a setId and new Disposition Set Data
    *
    * @param setId The Id of the Disposition Set to search for
    * @param newDispositionSet The data for the new Disposition Set
    * @return The updated Disposition Set if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Disposition Set
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    */
   @Path("{setId}")
   @PUT
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   @Operation(summary = "Edit a specific Disposition Set given a setId and new Disposition Set Data")
   @Tags(value = {@Tag(name = "update"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Edited the Disposition Set"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find and edit any Disposition Sets")})
   public Response putDispoSet(
      @Parameter(description = "The Id of the Disposition Set to search for", required = true) @PathParam("setId") String setId,
      DispoSetData newDispositionSet,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
      Response.Status status;
      dispoApi.editDispoSet(branch, setId, newDispositionSet, userName);
      status = Status.OK;
      return Response.status(status).build();
   }

   /**
    * Edit a specific Disposition Set given a setId and new Disposition Set Data
    *
    * @param setId The Id of the Disposition Set to search for
    * @param newDispositionSet The data for the new Disposition Set
    * @return The updated Disposition Set if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Disposition Set
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    */
   @Path("{setId}/massDisposition")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   @Operation(summary = "Edit a specific Disposition Set given a setId and new Disposition Set Data")
   @Tags(value = {@Tag(name = "edit"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Edited the Disposition Set"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find and edit any Disposition Sets")})
   public Response runMassDisposition(
      @Parameter(description = "The Id of the Disposition Set to search for", required = true) @PathParam("setId") String setId,
      @Parameter(description = "The Resolution type", required = true) @QueryParam("resolutionType") String resolutionType,
      @Parameter(description = "The Resolution", required = true) @QueryParam("resolution") String resolution,
      List<String> itemIds, @QueryParam("userName") String userName) {
      Response.Status status;
      dispoApi.editMassDispositions(branch, setId, itemIds, resolutionType, resolution, userName);
      status = Status.OK;
      return Response.status(status).build();
   }

   /**
    * Delete a specific Disposition Set given a setId
    *
    * @param setId The Id of the Disposition Set to search for
    * @return Response Code
    * @response.representation.200.doc OK, Found Disposition Set
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    */
   @Path("{setId}")
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @DELETE
   @Operation(summary = "Delete a specific Disposition Set given a setId")
   @Tags(value = {@Tag(name = "delete"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Deleted the Disposition Set"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find and delete any Disposition Sets")})
   public Response deleteDispoSet(
      @Parameter(description = "The Id of the Disposition Set to search for", required = true) @PathParam("setId") String setId,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
      Response.Status status = Status.NOT_FOUND;
      boolean wasDeleted = dispoApi.deleteDispoSet(branch, setId, userName);
      if (wasDeleted) {
         status = Status.OK;
      } else {
         status = Status.NOT_FOUND;
      }
      return Response.status(status).build();
   }

   @Path("{setId}/file")
   @Operation(summary = "Get Dispo source files given a Set ID")
   @Tag(name = "source")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Retrieved DispoSourceFileResource"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Disposition Sets")})
   public DispoSourceFileResource getDispoSourceFiles(
      @Parameter(description = "The Id of the Disposition Set to search for", required = true) @PathParam("setId") String setId) {
      return new DispoSourceFileResource(dispoApi, branch, setId);
   }

   @Path("{setId}/item")
   @Operation(summary = "Get Dispo Items given a Set ID")
   @Tag(name = "items")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Retrieved Dispo Items"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Disposition Sets")})
   public DispoItemEndpoint getDispositionableItems(
      @Parameter(description = "The Id of the Disposition Set to search for", required = true) @PathParam("setId") String setId) {
      return new DispoItemEndpoint(dispoApi, branch, setId);
   }

   /**
    * Get a specific Dispositionable Item given a key word within the Dispositions
    *
    * @param itemId The Id of the Dispositionable Item to search for
    * @return The found Dispositionable Item if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Dispositionable Item
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @Path("{setId}/search")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get a specific Dispositionable Items given a key word within the Dispositions")
   @Tag(name = "items")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Retrieved Dispo Items"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Dispositionable Items")})
   public Iterable<DispoItem> getDispoItemsByAnnotationText(
      @Parameter(description = "The Id of the Disposition Set to search for", required = true) @PathParam("setId") String setId,
      @Parameter(description = "The Annotation text to search for", required = true) @QueryParam("value") String value,
      @Parameter(description = "Is detailed", required = true) @QueryParam("isDetailed") boolean isDetailed) {
      Collection<DispoItem> foundItems = dispoApi.getDispoItemByAnnotationText(branch, setId, value, isDetailed);
      return foundItems;
   }
}
