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

package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class Country extends AtsConfigObject implements IAtsCountry {

   public Country(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, AtsArtifactTypes.Country);
   }

   @Override
   public Long getId() {
      return artifact.getId();
   }

   @Override
   public String getDescription() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Description, "");
   }
}