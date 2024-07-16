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

import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class CountryEndpointImpl extends BaseConfigEndpointImpl<JaxCountry> implements CountryEndpointApi {

   public CountryEndpointImpl(AtsApi atsApi) {
      super(AtsArtifactTypes.Country, AtsArtifactToken.CountryFolder, atsApi);
   }

   @Override
   public List<JaxCountry> get() {
      return getConfigs();
   }

   @Override
   public JaxCountry create(JaxCountry country) {
      return createConfig(country);
   }

   @Override
   public void delete(long id) {
      deleteConfig(id);
   }

   @Override
   public JaxCountry update(JaxCountry country) {
      return createConfig(country);
   }

   @Override
   public JaxCountry getConfig(ArtifactId artifact) {
      return atsApi.getProgramService().getCountry(artifact);
   }

   @Override
   public ProgramEndpointApi getProgram(long id) {
      return new ProgramEndpointImpl(atsApi, id);
   }

}
