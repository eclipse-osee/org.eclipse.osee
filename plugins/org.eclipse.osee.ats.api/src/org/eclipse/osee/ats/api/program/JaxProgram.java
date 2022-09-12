/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.program;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxNewAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class JaxProgram extends JaxNewAtsConfigObject implements IAtsProgram {

   long countryId;

   public long getCountryId() {
      return countryId;
   }

   public void setCountryId(long countryId) {
      this.countryId = countryId;
   }

   public static JaxProgram create(ArtifactToken artifact, AtsApi atsApi) {
      JaxProgram jaxProgram = new JaxProgram();
      IAtsProgram program = atsApi.getProgramService().getProgramById(artifact);
      jaxProgram.setName(program.getName());
      jaxProgram.setId(program.getId());
      jaxProgram.setActive(program.isActive());
      jaxProgram.setDescription(program.getDescription());
      jaxProgram.setStoreObject(artifact);
      return jaxProgram;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.Program;
   }
}
