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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Angel Avila
 */
@Swagger
public class DispoAnnotationEndpoint {
   private final DispoApi dispoApi;
   private final BranchId branch;
   private final String itemId;

   public DispoAnnotationEndpoint(DispoApi dispoApi, BranchId branch, String setUuid, String dispResourceId) {
      this.dispoApi = dispoApi;
      this.branch = branch;
      this.itemId = dispResourceId;
   }

   /**
    * Create a new Annotation given an AnnotationData object
    *
    * @param annotation AnnotationData which must include location reference
    * @return The created Annotation if successful. Error Code otherwise
    * @response.representation.201.doc Created the Annotation
    * @response.representation.400.doc Bad Request, did not provide a location range
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Operation(summary = "Create a new Annotation given an AnnotationData object")
   @Tag(name = "annotation")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully created the Annotation"),
      @ApiResponse(responseCode = "400", description = "Bad Request. Did not provide a location range")})
   public Response postDispoAnnotation(DispoAnnotationData annotation,
      @Parameter(description = "The Username") @QueryParam("userName") String userName) {
      Response.Status status;
      Response response;
      DispoAnnotationData createdAnnotation;
      if (!annotation.getLocationRefs().isEmpty()) {
         String createdAnnotationId = dispoApi.createDispoAnnotation(branch, itemId, annotation, userName, false);
         if (!createdAnnotationId.equals("")) {
            status = Status.CREATED;
            createdAnnotation = dispoApi.getDispoAnnotationById(branch, itemId, createdAnnotationId);
         } else {
            status = Status.NOT_ACCEPTABLE;
            createdAnnotation = null;
         }
         response = Response.status(status).entity(createdAnnotation).build();
      } else {
         status = Status.BAD_REQUEST;
         response = Response.status(status).entity(DispoMessages.Annotation_EmptyLocRef).build();
      }
      return response;
   }

   /**
    * Get all Annotations for the DisposionableItem
    *
    * @return The Annotation found for the DisposionableItem
    * @response.representation.200.doc OK, Found Annotations
    * @response.representation.404.doc Not Found, Could not find any Annotations
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get all Annotations for the DisposioableItem")
   @Tag(name = "annotations")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Annotations"),
      @ApiResponse(responseCode = "404", description = "Not found. Could not find any Annotations")})
   public Iterable<DispoAnnotationData> getAllDispoAnnotations() {
      return dispoApi.getDispoAnnotations(branch, itemId);
   }

   /**
    * Get all Annotations for a specific resolution Type the DisposionableItem
    *
    * @return The Annotation found for the DisposionableItem
    * @response.representation.200.doc OK, Found Annotations
    * @response.representation.404.doc Not Found, Could not find any Annotations
    */
   @Path("resolutionType/{resolutionType}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get all Annotations for a specific resolution Type for the DisposionableItem")
   @Tag(name = "annotations")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Annotations"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find any Annotations")})
   public Iterable<DispoAnnotationData> getAllDispoAnnotationsByType(
      @Parameter(description = "The resolution type", required = true) @PathParam("resolutionType") String resolutionType) {
      Iterable<DispoAnnotationData> annotationData = dispoApi.getDispoAnnotations(branch, itemId);
      return dispoApi.getDispoAnnotationsByType(annotationData, resolutionType);
   }

   /**
    * Get a specific Annotation given an Id
    *
    * @param id The Id of the Annotation to search for
    * @return The found Annotation if successful. Error Code otherwise
    * @response.representation.200.doc OK, found Annotation
    * @response.representation.404.doc Not Found, Could not find the Annotation
    */
   @Path("{annotationId}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get a specific Annotation given an Id")
   @Tag(name = "annotation")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Annotation"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find the Annotation")})
   public DispoAnnotationData getAnnotationByIdJson(
      @Parameter(description = "The Id of the Annotation to search for", required = true) @PathParam("annotationId") String annotationId) {
      return dispoApi.getDispoAnnotationById(branch, itemId, annotationId);
   }

   /**
    * Edit a specific Annotation given an Id and new Annotation Data
    *
    * @param id The Id of the Annotation to update
    * @param newAnnotation The data for the new Annotation
    * @return The updated Annotation if successful. Error Code otherwise
    * @response.representation.200.doc OK
    * @response.representation.404.doc Not Found, Could not find the Annotation
    */
   @Path("{annotationId}")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Operation(summary = "Edit a specific Annotation given an Id and new Annotation Data")
   @Tags(value = {@Tag(name = "annotation"), @Tag(name = "edit")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Annotation updated"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find the Annotation")})
   public Response putDispoAnnotation(
      @Parameter(description = "The Id of the Annotation to update", required = true) @PathParam("annotationId") String annotationId,
      DispoAnnotationData newAnnotation,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
      Response response;
      boolean wasEdited = dispoApi.editDispoAnnotation(branch, itemId, annotationId, newAnnotation, userName, false);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();

      } else {
         response = Response.status(Response.Status.NOT_MODIFIED).build();
      }
      return response;
   }

   /**
    * Delete a specific Annotation given an Id
    *
    * @param id The Id of the Annotation to delete
    * @return Response Code
    * @response.representation.200.doc OK
    * @response.representation.404.doc Not Found, Could not find the Annotation
    */
   @Path("{annotationId}")
   @DELETE
   @Operation(summary = "Delete a specific Annotation given an Id")
   @Tag(name = "annotation")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "404", description = "Not Found. Could not find the Annotation")})
   public Response deleteDispoAnnotation(
      @Parameter(description = "The Id of the Annotation to delete", required = true) @PathParam("annotationId") String annotationId,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
      Response response;
      boolean wasEdited = dispoApi.deleteDispoAnnotation(branch, itemId, annotationId, userName, false);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_MODIFIED).build();
      }
      return response;
   }
}
