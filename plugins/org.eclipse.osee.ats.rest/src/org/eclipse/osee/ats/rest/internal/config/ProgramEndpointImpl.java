/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.program.ProgramVersions;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.SkipAtsConfigJsonWriter;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

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

   @Override
   @SkipAtsConfigJsonWriter
   public List<JaxProgram> get() {
      return getConfigs();
   }

   @Override
   @SkipAtsConfigJsonWriter
   public JaxProgram create(JaxProgram program) {
      return createConfig(program);
   }

   @Override
   public JaxProgram getConfig(long id) {
      JaxProgram jProg = super.getConfig(id);
      ArtifactToken country = atsApi.getRelationResolver().getRelatedOrNull(ArtifactId.valueOf(id),
         AtsRelationTypes.CountryToProgram_Country);
      jProg.setCountryId(country.getId());
      return jProg;
   }

   @Override
   public void delete(long id) {
      deleteConfig(id);
   }

   @Override
   @SkipAtsConfigJsonWriter
   public JaxProgram update(JaxProgram program) {
      return createConfig(program);
   }

   @Override
   public JaxProgram getConfig(ArtifactId artifact) {
      return atsApi.getProgramService().getJaxProgram(artifact);
   }

   @Override
   public List<JaxProgram> getConfigs() {
      List<JaxProgram> programs = new ArrayList<>();
      if (countryId == 0L) {
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
            programs.add(getConfig(art));
         }
      } else {
         for (ArtifactToken art : atsApi.getRelationResolver().getRelated(
            atsApi.getQueryService().getArtifact(countryId), AtsRelationTypes.CountryToProgram_Program)) {
            JaxProgram program = atsApi.getProgramService().getJaxProgram(art);
            program.setCountryId(countryId);
            programs.add(program);
         }
      }
      return programs;
   }

   @Override
   protected void getConfigExt(ArtifactToken art, JaxProgram config) {
      super.getConfigExt(art, config);
   }

   @Override
   protected void createConfigExt(JaxProgram jaxProgram, ArtifactId programArtId, IAtsChangeSet changes) {
      ArtifactReadable programArt = (ArtifactReadable) programArtId;
      if (programArt.getRelatedCount(AtsRelationTypes.CountryToProgram_Country) == 0) {
         ArtifactReadable countryArt =
            (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxProgram.getCountryId());
         changes.relate(countryArt, AtsRelationTypes.CountryToProgram_Program, programArt);
      }
   }

   @Override
   public InsertionEndpointApi getInsertion(long id) {
      return new InsertionEndpointImpl(atsApi, id);
   }

   @Override
   public List<ProgramVersions> getVersions(UriInfo uriInfo) {
      boolean activeOnly = true;
      ArtifactTypeToken artType = AtsArtifactTypes.Program;
      if (uriInfo != null) {
         MultivaluedMap<String, String> qp = uriInfo.getQueryParameters(true);
         String activeStr = qp.getFirst("activeOnly");
         if (Strings.isValid(activeStr)) {
            activeOnly = "true".equals(activeStr);
         }
         String artifactTypeId = qp.getFirst("artifactTypeId");
         if (Strings.isNumeric(artifactTypeId)) {
            artType = atsApi.tokenService().getArtifactType(Long.valueOf(artifactTypeId));
         }
      }
      return atsApi.getProgramService().getProgramVersions(artType, activeOnly);
   }

}
