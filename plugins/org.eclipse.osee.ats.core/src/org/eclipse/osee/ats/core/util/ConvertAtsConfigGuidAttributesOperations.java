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
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsConfigGuidAttributesOperations {

   public static final AttributeTypeToken TeamDefinition =
      AtsAttributeTypes.createType(1152921504606847201L, "Team Definition");
   public static final AttributeTypeToken ActionableItem = AtsAttributeTypes.createType(1152921504606847200L,
      "Actionable Item", "Actionable Items that are impacted by this change.");

   public static void convertActionableItemsIfNeeded(IAtsChangeSet changes, ArtifactToken art, IAtsServices services) {
      // convert guids to id
      Collection<ArtifactId> currentAiRefIds =
         services.getAttributeResolver().getAttributeValues(art, AtsAttributeTypes.ActionableItemReference);

      List<ArtifactId> neededAiRefIds = new LinkedList<>();
      for (IAttribute<?> attr : services.getAttributeResolver().getAttributes(art, ActionableItem)) {
         String aiArtGuid = (String) attr.getValue();
         IAtsActionableItem ai = services.getConfigItem(aiArtGuid);
         if (ai == null) {
            services.getLogger().error("AI not found for aiArtGuid " + aiArtGuid + " for art " + art.toStringWithId());
         } else if (!currentAiRefIds.contains(ai.getId())) {
            neededAiRefIds.add(ai.getStoreObject());
         }
      }

      for (ArtifactId need : neededAiRefIds) {
         changes.addAttribute(art, AtsAttributeTypes.ActionableItemReference, need);
      }

      // convert id to guid
      Collection<IAttribute<Object>> aiGuidAttrs = services.getAttributeResolver().getAttributes(art, ActionableItem);
      List<String> currentAiGuidIds = new LinkedList<>();
      for (IAttribute<Object> aiRefAttr : aiGuidAttrs) {
         currentAiGuidIds.add(aiRefAttr.getValue().toString());
      }

      List<String> neededAiGuidIds = new LinkedList<>();
      Collection<ArtifactId> aiArts =
         services.getAttributeResolver().getAttributeValues(art, AtsAttributeTypes.ActionableItemReference);
      for (ArtifactId id : aiArts) {
         IAtsActionableItem ai = services.getConfigItem(id);
         if (ai == null) {
            services.getLogger().error("AI not found for id " + id + " for art " + art.toStringWithId());
         } else if (!currentAiGuidIds.contains(ai.getStoreObject().getGuid())) {
            neededAiGuidIds.add(ai.getStoreObject().getGuid());
         }
      }

      for (String guid : neededAiGuidIds) {
         changes.addAttribute(art, ActionableItem, guid);
      }
   }

   public static void convertTeamDefinitionIfNeeded(IAtsChangeSet changes, ArtifactToken art, IAtsServices services) {
      // convert guid to id
      String teamDefId = services.getAttributeResolver().getSoleAttributeValueAsString(art,
         AtsAttributeTypes.TeamDefinitionReference, null);
      if (!Strings.isNumeric(teamDefId)) {
         String teamDefGuid = services.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
         if (Strings.isValid(teamDefGuid)) {
            IAtsTeamDefinition teamDef = services.getConfigItem(teamDefGuid);
            changes.setSoleAttributeValue(art, AtsAttributeTypes.TeamDefinitionReference, teamDef.getStoreObject());
         }
      }
      // convert id to guid
      String teamDefGuid = services.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
      if (!Strings.isValid(teamDefGuid)) {
         teamDefId = services.getAttributeResolver().getSoleAttributeValueAsString(art,
            AtsAttributeTypes.TeamDefinitionReference, null);
         if (Strings.isNumeric(teamDefId)) {
            ArtifactId artifact = services.getArtifact(Long.valueOf(teamDefId));
            if (artifact != null) {
               changes.setSoleAttributeValue(art, TeamDefinition, artifact.getGuid());
            }
         }
      }
   }

}
