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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
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
import org.eclipse.osee.disposition.model.DispoProgamDescriptorData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoRoles;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.util.JsonUtil;

/**
 * @author Angel Avila
 */
@Path("program")
public class DispoProgramResource {

   private final DispoApi dispoApi;

   public DispoProgramResource(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
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
   @Produces(MediaType.TEXT_PLAIN)
   public Response createProgram(DispoProgamDescriptorData programDescriptor, @QueryParam("userName") String userName) {
      String name = programDescriptor.getName();
      Response.Status status;
      Response response;
      if (!name.isEmpty()) {
         boolean isUniqueName = dispoApi.isUniqueProgramName(name);
         if (isUniqueName) {
            long createdProgramId = dispoApi.createDispoProgram(name, userName);
            status = Status.CREATED;
            response = Response.status(status).entity(createdProgramId).build();
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
    * Get all Disposition Programs as JSON
    *
    * @return The Disposition Programs found
    * @response.representation.200.doc OK, Found Disposition Program
    * @response.representation.404.doc Not Found, Could not find any Disposition Programs
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getAllPrograms() {
      List<IOseeBranch> allPrograms = dispoApi.getDispoPrograms();
      Collections.sort(allPrograms, new Comparator<IOseeBranch>() {
         @Override
         public int compare(IOseeBranch o1, IOseeBranch o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      List<Map<String, String>> branchList = new LinkedList<>();

      for (IOseeBranch branch : allPrograms) {
         Map<String, String> mapObject = new HashMap<>();

         String uuid = branch.getIdString();
         mapObject.put("value", uuid);
         mapObject.put("text", branch.getName());
         branchList.add(mapObject);
      }
      Status status;
      if (allPrograms.isEmpty()) {
         status = Status.NOT_FOUND;
      } else {
         status = Status.OK;
      }

      String branchListJson = JsonUtil.toJson(branchList);
      return Response.status(status).entity(branchListJson).build();
   }

   /**
    * Import All Disposition Sets that are in a given State. Default state is "NONE".
    *
    * @param filterState Data used to specify what the user wants to import by its state
    * @param userName Data for current users
    * @return Error code if failing.
    * @response.representation.200.doc OK, looking through all Disposition Sets.
    * @response.representation.404.doc Not Found, can't connect to server.
    * @response.representation.405.doc Method Not Allowed, invalid permission.
    * @response.representation.415.doc Unsupported Media Type.
    */
   @Path("importAll")
   @PUT
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response putDispoSet(String filterState, @QueryParam("userName") String userName) {
      Response.Status status;
      dispoApi.importAllDispoPrograms(filterState, userName);
      status = Status.OK;
      return Response.status(status).build();
   }

   @Path("{branchId}/set")
   public DispoSetResource getAnnotation(@PathParam("branchId") BranchId branch) {
      return new DispoSetResource(dispoApi, branch);
   }

   @Path("{branchId}/admin")
   public DispoAdminResource getDispoSetReport(@PathParam("branchId") BranchId branch) {
      return new DispoAdminResource(dispoApi, branch);
   }

   @Path("{branchId}/config")
   public DispoConfigResource getDispoDataStore(@PathParam("branchId") BranchId branch) {
      return new DispoConfigResource(dispoApi, branch);
   }
}
