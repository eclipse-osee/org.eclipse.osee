/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.JaxNamedId;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.rest.internal.agile.operations.ProgramOperations;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("program")
public class ProgramResource extends AbstractConfigResource {

   @Context
   private UriInfo uriInfo;
   private ProgramOperations programOperations;

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   public ProgramResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.Program, atsApi, orcsApi);
   }

   @GET
   @Path("version")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxNamedId> getProgramsVersions() {
      List<JaxNamedId> programsVers = new LinkedList<>();
      for (IAtsProgram program : atsApi.getProgramService().getPrograms()) {
         for (IAtsVersion ver : atsApi.getProgramService().getVersions(program)) {
            programsVers.add(
               JaxNamedId.construct(ver.getId(), String.format("%s - %s", program.getName(), ver.getName())));
         }
      }
      return programsVers;
   }

   @GET
   @Path("{id}/insertion")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getProgramInsertions(@PathParam("id") long id) {
      org.eclipse.osee.framework.core.data.ArtifactToken programArt = atsApi.getQueryService().getArtifact(id);
      if (programArt == null) {
         throw new OseeCoreException("Given id not found");
      }
      if (!atsApi.getStoreService().isOfType(programArt, AtsArtifactTypes.Program)) {
         throw new OseeCoreException("Given id not program type");
      }
      // get the insertions related to the given program
      Collection<ArtifactToken> results =
         atsApi.getRelationResolver().getRelated(programArt, AtsRelationTypes.ProgramToInsertion_Insertion);
      List<IAtsConfigObject> insertions = new LinkedList<>();
      for (ArtifactToken insertion : results) {
         // want to make sure to reload, so use the id
         insertions.add(atsApi.getProgramService().getInsertion(insertion.getId()));
      }
      // http://aruld.info/handling-generified-collections-in-jersey-jax-rs/
      GenericEntity<List<IAtsConfigObject>> entity = new GenericEntity<List<IAtsConfigObject>>(insertions) { //
      };
      return Response.ok(entity).build();
   }

   @POST
   @Path("{programId}/insertion")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createInsertion(@PathParam("programId") long programId, JaxInsertion newInsertion) {
      ArtifactToken programArt = atsApi.getQueryService().getArtifact(programId);
      if (programArt == null) {
         throw new OseeCoreException("Given id not found");
      }
      if (!atsApi.getStoreService().isOfType(programArt, AtsArtifactTypes.Program)) {
         throw new OseeCoreException("Given id not program type");
      }
      ArtifactToken artifact = atsApi.getQueryService().getArtifact(newInsertion.getId());
      if (artifact != null) {
         throw new OseeStateException("Insertion with id %d already exists", newInsertion.getId());
      }
      IAtsInsertion created = getProgramOperations().createInsertion(programArt, newInsertion);
      return getResponse(created);
   }

   ProgramOperations getProgramOperations() {
      if (programOperations == null) {
         programOperations = new ProgramOperations(atsApi);
      }
      return programOperations;
   }

   @PUT
   @Path("{programId}/insertion")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateInsertion(@PathParam("programId") long programId, JaxInsertion insertion) {
      ArtifactToken insertionArt = atsApi.getQueryService().getArtifact(insertion.getId());
      if (insertionArt == null) {
         throw new OseeCoreException("Given insertion id not found");
      }
      if (!atsApi.getStoreService().isOfType(insertionArt, AtsArtifactTypes.Insertion)) {
         throw new OseeCoreException("Given insertion id not insertion type");
      }
      Response response = null;
      if (!insertionArt.getName().equals(insertion.getName())) {
         IAtsConfigObject updated = getProgramOperations().updateInsertion(insertion);
         response = Response.ok().entity(updated).build();
      } else {
         response = Response.notModified().build();
      }
      return response;
   }

   @GET
   @Path("{programId}/insertion/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInsertionDetails(@PathParam("programId") long programId, @PathParam("id") long id) {
      ArtifactToken configArt = atsApi.getQueryService().getArtifact(id);
      if (configArt == null) {
         throw new OseeCoreException("Given id not found");
      }
      // want to make sure to reload, so use the id
      return Response.ok().entity(atsApi.getProgramService().getInsertion(configArt.getId())).build();
   }

   @DELETE
   @Path("{programId}/insertion/{insertionId}")
   public Response deleteInsertion(@PathParam("insertionId") ArtifactId insertionId) {
      getProgramOperations().deleteInsertion(insertionId);
      return Response.ok().build();
   }

   @GET
   @Path("{programId}/insertion/{insertionId}/activity")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInsertionActivitys(@PathParam("programId") long programId, @PathParam("insertionId") long insertionId) {
      ArtifactToken insertion = atsApi.getQueryService().getArtifact(insertionId);
      if (insertion == null) {
         throw new OseeCoreException("Given insertion id not found");
      }
      if (!atsApi.getStoreService().isOfType(insertion, AtsArtifactTypes.Insertion)) {
         throw new OseeCoreException("Given id not insertion type");
      }
      // get the insertions related to the given program
      Collection<ArtifactToken> results = atsApi.getRelationResolver().getRelated(insertion,
         AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity);
      List<IAtsConfigObject> insertionActivitys = new LinkedList<>();
      for (ArtifactToken insertionActivity : results) {
         insertionActivitys.add(atsApi.getProgramService().getInsertionActivityById(insertionActivity));
      }
      // http://aruld.info/handling-generified-collections-in-jersey-jax-rs/
      GenericEntity<List<IAtsConfigObject>> entity = new GenericEntity<List<IAtsConfigObject>>(insertionActivitys) { //
      };
      return Response.ok(entity).build();
   }

   @POST
   @Path("{programId}/insertion/{insertionId}/activity")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createInsertionActivity(@PathParam("programId") long programId, @PathParam("insertionId") long insertionId, JaxInsertionActivity newActivity) {
      ArtifactToken insertion = atsApi.getQueryService().getArtifact(insertionId);
      if (insertion == null) {
         throw new OseeCoreException("Given insertion id not found");
      }
      if (!atsApi.getStoreService().isOfType(insertion, AtsArtifactTypes.Insertion)) {
         throw new OseeCoreException("Given id not insertion type");
      }
      ArtifactToken artifact = atsApi.getQueryService().getArtifact(newActivity.getId());
      if (artifact != null) {
         throw new OseeStateException("Insertion Activity with id %d already exists", newActivity.getId());
      }
      IAtsInsertionActivity created = getProgramOperations().createInsertionActivity(insertion, newActivity);
      return getResponse(created);
   }

   @PUT
   @Path("{programId}/insertion/{insertionId}/activity")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateInsertionActivity(@PathParam("programId") long programId, @PathParam("insertionId") long insertionId, JaxInsertionActivity newActivity) {
      ArtifactToken insertionActivityArt = atsApi.getQueryService().getArtifact(newActivity.getId());
      if (insertionActivityArt == null) {
         throw new OseeCoreException("Given insertion activity id not found");
      }
      if (!atsApi.getStoreService().isOfType(insertionActivityArt, AtsArtifactTypes.InsertionActivity)) {
         throw new OseeCoreException("Given insertion activity id not insertion activity type");
      }
      Response response = null;
      if (!insertionActivityArt.getName().equals(newActivity.getName())) {
         IAtsConfigObject updated = getProgramOperations().updateInsertionActivity(newActivity);
         response = Response.ok().entity(updated).build();
      } else {
         response = Response.notModified().build();
      }
      return response;
   }

   @GET
   @Path("{programId}/insertion/{insertionId}/activity/{iaId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInsertionActivityDetails(@PathParam("programId") long programId, @PathParam("insertionId") long insertionId, @PathParam("iaId") ArtifactId iaId) {
      return Response.ok().entity(atsApi.getProgramService().getInsertionActivityById(iaId)).build();
   }

   @DELETE
   @Path("{programId}/insertion/{insertionId}/activity/{iaId}")
   public Response deleteInsertionActivity(@PathParam("iaId") ArtifactId iaId) {
      getProgramOperations().deleteInsertionActivity(iaId);
      return Response.ok().build();
   }

   private Response getResponse(IAtsConfigObject config) {
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path(String.valueOf(config.getId())).build();
      Response response = Response.created(location).entity(config).build();
      return response;
   }
}
