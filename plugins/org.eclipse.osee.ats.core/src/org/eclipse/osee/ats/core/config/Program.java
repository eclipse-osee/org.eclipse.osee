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
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class Program extends AtsConfigObject implements IAtsProgram {
   IAtsTeamDefinition teamDefinition = null;
   private final IAtsServices atsServices;

   public Program(Log logger, IAtsServices atsServices, ArtifactId artifact) {
      super(logger, atsServices, artifact);
      this.atsServices = atsServices;
   }

   @Override
   public String getTypeName() {
      return "Program";
   }

   @Override
   public long getUuid() {
      return artifact.getUuid();
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      if (teamDefinition == null) {
         String teamDefGuid =
            atsServices.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.TeamDefinition, "");
         Long uuid = atsServices.getStoreService().getUuidFromGuid(teamDefGuid);
         teamDefinition = atsServices.getSoleByUuid(uuid, IAtsTeamDefinition.class);
      }
      return teamDefinition;
   }

   @Override
   public String getNamespace() {
      return atsServices.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Namespace, "");
   }

   @Override
   public long getCountryUuid() {
      long uuid = 0L;
      ArtifactId countryArt =
         atsServices.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.CountryToProgram_Country);
      if (countryArt != null) {
         uuid = countryArt.getUuid();
      }
      return uuid;
   }

}
