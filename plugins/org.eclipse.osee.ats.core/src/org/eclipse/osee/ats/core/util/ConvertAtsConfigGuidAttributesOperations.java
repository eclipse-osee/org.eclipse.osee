/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsConfigGuidAttributesOperations {

   public static void convertTeamDefinitionIfNeeded(IAtsChangeSet changes, ArtifactToken art, AtsApi atsApi) {
      // convert guid to id
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isInvalid()) {
         String teamDefGuid =
            atsApi.getAttributeResolver().getSoleAttributeValue(art, AtsAttributeTypes.TeamDefinition, "");
         if (Strings.isValid(teamDefGuid)) {
            ArtifactToken artifact = atsApi.getQueryService().getArtifactByGuidOrSentinel(teamDefGuid);
            IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(artifact);
            changes.setSoleAttributeValue(art, AtsAttributeTypes.TeamDefinitionReference, teamDef.getStoreObject());
         }
      }
      // convert id to guid
      String teamDefGuid =
         atsApi.getAttributeResolver().getSoleAttributeValue(art, AtsAttributeTypes.TeamDefinition, "");
      if (!Strings.isValid(teamDefGuid)) {
         ArtifactId teamDefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
         ArtifactToken artifact = atsApi.getQueryService().getArtifact(teamDefArt);
         if (artifact != null) {
            changes.setSoleAttributeValue(art, AtsAttributeTypes.TeamDefinition, artifact.getGuid());
         }
      }
   }

}
