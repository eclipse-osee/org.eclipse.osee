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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.HtmlWriter;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Angel Avila
 */
public class AnnotationResource {
   private final DispoApi dispoApi;
   private final HtmlWriter writer;
   private final DispoProgram program;
   private final String itemId;

   public AnnotationResource(DispoApi dispoApi, HtmlWriter writer, DispoProgram program, String setUuid, String dispResourceId) {
      this.dispoApi = dispoApi;
      this.program = program;
      this.itemId = dispResourceId;
      this.writer = writer;
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
   public Response postDispoAnnotation(DispoAnnotationData annotation) {
      Response.Status status;
      Response response;
      if (!annotation.getLocationRefs().isEmpty()) {
         String createdAnnotationId = dispoApi.createDispoAnnotation(program, itemId, annotation);
         status = Status.CREATED;
         DispoAnnotationData createdAnnotation =
            dispoApi.getDispoAnnotationByIndex(program, itemId, createdAnnotationId);
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
   @Produces(MediaType.TEXT_HTML)
   public Response getAllDispoAnnotations() {
      Response.Status status;
      String html;
      ResultSet<DispoAnnotationData> dispositionAnnotations = dispoApi.getDispoAnnotations(program, itemId);

      if (dispositionAnnotations.isEmpty()) {
         status = Status.NOT_FOUND;
         html = DispoMessages.Annotation_NoneFound;
      } else {
         status = Status.OK;
         html = writer.createDispositionPage("Annotations", "annotation/", dispositionAnnotations);
      }
      return Response.status(status).entity(html).build();
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
      DispoAnnotationData result = dispoApi.getDispoAnnotationByIndex(program, itemId, annotationId);
      if (result == null) {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Annotation_NotFound).build();
      } else {
         response = Response.status(Response.Status.OK).entity(result).build();
      }
      return response;
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
   @Produces(MediaType.TEXT_HTML)
   public Response getAnnotationByIdHtml(@PathParam("annotationId") String annotationId) {
      Response.Status status;
      String html;
      DispoAnnotationData dispositionAnnotation = dispoApi.getDispoAnnotationByIndex(program, itemId, annotationId);
      if (dispositionAnnotation == null) {
         status = Status.NOT_FOUND;
         html = DispoMessages.Annotation_NotFound;
      } else {
         String notes = dispositionAnnotation.getNotesList().toString();
         status = Status.OK;
         html = writer.createDispoPage(dispositionAnnotation.getLocationRefs(), "", "", notes);
      }
      return Response.status(status).entity(html).build();
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
   public Response putDispoAnnotation(@PathParam("annotationId") String annotationId, DispoAnnotationData newAnnotation) {
      Response response;
      boolean wasEdited = dispoApi.editDispoAnnotation(program, itemId, annotationId, newAnnotation);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Annotation_NotFound).build();
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
   public Response deleteDispoAnnotation(@PathParam("annotationId") String annotationId) {
      Response response;
      boolean wasEdited = dispoApi.deleteDispoAnnotation(program, itemId, annotationId);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Annotation_NotFound).build();
      }
      return response;
   }
}
