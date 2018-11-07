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
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.program.ProgramVersions;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class ProgramEndpointImpl extends BaseConfigEndpointImpl<JaxProgram> implements ProgramEndpointApi {

   private final long countryId;

   public ProgramEndpointImpl(AtsApi atsApi) {
      this(atsApi, 0L);
   }

   public ProgramEndpointImpl(AtsApi atsApi, long countryId) {
      super(AtsArtifactTypes.Program, null, atsApi);
      this.countryId = countryId;
   }

   @PUT
   @Override
   public Response update(JaxProgram jaxProgram) throws Exception {
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxProgram.getId());
      if (artifact == null) {
         throw new OseeStateException("Artifact with id %d not found", jaxProgram.getId());
      }
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create " + artifactType.getName(), AtsCoreUsers.SYSTEM_USER);
      ArtifactToken programArt = changes.createArtifact(artifactType, jaxProgram.getName(), jaxProgram.getId());
      IAtsProgram program = atsApi.getProgramService().getProgramById(programArt);
      if (!programArt.getName().equals(jaxProgram.getName())) {
         changes.setSoleAttributeValue(program, CoreAttributeTypes.Name, jaxProgram.getName());
      }
      changes.execute();
      return Response.created(new URI("/" + jaxProgram.getId())).build();
   }

   @Override
   public JaxProgram getConfigObject(ArtifactId artifact) {
      JaxProgram jaxProgram = new JaxProgram();
      IAtsProgram program = atsApi.getProgramService().getProgramById(artifact);
      jaxProgram.setName(program.getName());
      jaxProgram.setId(program.getId());
      jaxProgram.setActive(program.isActive());
      jaxProgram.setDescription(program.getDescription());
      return jaxProgram;
   }

   @Override
   public List<JaxProgram> getObjects() {
      List<JaxProgram> configs = new ArrayList<>();
      if (countryId == 0L) {
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
            configs.add(getConfigObject(art));
         }
      } else {
         for (ArtifactToken art : atsApi.getRelationResolver().getRelated(
            atsApi.getQueryService().getArtifact(countryId), AtsRelationTypes.CountryToProgram_Program)) {
            JaxProgram program = getConfigObject(art);
            program.setCountryId(countryId);
            configs.add(program);
         }
      }
      return configs;
   }

   @Override
   protected void create(JaxProgram jaxProgram, ArtifactId programArtId, IAtsChangeSet changes) {
      ArtifactReadable programArt = (ArtifactReadable) programArtId;
      if (programArt.getRelatedCount(AtsRelationTypes.CountryToProgram_Country) == 0) {
         ArtifactReadable countryArt =
            (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxProgram.getCountryId());
         changes.relate(countryArt, AtsRelationTypes.CountryToProgram_Program, programArt);
      }
   }

   @Override
   public InsertionEndpointApi getInsertion(long programId) {
      return new InsertionEndpointImpl(atsApi, programId);
   }

   @Override
   @GET
   @Path("version")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ProgramVersions> getVersions(@Context UriInfo uriInfo) {
      boolean activeOnly = true;
      IArtifactType artType = AtsArtifactTypes.Program;
      if (uriInfo != null) {
         MultivaluedMap<String, String> qp = uriInfo.getQueryParameters(true);
         String activeStr = qp.getFirst("activeOnly");
         if (Strings.isValid(activeStr)) {
            activeOnly = "true".equals(activeStr);
         }
         String artifactTypeId = qp.getFirst("artifactTypeId");
         if (Strings.isNumeric(artifactTypeId)) {
            artType = atsApi.getStoreService().getArtifactType(Long.valueOf(artifactTypeId));
         }
      }

      return atsApi.getProgramService().getProgramVersions(artType, activeOnly);
   }
}
