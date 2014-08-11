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
package org.eclipse.osee.disposition.rest.resources;

import java.io.IOException;
import java.util.List;
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
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Angel Avila
 */
public class AnnotationResource {
   private final DispoApi dispoApi;
   private final DispoProgram program;
   private final String itemId;

   public AnnotationResource(DispoApi dispoApi, DispoProgram program, String setUuid, String dispResourceId) {
      this.dispoApi = dispoApi;
      this.program = program;
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
   public Response postDispoAnnotation(DispoAnnotationData annotation, @QueryParam("userName") String userName) {
      Response.Status status;
      Response response;
      DispoAnnotationData createdAnnotation;
      if (!annotation.getLocationRefs().isEmpty()) {
         String createdAnnotationId = dispoApi.createDispoAnnotation(program, itemId, annotation, userName);
         if (createdAnnotationId != "") {
            status = Status.CREATED;
            createdAnnotation = dispoApi.getDispoAnnotationById(program, itemId, createdAnnotationId);
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
    * @throws JSONException
    * @throws IOException
    * @response.representation.200.doc OK, Found Annotations
    * @response.representation.404.doc Not Found, Could not find any Annotations
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getAllDispoAnnotations() throws JSONException {
      Response.Status status;
      List<DispoAnnotationData> dispositionAnnotations = dispoApi.getDispoAnnotations(program, itemId);

      status = Status.OK;
      JSONArray array = new JSONArray();
      for (DispoAnnotationData annotation : dispositionAnnotations) {
         array.put(annotation.getIndex(), DispoUtil.annotationToJsonObj(annotation));
      }
      return Response.status(status).entity(array.toString()).build();
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
   public Response getAnnotationByIdJson(@PathParam("annotationId") String annotationId) {
      Response response;
      DispoAnnotationData result = dispoApi.getDispoAnnotationById(program, itemId, annotationId);
      if (result == null) {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Annotation_NotFound).build();
      } else {
         response = Response.status(Response.Status.OK).entity(result).build();
      }
      return response;
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
   public Response putDispoAnnotation(@PathParam("annotationId") String annotationId, DispoAnnotationData newAnnotation, @QueryParam("userName") String userName) {
      Response response;
      boolean wasEdited = dispoApi.editDispoAnnotation(program, itemId, annotationId, newAnnotation, userName);
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
   public Response deleteDispoAnnotation(@PathParam("annotationId") String annotationId, @QueryParam("userName") String userName) {
      Response response;
      boolean wasEdited = dispoApi.deleteDispoAnnotation(program, itemId, annotationId, userName);
      if (wasEdited) {
         DispoItem dispoItemById = dispoApi.getDispoItemById(program, itemId);
         response = Response.status(Response.Status.OK).entity(dispoItemById.getStatus()).build();
      } else {
         response = Response.status(Response.Status.NOT_MODIFIED).build();
      }
      return response;
   }
}
