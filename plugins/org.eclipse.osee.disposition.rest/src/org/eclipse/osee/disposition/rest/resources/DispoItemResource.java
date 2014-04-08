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

import java.util.List;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoHtmlWriter;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Angel Avila
 */
public class DispoItemResource {
   private final DispoApi dispoApi;
   private final DispoHtmlWriter writer;
   private final DispoProgram program;
   private final String setId;

   public DispoItemResource(DispoApi dispoApi, DispoHtmlWriter writer, DispoProgram program, String setId) {
      this.dispoApi = dispoApi;
      this.program = program;
      this.setId = setId;
      this.writer = writer;
   }

   @POST
   public Response postDispoItem(DispoItemData dispoItem) {
      Identifiable<String> createDispoItem = dispoApi.createDispoItem(program, setId, dispoItem);
      return Response.status(Status.OK).entity(createDispoItem.getGuid()).build();
   }

   /**
    * Get all Dispositionable Items under the Disposition Set
    * 
    * @return The Dispositionable Items found under the Disposition Set
    * @throws Exception
    * @response.representation.200.doc OK, Found Dispositionable Items
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getAllDispoItems() throws Exception {
      List<DispoItem> dispoItems = dispoApi.getDispoItems(program, setId);
      StreamingOutput streamingOutput = new DispoHtmlOutputStream(dispoItems);
      ResponseBuilder builder = Response.ok(streamingOutput);
      return builder.build();
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
   public Response getDispoItemsByIdJson(@PathParam("itemId") String itemId) {
      Response response;
      DispoItem result = dispoApi.getDispoItemById(program, itemId);
      if (result == null) {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Item_NotFound).build();
      } else {
         response = Response.status(Response.Status.OK).entity(result).build();
      }
      return response;
   }

   /**
    * Edit a specific Dispositionable Item given a itemId and new Dispositionable Item Data
    * 
    * @param itemId The Id of the Dispositionable Item to search for
    * @param newDispoItem The data for the new Dispositionable Item
    * @return The updated Dispositionable Item if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Dispositionable Item
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @Path("{itemId}")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   public Response putDispoItem(@PathParam("itemId") String itemId, DispoItemData newDispoItem) {
      Response response;
      boolean wasEdited = dispoApi.editDispoItem(program, itemId, newDispoItem);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Item_NotFound).build();
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
   public Response deleteDispoItem(@PathParam("itemId") String itemId) {
      Response response;
      boolean wasEdited = dispoApi.deleteDispoItem(program, itemId);
      if (wasEdited) {
         response = Response.status(Response.Status.OK).build();
      } else {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Item_NotFound).build();
      }
      return response;
   }

   @Path("{itemId}/annotation/")
   public AnnotationResource getAnnotation(@PathParam("itemId") String itemId) {
      return new AnnotationResource(dispoApi, writer, program, setId, itemId);
   }
}
