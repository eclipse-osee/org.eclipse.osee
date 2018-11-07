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
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class CountryEndpointImpl extends BaseConfigEndpointImpl<JaxCountry> implements CountryEndpointApi {

   public CountryEndpointImpl(AtsApi atsApi) {
      super(AtsArtifactTypes.Country, AtsArtifactToken.CountryFolder, atsApi);
   }

   @Override
   public Response update(JaxCountry country) throws Exception {
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(country.getId());
      if (artifact == null) {
         throw new OseeStateException("Artifact with id %d not found", country.getId());
      }
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create " + artifactType.getName(), AtsCoreUsers.SYSTEM_USER);
      ArtifactToken configArtifact = changes.createArtifact(artifactType, country.getName(), country.getId());
      if (!configArtifact.getName().equals(country.getName())) {
         changes.setSoleAttributeValue(configArtifact, CoreAttributeTypes.Name, country.getName());
      }
      changes.execute();
      return Response.created(new URI("/" + country.getId())).build();
   }

   @Override
   public JaxCountry getConfigObject(ArtifactId artifact) {
      JaxCountry jaxCountry = new JaxCountry();
      IAtsCountry country = atsApi.getProgramService().getCountryById(artifact);
      jaxCountry.setName(country.getName());
      jaxCountry.setId(country.getId());
      jaxCountry.setActive(country.isActive());
      jaxCountry.setDescription(country.getDescription());
      return jaxCountry;
   }

   @Override
   public List<JaxCountry> getObjects() {
      List<JaxCountry> configs = new ArrayList<>();
      for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
         configs.add(getConfigObject(art));
      }
      return configs;
   }

   @Override
   public ProgramEndpointApi getProgram(long countryId) {
      return new ProgramEndpointImpl(atsApi, countryId);
   }

}
