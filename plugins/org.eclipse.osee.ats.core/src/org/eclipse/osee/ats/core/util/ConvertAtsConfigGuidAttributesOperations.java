/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsConfigGuidAttributesOperations {

   public static void convertActionableItemsIfNeeded(IAtsChangeSet changes, ArtifactToken art, AtsApi atsApi) {
      // convert guids to id
      Collection<ArtifactId> currentAiRefIds =
         atsApi.getAttributeResolver().getAttributeValues(art, AtsAttributeTypes.ActionableItemReference);

      List<ArtifactId> neededAiRefIds = new LinkedList<>();
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(art, AtsAttributeTypes.ActionableItem)) {
         String aiArtGuid = (String) attr.getValue();
         ArtifactToken ai = atsApi.getQueryService().getArtifactByGuidOrSentinel(aiArtGuid);
         if (ai.isInvalid()) {
            atsApi.getLogger().error("AI not found for aiArtGuid " + aiArtGuid + " for art " + art.toStringWithId());
         } else if (!currentAiRefIds.contains(ai.getId())) {
            neededAiRefIds.add(ai);
         }
      }

      for (ArtifactId need : neededAiRefIds) {
         changes.addAttribute(art, AtsAttributeTypes.ActionableItemReference, need);
      }

      // convert id to guid
      List<String> currentAiGuidIds =
         atsApi.getAttributeResolver().getAttributesToStringList(art, AtsAttributeTypes.ActionableItem);

      List<String> neededAiGuidIds = new LinkedList<>();
      Collection<ArtifactId> aiArts =
         atsApi.getAttributeResolver().getAttributeValues(art, AtsAttributeTypes.ActionableItemReference);
      for (ArtifactId id : aiArts) {
         IAtsActionableItem ai = atsApi.getQueryService().getConfigItem(id);
         if (ai == null) {
            atsApi.getLogger().error("AI not found for id " + id + " for art " + art.toStringWithId());
         } else if (!currentAiGuidIds.contains(ai.getStoreObject().getGuid())) {
            neededAiGuidIds.add(ai.getStoreObject().getGuid());
         }
      }

      for (String guid : neededAiGuidIds) {
         changes.addAttribute(art, AtsAttributeTypes.ActionableItem, guid);
      }
   }

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
