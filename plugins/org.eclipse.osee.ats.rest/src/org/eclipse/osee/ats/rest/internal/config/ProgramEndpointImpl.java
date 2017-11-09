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
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Donald G. Dunne
 */
public class ProgramEndpointImpl extends BaseConfigEndpointImpl<JaxProgram> implements ProgramEndpointApi {

   private final long countryId;

   public ProgramEndpointImpl(IAtsServer atsServer) {
      this(atsServer, 0L);
   }

   public ProgramEndpointImpl(IAtsServer atsServer, long countryId) {
      super(AtsArtifactTypes.Program, null, atsServer);
      this.countryId = countryId;
   }

   @PUT
   @Override
   public Response update(JaxProgram program) throws Exception {
      ArtifactReadable artifact = atsServer.getArtifact(program.getId());
      if (artifact == null) {
         throw new OseeStateException("Artifact with id %d not found", program.getId());
      }
      IAtsChangeSet changes =
         atsServer.getStoreService().createAtsChangeSet("Create " + artifactType.getName(), AtsCoreUsers.SYSTEM_USER);
      ArtifactReadable configArtifact =
         (ArtifactReadable) changes.createArtifact(artifactType, program.getName(), GUID.create(), program.getId());
      IAtsConfigObject configObject = atsServer.getConfigItemFactory().getConfigObject(configArtifact);
      if (!configArtifact.getName().equals(program.getName())) {
         changes.setSoleAttributeValue(configObject, CoreAttributeTypes.Name, program.getName());
      }
      changes.execute();
      return Response.created(new URI("/" + program.getId())).build();
   }

   @Override
   public JaxProgram getConfigObject(ArtifactId artifact) {
      JaxProgram jaxProgram = new JaxProgram();
      IAtsProgram program = atsServer.getConfigItemFactory().getProgram(artifact);
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
         for (ArtifactReadable art : atsServer.getQuery().andIsOfType(artifactType).getResults()) {
            configs.add(getConfigObject(art));
         }
      } else {
         for (ArtifactReadable art : atsServer.getArtifact(countryId).getRelated(
            AtsRelationTypes.CountryToProgram_Program)) {
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
         ArtifactReadable countryArt = atsServer.getArtifact(jaxProgram.getCountryId());
         changes.relate(countryArt, AtsRelationTypes.CountryToProgram_Program, programArt);
      }
   }

   @Override
   public InsertionEndpointApi getInsertion(long programId) {
      return new InsertionEndpointImpl(atsServer, programId);
   }
}
