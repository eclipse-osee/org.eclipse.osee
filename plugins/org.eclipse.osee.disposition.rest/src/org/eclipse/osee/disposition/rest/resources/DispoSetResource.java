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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class DispoSetResource {

   private final DispoApi dispoApi;
   private final DispoProgram program;

   public DispoSetResource(DispoApi dispoApi, DispoProgram program) {
      this.dispoApi = dispoApi;
      this.program = program;
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
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response postDispoSet(DispoSetDescriptorData descriptor) {
      Response.Status status;
      Response response;
      String name = descriptor.getName();
      String importPath = descriptor.getImportPath();
      String dispoType = descriptor.getDispoType();

      if (!name.isEmpty() && !importPath.isEmpty() && !dispoType.isEmpty()) {
         boolean isUniqueSetName = dispoApi.isUniqueSetName(program, name);
         if (isUniqueSetName) {
            String createdSetId = dispoApi.createDispoSet(program, descriptor).getGuid();
            DispoSet createdSet = dispoApi.getDispoSetById(program, createdSetId);
            status = Status.CREATED;
            response = Response.status(status).entity(DispoUtil.setArtToSetData(createdSet)).build();
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
   public Response getDispoSetByIdJson(@PathParam("setId") String setId) {
      Response response;
      DispoSet result = dispoApi.getDispoSetById(program, setId);
      if (result == null) {
         response = Response.status(Response.Status.NOT_FOUND).entity(DispoMessages.Set_NotFound).build();
      } else {
         response = Response.status(Response.Status.OK).entity(DispoUtil.setArtToSetData(result)).build();
      }
      return response;
   }

   /**
    * Get all Disposition Sets on the given program branch
    * 
    * @return The Disposition Sets found on the program branch
    * @throws JSONException
    * @response.representation.200.doc OK, Found Disposition Sets
    * @response.representation.404.doc Not Found, Could not find any Disposition Sets
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getAllDispoSets(@QueryParam("type") String type) throws JSONException {
      List<DispoSet> allDispoSets = dispoApi.getDispoSets(program);
      JSONArray jarray = new JSONArray();

      for (DispoSet set : allDispoSets) {
         JSONObject jobject = new JSONObject();
         if (set.getDispoType().equalsIgnoreCase(type)) {
            jobject.put("guid", set.getGuid());
            jobject.put("name", set.getName());
            jobject.put("importPath", set.getImportPath());
            jobject.put("notesList", set.getNotesList());
            jarray.put(jobject);
         }
      }
      Status status;
      if (allDispoSets.isEmpty()) {
         status = Status.NOT_FOUND;
      } else {
         status = Status.OK;
      }
      return Response.status(status).entity(jarray.toString()).build();
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
   @Consumes(MediaType.APPLICATION_JSON)
   public Response putDispoSet(@PathParam("setId") String setId, DispoSetData newDispositionSet) {
      Response.Status status;
      boolean wasEdited = dispoApi.editDispoSet(program, setId, newDispositionSet);
      if (wasEdited) {
         status = Status.OK;
      } else {
         status = Status.NOT_FOUND;
      }
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
   @DELETE
   public Response deleteDispoSet(@PathParam("setId") String setId) {
      Response.Status status = Status.NOT_FOUND;
      boolean wasDeleted = dispoApi.deleteDispoSet(program, setId);
      if (wasDeleted) {
         status = Status.OK;
      } else {
         status = Status.NOT_FOUND;
      }
      return Response.status(status).build();
   }

   @Path("{setId}/item")
   public DispoItemResource getDispositionableItems(@PathParam("setId") String setId) {
      return new DispoItemResource(dispoApi, program, setId);
   }
}
