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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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
import org.eclipse.osee.disposition.model.CopySetParamOption;
import org.eclipse.osee.disposition.model.CopySetParams;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgamDescriptorData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoRoles;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Angel Avila
 */
@Path("program")
@Swagger
public class DispoProgramEndpoint {

   private final DispoApi dispoApi;

   public DispoProgramEndpoint(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   /**
    * Create a new Disposition Set given a DispoSetDescriptor
    *
    * @param descriptor Descriptor Data which includes name and import path
    * @return Response type for success of call
    * @response.representation.201.doc Created the Disposition Set
    * @response.representation.409.doc Conflict, tried to create a Disposition Set with same name
    * @response.representation.400.doc Bad Request, did not provide both a Name and a valid Import Path
    */
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public Response createProgram(DispoProgamDescriptorData programDescriptor,
      @Parameter(description = "The Username") @QueryParam("userName") String userName) {
      String name = "(DISPO)" + programDescriptor.getName();
      Response.Status status;
      Response response;
      if (!name.isEmpty()) {
         boolean isUniqueName = dispoApi.isUniqueProgramName(name);
         if (isUniqueName) {
            long createdProgramId = dispoApi.createDispoProgram(name);
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
    * Create a new Disposition Set given a name
    *
    * @param name String used to name the branch
    * @return Response type for success of call
    * @response.representation.201.doc Created the Disposition Set
    * @response.representation.409.doc Conflict, tried to create a Disposition Set with same name
    * @response.representation.400.doc Bad Request, did not provide both a Name and a valid Import Path
    */
   @Path("{name}")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.TEXT_PLAIN)
   @Operation(summary = "Create a new Disposition Set given a name")
   @Tags(value = {@Tag(name = "create"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "OK. Created the Disposition Set"),
      @ApiResponse(responseCode = "409", description = "Conflict. Tried to create a Disposition Set with same name"),
      @ApiResponse(responseCode = "400", description = "Bad Request. Did not provide both a Name and a valid Import Path")})
   public Response createDispoProgramByName(@PathParam("name") String name, @QueryParam("userName") String userName) {
      DispoProgamDescriptorData programDescriptor = new DispoProgamDescriptorData();
      programDescriptor.setName(name);
      return createProgram(programDescriptor, userName);
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
   @Operation(summary = "Get all Disposition Programs as JSON")
   @Tag(name = "program")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Disposition Programs"),
      @ApiResponse(responseCode = "400", description = "Not Found. Could not find any Disposition Programs")})
   public Response getAllPrograms() {
      List<BranchToken> allPrograms = dispoApi.getDispoPrograms();
      Collections.sort(allPrograms, new Comparator<BranchToken>() {
         @Override
         public int compare(BranchToken o1, BranchToken o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      List<Map<String, String>> branchList = new LinkedList<>();

      for (BranchToken branch : allPrograms) {
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
    * @return The found branchId if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found branchId
    * @response.representation.404.doc Not Found, Could not find any branchId
    */
   @Path("getDispoBranchId")
   @GET
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get a Branch ID given a Branch name")
   @Tag(name = "branch")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Found Branch ID"),
      @ApiResponse(responseCode = "400", description = "Not Found. Could not find any Branch ID")})
   public String getDispoBranchId(
      @Parameter(description = "The Branch name", required = true) @FormParam("name") String branchName) {
      return dispoApi.getDispoProgramIdByName(branchName).getIdString();
   }

   /**
    * Import All Disposition Sets that are in a given State. Default state is "NONE".
    *
    * @param filterState Data used to specify what the user wants to import by its state
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
   @Operation(summary = "Import All Disposition Sets that are in a given State. Default state is \"NONE\"")
   @Tags(value = {@Tag(name = "import"), @Tag(name = "sets")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Import successful"),
      @ApiResponse(responseCode = "404", description = "Not Found. Can't connect to server"),
      @ApiResponse(responseCode = "405", description = "Method Not Allowed. Invalid permission"),
      @ApiResponse(responseCode = "415", description = "Unsupported Media Type")})
   public Response importAllDispoSets(String filterState) {
      Response.Status status;
      dispoApi.importAllDispoPrograms(filterState);
      status = Status.OK;
      return Response.status(status).build();
   }

   /**
    * Import All Disposition Sets that are in a given Branch and State. Default state is "NONE".
    *
    * @param filterState Data used to specify what the user wants to import by its state
    * @return Error code if failing.
    * @response.representation.200.doc OK, looking through Disposition Sets.
    * @response.representation.404.doc Not Found, can't connect to server.
    * @response.representation.415.doc Unsupported Media Type.
    */
   @Path("importDispoBranch")
   @PUT
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Operation(summary = "Import All Disposition Sets that are in a given Branch and State. Default state is \"NONE\"")
   @Tags(value = {@Tag(name = "import"), @Tag(name = "branch")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Import successful"),
      @ApiResponse(responseCode = "404", description = "Not Found. Can't connect to server"),
      @ApiResponse(responseCode = "415", description = "Unsupported Media Type")})
   public Response importDispoBranchByName(
      @Parameter(description = "The Filter state", required = true) @FormParam("filterState") String filterState,
      @Parameter(description = "The Branch name", required = true) @FormParam("name") String branchName) {
      BranchToken branch = dispoApi.getDispoProgramIdByName(branchName);
      Response.Status status;
      dispoApi.importAllDispoSets(branch, filterState);
      status = Status.OK;
      return Response.status(status).build();
   }

   @Path("{programName}/set/{setName}/import")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   public Response putDispoSetByName(@PathParam("programName") String programName, @PathParam("setName") String setName,
      String importPath, @QueryParam("sourceSet") @DefaultValue("") String sourceSet) {
      programName = String.format("(DISPO)%s", programName);
      importPath = importPath.replaceAll("\"", "");
      BranchToken programId = dispoApi.getDispoProgramIdByName(programName);
      String setId = dispoApi.getDispoSetIdByName(programId, setName);
      if (setId != null) {
         dispoApi.importDispoSet(programId, setId, importPath);
      } else {
         ArtifactId createdSetId = dispoApi.createSet(programId, importPath, setName);
         setId = dispoApi.getDispoSetById(programId, ArtifactId.valueOf(createdSetId).getIdString()).getIdString();
         dispoApi.importDispoSet(programId, setId, importPath);
         if (!sourceSet.isEmpty()) {
            String sourceSetId = dispoApi.getDispoSetIdByName(programId, sourceSet);
            if (sourceSetId != null) {
               CopySetParams params = new CopySetParams(CopySetParamOption.OVERRIDE, CopySetParamOption.OVERRIDE,
                  CopySetParamOption.OVERRIDE, CopySetParamOption.OVERRIDE, false);
               dispoApi.copyDispoSet(programId, setId, programId, sourceSetId, params);
            }
         }
      }
      return Response.status(Status.OK).build();
   }

   @Path("{branchId}/set")
   @Operation(summary = "Get Annotation")
   @Tag(name = "annotation")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Annotation found"),
      @ApiResponse(responseCode = "404", description = "Not Found. Can't find annotation")})
   public DispoSetEndpoint getAnnotation(
      @Parameter(description = "The Branch ID", required = true) @PathParam("branchId") BranchId branch) {
      return new DispoSetEndpoint(dispoApi, branch);
   }

   @Path("{branchId}/admin")
   @Operation(summary = "Get Dispo Set report")
   @Tag(name = "annotation")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Retrieved Dispo Set report"),
      @ApiResponse(responseCode = "404", description = "Not Found. Can't find Dispo Set report")})
   public DispoAdminEndpoint getDispoSetReport(
      @Parameter(description = "The Branch ID", required = true) @PathParam("branchId") BranchId branch) {
      return new DispoAdminEndpoint(dispoApi, branch);
   }

   @Path("{branchId}/config")
   @Operation(summary = "Get Dispo Datastore")
   @Tag(name = "annotation")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK. Retrieved the Dispo Datastore"),
      @ApiResponse(responseCode = "404", description = "Not Found. Can't find the Dispo Datastore")})
   public DispoConfigEndpoint getDispoDataStore(
      @Parameter(description = "The Branch ID", required = true) @PathParam("branchId") BranchId branch) {
      return new DispoConfigEndpoint(dispoApi, branch);
   }
}
