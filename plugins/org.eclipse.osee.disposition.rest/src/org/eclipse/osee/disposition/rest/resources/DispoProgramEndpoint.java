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

import io.swagger.v3.oas.annotations.Parameter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoRoles;
import org.eclipse.osee.disposition.rest.internal.report.ExportSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

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
    */
   @Path("{name}")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.TEXT_PLAIN)
   public Response createDispoProgramByName(@PathParam("name") String name, @QueryParam("userName") String userName) {
      DispoProgamDescriptorData programDescriptor = new DispoProgamDescriptorData();
      programDescriptor.setName(name);
      return createProgram(programDescriptor, userName);
   }

   /**
    * Get all Disposition Programs as JSON
    *
    * @return The Disposition Programs found
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
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
    * Get all Disposition Programs as a return delimited string
    *
    * @return The Disposition Programs found
    */
   @Path("list")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String getAllProgramNames() {
      List<String> allProgramNames = dispoApi.getDispoProgramNames();
      Collections.sort(allProgramNames);
      return String.join("\n", allProgramNames);
   }

   /**
    * @return The found branchId if successful. Error Code otherwise
    */
   @Path("getDispoBranchId")
   @GET
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   public String getDispoBranchId(
      @Parameter(description = "The Branch name", required = true) @FormParam("name") String branchName) {
      return dispoApi.getDispoProgramIdByName(branchName).getIdString();
   }

   /**
    * Import All Disposition Sets that are in a given State. Default state is "NONE".
    *
    * @param filterState Data used to specify what the user wants to import by its state
    * @return Error code if failing.
    */
   @Path("importAll")
   @PUT
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
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
    */
   @Path("importDispoBranch")
   @PUT
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
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
      }
      if (!sourceSet.isEmpty()) {
         String sourceSetId = dispoApi.getDispoSetIdByName(programId, sourceSet);
         if (sourceSetId != null) {
            CopySetParams params = new CopySetParams(CopySetParamOption.OVERRIDE, CopySetParamOption.OVERRIDE,
               CopySetParamOption.OVERRIDE, CopySetParamOption.OVERRIDE, false);
            dispoApi.copyDispoSet(programId, setId, programId, sourceSetId, params);
         } else {
            return Response.status(Status.EXPECTATION_FAILED).entity(String.format(
               "Vectorcast Update Successful. Failed to import manual dispositions from [%s].", sourceSet)).build();
         }
      }
      return Response.status(Status.OK).build();
   }

   @Path("{programName}/scripts")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public String getTestScripts(@PathParam("programName") String programName) {
      programName = String.format("(DISPO)%s", programName);
      BranchToken programId = dispoApi.getDispoProgramIdByName(programName);
      return String.join("\n", dispoApi.getTestScripts(programId));
   }

   @Path("{programName}/set/{setName}/scripts")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public String getTestScripts(@PathParam("programName") String programName, @PathParam("setName") String setName) {
      programName = String.format("(DISPO)%s", programName);
      BranchToken programId = dispoApi.getDispoProgramIdByName(programName);
      String setId = dispoApi.getDispoSetIdByName(programId, setName);
      if (setId != null) {
         return String.join("\n", dispoApi.getTestScripts(programId, setId));
      }
      return "";
   }

   @Path("{programName}/exportAll")
   @GET
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response exportAllDispoSets(@PathParam("programName") String programName) {

      final ExportSet writer = new ExportSet(dispoApi);
      final String option = "detailed";

      BranchToken programId = dispoApi.getDispoProgramIdByName(String.format("(DISPO)%s", programName));
      List<DispoSet> dispoSets = dispoApi.getDispoSets(programId, DispoStrings.CODE_COVERAGE);

      Date date = new Date();
      String newstring = new SimpleDateFormat("yyyy-MM-dd").format(date);
      final String zipName = String.format("%s_%s.zip", programName, newstring);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try (FileOutputStream fos = new FileOutputStream(zipName)) {
         baos.writeTo(fos);
         ZipOutputStream zos = new ZipOutputStream(baos);

         for (DispoSet dispoSet : dispoSets) {
            if (!dispoSet.getImportState().equalsIgnoreCase("NONE")) {
               final String fileName = String.format("%s_%s.xml", dispoSet.getName(), newstring);

               zos.putNextEntry(new ZipEntry(fileName));
               zos.write(writer.runCoverageReports(programId, dispoSet, option, fileName).toByteArray());
               zos.closeEntry();
            }
         }
         baos.close();
         zos.close();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

      String contentDisposition =
         String.format("attachment; filename=\"%s\"; creation-date=\"%s\"", zipName, new Date());
      return Response.ok(baos.toByteArray()).header("Content-Disposition", contentDisposition).type(
         "application/zip").build();
   }

   /**
    * Get all Disposition Sets on the given branch
    *
    * @return The Disposition Sets found on the branch
    */
   @GET
   @Path("{programName}/sets")
   @Produces(MediaType.APPLICATION_JSON)
   public String getAllDispoSets(@PathParam("programName") String programName) {
      BranchToken programId = dispoApi.getDispoProgramIdByName(String.format("(DISPO)%s", programName));
      List<String> allDispoSets = dispoApi.getDispoSetNames(programId, DispoStrings.CODE_COVERAGE);
      return String.join("\n", allDispoSets);
   }

   @Path("{branchId}/set")
   public DispoSetEndpoint getAnnotation(
      @Parameter(description = "The Branch ID", required = true) @PathParam("branchId") BranchId branch) {
      return new DispoSetEndpoint(dispoApi, branch);
   }

   @Path("{branchId}/admin")
   public DispoAdminEndpoint getDispoSetReport(
      @Parameter(description = "The Branch ID", required = true) @PathParam("branchId") BranchId branch) {
      return new DispoAdminEndpoint(dispoApi, branch);
   }

   @Path("{branchId}/config")
   public DispoConfigEndpoint getDispoDataStore(
      @Parameter(description = "The Branch ID", required = true) @PathParam("branchId") BranchId branch) {
      return new DispoConfigEndpoint(dispoApi, branch);
   }
}
