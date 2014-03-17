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
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.HtmlWriter;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Angel Avila
 */
public class DispoItemResource {
   private final DispoApi dispoApi;
   private final HtmlWriter writer;
   private final DispoProgram program;
   private final String setId;

   public DispoItemResource(DispoApi dispoApi, HtmlWriter writer, DispoProgram program, String setId) {
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
    * @response.representation.200.doc OK, Found Dispositionable Items
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getAllDispoItems() {
      ResultSet<DispoItemData> dispoItems = dispoApi.getDispoItems(program, setId);
      Response.Status status;
      String html;
      if (dispoItems.isEmpty()) {
         status = Status.NOT_FOUND;
         html = "There are currently no disposition items available under this set";
      } else {
         status = Status.OK;
         html = writer.createDispositionPage("Dispositionable Items", "item/", dispoItems);
      }
      return Response.status(status).entity(html).build();
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
    * Get a specific Dispositionable Item given a itemId
    * 
    * @param itemId The Id of the Dispositionable Item to search for
    * @return The found Dispositionable Item if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Dispositionable Item
    * @response.representation.404.doc Not Found, Could not find any Dispositionable Items
    */
   @Path("{itemId}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getDispoItemsByIdHtml(@PathParam("itemId") String itemId) {
      String html;
      Response.Status status;
      DispoItem dispoItem = dispoApi.getDispoItemById(program, itemId);
      if (dispoItem == null) {
         status = Status.NOT_FOUND;
         html = DispoMessages.Item_NotFound;
      } else {
         status = Status.OK;
         String title = "Annotations";
         String prefixPath = itemId + "/annotation";
         html = writer.createDispoPage(dispoItem.getName(), prefixPath, title, "[]");
      }
      return Response.status(status).entity(html).build();
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
