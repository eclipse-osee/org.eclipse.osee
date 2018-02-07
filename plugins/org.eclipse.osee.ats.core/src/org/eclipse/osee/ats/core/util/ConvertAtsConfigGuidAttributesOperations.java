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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsConfigGuidAttributesOperations {

   // Remove static after 25.0
   public static final AttributeTypeToken TeamDefinition =
      AtsAttributeTypes.createType(1152921504606847201L, "Team Definition");
   public static final AttributeTypeToken ActionableItem = AtsAttributeTypes.createType(1152921504606847200L,
      "Actionable Item", "Actionable Items that are impacted by this change.");
   // Remove static after 26.0
   public static final AttributeTypeToken WorkflowDefinition = AtsAttributeTypes.createType(1152921504606847149L,
      "Workflow Definition", "Specific work flow definition id used by this Workflow artifact");
   public static final AttributeTypeToken RelatedTaskWorkDefinition = AtsAttributeTypes.createType(1152921504606847152L,
      "Related Task Workflow Definition", "Specific work flow definition id used by Tasks related to this Workflow");
   public static final AttributeTypeToken RelatedPeerWorkflowDefinition =
      AtsAttributeTypes.createType(1152921504606847870L, "Related Peer Workflow Definition",
         "Specific work flow definition id used by Peer To Peer Reviews for this Team");

   public static void convertActionableItemsIfNeeded(IAtsChangeSet changes, ArtifactToken art, AtsApi atsApi) {
      // convert guids to id
      Collection<ArtifactId> currentAiRefIds =
         atsApi.getAttributeResolver().getAttributeValues(art, AtsAttributeTypes.ActionableItemReference);

      List<ArtifactId> neededAiRefIds = new LinkedList<>();
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(art, ActionableItem)) {
         String aiArtGuid = (String) attr.getValue();
         IAtsActionableItem ai = atsApi.getQueryService().getConfigItem(aiArtGuid);
         if (ai == null) {
            atsApi.getLogger().error("AI not found for aiArtGuid " + aiArtGuid + " for art " + art.toStringWithId());
         } else if (!currentAiRefIds.contains(ai.getId())) {
            neededAiRefIds.add(ai.getStoreObject());
         }
      }

      for (ArtifactId need : neededAiRefIds) {
         changes.addAttribute(art, AtsAttributeTypes.ActionableItemReference, need);
      }

      // convert id to guid
      Collection<IAttribute<Object>> aiGuidAttrs = atsApi.getAttributeResolver().getAttributes(art, ActionableItem);
      List<String> currentAiGuidIds = new LinkedList<>();
      for (IAttribute<Object> aiRefAttr : aiGuidAttrs) {
         currentAiGuidIds.add(aiRefAttr.getValue().toString());
      }

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
         changes.addAttribute(art, ActionableItem, guid);
      }
   }

   public static void convertTeamDefinitionIfNeeded(IAtsChangeSet changes, ArtifactToken art, AtsApi atsApi) {
      // convert guid to id
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isInvalid()) {
         String teamDefGuid = atsApi.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
         if (Strings.isValid(teamDefGuid)) {
            IAtsTeamDefinition teamDef = atsApi.getQueryService().getConfigItem(teamDefGuid);
            changes.setSoleAttributeValue(art, AtsAttributeTypes.TeamDefinitionReference, teamDef.getStoreObject());
         }
      }
      // convert id to guid
      String teamDefGuid = atsApi.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
      if (!Strings.isValid(teamDefGuid)) {
         ArtifactId teamDefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
         ArtifactToken artifact = atsApi.getQueryService().getArtifact(teamDefArt);
         if (artifact != null) {
            changes.setSoleAttributeValue(art, TeamDefinition, artifact.getGuid());
         }
      }
   }

   public static void convertWorkflowDefinitionIfNeeded(IAtsChangeSet changes, ArtifactToken art, AtsApi atsApi) {
      // convert string to id
      ArtifactId workDefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
         AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
      if (workDefArt.isInvalid()) {
         String workDefName = atsApi.getAttributeResolver().getSoleAttributeValue(art, WorkflowDefinition, null);
         if (Strings.isValid(workDefName)) {
            IAtsWorkDefinition workDef = atsApi.getWorkDefinitionService().getWorkDefinition(workDefName);
            ArtifactId workDefArt2 = getWorkDefArt(workDef, atsApi);
            changes.setSoleAttributeValue(art, AtsAttributeTypes.WorkflowDefinitionReference, workDefArt2);
         }
      }
      // convert id to string
      String teamDefGuid = atsApi.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
      if (!Strings.isValid(teamDefGuid)) {
         ArtifactId teamDefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
         ArtifactToken artifact = atsApi.getQueryService().getArtifact(teamDefArt);
         if (artifact != null) {
            changes.setSoleAttributeValue(art, TeamDefinition, artifact.getGuid());
         }
      }
   }

   private static final Map<IAtsWorkDefinition, ArtifactId> workDefToArt = new HashMap<>();

   private static ArtifactId getWorkDefArt(IAtsWorkDefinition workDef, AtsApi atsApi) {
      ArtifactId art = workDefToArt.get(workDef);
      if (art == null) {
         art = atsApi.getQueryService().getArtifact(workDef.getId());
      }
      return art;
   }

}
