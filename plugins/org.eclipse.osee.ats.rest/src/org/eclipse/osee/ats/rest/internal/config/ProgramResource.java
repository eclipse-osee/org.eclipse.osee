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
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Donald G. Dunne
 */
@Path("program")
public class ProgramResource extends AbstractConfigResource {

   @Context
   private UriInfo uriInfo;

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   public ProgramResource(IAtsServer atsServer) {
      super(AtsArtifactTypes.Program, atsServer);
   }

   @GET
   @Path("version")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxNamedId> getProgramsVersions() throws Exception {
      List<JaxNamedId> programsVers = new LinkedList<>();
      for (IAtsProgram program : atsServer.getProgramService().getPrograms()) {
         for (IAtsVersion ver : atsServer.getProgramService().getVersions(program)) {
            programsVers.add(
               JaxNamedId.construct(ver.getId(), String.format("%s - %s", program.getName(), ver.getName())));
         }
      }
      return programsVers;
   }

   @GET
   @Path("{uuid}/insertion")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getProgramInsertions(@PathParam("uuid") long uuid) throws Exception {
      ArtifactReadable programArt = atsServer.getArtifact(uuid);
      if (programArt == null) {
         throw new OseeCoreException("Given uuid not found");
      }
      if (!programArt.isOfType(AtsArtifactTypes.Program)) {
         throw new OseeCoreException("Given uuid not program type");
      }
      // get the insertions related to the given program
      ResultSet<ArtifactReadable> results = programArt.getRelated(AtsRelationTypes.ProgramToInsertion_Insertion);
      List<IAtsConfigObject> insertions = new LinkedList<>();
      for (ArtifactReadable insertion : results) {
         insertions.add(atsServer.getConfigItemFactory().getInsertion(insertion));
      }
      // http://aruld.info/handling-generified-collections-in-jersey-jax-rs/
      GenericEntity<List<IAtsConfigObject>> entity = new GenericEntity<List<IAtsConfigObject>>(insertions) { //
      };
      return Response.ok(entity).build();
   }

   @POST
   @Path("{programUuid}/insertion")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createInsertion(@PathParam("programUuid") long programUuid, JaxInsertion newInsertion) throws Exception {
      ArtifactReadable programArt = atsServer.getArtifact(programUuid);
      if (programArt == null) {
         throw new OseeCoreException("Given uuid not found");
      }
      if (!programArt.isOfType(AtsArtifactTypes.Program)) {
         throw new OseeCoreException("Given uuid not program type");
      }
      ResultSet<?> results = atsServer.getQuery().andUuid(newInsertion.getUuid()).getResults();
      if (results.size() != 0) {
         throw new OseeStateException("Insertion with id %d already exists", newInsertion.getUuid());
      }
      IAtsInsertion created = atsServer.getConfigItemFactory().createInsertion(programArt, newInsertion);
      return getResponse(created);
   }

   @PUT
   @Path("{programUuid}/insertion")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateInsertion(@PathParam("programUuid") long programUuid, JaxInsertion insertion) {
      ArtifactReadable insertionArt = atsServer.getArtifact(insertion.getUuid());
      if (insertionArt == null) {
         throw new OseeCoreException("Given insertion uuid not found");
      }
      if (!insertionArt.isOfType(AtsArtifactTypes.Insertion)) {
         throw new OseeCoreException("Given insertion uuid not insertion type");
      }
      Response response = null;
      if (!insertionArt.getName().equals(insertion.getName())) {
         IAtsConfigObject updated = atsServer.getConfigItemFactory().updateInsertion(insertion);
         response = Response.ok().entity(updated).build();
      } else {
         response = Response.notModified().build();
      }
      return response;
   }

   @GET
   @Path("{programUuid}/insertion/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInsertionDetails(@PathParam("programUuid") long programUuid, @PathParam("uuid") long uuid) throws Exception {
      ArtifactReadable configArt = atsServer.getArtifact(uuid);
      if (configArt == null) {
         throw new OseeCoreException("Given uuid not found");
      }
      return Response.ok().entity(atsServer.getConfigItemFactory().getInsertion(configArt)).build();
   }

   @DELETE
   @Path("{programUuid}/insertion/{insertionUuid}")
   public Response deleteInsertion(@PathParam("insertionUuid") long insertionUuid) throws Exception {
      ArtifactReadable insertion = atsServer.getArtifact(insertionUuid);
      if (insertion == null) {
         throw new OseeCoreException("Given insertion uuid not found");
      }
      atsServer.getConfigItemFactory().deleteInsertion(insertion);
      return Response.ok().build();
   }

   @GET
   @Path("{programUuid}/insertion/{insertionUuid}/activity")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInsertionActivitys(@PathParam("programUuid") long programUuid, @PathParam("insertionUuid") long insertionUuid) throws Exception {
      ArtifactReadable insertion = atsServer.getArtifact(insertionUuid);
      if (insertion == null) {
         throw new OseeCoreException("Given insertion uuid not found");
      }
      if (!insertion.isOfType(AtsArtifactTypes.Insertion)) {
         throw new OseeCoreException("Given uuid not insertion type");
      }
      // get the insertions related to the given program
      ResultSet<ArtifactReadable> results =
         insertion.getRelated(AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity);
      List<IAtsConfigObject> insertionActivitys = new LinkedList<>();
      for (ArtifactReadable insertionActivity : results) {
         insertionActivitys.add(atsServer.getConfigItemFactory().getInsertionActivity(insertionActivity));
      }
      // http://aruld.info/handling-generified-collections-in-jersey-jax-rs/
      GenericEntity<List<IAtsConfigObject>> entity = new GenericEntity<List<IAtsConfigObject>>(insertionActivitys) { //
      };
      return Response.ok(entity).build();
   }

   @POST
   @Path("{programUuid}/insertion/{insertionUuid}/activity")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createInsertionActivity(@PathParam("programUuid") long programUuid, @PathParam("insertionUuid") long insertionUuid, JaxInsertionActivity newActivity) throws Exception {
      ArtifactReadable insertion = atsServer.getArtifact(insertionUuid);
      if (insertion == null) {
         throw new OseeCoreException("Given insertion uuid not found");
      }
      if (!insertion.isOfType(AtsArtifactTypes.Insertion)) {
         throw new OseeCoreException("Given uuid not insertion type");
      }
      ResultSet<?> results = atsServer.getQuery().andUuid(newActivity.getUuid()).getResults();
      if (results.size() != 0) {
         throw new OseeStateException("Insertion Activity with id %d already exists", newActivity.getUuid());
      }
      IAtsInsertionActivity created = atsServer.getConfigItemFactory().createInsertionActivity(insertion, newActivity);
      return getResponse(created);
   }

   @PUT
   @Path("{programUuid}/insertion/{insertionUuid}/activity")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateInsertionActivity(@PathParam("programUuid") long programUuid, @PathParam("insertionUuid") long insertionUuid, JaxInsertionActivity newActivity) throws Exception {
      ArtifactReadable insertionActivityArt = atsServer.getArtifact(newActivity.getUuid());
      if (insertionActivityArt == null) {
         throw new OseeCoreException("Given insertion activity uuid not found");
      }
      if (!insertionActivityArt.isOfType(AtsArtifactTypes.InsertionActivity)) {
         throw new OseeCoreException("Given insertion activity uuid not insertion activity type");
      }
      Response response = null;
      if (!insertionActivityArt.getName().equals(newActivity.getName())) {
         IAtsConfigObject updated = atsServer.getConfigItemFactory().updateInsertionActivity(newActivity);
         response = Response.ok().entity(updated).build();
      } else {
         response = Response.notModified().build();
      }
      return response;
   }

   @GET
   @Path("{programUuid}/insertion/{insertionUuid}/activity/{iaUuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInsertionActivityDetails(@PathParam("programUuid") long programUuid, @PathParam("insertionUuid") long insertionUuid, @PathParam("iaUuid") long iaUuid) throws Exception {
      ArtifactReadable configArt = atsServer.getQuery().andUuid(iaUuid).getResults().getExactlyOne();
      return Response.ok().entity(atsServer.getConfigItemFactory().getInsertionActivity(configArt)).build();
   }

   @DELETE
   @Path("{programUuid}/insertion/{insertionUuid}/activity/{iaUuid}")
   public Response deleteInsertionActivity(@PathParam("iaUuid") long iaUuid) throws Exception {
      ArtifactReadable insertionActivity = atsServer.getQuery().andUuid(iaUuid).getResults().getExactlyOne();
      atsServer.getConfigItemFactory().deleteInsertionActivity(insertionActivity);
      return Response.ok().build();
   }

   private Response getResponse(IAtsConfigObject config) {
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path(String.valueOf(config.getId())).build();
      Response response = Response.created(location).entity(config).build();
      return response;
   }
}
